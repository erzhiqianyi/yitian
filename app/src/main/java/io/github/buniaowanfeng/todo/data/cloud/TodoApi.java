package io.github.buniaowanfeng.todo.data.cloud;

import io.github.buniaowanfeng.data.VersionInfo;
import io.github.buniaowanfeng.todo.data.model.ApiModel;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by caofeng on 16-9-19.
 */
public interface TodoApi {
    @POST("users")
    Observable<ApiModel> createUser(@Body byte[] user);

    @POST("user")
    Observable<ApiModel> getUser(@Body byte[] user);

    @POST("todos")
    Observable<ApiModel> addTodos(@Body byte[] todos);

    @GET("todos/all")
    Observable<ApiModel> getTodos();

    @GET("todos/{day}")
    Observable<ApiModel> getTodoByDay(@Path("day") int day);

    @PUT("todos")
    Observable<ApiModel> updateTodo(@Body byte[] todos);

    @DELETE("todos")
    Observable<ApiModel> deleteTodo();

    @GET("version")
    Observable<VersionInfo> getVersionInfo();

}
