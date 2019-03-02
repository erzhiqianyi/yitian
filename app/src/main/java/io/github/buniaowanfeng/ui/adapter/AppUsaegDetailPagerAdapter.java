package io.github.buniaowanfeng.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.ui.fragment.AppUsageDetailFragment;

/**
 * Created by caofeng on 16-7-21.
 */
public class AppUsaegDetailPagerAdapter extends FragmentStatePagerAdapter {
    private List<Integer> days = new ArrayList<>();
    public AppUsaegDetailPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        AppUsageDetailFragment fragment=
                AppUsageDetailFragment.newInstance(days.get(position));
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
