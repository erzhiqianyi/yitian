package io.github.buniaowanfeng.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.data.Soul;
import io.github.buniaowanfeng.event.ClickEvent;
import io.github.buniaowanfeng.presententer.AppUsageDetailPresenter;
import io.github.buniaowanfeng.ui.adapter.AppUsageDetailRecyclerAdapter;
import io.github.buniaowanfeng.util.ImageUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.ShareUtil;
import io.github.buniaowanfeng.view.IAppUsageDetailFragment;
import io.github.buniaowanfeng.view.IAppUsageDetailPresenter;

/**
 * Created by caofeng on 16-7-21.
 */
public class AppUsageDetailFragment extends Fragment implements IAppUsageDetailFragment{
    private static final String ARGS_DAY = "day";
    private static final String TAG = "fragmentusage";
    private int day;

    private ProgressBar progressBar;
    private RecyclerView appUsageRecycler;
    private IAppUsageDetailPresenter presenter;
    private AppUsageDetailRecyclerAdapter adapter;
    public static AppUsageDetailFragment newInstance(int day){
        AppUsageDetailFragment fragment = new AppUsageDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_DAY,day);
        fragment.setArguments(args);
        fragment.getTag();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = getArguments().getInt(ARGS_DAY);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_usage_detail, container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = (ProgressBar) view.findViewById(R.id.app_usage_progress_bar);
        presenter = new AppUsageDetailPresenter(this);
        appUsageRecycler = (RecyclerView) view.findViewById(R.id.app_usage_detail);
        appUsageRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AppUsageDetailRecyclerAdapter(this);
        getData();
    }

    public void getData() {
        presenter.getAppUsageInfo(day);
    }


    @Override
    public void showRefresh() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRefresh() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setAdapterData(List<Soul<AppMessage>> data) {
        LogUtil.i(TAG,data.get(0).headMessage.toString());
        adapter.setSouls(data);
        appUsageRecycler.setAdapter(adapter);
    }

    public void scrollTo(int position){
        appUsageRecycler.smoothScrollToPosition(position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(ClickEvent event){
        if(event.type == ClickEvent.TYPE_SHARE_IN_MAIN){
            Bitmap bitmap = ShareUtil.getScreenshotFromRecyclerView(appUsageRecycler);
            ImageUtil.saveShare(bitmap, String.valueOf(System.currentTimeMillis()));
        }

    }
}
