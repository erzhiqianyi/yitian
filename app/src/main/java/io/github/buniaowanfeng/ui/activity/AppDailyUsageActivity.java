package io.github.buniaowanfeng.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.AppDailyHead;
import io.github.buniaowanfeng.database.dao.AllAppInfoDao;
import io.github.buniaowanfeng.presententer.AppDailyPresenter;
import io.github.buniaowanfeng.ui.adapter.AppDailyPagerAdapter;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.view.IAppDailyActivity;
import io.github.buniaowanfeng.view.IAppDailyPresenter;
import cn.qqtheme.framework.picker.OptionPicker;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AppDailyUsageActivity extends AppCompatActivity implements IAppDailyActivity, View.OnClickListener {

    private static final String PACKAGE = "package";
    private static final String DAY = "day";
    private static final String TAG = "appdailyactivity";
    private String packageName;

    private CardView mHead;
    private ImageView mImageIcon;
    private TextView mTvAppName;
    private ViewPager dailyInfoPager;
    private TextView mTvHint;
    private AppDailyPagerAdapter adaper;
    private IAppDailyPresenter presenter;
    private int day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_daily_usage);
        packageName = getIntent().getStringExtra(PACKAGE);
        day = getIntent().getIntExtra(DAY,0);
        initView();
        initData();
    }

    private void initData() {
        presenter.getHead(packageName);
    }

    private void initView() {
        mHead = (CardView) findViewById(R.id.app_daily_head);
        mImageIcon = (ImageView) findViewById(R.id.daily_app_icon);
        mTvAppName = (TextView) findViewById(R.id.daily_app_name);
        mTvHint = (TextView) findViewById(R.id.hint_empty);
        dailyInfoPager = (ViewPager) findViewById(R.id.app_daily_pager);
        dailyInfoPager.setOffscreenPageLimit(4);
        adaper = new AppDailyPagerAdapter(getSupportFragmentManager(),packageName);
        presenter = new AppDailyPresenter(this);
        mHead.setOnClickListener(this);
    }

    public static void startActivity(Context context, String packageName,int day){
        Intent intent = new Intent(context,AppDailyUsageActivity.class);
        intent.putExtra(PACKAGE,packageName);
        intent.putExtra(DAY,day);
        context.startActivity(intent);
    }


    @Override
    public void setHead(AppDailyHead head) {
        LogUtil.d(TAG," head " + head.toString());
        if (head.packageName.equals(TrendActivity.WHOLE)){
            Glide.with(this).load(R.drawable.ic_phone).into(mImageIcon);
        }else {
            Glide.with(this)
                    .load(new File(YiTian.mIconPath, head.packageName + ".png"))
                    .into(mImageIcon);
        }
        if (head.packageName.equals(YiTian.mLauncherPackage)){
            mTvAppName.setText("launcher");
        }else {
            mTvAppName.setText(head.appName);
        }

    }

    @Override
    public void setAdapter(List<Integer> days) {
        if (days.size() == 0){
            LogUtil.d(TAG," size " +"还没有使用过这个应用哦");
            mTvHint.setVisibility(View.VISIBLE);
            dailyInfoPager.setVisibility(View.GONE);
        }else {
            mTvHint.setVisibility(View.GONE);
            dailyInfoPager.setVisibility(View.VISIBLE);
            adaper.setDays(days);
            int position = 0 ;
            for(int i =0 ; i < days.size() ; i++){
                if (day == days.get(i)) {
                    position = i;
                    break;
                }
            }
            if (position == 0){
                position = days.size()-1;
            }
            LogUtil.d(TAG," day " + day + position);
            dailyInfoPager.setAdapter(adaper);
            dailyInfoPager.setCurrentItem(position);
        }

    }

    @Override
    public void onClick(View view) {
        getPickerData();
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
        head.packageName = TrendActivity.WHOLE;
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
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int position, String option) {
                LogUtil.d(TAG," select " + option + heads.get(position).packageName);
                packageName  =  heads.get(position).packageName;
                day = DateUtil.day(System.currentTimeMillis());
                dailyInfoPager = null;
                initView();
                presenter.getHead(packageName);
            }
        });
        picker.show();

    }

}
