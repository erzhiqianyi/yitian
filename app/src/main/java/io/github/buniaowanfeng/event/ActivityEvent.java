package io.github.buniaowanfeng.event;

/**
 * Created by caofeng on 16-9-20.
 */
public class ActivityEvent <T> {
    public static final int BACK_EVENT = 0;
    public static final int DONE_EVENT = 1;
    public static final int LOGIN_EVENT = 2;
    public static final int NEW_TODO_EVENT = 3;
    public static final int EDIT_EMPTY_TODO_EVENT = 4;

    public int type;
    public T data;
    public int position;

}
