package io.github.buniaowanfeng.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.ui.widget.Circle;

/**
 * Created by caofeng on 16-7-27.
 */
public class AppDailyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<AppMessage> messages ;

    public AppDailyRecyclerAdapter() {
        messages = new ArrayList<>();
    }

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_daily_detail,parent,false);
        return new AppDailyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AppDailyHolder)
            ((AppDailyHolder) holder).setData(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<AppMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public class AppDailyHolder extends RecyclerView.ViewHolder{
        private Circle circleProgress;
        private TextView mTvUsedTime;
        private TextView mTvTime;
        public AppDailyHolder(View itemView) {
            super(itemView);
            circleProgress = (Circle) itemView.findViewById(R.id.app_daily_progress);
            mTvUsedTime = (TextView) itemView.findViewById(R.id.app_detail_used_time);
            mTvTime = (TextView) itemView.findViewById(R.id.app_detail_time);
        }

        public void setData(AppMessage message) {
            circleProgress.setData(message.startSweap,message.progress);
            mTvUsedTime.setText(message.usedTime);
            mTvTime.setText(message.startTime+"~"+message.endTime);
        }
    }
}
