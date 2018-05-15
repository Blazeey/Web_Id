package com.webauth.webid.Activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.webauth.webid.Adapter.ViewPagerAdapter;
import com.webauth.webid.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import devlight.io.library.ntb.NavigationTabBar;

/**
 * Created by venki on 28/2/18.
 */

public class NavigationTabLayout extends AppCompatActivity {

    ArrayList<NavigationTabBar.Model> model = new ArrayList<>();

    List<Integer> resourceList;
    ViewPagerAdapter viewPagerAdapter;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.navbar)
    NavigationTabBar navigationTabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);

        navigationTabBar = findViewById(R.id.navbar);
        Log.v("navbar",navigationTabBar.getBgColor()+"");
        viewPager = findViewById(R.id.view_pager);
        resourceList = new ArrayList<>();

        model.add(new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_account_box), Color.CYAN)
                .title("User Details")
                .badgeTitle("Account Details")
                .build());

        model.add(new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_lock_outline_black), Color.YELLOW)
                .title("Passwords")
                .badgeTitle("Passwords")
                .build());

        model.add(new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.ic_search_black), Color.GREEN)
                .title("Scan")
                .badgeTitle("Scan QR")
                .build());


        resourceList.add(R.layout.fragment_account);
        resourceList.add(R.layout.fragment_password);
        resourceList.add(R.layout.fragment_scan);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), resourceList);
        viewPager.setAdapter(viewPagerAdapter);

        navigationTabBar.setModels(model);
        navigationTabBar.setViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FullScreencall();
    }
    public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}

