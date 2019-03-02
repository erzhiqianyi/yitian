package io.github.buniaowanfeng.data;

import java.util.List;

/**
 * Created by caofeng on 16-7-22.
 */
public class AppDetailRecyclerData {
    public DailyInfo dailyInfo;
    public List<AppInfo> appInfos;

    public AppDetailRecyclerData(DailyInfo dailyInfo, List<AppInfo> appInfos) {
        this.dailyInfo = dailyInfo;
        this.appInfos = appInfos;
    }
}
