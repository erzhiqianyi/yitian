package io.github.buniaowanfeng.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.Base;
import io.github.buniaowanfeng.data.UsageAmount;
import io.github.buniaowanfeng.ui.activity.AppDailyUsageActivity;

/**
 * Created by caofeng on 16-7-25.
 */
public class AppCountRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEAD = 0;
    public static final int TYPE_TIME = 1;
    public static final int TYPE_TIMES = 2;
    private int day;
    private List<Base<String,UsageAmount>>  data;

    public AppCountRecyclerAdapter() {
        data = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType){
            case TYPE_HEAD:
                view = inflater.inflate(R.layout.item_count_header,parent,false);
                return new CountHeadHolder(view);
            case TYPE_TIME:
                view = inflater.inflate(R.layout.item_count_time,parent,false);
                return new CountTimeHolder(view);
            case TYPE_TIMES:
                view = inflater.inflate(R.layout.item_count_time,parent,false);
                return new CountTimesHolder(view);
            default:
                view = inflater.inflate(R.layout.item_count_header,parent,false);
                return new CountHeadHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CountHeadHolder){
            ((CountHeadHolder) holder).setData(data.get(position).head);
        }else if (holder instanceof CountTimeHolder){
            ((CountTimeHolder) holder).setData(data.get(position).body);
        }else if (holder instanceof  CountTimesHolder){
            ((CountTimesHolder) holder).setData(data.get(position).body);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = data.get(position).type;
        switch (type){
            case TYPE_HEAD :
                return TYPE_HEAD;
            case TYPE_TIME:
                return TYPE_TIME;
            case TYPE_TIMES:
                return TYPE_TIMES;
            default:
                return TYPE_HEAD;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Base<String, UsageAmount>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public class CountHeadHolder extends RecyclerView.ViewHolder{
        private TextView mTvDay;
        public CountHeadHolder(View itemView) {
            super(itemView);
            mTvDay = (TextView) itemView.findViewById(R.id.tv_count_day);
        }
        public void setData(String day){
            mTvDay.setText(day);
        }
    }

    public class CountTimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageIcon;
        private TextView mTvInfo;
        private ProgressBar mProgress;
        public CountTimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImageIcon = (ImageView) itemView.findViewById(R.id.image_count_time_icon);
            mTvInfo = (TextView) itemView.findViewById(R.id.tv_info);
            mProgress = (ProgressBar) itemView.findViewById(R.id.count_usage);
        }

        public void setData(UsageAmount usage){
            Glide.with(itemView.getContext())
                    .load( new File(YiTian.mIconPath,usage.appPackage+".png"))
                    .into(mImageIcon);
            mTvInfo.setText(usage.usedTimes);
            mProgress.setProgress(usage.usedRate);
        }

        @Override
        public void onClick(View view) {
//            SingleAppDetailActivity.startActivity(view.getContext(),data.get(getAdapterPosition()).body.appPackage);
            AppDailyUsageActivity.startActivity(view.getContext(),
                    data.get(getAdapterPosition()).body.appPackage,
                    data.get(getAdapterPosition()).body.day);
        }
    }

    private class CountTimesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageIcon;
        private TextView mTvInfo;
        private ProgressBar mProgress;

        public CountTimesHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImageIcon = (ImageView) itemView.findViewById(R.id.image_count_time_icon);
            mTvInfo = (TextView) itemView.findViewById(R.id.tv_info);
            mProgress = (ProgressBar) itemView.findViewById(R.id.count_usage);
        }

        public void setData(UsageAmount usage){
            Glide.with(itemView.getContext())
                    .load( new File(YiTian.mIconPath,usage.appPackage+".png"))
                    .into(mImageIcon);
            mTvInfo.setText(usage.numberOfTimes + "次");
            mProgress.setProgress(usage.numberOfTimeRate);
        }

        @Override
        public void onClick(View view) {
//            SingleAppDetailActivity.startActivity(view.getContext(),data.get(getAdapterPosition()).body.appPackage);
            AppDailyUsageActivity.startActivity(view.getContext(),
                    data.get(getAdapterPosition()).body.appPackage,
                    data.get(getAdapterPosition()).body.day);
        }
    }

    public List<Base<String, UsageAmount>> getData() {
        return data;
    }
}
