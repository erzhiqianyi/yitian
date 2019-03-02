package io.github.buniaowanfeng.util;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.buniaowanfeng.YiTian;

/**
 * Created by caofeng on 16-9-8.
 */
public class SpUtil {
    public static final String KEY_THEME = "theme";
    public static final String NAME = "setting";
    public static final String KEY_CLEAR_CAHCHE = "clear";
    public static final String KEY_IS_LOGIN = "login";
    public static final String KEY_IS_SIGN = "sign";
    public static final String KEY_NAME = "name";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_IS_NEW_VERSION = "new_version";
    public static final String KEY_DAILY_GOAL = "daily_goal";
    public static final String KEY_URL = "url";
    public static final String KEY_USER_ID = "userid";
    public static final String KEY_TAG = "tag ";
    public static final String KEY_LOCATION = "location";
    public static final java.lang.String KEY_QUERY_ID = "last_id";
    public static final String KEY_LAST_TIME_ID_TEMP = "last_id_temp";
    public static final String KEY_IDLE_TIME = "idle_time";
    public static final String KEY_FIRST_USE_TODO = "first_get_todo";
    public static final String KEY_IS_SYNC = "sync";
    public static final String KEY_VERSION = "version";
    public static final String VERSION_URL = "version_url";
    public static final String VERSION_DESC = "version_desc";

    private SharedPreferences mSp;

    private SpUtil(){
        mSp = YiTian.mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public long getLong(String key) {
        return mSp.getLong(key,0);
    }

    public void putLong(String key, long value) {
        mSp.edit().putLong(key,value).apply();
    }


    private static class SpHolder{
        private static SpUtil spUtil = new SpUtil();
    }

    public static SpUtil getInstance(){
        return SpHolder.spUtil;
    }

    public int getInt(String key){
        return mSp.getInt(key,0);
    }

    public int getInt(String key,int defaulValue){
        return mSp.getInt(key,defaulValue);
    }

    public void putInt(String key ,int value){
        mSp.edit().putInt(key,value).apply();
    }

    public boolean getBoolean(String key){
        return mSp.getBoolean(key,false);
    }

    public void putBoolean(String key,boolean value){
        mSp.edit().putBoolean(key,value).apply();
    }

    public void putString(String key, String username) {
        mSp.edit().putString(key,username).apply();
    }

    public String getString(String key){
        return mSp.getString(key,"");
    }

}
