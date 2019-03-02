package io.github.buniaowanfeng.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import io.github.buniaowanfeng.ui.fragment.AppCountFragment;

/**
 * Created by caofeng on 16-7-25.
 */
public class AppCountPagerAdapter extends FragmentStatePagerAdapter {
    private List<Integer> days;

    public AppCountPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        AppCountFragment fragment = AppCountFragment.newInstance(days.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return days.size();
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }
}
