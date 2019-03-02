package io.github.buniaowanfeng.ui.adapter.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.data.AppMultiple;
import io.github.buniaowanfeng.ui.activity.AppDailyUsageActivity;
import io.github.buniaowanfeng.ui.adapter.AppUsageDetailRecyclerAdapter;

/**
 * Created by caofeng on 16-7-23.
 */
public class MultipleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "MultipleHolder";
    private TextView mTvStartTime;
    private TextView mTvUsedTime;
    private ImageView mImage1;
    private ImageView mImage2;
    private ImageView mImage3;
    private ImageView mImage4;
    private ImageView mImage5;
    private ImageView mImageMore;
    private AppUsageDetailRecyclerAdapter adapter;

    public MultipleHolder(View itemView, AppUsageDetailRecyclerAdapter adapter) {
        super(itemView);
        if (this.adapter == null){
            this.adapter = adapter;
        }
        itemView.setOnClickListener(this);
        mTvStartTime = (TextView) itemView.findViewById(R.id.tv_multiple_start_time);
        mTvUsedTime = (TextView) itemView.findViewById(R.id.tv_multiple_used_time);
        mImage1 = (ImageView) itemView.findViewById(R.id.image1);
        mImage2 = (ImageView) itemView.findViewById(R.id.image2);
        mImage3 = (ImageView) itemView.findViewById(R.id.image3);
        mImage4 = (ImageView) itemView.findViewById(R.id.image4);
        mImage5 = (ImageView) itemView.findViewById(R.id.image5);
        mImageMore = (ImageView) itemView.findViewById(R.id.image_more);

    }

    public void setData(AppMultiple appMultiple){
        List<Drawable> list = appMultiple.appIcon;

        mTvStartTime.setText(appMultiple.startTime);
        int size = list.size();
        if(size == 1){
            if (appMultiple.appName.equals(AppInfo.APP_IDLE)) {
                    mTvUsedTime.setText(  "launcher " +appMultiple.usedTime);
                }else {
                    mTvUsedTime.setText(appMultiple.appName + " " +appMultiple.usedTime);
            }
        }
        else {
            mTvUsedTime.setText(appMultiple.usedTime);
        }

        if (size >= 1){
            mImage1.setVisibility(View.VISIBLE);
            Glide.with(mTvStartTime.getContext())
                    .load(new File(YiTian.mIconPath,appMultiple.appPackage.get(0)+".png"))
                    .into(mImage1);
//            mImage1.setImageDrawable(list.get(0));
            mImage2.setVisibility(View.GONE);
            mImage3.setVisibility(View.GONE);
            mImage4.setVisibility(View.GONE);
            mImage5.setVisibility(View.GONE);
            mImageMore.setVisibility(View.GONE);
            if (size >= 2){
                mImage2.setVisibility(View.VISIBLE);
                Glide.with(mTvStartTime.getContext())
                        .load(new File(YiTian.mIconPath,appMultiple.appPackage.get(1)+".png"))
                        .into(mImage2);
//                mImage2.setImageDrawable(list.get(1));

                if (size >= 3 ){
                    mImage3.setVisibility(View.VISIBLE);
                    Glide.with(mTvStartTime.getContext())
                            .load(new File(YiTian.mIconPath,appMultiple.appPackage.get(2)+".png"))
                            .into(mImage3);
//                    mImage3.setImageDrawable(list.get(2));
                    if (size >= 4){
                        mImage4.setVisibility(View.VISIBLE);
//                        mImage4.setImageDrawable(list.get(3));
                        Glide.with(mTvStartTime.getContext())
                                .load(new File(YiTian.mIconPath,appMultiple.appPackage.get(3)+".png"))
                                .into(mImage4);
                        if (size >= 5){
                            mImage5.setVisibility(View.VISIBLE);
//                            mImage5.setImageDrawable(list.get(4));
                            Glide.with(mTvStartTime.getContext())
                                    .load(new File(YiTian.mIconPath,appMultiple.appPackage.get(4)+".png"))
                                    .into(mImage5);
                            if (size >= 6) {
                                mImageMore.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        if (adapter.getSouls().get(position).appMultiple.appIcon.size() == 1){
            AppDailyUsageActivity.startActivity(view.getContext()
                    , adapter.getSouls().get(position).data.get(0).packageName,
                    adapter.getSouls().get(position).appMultiple.day);
        }else {
            adapter.getSouls().get(position).type = AppInfo.TYPE_SINGLE;
            adapter.notifyItemChanged(position);
        }
    }
}
