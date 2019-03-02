package io.github.buniaowanfeng.presententer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.data.DailyInfo;
import io.github.buniaowanfeng.data.ScreenRecord;
import io.github.buniaowanfeng.data.UsageAmount;
import io.github.buniaowanfeng.database.dao.AppUsageDao;
import io.github.buniaowanfeng.database.dao.DailyInfoDao;
import io.github.buniaowanfeng.database.dao.ScreenRecordDao;
import io.github.buniaowanfeng.database.dao.UsageAmountDao;
import io.github.buniaowanfeng.ui.activity.TrendActivity;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.view.IAppDailyDetailPresenter;
import io.github.buniaowanfeng.view.IAppDailyFragment;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by caofeng on 16-7-27.
 */
public class AppDailyDetailPresenter implements IAppDailyDetailPresenter {

    private static final String TAG = "appdailypresenter";
    private IAppDailyFragment fragment;

    public AppDailyDetailPresenter(IAppDailyFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void getDailyHead(int day, String packageName) {
        if (packageName.equals(TrendActivity.WHOLE)){
            getHeadFromDailyInfo(day);
        }else {
            getHeadFromUsageAmount(day,packageName);
        }

    }

    private void getHeadFromUsageAmount(int day, String packageName) {
        Observable.zip(Observable.just(day), Observable.just(packageName),
                (day1, packageName1) ->
                        UsageAmountDao.getUsageAmount(day1, packageName1))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<UsageAmount>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG," get " + day + packageName + " sucess");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(UsageAmount usageAmount) {
                        fragment.setHead(usageAmount);
                    }
                });
    }

    private void getHeadFromDailyInfo(int day) {
        Observable.just(day)
                .map(requestDay -> DailyInfoDao.getDailyUseInfo(requestDay))
                .map(dailyInfo -> toUsageAmountHead(dailyInfo))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<UsageAmount>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getHeadFromDailyInfo(day);
                    }

                    @Override
                    public void onNext(UsageAmount usageAmount) {
                        fragment.setHead(usageAmount);
                    }
                });
    }

    private UsageAmount toUsageAmountHead(DailyInfo dailyInfo) {
        UsageAmount usageAmount  = new UsageAmount();
        usageAmount.usedTimes = DateUtil.longToDayInfo(dailyInfo.usedTime);
        usageAmount.numberOfTimes = dailyInfo.numberOfTimes;
        long average = (long) (dailyInfo.usedTime * 1.0 / dailyInfo.numberOfTimes);
        LogUtil.d(TAG,"average" +average + DateUtil.longToDayInfo(average));
        usageAmount.average = DateUtil.longToDayInfo(average);
        return usageAmount;
    }

    @Override
    public void getAppUsage(int day, String packageName) {
        if (packageName.equals(TrendActivity.WHOLE)){
            getDetailFromScreen(day);
        }else{
            getDetailFromAppUsage(day,packageName);
        }

    }

    private void getDetailFromAppUsage(int day, String packageName) {
        Observable.zip(Observable.just(day), Observable.just(packageName),
                (Func2<Integer, String, List<AppInfo>>) (day1, packageName1)
                        -> AppUsageDao.getAppInfo(day1, packageName1))
                .map(appInfos -> toAppMessage(appInfos))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<List<AppMessage>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getAppUsage(day,packageName);
                    }

                    @Override
                    public void onNext(List<AppMessage> appMessages) {
                        fragment.setAdapter(appMessages);
                    }
                });
    }

    private void getDetailFromScreen(int day) {
        Observable.just(day)
                .map(requestDay -> ScreenRecordDao.getScreen(day))
                .map(screenRecords -> screenToAppInfo(screenRecords))
                .map(infos -> toAppMessage(infos))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<AppMessage>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getAppUsage(day,TrendActivity.WHOLE);
                    }

                    @Override
                    public void onNext(List<AppMessage> appMessages) {
                        fragment.setAdapter(appMessages);
                    }
                });
    }

    private List<AppInfo> screenToAppInfo(List<ScreenRecord> screenRecords) {
        List<AppInfo> infos = new ArrayList<>();
        if (screenRecords.size() == 0) {
            return infos;
        }

        Date date = new Date();
        date.setTime(screenRecords.get(0).startTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        long dayStartTime = calendar.getTimeInMillis();
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE,day + 1);
        long dayEndStartTime = calendar.getTimeInMillis() - 1000;


        if (screenRecords.get(0).type.equals(ScreenRecord.SCREEN_OFF)){
            ScreenRecord record = new ScreenRecord();

            record.startTime = AppUsageDao.getStartTime(DateUtil.day(screenRecords.get(0).startTime));
            record.type = ScreenRecord.SCREEN_ON;
            record.day = day;
            screenRecords.add(0,record);
        }

        if (screenRecords.get(screenRecords.size()-1).type.equals(ScreenRecord.SCREEN_ON)){
            ScreenRecord record = new ScreenRecord();
            if (System.currentTimeMillis() > dayEndStartTime){
                record.startTime  = dayStartTime;
            }else {
                record.startTime = System.currentTimeMillis();
            }
            record.type = ScreenRecord.SCREEN_OFF;
            record.day = day;
            screenRecords.add(screenRecords.size(),record);
        }

        for (ScreenRecord screenRecord : screenRecords){
            LogUtil.d(TAG,screenRecord.toString());
        }

        for ( int i = 0 ; i < screenRecords.size() -1; i = i + 2){
            AppInfo appInfo = new AppInfo();
            long startTime = screenRecords.get(i).startTime;
            long endTime = screenRecords.get(i+1).startTime;
            appInfo.startTime = startTime;
            appInfo.endTime = endTime;
            appInfo.usedTime = endTime - startTime;
            appInfo.appName = TrendActivity.WHOLE;
            appInfo.appPackage = TrendActivity.WHOLE;
            infos.add(appInfo);
        }
        return infos;
    }

    private List<AppMessage> toAppMessage(List<AppInfo> appInfos){
        List<AppMessage> messages = new ArrayList<>();
        long usedTime = 0;

        for ( AppInfo appInfo : appInfos){
            usedTime = usedTime + appInfo.usedTime;
        }

        long currentUsedTime = 0;
        for (AppInfo appInfo : appInfos){
            AppMessage appMessage = new AppMessage();
            appMessage.startTime = DateUtil.time(appInfo.startTime);
            appMessage.endTime = DateUtil.time(appInfo.endTime);
            appMessage.usedTime = DateUtil.longToDayInfo(appInfo.usedTime);
            appMessage.progress = (float) (appInfo.usedTime * 1.0 / usedTime * 360);
            currentUsedTime = appInfo.usedTime + currentUsedTime;
            appMessage.startSweap = (float) (currentUsedTime * 1.0f / usedTime * 360);
            if (appInfo.usedTime >= 1000){
                messages.add(appMessage);
            }
        }

        float filter = 0;
        if (messages.size() >=1){
            filter = -90 - messages.get(0).startSweap;
        }

        for (AppMessage appMessage : messages){
            appMessage.startSweap = appMessage.startSweap + filter;
        }
        return messages;
    }
}
