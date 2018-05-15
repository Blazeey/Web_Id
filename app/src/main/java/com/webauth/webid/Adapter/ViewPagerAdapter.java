package com.webauth.webid.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.webauth.webid.Fragment.AccountFragment;
import com.webauth.webid.Fragment.PasswordFragment;
import com.webauth.webid.Fragment.ScannerFragment;

import java.util.List;

/**
 * Created by venki on 28/2/18.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Integer> resourceList;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm,List<Integer> resourceList) {
        super(fm);
        this.resourceList = resourceList;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        if(position==0){
            fragment = new AccountFragment();
        }
        if(position==1){
            fragment = new PasswordFragment();
        }
        if(position==2){
            fragment= new ScannerFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return resourceList.size();
    }
}

