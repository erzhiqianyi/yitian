package io.github.buniaowanfeng.view;

/**
 * Created by caofeng on 16-7-27.
 */
public interface IAppDailyDetailPresenter {
    void getDailyHead(int day,String packageName);
    void getAppUsage(int day ,String packageName);
}
