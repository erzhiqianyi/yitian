package io.github.buniaowanfeng.view;

import java.util.List;

import io.github.buniaowanfeng.data.Base;
import io.github.buniaowanfeng.data.UsageAmount;

/**
 * Created by caofeng on 16-7-25.
 */
public interface IAppCountFragment extends  IRefresh{
    void setAdapterData(List<Base<String,UsageAmount>> data);
}
