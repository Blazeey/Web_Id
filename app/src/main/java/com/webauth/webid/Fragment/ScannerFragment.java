package com.webauth.webid.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by venki on 28/2/18.
 */

public class ScannerFragment extends Fragment implements ZXingScannerView.ResultHandler{

    private static final String TAG = "SCAN";
    private String domain,id;

    private ZXingScannerView zXingScannerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        zXingScannerView = new ZXingScannerView(getActivity());
        return zXingScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        Toast.makeText(getActivity(), rawResult.getText(), Toast.LENGTH_SHORT).show();

        // If you would like to resume scanning, call this method below:
        zXingScannerView.resumeCameraPreview(this);

        ScannerFragment.QRResult qrResult = new ScannerFragment.QRResult(getActivity());
        qrResult.execute(rawResult.getText());
    }

    public class QRResult extends AsyncTask<String, Void, String> {

        public Context context;
        public String post_data;

        public QRResult(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!result.equals("")) {
                Toast.makeText(context, "Sent Successfully", Toast.LENGTH_SHORT).show();

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference();

                databaseReference.child("Sessions").child(firebaseAuth.getUid()).child(domain).setValue(id);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String url = "https://us-central1-abacus-webid.cloudfunctions.net/getAuthorizationPost";
            JSONObject result = null;
            try {
                String p = params[0];
                if(p.charAt(0)=='"')
                    return "";
                result = foo(url,parseData(params[0]));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert result != null;
            return result.toString();
        }
    }
    public static JSONObject foo(String url, JSONObject json) {
        JSONObject jsonObjectResp = null;

        try {

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();

            Log.v("JSON",json.toString());
            okhttp3.RequestBody body = RequestBody.create(JSON, json.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Log.v("JSON","here");
            okhttp3.Response response = client.newCall(request).execute();

            String networkResp = response.body().string();
            if (!networkResp.isEmpty()) {
                jsonObjectResp = parseJSONStringToJSONObject(networkResp);
            }
        } catch (Exception ex) {
            String err = String.format("{\"result\":\"false\",\"error\":\"%s\"}", ex.getMessage());
            jsonObjectResp = parseJSONStringToJSONObject(err);
        }

        return jsonObjectResp;
    }
    private static JSONObject parseJSONStringToJSONObject(final String strr) {

        JSONObject response = null;
        try {
            response = new JSONObject(strr);
        } catch (Exception ex) {
//              Log.e("Could not parse malformed JSON: \"" + json + "\"");
            try {
                response = new JSONObject();
                response.put("result", "failed");
                response.put("data", strr);
                response.put("error", ex.getMessage());
            } catch (Exception exx) {
            }
        }
        return response;
    }

    JSONObject parseData(String query) throws JSONException {

        JSONObject jsonObject = new JSONObject(query);
        domain = jsonObject.getString("domain");
        id = jsonObject.getString("id");
        jsonObject.put("uid", FirebaseAuth.getInstance().getUid());
        return jsonObject;
    }

}
