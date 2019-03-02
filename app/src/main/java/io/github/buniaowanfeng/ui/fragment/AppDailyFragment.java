package io.github.buniaowanfeng.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.data.UsageAmount;
import io.github.buniaowanfeng.presententer.AppDailyDetailPresenter;
import io.github.buniaowanfeng.ui.adapter.AppDailyRecyclerAdapter;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.view.IAppDailyDetailPresenter;
import io.github.buniaowanfeng.view.IAppDailyFragment;

/**
 * Created by caofeng on 16-7-27.
 */
public class AppDailyFragment extends Fragment implements IAppDailyFragment {
    private static final String ARGS_DAY = "day";
    private static final String ARGS_PACKAGE = "package";
    private static final String TAG = "fragmentappdaily";
    private int day;
    private String packageName;

    private TextView mTvDailyInfo;
    private TextView mTvUsedTime;
    private TextView mTvNumberOfTime;
    private TextView mTvAverage;
    private RecyclerView appDailyRecycler;
    private AppDailyRecyclerAdapter adapter;
    private IAppDailyDetailPresenter presenter;
    public static AppDailyFragment newInstance(int day,String pacakgeName){
        AppDailyFragment fragment = new AppDailyFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_DAY,day);
        args.putString(ARGS_PACKAGE,pacakgeName);
        fragment.setArguments(args);
        LogUtil.d(TAG," day  " + day );
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = getArguments().getInt(ARGS_DAY);
        packageName = getArguments().getString(ARGS_PACKAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_daily,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvDailyInfo  = (TextView) view.findViewById(R.id.app_daily_fragment_day);
        mTvUsedTime = (TextView) view.findViewById(R.id.app_daily_fragment_used_time);
        mTvNumberOfTime = (TextView) view.findViewById(R.id.app_daily_fragment_number_of_times);
        mTvAverage = (TextView) view.findViewById(R.id.app_daily_fragment_average);
        appDailyRecycler = (RecyclerView) view.findViewById(R.id.app_daily_recycler);
        appDailyRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AppDailyRecyclerAdapter();

        presenter = new AppDailyDetailPresenter(this);

        initData();
    }

    private void initData() {
        presenter.getDailyHead(day,packageName);
    }

    @Override
    public void setHead(UsageAmount usageAmount) {
        mTvDailyInfo.setText(DateUtil.intToString(day));
        mTvUsedTime.setText("时间:"+usageAmount.usedTimes);
        mTvNumberOfTime.setText("次数:"+ DateUtil.intToDayInfo(usageAmount.numberOfTimes)+"次");
        mTvAverage.setText("平均："+usageAmount.average+"/次");
        presenter.getAppUsage(day,packageName);
    }

    @Override
    public void setAdapter(List<AppMessage> messages) {
        adapter.setMessages(messages);
        appDailyRecycler.setAdapter(adapter);
    }

}
