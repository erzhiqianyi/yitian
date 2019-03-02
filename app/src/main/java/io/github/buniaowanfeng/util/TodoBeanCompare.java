package io.github.buniaowanfeng.util;

import java.util.Comparator;

import io.github.buniaowanfeng.todo.data.model.TodoBean;

/**
 * Created by caofeng on 16-9-25.
 */

public class TodoBeanCompare implements Comparator<TodoBean> {
    @Override
    public int compare(TodoBean o1, TodoBean o2) {
        return (int) (o2.id - o1.id);
    }
}
