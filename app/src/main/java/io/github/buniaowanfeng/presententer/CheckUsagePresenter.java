package io.github.buniaowanfeng.presententer;

import android.text.TextUtils;

import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.data.CheckResult;
import io.github.buniaowanfeng.data.DailyInfo;
import io.github.buniaowanfeng.data.ScreenRecord;
import io.github.buniaowanfeng.data.UsageAmount;
import io.github.buniaowanfeng.database.dao.AllAppInfoDao;
import io.github.buniaowanfeng.database.dao.AppUsageDao;
import io.github.buniaowanfeng.database.dao.DailyInfoDao;
import io.github.buniaowanfeng.database.dao.ScreenRecordDao;
import io.github.buniaowanfeng.database.dao.TodoDao;
import io.github.buniaowanfeng.database.dao.UsageAmountDao;
import io.github.buniaowanfeng.database.model.UsageCheck;
import io.github.buniaowanfeng.util.Androids;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.ImageUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.SharePreferenceUtil;
import io.github.buniaowanfeng.view.ICheckUsagePresenter;
import io.github.buniaowanfeng.view.INotification;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by caofeng on 16-7-9.
 */
public class CheckUsagePresenter implements ICheckUsagePresenter {
    public static final String TAG = "check";
    private INotification notification;

    public CheckUsagePresenter(INotification notification) {
        this.notification = notification;
    }

    @Override
    public void screenOn() {
        long curTime = System.currentTimeMillis();
        saveScreenRecord(curTime,ScreenRecord.SCREEN_ON);
        CheckResult checkResult = new CheckResult();
        checkResult.isNull = false;
        checkResult.curTime = curTime;
        checkResult.prePackage = AppInfo.APP_IDLE;
        save(checkResult);
    }

    @Override
    public void screenOff() {
        long curTime = System.currentTimeMillis();
        saveScreenRecord(curTime,ScreenRecord.SCREEN_OFF);
        String curPackage = UsageCheck.getCurrentUsedPackage(System.currentTimeMillis());
        CheckResult checkResult = new CheckResult();
        checkResult.curTime = curTime;
        checkResult.prePackage = curPackage;
        checkResult.isNull = false;

        countAppUsage(checkResult)
                .doOnNext(result -> LogUtil.d(TAG," screen " + result.toString()))
                .filter(result -> result.usedTime > 0)
                .doOnNext(saveData -> saveAppUsage(saveData))
                //save app usage amount to database
                .doOnNext(saveData -> saveAppAmount(saveData))

                .subscribe();

    }

    @Override
    public void check() {
        UsageCheck.checkAppUsage()
                .filter(checkResult -> !checkResult.isNull)
                //save the first start time of YiTian to the sharedPreference file
                .doOnNext(result -> {
                    if (TextUtils.isEmpty(result.prePackage)) {
                        SharePreferenceUtil.putLong(SharePreferenceUtil.KEY_PRE_START_TIME, result.curTime);
                    }
                })
                .subscribe(checkResult -> save(checkResult));

    }

    private void save(CheckResult checkResult){
        countAppUsage(checkResult)
                .filter(result -> result.usedTime > 0)
                .doOnNext(saveData -> saveAppUsage(saveData))
                .doOnNext(appInfo -> checkIsNewInstall(appInfo))
                //save app usage amount to database
                .flatMap(saveData -> saveAppAmount(saveData))
                .filter(saveResult -> saveResult == true)
                .flatMap(saveResult -> countDailyInfo())
                .flatMap(dailyInfo -> saveDailyInfo(dailyInfo))
                .filter(dailyInfo -> dailyInfo.isReported == true)
                .subscribe(dailyInfo -> notification.updateNotification(dailyInfo));
    }

    private void checkIsNewInstall(AppInfo appInfo) {
        if (AllAppInfoDao.isNewInstalled(appInfo)){
            AllAppInfoDao.insert(appInfo);
            AppMessage appMessage = new AppMessage();
            appMessage.packageName = appInfo.appPackage;
            appMessage.appIcon = Androids.getAppIcon(appMessage.packageName);
            ImageUtil.saveIcon(appMessage);
        }
    }

