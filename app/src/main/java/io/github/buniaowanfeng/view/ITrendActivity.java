package io.github.buniaowanfeng.view;

import java.util.List;

import io.github.buniaowanfeng.data.TrendData;

/**
 * Created by caofeng on 16-7-28.
 */
public interface ITrendActivity {
    void setHead(TrendData data);
    void setDetail(List<TrendData> details);
    void showEmpty();
}
