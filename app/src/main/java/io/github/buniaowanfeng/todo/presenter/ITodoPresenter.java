package io.github.buniaowanfeng.todo.presenter;

import java.util.ArrayList;

import io.github.buniaowanfeng.todo.data.model.TodoBean;

/**
 * Created by caofeng on 16-9-19.
 */
public interface ITodoPresenter {
    void getTodoFromLocal(int day);
    void getTodoFromCloud(int day);
    void syncTodoToCloud();
    void todoChanged(TodoBean bean);
    void addNewTodo(TodoBean bean);
    void filterData(ArrayList<TodoBean> been);
}
