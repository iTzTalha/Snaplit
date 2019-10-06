package com.vampyr.demo.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vampyr.demo.Fragments.MyFavoriteFragment;
import com.vampyr.demo.Fragments.MyPostsFragment;
import com.vampyr.demo.Fragments.MyProfileCommentsFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public PageAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MyPostsFragment();
            case 1:
                return new MyProfileCommentsFragment();
            case 2:
                return new MyFavoriteFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
