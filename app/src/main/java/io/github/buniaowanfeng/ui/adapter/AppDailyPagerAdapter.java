package io.github.buniaowanfeng.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.ui.fragment.AppDailyFragment;

/**
 * Created by caofeng on 16-7-27.
 */
public class AppDailyPagerAdapter extends FragmentStatePagerAdapter{
    List<Integer> days;
    private String packageName;
    public AppDailyPagerAdapter(FragmentManager fm,String packageName) {
        super(fm);
        days = new ArrayList<>();
        this.packageName = packageName;
    }

    @Override
    public Fragment getItem(int position) {
        return AppDailyFragment.newInstance(days.get(position),packageName);
    }

    @Override
    public int getCount() {
        return days.size();
    }

    public void setDays(List<Integer> days) {
        this.days = days;
        notifyDataSetChanged();
    }
}
