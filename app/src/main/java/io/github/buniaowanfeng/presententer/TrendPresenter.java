package io.github.buniaowanfeng.presententer;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.DailyInfo;
import io.github.buniaowanfeng.data.TrendData;
import io.github.buniaowanfeng.data.UsageAmount;
import io.github.buniaowanfeng.database.dao.DailyInfoDao;
import io.github.buniaowanfeng.database.dao.UsageAmountDao;
import io.github.buniaowanfeng.ui.activity.TrendActivity;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.view.ITrendActivity;
import io.github.buniaowanfeng.view.ITrendPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by caofeng on 16-7-28.
 */
public class TrendPresenter implements ITrendPresenter {
    private static final String TAG = "trendpresenter";
    private ITrendActivity activity;

    public TrendPresenter(ITrendActivity activity) {
        this.activity = activity;
    }

    @Override
    public void getTrend(String packageName,String name) {
        if (packageName.equals(TrendActivity.WHOLE)){
            getTrendFromDailyInfo(packageName);
        }else {
            getTrendFromUsageAmount(packageName,name);
        }
    }


    private void getTrendFromDailyInfo(String packageName) {
        Observable.just("whole")
                .map(info -> DailyInfoDao.getDailyInfo())
                .filter(infos -> infos.size() > 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<DailyInfo>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG," get daily usage competed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<DailyInfo> dailyInfos) {
                        showHead(toTrendHead(dailyInfos));
                        Observable.just(dailyInfos)
                                .map(infos -> toTrendDetail(infos))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Subscriber<List<TrendData>>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onNext(List<TrendData> trendDatas) {
                                        showDetail(trendDatas);
                                    }
                                });
                    }
                });
    }

    private List<TrendData> toTrendDetail(List<DailyInfo> dailyInfos) {
        List<TrendData> details = new ArrayList<>();

        long usedTime = 0;
        for (DailyInfo dailyInfo : dailyInfos){
            usedTime = dailyInfo.usedTime + usedTime;
        }

        for (DailyInfo info : dailyInfos){
            TrendData data = new TrendData();
            data.appName = TrendActivity.WHOLE;
            data.packageName = TrendActivity.WHOLE;
            data.usedTime = DateUtil.longToDayInfo(info.usedTime);
            data.usedTimes = DateUtil.intToDayInfo(info.numberOfTimes);
            data.wakeTimes = DateUtil.intToDayInfo(info.numberOfWake);
            data.day = info.day;
            data.type = TrendData.TYPE_WHOLE;
            data.progress = (int) (info.usedTime * 1.0 / usedTime * 100);
            details.add(data);
        }
        return details;
    }

    private void showHead(TrendData head) {
        activity.setHead(head);
    }

    private void  showDetail(List<TrendData> details){
        LogUtil.d(TAG," detail " + details.get(0).day);
        for ( TrendData detail : details){
            LogUtil.d(TAG," detail " + detail.toString());
        }
        if (details.get(0).day != 0){
            activity.setDetail(details);
        }else {
            activity.showEmpty();
        }
    }

    private TrendData toTrendHead(List<DailyInfo> dailyInfos) {
        TrendData data = new TrendData();
        data.packageName = TrendActivity.WHOLE;
        data.appName = YiTian.mContext.getString(R.string.phone);
        data.countDay = dailyInfos.size() + YiTian.mContext.getString(R.string.day);
        long usedTime = 0;
        int usedTimes = 0 ;
        int wakeTimes = 0;
        for (DailyInfo dailyInfo : dailyInfos){
            usedTime = dailyInfo.usedTime + usedTime;
            usedTimes = dailyInfo.numberOfTimes + usedTimes;
            wakeTimes = dailyInfo.numberOfWake + wakeTimes;
        }
        data.usedTime = DateUtil.longToDayInfo(usedTime);
        data.usedTimes = DateUtil.intToDayInfo(usedTimes);
        data.wakeTimes = DateUtil.intToDayInfo(wakeTimes);
        return data;
    }

    private void getTrendFromUsageAmount(String packageName,String appName) {
        Observable.just(packageName)
                .map(name -> UsageAmountDao.getSingleAppUsage(packageName,appName))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<UsageAmount>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG,"load data completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<UsageAmount> usageAmounts) {
                        showHead(toTrendHeadFromUsage(usageAmounts));
                        showDetail(toTrendDetailFromUsage(usageAmounts));
                    }
                });
    }

    private List<TrendData> toTrendDetailFromUsage(List<UsageAmount> usageAmounts) {
        List<TrendData> details = new ArrayList<>();
        long usedTime = 0 ;
        for (UsageAmount usageAmount : usageAmounts){
            usedTime = usedTime + usageAmount.usedTime;
        }

        for (UsageAmount info : usageAmounts){
            TrendData data = new TrendData();
            data.appName = info.appName;
            data.packageName = info.appPackage;
            data.usedTime = DateUtil.longToDayInfo(info.usedTime);
            data.usedTimes = DateUtil.intToDayInfo(info.numberOfTimes);
            data.day = info.day;
            data.type = TrendData.TYPE_APP;
            data.progress = (int) (info.usedTime * 1.0 / usedTime * 100);
            details.add(data);
        }
        return details;
    }

    private TrendData toTrendHeadFromUsage(List<UsageAmount> usageAmounts) {
        TrendData data = new TrendData();
        if (usageAmounts.get(0).appPackage.equals(YiTian.mLauncherPackage)){
            data.appName = "launcher";
        }else {
            data.appName = usageAmounts.get(0).appName;
        }
        data.packageName = usageAmounts.get(0).appPackage;

        long usedTime = 0;
        int usedTimes = 0 ;
        for (UsageAmount dailyInfo : usageAmounts){
            usedTime = dailyInfo.usedTime + usedTime;
            usedTimes = dailyInfo.numberOfTimes + usedTimes;
        }
        if (usedTimes != 0){
            data.countDay = usageAmounts.size() + YiTian.mContext.getString(R.string.day);
        }else {
            data.countDay = 0 + YiTian.mContext.getString(R.string.day);

        }

        data.usedTime = DateUtil.longToDayInfo(usedTime);
        data.usedTimes = DateUtil.intToDayInfo(usedTimes);
        return  data;
    }
}
