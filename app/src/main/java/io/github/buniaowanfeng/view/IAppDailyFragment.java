package io.github.buniaowanfeng.view;

import java.util.List;

import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.data.UsageAmount;

/**
 * Created by caofeng on 16-7-27.
 */
public interface IAppDailyFragment {
    void setHead(UsageAmount usageAmount);
    void setAdapter(List<AppMessage> messages);
}
