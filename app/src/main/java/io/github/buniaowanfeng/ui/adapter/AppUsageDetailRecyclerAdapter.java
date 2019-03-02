package io.github.buniaowanfeng.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.data.AppInfo;
import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.data.Soul;
import io.github.buniaowanfeng.ui.adapter.viewholder.HeadHolder;
import io.github.buniaowanfeng.ui.adapter.viewholder.IdleHolder;
import io.github.buniaowanfeng.ui.adapter.viewholder.MultipleHolder;
import io.github.buniaowanfeng.ui.adapter.viewholder.SingleRecyclerHolder;
import io.github.buniaowanfeng.ui.fragment.AppUsageDetailFragment;

/**
 * Created by caofeng on 16-7-22.
 */
public class AppUsageDetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "adapter";
    private List<Soul<AppMessage>> souls;
    private AppUsageDetailFragment fragment;

    public AppUsageDetailRecyclerAdapter(AppUsageDetailFragment fragment) {
        souls = new ArrayList<>();
        this.fragment = fragment;
    }

    public void setSouls(List<Soul<AppMessage>> souls) {
        this.souls.clear();
        this.souls = souls;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view ;
        switch (viewType){
            case AppInfo.TYPE_IDLE:
                view = inflater.inflate(R.layout.item_timeline_idle,parent,false);
                return new IdleHolder(view);
            case AppInfo.TYPE_HEAD:
                view = inflater.inflate(R.layout.item_target, parent, false);
                return new HeadHolder(view);
            case AppInfo.TYPE_MULTIPLE:
                view = inflater.inflate(R.layout.item_timeline_mutiple,parent,false);
                return new MultipleHolder(view,this);
            case AppInfo.TYPE_SINGLE:
                view = inflater.inflate(R.layout.item_single,parent,false);
                return new SingleRecyclerHolder(view);
            default:
                view = inflater.inflate(R.layout.item_target,parent,false);
                return new HeadHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof IdleHolder){
            if (souls.get(position).data != null && !souls.get(position).data.get(0).usedTime.equals("0s")) {
                ((IdleHolder) holder).setData(souls.get(position).data.get(0));
            }
        }else if (holder instanceof HeadHolder){
            ((HeadHolder) holder).setData(souls.get(0).headMessage);
        }else if (holder instanceof MultipleHolder){
            if (souls.get(position).appMultiple != null){
            ((MultipleHolder) holder).setData(souls.get(position).appMultiple);
            }
        }else if (holder instanceof SingleRecyclerHolder){
            ((SingleRecyclerHolder) holder).setData(position,this);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = souls.get(position).type;
        switch (type){
            case AppInfo.TYPE_IDLE:
                return AppInfo.TYPE_IDLE;
            case AppInfo.TYPE_HEAD:
                return AppInfo.TYPE_HEAD;
            case AppInfo.TYPE_MULTIPLE:
                return AppInfo.TYPE_MULTIPLE;
            case AppInfo.TYPE_SINGLE:
                return AppInfo.TYPE_SINGLE;
            default:
                return AppInfo.TYPE_MULTIPLE;
        }

    }

    @Override
    public int getItemCount() {
        return souls.size();
    }

    public List<Soul<AppMessage>> getSouls() {
        return souls;
    }

    public AppUsageDetailFragment getFragment() {
        return fragment;
    }
}
