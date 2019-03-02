package io.github.buniaowanfeng.presententer;

import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.AppDailyHead;
import io.github.buniaowanfeng.database.dao.AllAppInfoDao;
import io.github.buniaowanfeng.database.dao.DailyInfoDao;
import io.github.buniaowanfeng.database.dao.UsageAmountDao;
import io.github.buniaowanfeng.ui.activity.TrendActivity;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.view.IAppDailyActivity;
import io.github.buniaowanfeng.view.IAppDailyPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by caofeng on 16-7-27.
 */
public class AppDailyPresenter implements IAppDailyPresenter {
    private static final String TAG = "appdailypresenter";
    private IAppDailyActivity activity;

    public AppDailyPresenter(IAppDailyActivity activity) {
        this.activity = activity;
    }

    @Override
    public void getHead(String packageName) {
        LogUtil.d(TAG,packageName);
        if (packageName.equals(TrendActivity.WHOLE)){
            AppDailyHead head = new AppDailyHead();
            head.appName = YiTian.mContext.getString(R.string.shouji);
            head.packageName = TrendActivity.WHOLE;
            activity.setHead(head);
            getDaysFromDailyInfo();
        }else {
            Observable.just(packageName)
                    .map(name -> AllAppInfoDao.getGoal(packageName))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<AppDailyHead>() {
                        @Override
                        public void onCompleted() {
                            LogUtil.v(TAG,"get head complete");
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(AppDailyHead appDailyHead) {
                            activity.setHead(appDailyHead);
                            getDays(packageName);
                        }
                    });
        }

    }

    private void getDaysFromDailyInfo() {
        Observable.just(0)
                .map(type -> DailyInfoDao.getDays(type))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG,"load days completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getDaysFromDailyInfo();
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        activity.setAdapter(integers);
                    }
                });
    }

    private void getDays(String packageName){
        Observable.just(packageName)
                .map(name -> UsageAmountDao.getDays(packageName))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG,"load days for " + packageName + " scucess");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        activity.setAdapter(integers);
                    }
                });
    }
}
