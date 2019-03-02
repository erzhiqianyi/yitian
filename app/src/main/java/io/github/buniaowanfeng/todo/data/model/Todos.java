package io.github.buniaowanfeng.todo.data.model;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by caofeng on 16-9-19.
 */
public class Todos {
    public ArrayList<TodoSave> todos;

    public Todos() {
    }

    public Todos(ArrayList<TodoSave> todos) {
        this.todos = todos;
    }

    public static Todos fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,Todos.class);
    }

    public static String toJson(Todos todos){
        Gson gson = new Gson();
        return gson.toJson(todos,Todos.class);
    }
}
