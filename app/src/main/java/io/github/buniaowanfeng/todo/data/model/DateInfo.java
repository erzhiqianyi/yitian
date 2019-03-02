package io.github.buniaowanfeng.todo.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by caofeng on 16-9-19.
 */
public class DateInfo implements Parcelable {
    public DateInfo() {
    }

    /**
     * the date of the day
     */
    public String date;

    /**
     * day
     */
    public int day;

    /**
     * month of the day
     */
    public int month;

    /**
     * year of the day
     */
    public int year;


    protected DateInfo(Parcel in){
        date = in.readString();
        day = in.readInt();
        month = in.readInt();
        year = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeInt(day);
        parcel.writeInt(month);
        parcel.writeInt(year);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<DateInfo> CREATOR = new Parcelable.ClassLoaderCreator<DateInfo>() {
        @Override
        public DateInfo createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new DateInfo(parcel);
        }

        @Override
        public DateInfo createFromParcel(Parcel parcel) {
            return new DateInfo(parcel);
        }

        @Override
        public DateInfo[] newArray(int i) {
            return new DateInfo[0];
        }
    };


    @Override
    public String toString() {
        return "DateInfo{" +
                "date=" + date +
                ", day=" + day +
                ", month=" + month +
                ", year=" + year +
                '}';
    }
}
