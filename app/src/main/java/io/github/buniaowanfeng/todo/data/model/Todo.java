package io.github.buniaowanfeng.todo.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by caofeng on 16-9-19.
 */
public class Todo implements Parcelable {

    public Todo() {
    }

    /**
     * tag of the todo event
     */
    public String tag;

    /**
     * description of the todo event
     */
    public String desc;

    /**
     * locaiton of the todo event
     */
    public String location;

    /**
     * the event time can change or not,default is 0 presenter can't change
     */
    public int canChangeTime;

    protected Todo(Parcel in){
        tag = in.readString();
        desc = in.readString();
        location = in.readString();
        canChangeTime = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tag);
        parcel.writeString(desc);
        parcel.writeString(location);
        parcel.writeInt(canChangeTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<Todo> CREATOR = new ClassLoaderCreator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new Todo(parcel);
        }

        @Override
        public Todo createFromParcel(Parcel parcel) {
            return new Todo(parcel);
        }

        @Override
        public Todo[] newArray(int i) {
            return new Todo[0];
        }
    };

    @Override
    public String toString() {
        return "Todo{" +
                "tag='" + tag + '\'' +
                ", desc='" + desc + '\'' +
                ", location='" + location + '\'' +
                ", canChangeTime=" + canChangeTime +
                '}';
    }
    public boolean isNotNone(){
        if (!TextUtils.isEmpty(tag) || !TextUtils.isEmpty(desc) || !TextUtils.isEmpty(location)){
            return true;
        }else {
            return false;
        }
    }
}
