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
import io.github.buniaowanfeng.data.SingleDetail;
import io.github.buniaowanfeng.ui.activity.AppDailyUsageActivity;
import io.github.buniaowanfeng.util.LogUtil;

/**
 * Created by caofeng on 16-7-26.
 */
public class SingleDetailAdapter extends RecyclerView.Adapter<SingleDetailAdapter.SingleDetailHolder> {
    private static final String TAG = "singleAdapter";
    List<SingleDetail> datas ;
    private String packageName ;
    private int day;
    public SingleDetailAdapter() {
        datas = new ArrayList<>();
    }

    @Override
    public SingleDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_detail,parent,false);
        return new SingleDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(SingleDetailHolder holder, int position) {
        holder.setData(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setDatas(List<SingleDetail> datas) {
        this.datas = datas;
        packageName  = datas.get(0).packageName;
        notifyDataSetChanged();
    }

    public class SingleDetailHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTvDay;
        private TextView mTvInfo;
        private ProgressBar mProgress;
        public SingleDetailHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTvDay = (TextView) itemView.findViewById(R.id.single_detail_recycler_day);
            mTvInfo = (TextView) itemView.findViewById(R.id.tv_single_detail_info);
            mProgress = (ProgressBar) itemView.findViewById(R.id.single_detail_usage_rate);
        }

        public void setData(SingleDetail data){
            mTvDay.setText(data.day);
            mTvInfo.setText(data.totalTime +" " + data.totalTimes);
            mProgress.setProgress(data.progress);
        }

        @Override
        public void onClick(View view) {
            day = datas.get(getAdapterPosition()).dayInInt;
            LogUtil.d(TAG,packageName + day);
            AppDailyUsageActivity.startActivity(
                    view.getContext(),packageName,day);
        }
    }
}

