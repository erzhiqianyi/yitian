package io.github.buniaowanfeng.ui.adapter.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.ui.activity.AppDailyUsageActivity;
import io.github.buniaowanfeng.ui.adapter.AppUsageDetailRecyclerAdapter;
import io.github.buniaowanfeng.ui.adapter.SingleAppAdapter;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;

/**
 * Created by caofeng on 16-7-23.
 */
public class SingleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "singleholder";
    private CardView cardView;
    private TextView mTvStartTime;
    private TextView mTvUsedTime;
    private TextView mTvAppName;
    private ImageView mImageIcon;
    private SingleAppAdapter singleAppAdapter;
    private AppUsageDetailRecyclerAdapter appAdapter;

    private int positionInSoul ;
    public SingleHolder(View itemView, SingleAppAdapter adapter) {
        super(itemView);
        singleAppAdapter = adapter;
        itemView.setOnClickListener(this);
        mTvStartTime = (TextView) itemView.findViewById(R.id.tv_single_start_time);
        mTvUsedTime = (TextView) itemView.findViewById(R.id.tv_single_used_time);
        mTvAppName = (TextView) itemView.findViewById(R.id.tv_single_app_name);
        mImageIcon = (ImageView) itemView.findViewById(R.id.image_app_icon);
        cardView = (CardView) itemView.findViewById(R.id.single_container);
    }

    public void setData(AppMessage appMessage,int positionInSoul,AppUsageDetailRecyclerAdapter appAdapter){

        this.appAdapter = appAdapter;
        this.positionInSoul = positionInSoul;
        mTvStartTime.setText(appMessage.startTime);
        mTvUsedTime.setText(appMessage.usedTime);
        mTvAppName.setText(appMessage.appName);
        File file = new File(YiTian.mIconPath,appMessage.packageName+".png");
        Glide.with(cardView.getContext())
                .load(file)
                .into(mImageIcon);
        ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
        layoutParams.height = appMessage.height;
        cardView.setLayoutParams(layoutParams);
        cardView.setOnClickListener(this);
        mImageIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.image_app_icon:
                LogUtil.i(TAG,"icon click "+ singleAppAdapter.getAppMessages().get(getAdapterPosition()).packageName);
                AppDailyUsageActivity.startActivity(view.getContext(),
                        singleAppAdapter.getAppMessages().get(getAdapterPosition()).packageName,
                        DateUtil.day(singleAppAdapter.getAppMessages().get(getAdapterPosition()).startTimeInLong));
                break;
            case R.id.single_container:
                LogUtil.i(TAG,"item click " + getAdapterPosition());
                appAdapter.getSouls().get(positionInSoul).type = AppInfo.TYPE_MULTIPLE;
                appAdapter.notifyItemChanged(positionInSoul);
                appAdapter.getFragment().scrollTo(positionInSoul);
                break;
        }
    }
}
