package io.github.buniaowanfeng.todo.ui.view;

import android.net.Uri;

import java.util.ArrayList;

import io.github.buniaowanfeng.todo.data.model.TodoBean;

/**
 * Created by caofeng on 16-9-19.
 */
public interface ITodoView  extends ISwipeView{
    void newData(ArrayList<TodoBean> beans);
    void showError(String message);
    void showAfterFilter(ArrayList<TodoBean> todo);
}
