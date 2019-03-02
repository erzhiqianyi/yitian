package io.github.buniaowanfeng.view;

import java.util.List;

import io.github.buniaowanfeng.data.AppDailyHead;

/**
 * Created by caofeng on 16-7-27.
 */
public interface IAppDailyActivity {
    void setHead(AppDailyHead head);
    void setAdapter(List<Integer> day);
}
