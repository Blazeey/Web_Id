package com.webauth.webid.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.webauth.webid.Models.Auth;
import com.webauth.webid.R;

import java.util.List;

/**
 * Created by venki on 28/2/18.
 */

public class DomainAdapter extends RecyclerView.Adapter<DomainAdapter.ViewHolder> {

    private Context context;
    private List<Auth> authList;

    public DomainAdapter(Context context, List<Auth> authList) {
        this.context = context;
        this.authList= authList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.domain_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Auth auth = authList.get(position);
        holder.domain.setImageResource(R.drawable.ic_facebook_app_symbol);
        holder.username.setText(auth.getUsername());
        holder.password.setText(auth.getPassword());
    }

    @Override
    public int getItemCount() {
        return authList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView domain;
        TextView username,password;

        public ViewHolder(View itemView) {
            super(itemView);
            domain = itemView.findViewById(R.id.domain);
            username = itemView.findViewById(R.id.username);
            password = itemView.findViewById(R.id.password);
        }
    }
}

