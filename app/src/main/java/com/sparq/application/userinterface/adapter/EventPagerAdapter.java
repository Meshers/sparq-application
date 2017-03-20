package com.sparq.application.userinterface.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sparq.application.userinterface.fragment.EventDetailsFragment;
import com.sparq.application.userinterface.fragment.PollFragment;
import com.sparq.application.userinterface.fragment.QuizFragment;
import com.sparq.application.userinterface.fragment.ThreadFragment;

/**
 * Created by sarahcs on 2/21/2017.
 */

public class EventPagerAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;

    public EventPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new EventDetailsFragment();
                break;
            case 1:
                fragment = new QuizFragment();
                break;
            case 2:
                fragment = new PollFragment();
                break;
            case 3:
                fragment = new ThreadFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