    /**
     * save daily usage information
     */
    private Observable<DailyInfo> saveDailyInfo(DailyInfo dailyInfo) {
        boolean result ;
        if (DailyInfoDao.isReported(dailyInfo.day)){
            result = DailyInfoDao.update(dailyInfo);
        }else{
            result = DailyInfoDao.insert(dailyInfo);
        }

        dailyInfo.isReported = result;
        LogUtil.d(TAG,dailyInfo.toString() + " save result " + result);
        return Observable.just(dailyInfo);
    }

    /**
     * count pre used app info
     * @param result
     * @return
     */
    private Observable<AppInfo> countAppUsage(CheckResult result){
        String prePackageName = TextUtils.isEmpty(result.prePackage) ?
                result.curPackage :
                result.prePackage ;
        long preEndTime = result.curTime;
        long preStartTime = SharePreferenceUtil.getLong(SharePreferenceUtil.KEY_PRE_START_TIME);
        long usedTime = preEndTime - preStartTime;
        int day = DateUtil.day(preStartTime);

        SharePreferenceUtil.putLong(SharePreferenceUtil.KEY_PRE_START_TIME,preEndTime);

        AppInfo appInfo = new AppInfo();
        appInfo.appPackage = prePackageName;
        appInfo.appName = Androids.getAppName(prePackageName);
        appInfo.startTime = preStartTime;
        appInfo.endTime = preEndTime;
        appInfo.usedTime = usedTime;
        appInfo.day = day;
        appInfo.type = appInfo.appName.equals(AppInfo.APP_IDLE) ? AppInfo.TYPE_IDLE
                : AppInfo.TYPE_APP;

        return Observable.just(appInfo);
    }

    /**
     * save screen record
     * @param time
     * @param type
     */
    private void saveScreenRecord(long time,String type){
        LogUtil.v(TAG," save screen record " + type);
        ScreenRecord record = new ScreenRecord();
        record.type=type;
        record.startTime = time;
        record.day = DateUtil.day(time);
        LogUtil.d(TAG,record.toString());
        Observable.just(ScreenRecordDao.insert(record))
                .subscribeOn(Schedulers.io())
                .doOnNext(result -> {LogUtil.d(TAG," save " + record + " result " + result);})
                .subscribe();
    }

    /**
     * save app information to database
     * @param appInfo
     */
    private void saveAppUsage(AppInfo appInfo){
        AppUsageDao.insert(appInfo);
        if (appInfo.appPackage.equals(AppInfo.APP_IDLE) && appInfo.usedTime >= 300000 ){
            TodoDao.insertTodo(appInfo);
        }

    }

    /**
     * save  app usage amount to database,if the app is not in the table insert else update
     * @param appInfo
     */
   private Observable<Boolean> saveAppAmount(AppInfo appInfo){
       UsageAmount amount = UsageAmountDao.isIn(appInfo);
       boolean result = false;
        if ( amount == null){
            amount = new UsageAmount();
            amount.appName = appInfo.appName;
            amount.appPackage = appInfo.appPackage;
            amount.day = appInfo.day;
            amount.numberOfTimes = 1;
            amount.usedTime = appInfo.usedTime;
            result = UsageAmountDao.insert(amount);
        }else{
            amount.usedTime = amount.usedTime +appInfo.usedTime;
            amount.numberOfTimes = amount.numberOfTimes + 1;
            result = UsageAmountDao.update(amount);
        }
        LogUtil.d(TAG,amount.toString() + " save " + result);
        return Observable.just(result);
   }

    public Observable<DailyInfo> countDailyInfo() {
        DailyInfo dailyInfo = UsageAmountDao.countDailyInfo(DateUtil.day(System.currentTimeMillis()));
        LogUtil.d(TAG,dailyInfo.toString());
        return Observable.just(dailyInfo);
    }
}
