package io.github.buniaowanfeng.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.data.AppMessage;
import io.github.buniaowanfeng.ui.adapter.viewholder.SingleHolder;

/**
 * Created by caofeng on 16-7-23.
 */
public class SingleAppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AppMessage> appMessages;
    private AppUsageDetailRecyclerAdapter adapter;
    private int positionInSoul;
    public SingleAppAdapter(int positionInSoul,AppUsageDetailRecyclerAdapter adapter) {
        this.positionInSoul = positionInSoul;
        appMessages = new ArrayList<>();
        this.adapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_single,parent,false);
        return new SingleHolder(view,this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SingleHolder){
            ((SingleHolder) holder).setData(appMessages.get(position),positionInSoul,adapter);
        }
    }

    @Override
    public int getItemCount() {
        return appMessages.size();
    }

    public void setAppMessages(List<AppMessage> appMessages) {
        this.appMessages = appMessages;
        notifyDataSetChanged();
    }

    public List<AppMessage> getAppMessages() {
        return appMessages;
    }
}
