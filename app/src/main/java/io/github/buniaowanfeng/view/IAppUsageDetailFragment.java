package io.github.buniaowanfeng.view;

import java.util.List;

import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.data.Soul;

/**
 * Created by caofeng on 16-7-22.
 */
public interface IAppUsageDetailFragment extends IRefresh{
    void setAdapterData(List<Soul<AppMessage>> data);
}
