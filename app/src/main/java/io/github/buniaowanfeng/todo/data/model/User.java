package io.github.buniaowanfeng.todo.data.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by caofeng on 16-9-19.
 */
public class User {
    @SerializedName("id")
    public int userId;
    public String username;
    public String password;
    public String email;
    public String token;
    @SerializedName("device_name")
    public String deviceName;
    @SerializedName("device_id")
    public String deviceId;

    public static String toJson(User user){
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    public static User fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,User.class);
    }
    @Override
    public String toString() {
        return "User{" +
                "user id = " + userId +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
