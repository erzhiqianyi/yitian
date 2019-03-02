package io.github.buniaowanfeng.presententer;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.DailyInfo;
import io.github.buniaowanfeng.data.SingleDetail;
import io.github.buniaowanfeng.data.UsageAmount;
import io.github.buniaowanfeng.database.dao.DailyInfoDao;
import io.github.buniaowanfeng.database.dao.UsageAmountDao;
import io.github.buniaowanfeng.ui.activity.SingleAppDetailActivity;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.view.ISingleDetailActivity;
import io.github.buniaowanfeng.view.ISingleDetailPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

/**
 * Created by caofeng on 16-7-26.
 */
public class SingleDetailPresenter implements ISingleDetailPresenter {
    private static final String TAG = "singlerpresenter";
    private ISingleDetailActivity activity;

    public SingleDetailPresenter(ISingleDetailActivity activity) {
        this.activity = activity;
    }

    @Override
    public void getSingleDetail(String packageName) {
        activity.showRefresh();
        if (packageName.equals(SingleAppDetailActivity.WHOLE)){
            getDay();
        }else {
            getApp(packageName);
        }

    }

    private void getDay() {
        Observable.just("whole")
                .map(info -> DailyInfoDao.getDailyInfo())
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
                        getDay();
                    }

                    @Override
                    public void onNext(List<DailyInfo> dailyInfos) {
                        showHead(dailyInfos);
                        makeRecyclerList(dailyInfos);
                    }
                });
    }

    private void makeRecyclerList(List<DailyInfo> dailyInfos) {
        Observable.just(dailyInfos)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe();
    }

    private void showHead(List<DailyInfo> dailyInfos) {
        Observable.just(dailyInfos)
                .map(dailyInfo -> toHeadMessage(dailyInfo) )
                .doOnNext(headMessage -> headMessage.packageName = "whole")
                .doOnNext(headMessage -> headMessage.appName = "手机")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Subscriber<SingleDetail>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG,"daily head computation ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SingleDetail singleDetail) {
                        LogUtil.d(TAG,singleDetail.toString());
                        activity.setHeadMessage(singleDetail);
                        activity.hideRefresh();
                    }
                });
    }

    private SingleDetail toHeadMessage(List<DailyInfo> infos) {
        SingleDetail headMessage = new SingleDetail();
        long usdTime = 0 ;
        int numberOfTimes = 0 ;
        int numberOfWake = 0 ;
        int numberOfApps = 0 ;
        for ( DailyInfo info : infos){
            usdTime = usdTime + info.usedTime;
            numberOfTimes = numberOfTimes + info.numberOfTimes;
            numberOfApps = numberOfApps + info.numberOfApps;
            numberOfWake = numberOfWake + info.numberOfWake;
        }

        headMessage.totalTime = DateUtil.longToMessage(usdTime);
        headMessage.totalTimes = numberOfTimes + "次";
        headMessage.apps = numberOfApps+ "个";
        headMessage.wakes = numberOfWake+"次";
        headMessage.countDay = infos.size()+"天";
        return headMessage;
    }


    private void getApp(String packageName) {
        Observable.just(packageName)
                .map(name -> UsageAmountDao.getSingleAppUsage(packageName,null))
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
                        makeHead(usageAmounts);
                        makeDetail(usageAmounts);
                    }
                });
    }

    private void makeDetail(List<UsageAmount> usageAmounts) {
        Observable.just(usageAmounts)
                .map(usages -> toSingleData(usages) )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<List<SingleDetail>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG," calculate detail completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<SingleDetail> singleDetails) {
                        activity.setAdater(singleDetails);
                    }
                });

    }

    /**
     * make detail from usageA amounts
     * @param usages
     * @return
     */
    private List<SingleDetail> toSingleData(List<UsageAmount> usages) {
        List<SingleDetail> details = new ArrayList<>();
        long totalTime = 0;
        for (UsageAmount usage : usages){
            totalTime += usage.usedTime;
        }
        LogUtil.d(TAG," total time " + totalTime);
        for ( UsageAmount usage : usages){
            SingleDetail detail = new SingleDetail();
            detail.totalTime = DateUtil.longToString(usage.usedTime);
            detail.totalTimes = usage.numberOfTimes+"次";
            detail.day = DateUtil.intToString(usage.day);
            detail.dayInInt = usage.day;
            detail.packageName = usage.appPackage;
            LogUtil.d(TAG," used time " + usage.usedTime + " " + (int) (usage.usedTime * 1.0 / totalTime * 100));
            detail.progress = (int) (usage.usedTime * 1.0 / totalTime * 100);
            details.add(detail);
        }
        for (SingleDetail detail: details){
            LogUtil.d(TAG,detail.day + " "+detail.totalTime + " "
                    + detail.totalTimes + " " + detail.progress);
        }
        return details;
    }

    /**
     * make head from usage amount
     * @param usageAmounts
     */
    private void  makeHead(List<UsageAmount> usageAmounts){
        int size = usageAmounts.size();
        int day = DateUtil.day(System.currentTimeMillis());
        Observable.just(usageAmounts)
                .filter(usages -> usages.size() > 0)
                .zip(
                        Observable.from(usageAmounts).map(app -> app.numberOfTimes).reduce((x, y) -> (x + y)),
                        Observable.from(usageAmounts).map(app -> app.usedTime).reduce((x, y) -> (x + y)),
                        Observable.from(usageAmounts).filter(app -> app.day == day).map(app -> app.numberOfTimes).reduce((x, y) -> (x + y)),
                        Observable.from(usageAmounts).filter(app -> app.day == day).map(app -> app.usedTime).reduce((x, y) -> (x + y)),
                        new Func4<Integer, Long, Integer, Long, SingleDetail>() {
                            @Override
                            public SingleDetail call(Integer totalTimes, Long totalTime, Integer todayTimes, Long todayTime) {
                                SingleDetail count = new SingleDetail();
                                count.totalTime = "总时间\n"+ DateUtil.longToString(totalTime);
                                count.totalTimes = "总次数\n"+totalTimes+"次";
                                count.today = "今天\n"+ DateUtil.longToString(todayTime)+ "\n"+ todayTimes+"次";
                                return count;
                            }
                        }
                )
                .doOnNext(result -> result.countDay = "统计天数\n"+size+"天")
                .doOnNext(result -> result.packageName = usageAmounts.get(0).appPackage)
                .doOnNext(result -> result.appName = usageAmounts.get(0).appName)
                .doOnNext(result -> {
                    if (result.packageName.equals(YiTian.mLauncherPackage)){
                        result.appName = " launcher";
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<SingleDetail>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG,"make head completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SingleDetail detailHead) {
                        activity.setHeadMessage(detailHead);
                        activity.hideRefresh();
                    }
                });
    }

}
