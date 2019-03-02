package io.github.buniaowanfeng.ui.adapter.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.data.AppMessage;

/**
 * Created by caofeng on 16-7-23.
 */
public class IdleHolder extends RecyclerView.ViewHolder {
    private CardView mIdleContainer;
    private TextView mTvStartTime;
    private TextView mTvUsedTime;
    public IdleHolder(View itemView) {
        super(itemView);
        mIdleContainer = (CardView) itemView.findViewById(R.id.idle_container);
        mTvStartTime = (TextView) itemView.findViewById(R.id.tv_idle_start_time);
        mTvUsedTime = (TextView) itemView.findViewById(R.id.tv_idle_used_time);
    }

    public void setData(AppMessage appMessage){
        mTvStartTime.setText(appMessage.startTime);
        mTvUsedTime.setText(appMessage.usedTime);
        ViewGroup.LayoutParams layoutParams = mIdleContainer.getLayoutParams();
        layoutParams.height = appMessage.height;
        mIdleContainer.setLayoutParams(layoutParams);
    }
}
