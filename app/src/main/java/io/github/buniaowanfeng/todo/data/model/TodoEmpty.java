package io.github.buniaowanfeng.todo.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by caofeng on 16-9-19.
 */
public class TodoEmpty implements Parcelable {

    public TodoEmpty() {
    }

    /**
     * event start hour
     */
    public int startHour;

    /**
     * event start minute
     */
    public int startMinute;

    /**
     * event end hour
     */
    public int endHour;

    /**
     * event end minute
     */
    public int endMinute;

    /**
     * used time on the event
     */
    public String usdTime;

    /**
     * icon to present time
     */
    public int iconId;

    protected TodoEmpty(Parcel in){
        startHour = in.readInt();
        startMinute = in.readInt();
        endHour = in.readInt();
        endMinute = in.readInt();
        usdTime = in.readString();
        iconId = in.readInt();
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(startHour);
        parcel.writeInt(startMinute);
        parcel.writeInt(endHour);
        parcel.writeInt(endMinute);
        parcel.writeString(usdTime);
        parcel.writeInt(iconId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TodoEmpty> CREATOR = new ClassLoaderCreator<TodoEmpty>() {
        @Override
        public TodoEmpty createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new TodoEmpty(parcel);
        }

        @Override
        public TodoEmpty createFromParcel(Parcel parcel) {
            return new TodoEmpty(parcel);
        }

        @Override
        public TodoEmpty[] newArray(int i) {
            return new TodoEmpty[0];
        }
    };

    @Override
    public String toString() {
        return "TodoEmpty{" +
                "startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", endHour=" + endHour +
                ", endMinute=" + endMinute +
                ", usdTime='" + usdTime + '\'' +
                ", iconId=" + iconId +
                '}';
    }
}
