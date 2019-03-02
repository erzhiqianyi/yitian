package io.github.buniaowanfeng.ui.fragment;

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

import java.util.Collections;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.data.Base;
import io.github.buniaowanfeng.data.UsageAmount;
import io.github.buniaowanfeng.event.ClickEvent;
import io.github.buniaowanfeng.presententer.AppCountPresenter;
import io.github.buniaowanfeng.ui.adapter.AppCountRecyclerAdapter;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.TimeCompare;
import io.github.buniaowanfeng.util.TimesCompare;
import io.github.buniaowanfeng.view.IAppCountFragment;
import io.github.buniaowanfeng.view.IAppUsageDetailPresenter;

/**
 * Created by caofeng on 16-7-25.
 */
public class AppCountFragment extends Fragment implements IAppCountFragment {
    private static final String ARGS_DAY = "day";
    private static final String TAG = "fragmentcount";
    private int day;

    private ProgressBar progressBar;
    private RecyclerView appCountRecycler;
    private AppCountRecyclerAdapter adapter;
    private IAppUsageDetailPresenter presenter;

    public static AppCountFragment newInstance(int day){
        AppCountFragment fragment = new AppCountFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_DAY,day);
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_app_count, container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = (ProgressBar) view.findViewById(R.id.count_progress_bar);
        appCountRecycler = (RecyclerView) view.findViewById(R.id.count_recycler);
        appCountRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AppCountRecyclerAdapter();
        presenter = new AppCountPresenter(this);
        getData();
    }

    private void getData() {
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
    public void setAdapterData(List<Base<String, UsageAmount>> data) {
        adapter.setData(data);
        appCountRecycler.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(ClickEvent event){
        LogUtil.d(TAG,"type " + event.type);
        LogUtil.d(TAG,"size"+adapter.getData().size());
        String head = adapter.getData().get(0).head;
        adapter.getData().remove(0);
        for ( Base<String,UsageAmount> base :adapter.getData()){
            switch (event.type){
                case ClickEvent.TYPE_TIME:
                    if(base.type == AppCountRecyclerAdapter.TYPE_TIMES) {
                        base.type = ClickEvent.TYPE_TIME;
                    }
                    break;
                case ClickEvent.TYPE_TIMES:
                    if(base.type == AppCountRecyclerAdapter.TYPE_TIME) {
                        base.type = ClickEvent.TYPE_TIMES;
                    }
                    break;
            }
        }

        switch (event.type){
            case ClickEvent.TYPE_TIME:
                Collections.sort(adapter.getData(),new TimeCompare());
                break;
            case ClickEvent.TYPE_TIMES:
                Collections.sort(adapter.getData(),new TimesCompare());
                break;

        }
        Base<String,UsageAmount> headBase = new Base<>();
        headBase.head = head;
        headBase.type = AppCountRecyclerAdapter.TYPE_HEAD;
        adapter.getData().add(0,headBase);
        adapter.notifyDataSetChanged();
    }
}
