package io.github.buniaowanfeng.todo.data.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by caofeng on 16-9-19.
 */
public class TodoSave {

    @SerializedName("todo_id")
    public long id;

    @SerializedName("user_id")
    public int userId;

    @SerializedName("start_time")
    public long startTime;

    @SerializedName("end_time")
    public long endTime;

    public String tag;
    public String description;
    public String location;

    public int level;
    public int day;

    /**
     * default value is 0 menus not sync ,1 menus synced
     */
    public int sync;

    public static String toJson(Todo todo){
        Gson gson = new Gson();
        return gson.toJson(todo);
    }

    public static Todo fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,Todo.class);
    }

    @Override
    public String toString() {
        return "TodoSave{" +
                "id=" + id +
                ", userId=" + userId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", tag='" + tag + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", level=" + level +
                ", day " + day +
                ", sync " + sync +
                '}';
    }
    public boolean isNotNone(){
        if (!TextUtils.isEmpty(tag) || !TextUtils.isEmpty(description) || !TextUtils.isEmpty(location)){
            return true;
        }else {
            return false;
        }
    }
}
