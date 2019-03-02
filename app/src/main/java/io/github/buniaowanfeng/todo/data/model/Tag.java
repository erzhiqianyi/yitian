package io.github.buniaowanfeng.todo.data.model;

/**
 * Created by caofeng on 16-9-19.
 */
public class Tag {
    public int id;
    public String value;

    @Override
    public String toString() {
        return "Tags{" +
                "id='" + id + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
