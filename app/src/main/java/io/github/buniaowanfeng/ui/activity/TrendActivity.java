package io.github.buniaowanfeng.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.AppDailyHead;
import io.github.buniaowanfeng.data.TrendData;
import io.github.buniaowanfeng.database.dao.AllAppInfoDao;
import io.github.buniaowanfeng.presententer.TrendPresenter;
import io.github.buniaowanfeng.ui.adapter.TrendAdapter;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.view.ITrendActivity;
import io.github.buniaowanfeng.view.ITrendPresenter;
import cn.qqtheme.framework.picker.OptionPicker;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TrendActivity extends AppCompatActivity implements ITrendActivity, View.OnClickListener {

    public static final String WHOLE = "whole";
    private static final String PACKAGE = "package";
    private static final String TAG = "trendacitivty";

    private String packageName;

    private RelativeLayout mHead;
    private ImageView mImageIcon;
    private TextView mTvName;

    private TextView mTvCountDay;
    private TextView mTvTotalTime;
    private TextView mTvTotalTimes;
    private LinearLayout mLlWakes;
    private TextView mTvWakes;
    private TextView mTvHint;
    private RecyclerView trendRecycler;
    private TrendAdapter adapter;

    private ITrendPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);
        packageName = getIntent().getStringExtra(PACKAGE);

        initView();
        initData();
    }

    private void initData() {
        presenter.getTrend(packageName,null);
    }

    private void initView() {
        mHead = (RelativeLayout) findViewById(R.id.trend_head);
        mImageIcon = (ImageView) findViewById(R.id.trend_app_icon);
        mTvName = (TextView) findViewById(R.id.trend_app_name);
        mTvCountDay = (TextView) findViewById(R.id.tv_trend_count_day);
        mTvTotalTime  = (TextView) findViewById(R.id.tv_trend_used_time);
        mTvTotalTimes = (TextView) findViewById(R.id.tv_number_of_times);
        mLlWakes = (LinearLayout) findViewById(R.id.ll_wakes);
        mTvWakes = (TextView) findViewById(R.id.tv_trend_number_of_wakes);
        mTvHint = (TextView) findViewById(R.id.trend_empty);
        trendRecycler = (RecyclerView) findViewById(R.id.trend_recycler);
        trendRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrendAdapter();
        presenter = new TrendPresenter(this);
        mHead.setOnClickListener(this);
    }

    public static void startActivity(Context context, String packageName){
        Intent intent = new Intent(context,TrendActivity.class);
        intent.putExtra(PACKAGE,packageName);
        context.startActivity(intent);
    }

    @Override
    public void setHead(TrendData data) {
        LogUtil.d(TAG,data.toString());
        if (data.packageName.equals(WHOLE)){
            mLlWakes.setVisibility(View.VISIBLE);
            mTvWakes.setText(data.wakeTimes);
            Glide.with(this).load(R.drawable.ic_phone).into(mImageIcon);
        }else {
            mLlWakes.setVisibility(View.GONE);
            Glide.with(this).load(new File(YiTian.mIconPath,
                    data.packageName+".png")).into(mImageIcon);
        }

        mTvName.setText(data.appName);
        mTvCountDay.setText(data.countDay);
        mTvTotalTime.setText(data.usedTime);
        mTvTotalTimes.setText(data.usedTimes);
    }

    @Override
    public void setDetail(List<TrendData> details) {
        mTvHint.setVisibility(View.GONE);
        trendRecycler.setVisibility(View.VISIBLE);
        adapter.setDatas(details);
        trendRecycler.setAdapter(adapter);
    }

    @Override
    public void showEmpty() {
        mTvHint.setVisibility(View.VISIBLE);
        trendRecycler.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.trend_head:
                getPickerData();
                break;
        }
    }

    private void getPickerData(){
        Observable.just("get app")
                .map(info -> AllAppInfoDao.getAllAppName())
                .doOnNext(apps -> filter(apps))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<AppDailyHead>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG," get all apps completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getPickerData();
                    }

                    @Override
                    public void onNext(List<AppDailyHead> appDailyHeads) {
                        showAppPicker(appDailyHeads);
                    }
                });
    }

    private List<AppDailyHead> filter(List<AppDailyHead> apps) {
        List<AppDailyHead> heads = apps;
        for ( int i = 0 ; i < apps.size() ;i++){
            if (apps.get(i).appName.startsWith("com")){
                heads.remove(i);
            }
        }
        AppDailyHead head = new AppDailyHead();
        head.appName = getString(R.string.shouji);
        head.packageName = WHOLE;
        heads.add(0,head);
        return heads;
    }

    private void showAppPicker(List<AppDailyHead> heads) {
        ArrayList<String> names = new ArrayList<>();
        for ( AppDailyHead appDailyHead : heads){
            names.add(appDailyHead.appName);
        }

        OptionPicker picker = new OptionPicker(this,names);
        picker.setOffset(4);
        picker.setSelectedIndex(4);
        picker.setTextSize(16);
        picker.setOnOptionPickListener((position, option) -> presenter.getTrend(heads.get(position).packageName,option));
        picker.show();

    }
}
