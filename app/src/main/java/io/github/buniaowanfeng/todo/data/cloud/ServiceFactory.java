package io.github.buniaowanfeng.todo.data.cloud;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.github.buniaowanfeng.util.SpUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by caofeng on 16-9-19.
 */
public class ServiceFactory {
    private static final long TIMEOUT_CONNECT = 5 * 1000;
    private static final String BASE_URL = "http://45.78.39.95/yitian/api/v1.0/";
    //        private static final String BASE_URL = "http://127.0.0.1:5000/api/v1.0/";
    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_CONNECT, TimeUnit.MILLISECONDS)
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(
                            HttpLoggingInterceptor.Level.BODY));

    private static Retrofit.Builder builder = new Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());


    public static <S> S createService(Class<S> serviceClass){
        return  createService(serviceClass,null);
    }

    public static <S> S createAuthService( Class<S> serviceClass){
        String token = SpUtil.getInstance().getString(SpUtil.KEY_TOKEN);

        //        String token = "eyJhbGciOiJIUzI1NiIsImV4cCI6MTUwNTgwMDMxNCwiaWF0IjoxNDc0MjY0MzE0fQ.eyJpZCI6bnVsbH0.4lTqzNK-ExJEqiws1uppedv1I7nDGlTqa-xok4gFUCw";
        return createService(serviceClass,token);
    }
    private static <S> S createService(
            Class<S> serviceClass,String token){
        if (!TextUtils.isEmpty(token)){
            String password = "unused";
            String credentials = token +":"+ password;

            String basic = "Basic " + Base64.encodeToString(credentials.getBytes(),Base64.NO_WRAP);

            Log.d("basic",basic);

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", basic)
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            });
        }
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
