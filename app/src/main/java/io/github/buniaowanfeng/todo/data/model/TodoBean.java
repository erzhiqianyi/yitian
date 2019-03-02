package io.github.buniaowanfeng.todo.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by caofeng on 16-9-19.
 */
public class TodoBean   implements Parcelable {

    public TodoBean() {

    }


    /**
     * haven't edit now
     */
    public static int EMPTY = 2;

    /**
     * edit
     */
    public static int DATA = 3;

    public int type;
    public DateInfo dateInfo;
    public TodoEmpty empty;
    public Todo data;
    public UserInfo userInfo;
    public long id;

    protected TodoBean(Parcel in){
        type = in.readInt();
        id = in.readLong();
        dateInfo = in.readParcelable(DateInfo.class.getClassLoader());
        empty = in.readParcelable(TodoEmpty.class.getClassLoader());
        data = in.readParcelable(Todo.class.getClassLoader());
        userInfo = in.readParcelable(UserInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeLong(id);
        parcel.writeParcelable(dateInfo,i);
        parcel.writeParcelable(empty,i);
        parcel.writeParcelable(data,i);
        parcel.writeParcelable(userInfo,i);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<TodoBean> CREATOR = new ClassLoaderCreator<TodoBean>() {
        @Override
        public TodoBean createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new TodoBean(parcel);
        }

        @Override
        public TodoBean createFromParcel(Parcel parcel) {
            return new TodoBean(parcel);
        }

        @Override
        public TodoBean[] newArray(int i) {
            return new TodoBean[0];
        }
    };

    @Override
    public String toString() {
        return "TodoBean{" +
                "type=" + type +
                "\n dateInfo=" + dateInfo +
                "\n empty=" + empty +
                "\n data=" + data +
                "\n userInfo=" + userInfo +
                ", id=" + id +
                '}';
    }
}
