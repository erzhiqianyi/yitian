package io.github.buniaowanfeng.todo.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by caofeng on 16-9-19.
 */
public class UserInfo implements Parcelable {

    public UserInfo() {
    }

    /**
     * user id
     */
    public int userId;

    /**
     * the level of people can read this todo event
     */
    public int permissionLevel;

    /**
     * user name
     */
    public String userName;

    /**
     * the path of the user icon
     */
    public String userIcon;


    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", permissionLevel=" + permissionLevel +
                ", userName='" + userName + '\'' +
                ", userIcon='" + userIcon + '\'' +
                '}';
    }

    protected UserInfo(Parcel in){
        userId = in.readInt();
        permissionLevel = in.readInt();
        userName = in.readString();
        userIcon = in.readString();
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(userId);
        parcel.writeInt(permissionLevel);
        parcel.writeString(userName);
        parcel.writeString(userIcon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInfo> CREATOR = new ClassLoaderCreator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new UserInfo(parcel);
        }

        @Override
        public UserInfo createFromParcel(Parcel parcel) {
            return new UserInfo(parcel);
        }

        @Override
        public UserInfo[] newArray(int i) {
            return new UserInfo[0];
        }
    };

}
