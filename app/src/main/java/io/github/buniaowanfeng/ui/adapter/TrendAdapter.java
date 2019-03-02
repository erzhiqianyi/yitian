package io.github.buniaowanfeng.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.data.TrendData;
import io.github.buniaowanfeng.ui.activity.AppDailyUsageActivity;
import io.github.buniaowanfeng.util.DateUtil;

/**
 * Created by caofeng on 16-7-29.
 */
public class TrendAdapter extends RecyclerView.Adapter< RecyclerView.ViewHolder> {
    private List<TrendData> datas;

    public TrendAdapter() {
        datas = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view ;
        switch (viewType){
            case TrendData.TYPE_WHOLE:
                view = inflater.inflate(R.layout.item_trend_whole,parent,false);
                return new WholeHolder(view);
            case TrendData.TYPE_APP:
                view = inflater.inflate(R.layout.item_trend_app,parent,false);
                return new AppHolder(view);
            default:
                view = inflater.inflate(R.layout.item_trend_whole,parent,false);
                return new WholeHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WholeHolder )
            ((WholeHolder) holder).setData(datas.get(position));
        else if (holder instanceof AppHolder)
            ((AppHolder) holder).setData(datas.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        int type = datas.get(position).type;
        switch (type){
            case TrendData.TYPE_APP:
                return TrendData.TYPE_APP;
            case TrendData.TYPE_WHOLE:
                return TrendData.TYPE_WHOLE;
            default:
                return TrendData.TYPE_WHOLE;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setDatas(List<TrendData> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    private class WholeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTvTotalTime;
        private TextView mTvTotalTimes;
        private TextView mTvWakes;
        private TextView mTvDay;
        private ProgressBar mProgress;
        public WholeHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mTvTotalTime  = (TextView) view.findViewById(R.id.tv_trend_used_time);
            mTvTotalTimes = (TextView) view.findViewById(R.id.tv_number_of_times);
            mTvWakes = (TextView) view.findViewById(R.id.tv_trend_number_of_wakes);
            mTvDay = (TextView) view.findViewById(R.id.trend_detail_day);
            mProgress = (ProgressBar) view.findViewById(R.id.trend_detail_progress);
        }
        public void setData(TrendData data){
            mTvDay.setText(DateUtil.intToString(data.day));
            mTvTotalTime.setText(data.usedTime);
            mTvTotalTimes.setText(data.usedTimes);
            mTvWakes.setText(data.wakeTimes);
            mProgress.setProgress(data.progress);
        }

        @Override
        public void onClick(View view) {
            AppDailyUsageActivity.startActivity(view.getContext(),
                    datas.get(getAdapterPosition()).packageName,
                    datas.get(getAdapterPosition()).day);
        }
    }

    private class AppHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTvTotalTime;
        private TextView mTvTotalTimes;
        private TextView mTvDay;
        private ProgressBar mProgress;
        public AppHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mTvTotalTime  = (TextView) view.findViewById(R.id.tv_trend_used_time);
            mTvTotalTimes = (TextView) view.findViewById(R.id.tv_number_of_times);
            mTvDay = (TextView) view.findViewById(R.id.trend_detail_day);
            mProgress = (ProgressBar) view.findViewById(R.id.trend_detail_progress);
        }
        public void setData(TrendData data){
            mTvDay.setText(DateUtil.intToString(data.day));
            mTvTotalTime.setText(data.usedTime);
            mTvTotalTimes.setText(data.usedTimes);
            mProgress.setProgress(data.progress);
        }

        @Override
        public void onClick(View view) {
            AppDailyUsageActivity.startActivity(view.getContext(),
                    datas.get(getAdapterPosition()).packageName,
                    datas.get(getAdapterPosition()).day);
        }
    }
}
