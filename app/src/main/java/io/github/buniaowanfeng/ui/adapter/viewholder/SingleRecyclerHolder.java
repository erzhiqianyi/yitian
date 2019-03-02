package io.github.buniaowanfeng.ui.adapter.viewholder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.ui.adapter.AppUsageDetailRecyclerAdapter;
import io.github.buniaowanfeng.ui.adapter.SingleAppAdapter;

/**
 * Created by caofeng on 16-7-23.
 */
public class SingleRecyclerHolder extends RecyclerView.ViewHolder {
    private RecyclerView singleRecycler;
    private SingleAppAdapter appAdapter;
    public SingleRecyclerHolder(View itemView) {
        super(itemView);
        singleRecycler = (RecyclerView) itemView.findViewById(R.id.single_recycler);
        singleRecycler.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
    }

    public void setData(int postiion, AppUsageDetailRecyclerAdapter adapter){
        appAdapter = new SingleAppAdapter(postiion,adapter);
        appAdapter.setAppMessages(adapter.getSouls().get(postiion).data);
        singleRecycler.setAdapter(appAdapter);
    }
}
