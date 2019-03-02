package io.github.buniaowanfeng.ui.adapter.viewholder;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.qqtheme.framework.picker.TimePicker;
import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.data.HeadMessage;
import io.github.buniaowanfeng.ui.activity.AppDailyUsageActivity;
import io.github.buniaowanfeng.ui.activity.TrendActivity;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.SpUtil;

/**
 * Created by caofeng on 16-7-23.
 */
public class HeadHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "holder";
    private TextView mTvDay;
    private TextView mTvGoal;
    private TextView mTvUsed;
    private TextView mTVRemain;
    private TextView mTvNumberOfWake;
    private TextView mTvNumberOfApps;
    private TextView mTvNumberOfTimes;
    private ProgressBar mUseProgress;
    private LinearLayout mLlGoal;
    private LinearLayout mLlHeadCound;
    private HeadMessage message;

    public HeadHolder(View itemView) {
        super(itemView);
        mTvDay = (TextView) itemView.findViewById(R.id.tv_day);
        mTvGoal = (TextView) itemView.findViewById(R.id.tv_goal);
        mTVRemain = (TextView) itemView.findViewById(R.id.tv_remain);
        mTvNumberOfWake= (TextView) itemView.findViewById(R.id.tv_target_number_of_wake);
        mTvNumberOfApps = (TextView) itemView.findViewById(R.id.tv_target_number_of_apps);
        mTvNumberOfTimes = (TextView) itemView.findViewById(R.id.tv_target_number_of_times);
        mUseProgress = (ProgressBar) itemView.findViewById(R.id.target_current);
        mTvUsed = (TextView) itemView.findViewById(R.id.tv_info);
        mLlGoal = (LinearLayout) itemView.findViewById(R.id.ll_goal);
        mLlHeadCound = (LinearLayout) itemView.findViewById(R.id.ll_head_count);
        mLlHeadCound.setOnClickListener(this);
        mLlGoal.setOnClickListener(this);
    }

    public void setData(HeadMessage headMessage){
        message = headMessage;
        mTvDay.setText(headMessage.day);
        mTvGoal.setText(headMessage.goal);
        mTVRemain.setText(headMessage.remain);
        mTvNumberOfWake.setText(headMessage.numberOfWake);
        mTvNumberOfApps.setText(headMessage.numberOfApps);
        mTvNumberOfTimes.setText(headMessage.numberOfTimes);
        mUseProgress.setProgress(headMessage.progress);
        mTvUsed.setText(headMessage.usedTime);
        if (headMessage.isOverUse){
            mTVRemain.setTextColor(Color.RED);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_goal:
                showGoalSetting(view);
                break;
            case R.id.ll_head_count:
                AppDailyUsageActivity.startActivity(
                        view.getContext(),TrendActivity.WHOLE,message.dayInInt);
                break;
        }
    }

    private void showGoalSetting(View view) {

        TimePicker picker = new TimePicker((Activity)view.getContext(), TimePicker.HOUR_OF_DAY);
        picker.setLabel("小时 ","分钟");
        picker.setTopLineVisible(false);
        picker.setOnTimePickListener((hour, minute) -> updateHead(hour,minute));
        picker.show();
    }

    private void updateHead(String h,String m) {
        int hour = Integer.parseInt(h);
        int minute = Integer.parseInt(m);
        long goal = hour * 60 * 60 * 1000 + minute * 60 * 1000;
        SpUtil.getInstance().putLong(SpUtil.KEY_DAILY_GOAL,goal);
        LogUtil.d(TAG,"time "+ DateUtil.longToMessage(SpUtil.getInstance().getLong(SpUtil.KEY_DAILY_GOAL)));
        mTvGoal.setText("目标\n"+hour+"小时"+minute+"分");
        long remain ;
        int progress = (int) (message.usedTimeInLong *1.0/ goal*100);
        if (progress > 100){
            remain = message.usedTimeInLong - goal;
            mTVRemain.setText("超出\n"+ DateUtil.longToMessage(remain));
            mTVRemain.setTextColor(Color.RED);
        }else {
            remain = goal - message.usedTimeInLong ;
            mTVRemain.setText("剩余\n"+ DateUtil.longToMessage(remain));
        }
        mUseProgress.setProgress(progress);
    }
}
