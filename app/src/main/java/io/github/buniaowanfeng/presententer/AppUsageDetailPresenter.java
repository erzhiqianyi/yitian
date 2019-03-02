package io.github.buniaowanfeng.presententer;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.AppDetailRecyclerData;
import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.data.AppMultiple;
import io.github.buniaowanfeng.data.DailyInfo;
import io.github.buniaowanfeng.data.HeadMessage;
import io.github.buniaowanfeng.data.Soul;
import io.github.buniaowanfeng.database.dao.AppUsageDao;
import io.github.buniaowanfeng.database.dao.DailyInfoDao;
import io.github.buniaowanfeng.util.Androids;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LastTimeCompare;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.view.IAppUsageDetailFragment;
import io.github.buniaowanfeng.view.IAppUsageDetailPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by caofeng on 16-7-22.
 */
public class AppUsageDetailPresenter implements IAppUsageDetailPresenter {
    private static final String TAG = " app usage presenter";
    private IAppUsageDetailFragment fragment;

    public AppUsageDetailPresenter(IAppUsageDetailFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void getAppUsageInfo(int day) {
        fragment.showRefresh();
        Observable.just(day)
                .doOnNext(requestDay -> LogUtil.d(TAG,"获取 " + requestDay + "数据"))
                .map(requestDay -> {
                    DailyInfo dailyInfo = DailyInfoDao.getDailyUseInfo(requestDay);

                    List<AppInfo> appInfos = AppUsageDao.getAppUsage(day);

                    return new AppDetailRecyclerData(dailyInfo,appInfos);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<AppDetailRecyclerData>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG,"load data from database completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getAppUsageInfo(day);
                    }

                    @Override
                    public void onNext(AppDetailRecyclerData appDetailRecyclerData) {
                        combineData(appDetailRecyclerData);
                    }
                });

    }

