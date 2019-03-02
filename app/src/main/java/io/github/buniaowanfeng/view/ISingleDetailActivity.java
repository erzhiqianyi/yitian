package io.github.buniaowanfeng.view;

import java.util.List;

import io.github.buniaowanfeng.data.SingleDetail;

/**
 * Created by caofeng on 16-7-26.
 */
public interface ISingleDetailActivity extends IRefresh{
    void setHeadMessage(SingleDetail head);
    void setAdater(List<SingleDetail> datas);
}
