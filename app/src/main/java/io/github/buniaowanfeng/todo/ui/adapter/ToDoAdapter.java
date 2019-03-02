package io.github.buniaowanfeng.todo.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.todo.data.model.TodoBean;
import io.github.buniaowanfeng.util.LogUtil;

/**
 * Created by caofeng on 16-9-19.
 */
public class ToDoAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "todoadpter";
    private ArrayList<TodoBean> lists;
    private OnEmptyItemClickListener onEmptyItemClickListener;
    private OnMoreItemClickListener onMoreItemClickListener;

    public void setOnEmptyItemClickListener(
            OnEmptyItemClickListener onEmptyItemClickListener) {
        this.onEmptyItemClickListener = onEmptyItemClickListener;
    }

    public void setOnMoreItemClickListener(
            OnMoreItemClickListener onMoreItemClickListener) {
        this.onMoreItemClickListener = onMoreItemClickListener;
    }

    public ToDoAdapter() {
        lists = new ArrayList<>();
    }

    public ArrayList<TodoBean> getLists() {
        return lists;
    }

    public void addNewData(ArrayList<TodoBean> newList) {
        lists.addAll(newList);
        notifyDataSetChanged();
    }

    public void setNewData(ArrayList<TodoBean> newTodos){
        lists.clear();
        lists.addAll(newTodos);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view ;
        switch (viewType){
            case 2:
                view = inflater.inflate(R.layout.item_todo_list_empty,parent,false);
                return new EmptyViewHolder(view);
            case 3:
                view = inflater.inflate(R.layout.item_todo_list,parent,false);
                return new TodoViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmptyViewHolder) {
            ((EmptyViewHolder) holder).setData(lists.get(position));
        } else if (holder instanceof TodoViewHolder) {
            ((TodoViewHolder) holder).setData(lists.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (lists.size() == 0 )
            return 0;
        switch (lists.get(position).type){
            case 2:
                return TodoBean.EMPTY;
            case 3:
                return TodoBean.DATA;
        }
        return super.getItemViewType(position);
    }

    public void clear() {
        lists.clear();
        notifyDataSetChanged();
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageTime;
        private TextView mTvTime;
        private TextView mTvEmpty;
        private TextView mTvDate;
        private TextView mTvUsedTime;
        public EmptyViewHolder(View view) {
            super(view);
            mImageTime = (ImageView) view.findViewById(R.id.img_time);
            mTvTime = (TextView) view.findViewById(R.id.tv_time);
            mTvEmpty = (TextView) view.findViewById(R.id.tv_empty);
            mTvDate = (TextView) view.findViewById(R.id.tv_date);
            mTvUsedTime = (TextView) view.findViewById(R.id.tv_used_time);
        }

        public void setData(TodoBean data) {
            Glide.with(mImageTime.getContext()).load(data.empty.iconId)
                    .into(mImageTime);
            String format = "";
            int startHour = data.empty.startHour;
            int startMinute = data.empty.startMinute;
            int endHour = data.empty.endHour;
            int endMinute = data.empty.endMinute;

            if (startMinute > 10 && endMinute > 10){
                format = YiTian.mContext.getString(R.string.empty_format);
            }else {
                if (startMinute < 10 && endMinute < 10 ) {
                   format = YiTian.mContext.getString(R.string.time_format_both);
                }else {
                    if (startMinute < 10 ){
                        format = YiTian.mContext.getString(R.string.time_format_one);
                    }else {
                        format = YiTian.mContext.getString(R.string.time_format_two);
                    }
                }
            }

            String timeMessage = String.format(format, startHour,
                    startMinute, endHour, endMinute);

            String dateMessage = String.format(YiTian.mContext.getString(R.string.head_format),
                    data.dateInfo.day,data.dateInfo.date);

            mTvTime.setText(timeMessage);
            mTvDate.setText(dateMessage);
            mTvUsedTime.setText(data.empty.usdTime);
            mTvEmpty.setOnClickListener(view -> {
                if (onEmptyItemClickListener != null){
                    onEmptyItemClickListener.onClick(lists
                                    .get(getAdapterPosition()),
                            getAdapterPosition());
                }
            });

        }
    }

    private class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageTime;
        private TextView mTvTime;
        private TextView mTvDate;
        private TextView mTvTag;
        private TextView mTvDesc;
        private TextView mTvLocation;
        private ImageView mImageMore;
        private LinearLayout mLlTag;
        private LinearLayout mLlLocation;
        private LinearLayout mLlDesc;
        private TextView mTvUsedTime;

        public TodoViewHolder(View view) {
            super(view);
            mImageTime = (ImageView) view.findViewById(R.id.img_time);
            mTvTime = (TextView) view.findViewById(R.id.tv_time);
            mTvTag = (TextView) view.findViewById(R.id.tv_tag);
            mTvDesc = (TextView) view.findViewById(R.id.tv_desc);
            mTvLocation = (TextView) view.findViewById(R.id.tv_location);
            mImageMore = (ImageView) view.findViewById(R.id.image_more);
            mTvDate = (TextView) view.findViewById(R.id.tv_date);
            mLlTag = (LinearLayout) view.findViewById(R.id.ll_tag);
            mLlLocation = (LinearLayout) view.findViewById(R.id.ll_locaiton);
            mLlDesc = (LinearLayout) view.findViewById(R.id.ll_desc);
            mTvUsedTime = (TextView) view.findViewById(R.id.tv_used_time);
            mImageMore.setOnClickListener(this);
        }

        public void setData(TodoBean data) {
            String format = "";
            int startHour = data.empty.startHour;
            int startMinute = data.empty.startMinute;
            int endHour = data.empty.endHour;
            int endMinute = data.empty.endMinute;

            if (startMinute > 10 && endMinute > 10){
                format = YiTian.mContext.getString(R.string.empty_format);
            }else {
                if (startMinute < 10 && endMinute < 10 ) {
                    format = YiTian.mContext.getString(R.string.time_format_both);
                }else {
                    if (startMinute < 10 ){
                        format = YiTian.mContext.getString(R.string.time_format_one);
                    }else {
                        format = YiTian.mContext.getString(R.string.time_format_two);
                    }
                }
            }

            String timeMessage = String.format(format, startHour,
                    startMinute, endHour, endMinute);

            String dateMessage = String.format(YiTian.mContext.getString(R.string.head_format),
                    data.dateInfo.day,data.dateInfo.date);

            String tagMessage = data.data.tag;
            String descMessage = data.data.desc;
            String locationMessage = data.data.location;

            Glide.with(mImageTime.getContext()).load(data.empty.iconId)
                    .into(mImageTime);
            mTvUsedTime.setText(data.empty.usdTime);
            mTvTime.setText(timeMessage);
            mTvDate.setText(dateMessage);
            if (tagMessage != null && !tagMessage.equals("null")){
                mLlTag.setVisibility(View.VISIBLE);
                mTvTag.setText(tagMessage);
            }else {
                mLlTag.setVisibility(View.GONE);
            }

            if (locationMessage != null && !locationMessage.equals("null")){
                mLlLocation.setVisibility(View.VISIBLE);
                mTvLocation.setText(locationMessage);
            }else {
                mLlLocation.setVisibility(View.GONE);
            }

            if (descMessage != null && !descMessage.equals("null")){
                mLlDesc.setVisibility(View.VISIBLE);
                mTvDesc.setText(descMessage);
            }else {
                LogUtil.d(TAG," gone");
                mLlDesc.setVisibility(View.GONE);

            }
        }

        @Override
        public void onClick(View view) {
            if (onMoreItemClickListener != null){
                onMoreItemClickListener.onClick(lists.get(getAdapterPosition()),
                        view,getAdapterPosition());
            }
        }
    }


    public  interface OnEmptyItemClickListener {
        void onClick(TodoBean bean,int position);
    }

    public interface  OnMoreItemClickListener{
        void onClick(TodoBean bean,View view,int position);
    }

}