    private void combineData(AppDetailRecyclerData appDetailRecyclerData) {
        Observable.just(appDetailRecyclerData)
                .map(data -> {
                   return toRecyclerData(makeHeadMessage(data.dailyInfo), makeSoul(data.appInfos));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Subscriber<List<Soul<AppMessage>>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG," completed calculate");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Soul<AppMessage>> souls) {
                        fragment.hideRefresh();
                        fragment.setAdapterData(souls);
                    }
                });


    }

    public List<Soul<AppMessage>> toRecyclerData(HeadMessage headMessage,List<Soul<AppInfo>> appInfos){
        List<Soul<AppMessage>> message = new ArrayList<>();
        Soul<AppMessage> head = new Soul<>();
        head.type = AppInfo.TYPE_HEAD;
        head.headMessage = headMessage;
        message.add(0,head);
        for ( Soul<AppInfo> appInfo : appInfos){
            Soul<AppMessage> appMessage = new Soul<>();
            appMessage.startTime = appInfo.startTime;
            appMessage.type = appInfo.type;
            appMessage.appMultiple = appInfo.appMultiple;
            appMessage.data = toAppMessage(appInfo.data);
            message.add(appMessage);
        }
        return message;
    }

    private List<AppMessage> toAppMessage(List<AppInfo> appInfos) {
        List<AppMessage> appMessages = new ArrayList<>();
        for (AppInfo appInfo : appInfos){
            AppMessage appMessage = new AppMessage();
            appMessage.appName = appInfo.appName;
            appMessage.packageName = appInfo.appPackage;
            appMessage.startTime = DateUtil.time(appInfo.startTime);
            appMessage.startTimeInLong = appInfo.startTime;
            appMessage.usedTime = DateUtil.longToString(appInfo.usedTime);
            if (appInfo.appPackage.equals(AppInfo.APP_IDLE)){
                appMessage.type = AppInfo.TYPE_IDLE;
                appInfo.type = AppInfo.TYPE_IDLE;
            }else {
                appMessage.type = AppInfo.TYPE_APP;
                appInfo.type = AppInfo.TYPE_APP;
                appMessage.appIcon = Androids.getAppIcon(appInfo.appPackage);
                if (appInfo.appPackage.equals(YiTian.mLauncherPackage)){
                    appMessage.appName = "launcher";
                }
            }
            appMessage.height = calculate(appInfo);
            if(appInfo.usedTime >= 1000 ){
                appMessages.add(appMessage);
            }

        }
        return appMessages;
    }

    private int calculate(AppInfo appInfo) {
        int height = 0 ;

        switch (appInfo.type){
            case AppInfo.TYPE_IDLE:
                height =  calculate(appInfo.usedTime,AppInfo.IDLE_BASE_HEIGHT);
                break;
            case AppInfo.TYPE_APP:
                height = calculate(appInfo.usedTime,AppInfo.APP_BASE_HEIGHT);
                break;
        }
        return height;
    }

    private int calculate(long usedTime,int baseHeight) {
        double percent = usedTime * 1.0 / AppInfo.SECONDS_OF_A_DAY ;
        int height = (int) (baseHeight * ( 1 + percent));
        return height;
    }

    private List<Soul<AppInfo>> makeSoul(List<AppInfo> appInfos ) {
        List<Soul<AppInfo>> souls = new ArrayList<>();
        List<Soul<AppInfo>> idleSoul;
        List<Soul<AppInfo>> appSoul;
        List<AppInfo> idleUInfo = new ArrayList<>();
        int tag = 0 ;
        for ( AppInfo info : appInfos){
            info.tag = tag;
            if (info.appPackage.equals(AppInfo.APP_IDLE) && info.usedTime >= 1000){
                tag = tag + 1;
                info.tag = -1;
                idleUInfo.add(info);
            }
        }

        idleSoul = idleInfoToSoul(idleUInfo);

        appSoul = appInfoToSoul(appInfos,tag);

        souls.addAll(idleSoul);
        souls.addAll(appSoul);
        Collections.sort(souls,new LastTimeCompare());

        return souls;
    }

    private List<Soul<AppInfo>> appInfoToSoul(List<AppInfo> appInfos,int tag) {
        List<Soul<AppInfo>> app = new ArrayList<>();
        for ( int i= 0; i <= tag ; i++){
            Soul<AppInfo>  soul = new Soul<>() ;
            List<AppInfo> infos = new ArrayList<>();
            for ( AppInfo appInfo : appInfos){
                if ( appInfo.tag == i){
                    infos.add(appInfo);
                }
            }
            if (infos.size() >= 1){
                soul.data = infos;
                soul.type = AppInfo.TYPE_MULTIPLE;
                soul.startTimeInLong = infos.get(0).startTime;
                soul.startTime = DateUtil.time(soul.startTimeInLong);
                soul.appMultiple = toMultiple(soul.data);
                app.add(soul);
            }
        }

        return app;
    }

    private AppMultiple toMultiple(List<AppInfo> data) {
        AppMultiple appMultiple = new AppMultiple();
        appMultiple.startTime = DateUtil.time(data.get(0).startTime);
        long usedTime = data.get(data.size() - 1).endTime - data.get(0).startTime;

        Set<String> appPackage = new HashSet<>();;
        for (AppInfo appInfo : data){
            appPackage.add(appInfo.appPackage);
        }

        if (appPackage.size() == 1){
            appMultiple.appName = data.get(0).appName;
            appMultiple.packageName = data.get(0).appPackage;
        }
        appMultiple.day = data.get(0).day;
        appMultiple.usedTime = DateUtil.longToString(usedTime);
        ArrayList<Drawable> icon = new ArrayList<>();

        ArrayList<String> name = new ArrayList<>();
        for (String packageName : appPackage){
            icon.add(Androids.getAppIcon(packageName));
            name.add(packageName);
        }
        appMultiple.appIcon = icon;
        appMultiple.appPackage = name;

        return appMultiple;
    }


    private List<Soul<AppInfo>> idleInfoToSoul(List<AppInfo> idleUInfo) {
        List<Soul<AppInfo>> idle = new ArrayList<>();
        for ( AppInfo appInfo : idleUInfo ){
            Soul<AppInfo> soul = new Soul<>();
            soul.type = AppInfo.TYPE_IDLE;
            soul.data = Arrays.asList(appInfo);
            soul.startTimeInLong = appInfo.startTime;
            soul.startTime = DateUtil.time(soul.startTimeInLong);
            AppMultiple appMultiple = new AppMultiple();
            appMultiple.usedTime = DateUtil.time(appInfo.usedTime);

            idle.add(soul);
        }


        return idle;
    }

    /**
     * make headmessage from daily information
     * @param dailyInfo
     * @return
     */
    private HeadMessage makeHeadMessage(DailyInfo dailyInfo) {
        HeadMessage message = new HeadMessage();
        message.numberOfApps = "使用个数\n"+dailyInfo.numberOfApps+"个";
        message.numberOfTimes = "使用次数\n"+ dailyInfo.numberOfTimes+"次";
        message.numberOfWake = "解锁次数\n" + dailyInfo.numberOfWake + "次";
        message.day = DateUtil.intToString(dailyInfo.day);
        message.dayInInt = dailyInfo.day;
        message.goal = "目标\n"+ DateUtil.longToMessage(dailyInfo.dailyGoal);
        message.usedTime = DateUtil.longToMessage(dailyInfo.usedTime);
        message.usedTimeInLong= dailyInfo.usedTime;
        if (dailyInfo.usedTime >= dailyInfo.dailyGoal){
            message.progress = 100;
            message.isOverUse = true;
            message.remain = "已超出\n" + DateUtil.longToMessage(dailyInfo.usedTime - dailyInfo.dailyGoal);
        }else {
            message.progress = (int) (dailyInfo.usedTime * 1.0 / dailyInfo.dailyGoal * 100);
            message.isOverUse = false;
            message.remain = "还剩余\n" +  DateUtil.longToMessage(dailyInfo.usedTime - dailyInfo.dailyGoal);
        }
        return message;
    }
}
