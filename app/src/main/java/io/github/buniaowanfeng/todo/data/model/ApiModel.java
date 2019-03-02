package io.github.buniaowanfeng.todo.data.model;

/**
 * Created by caofeng on 16-9-19.
 */
public class ApiModel {
    public int code;
    public byte[] result;

    @Override
    public String toString() {
        return "ApiModel{" +
                "code=" + code +
                ", result=" + result.toString() +
                '}';
    }
}
