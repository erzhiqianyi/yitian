package io.github.buniaowanfeng.todo.data.model;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by caofeng on 16-9-19.
 */
public class Tags {
    public ArrayList<Tag> tags;

    public static Tags fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,Tags.class);
    }

    public static String toJson(Tags tags){
        Gson gson = new Gson();
        return gson.toJson(tags,Tags.class);
    }
}
