package io.github.buniaowanfeng.presententer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.buniaowanfeng.data.Base;
import io.github.buniaowanfeng.data.UsageAmount;
import io.github.buniaowanfeng.database.dao.UsageAmountDao;
import io.github.buniaowanfeng.ui.adapter.AppCountRecyclerAdapter;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.NumberOfTimesCompare;
import io.github.buniaowanfeng.util.UsedTimeCompare;
import io.github.buniaowanfeng.view.IAppCountFragment;
import io.github.buniaowanfeng.view.IAppUsageDetailPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by caofeng on 16-7-25.
 */
public class AppCountPresenter implements IAppUsageDetailPresenter {
    private static final String TAG = "appcountpresenter";
    private IAppCountFragment fragment;

    public AppCountPresenter(IAppCountFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void getAppUsageInfo(int day) {
        fragment.showRefresh();
        Observable.just(day)
                .map(requestDay -> UsageAmountDao.getUsage(day))
                .doOnNext(usages -> calculate(usages))
                .map(usages -> toRecyclerData(usages))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Base<String, UsageAmount>>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG,"completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getAppUsageInfo(day);
                    }

                    @Override
                    public void onNext(List<Base<String, UsageAmount>> bases) {
                        fragment.hideRefresh();
                        fragment.setAdapterData(bases);
                    }
                });
    }

    private List<Base<String,UsageAmount>> toRecyclerData(List<UsageAmount> usages) {
        List<Base<String,UsageAmount>> data = new ArrayList<>();
        Base<String,UsageAmount> head = new Base<>();
        head.type = AppCountRecyclerAdapter.TYPE_HEAD;
        head.head = DateUtil.intToString(usages.get(0).day);
        data.add(head);
        for (UsageAmount usageAmount : usages){
            Base<String,UsageAmount> body = new Base<>();
            body.type =  AppCountRecyclerAdapter.TYPE_TIME;
            body.body = usageAmount;
            data.add(body);
        }
        return data;
    }

    private List<UsageAmount> calculate(List<UsageAmount> usages) {
        Collections.sort(usages,new NumberOfTimesCompare());
        int max = usages.get(0).numberOfTimes;
        for (UsageAmount usageAmount : usages){
            usageAmount.numberOfTimeRate = (int) (usageAmount.numberOfTimes * 1.0 / max * 100);
        }
        Collections.sort(usages,new UsedTimeCompare());
        long maxUsedTime = usages.get(0).usedTime;
        for (UsageAmount usageAmount : usages){
            usageAmount.usedRate = (int) (usageAmount.usedTime * 1.0 / maxUsedTime * 100);
        }

        return usages;
    }
}
