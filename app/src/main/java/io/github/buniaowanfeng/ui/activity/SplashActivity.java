package io.github.buniaowanfeng.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import java.util.concurrent.TimeUnit;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.data.VersionInfo;
import io.github.buniaowanfeng.database.dao.TodoDao;
import io.github.buniaowanfeng.todo.data.cloud.ServiceFactory;
import io.github.buniaowanfeng.todo.data.cloud.TodoApi;
import io.github.buniaowanfeng.todo.data.model.ApiModel;
import io.github.buniaowanfeng.todo.data.model.Todos;
import io.github.buniaowanfeng.util.Code;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.NetWorkUtils;
import io.github.buniaowanfeng.util.SpUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_splash, null);
        setContentView(view);

        //start animation
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(500);
        view.startAnimation(aa);

        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                LogUtil.e(TAG," start animation ");
                if (NetWorkUtils.isNetworkConnected())
                    syncTodo();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Observable.just(1000)
                        .delay(1000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> startMain());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void syncTodo() {
        Observable.just("get new version info")
                .flatMap(info -> ServiceFactory.createService(TodoApi.class).getVersionInfo())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<VersionInfo>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG," get version info complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogUtil.e(TAG," get version info failed");
                    }

                    @Override
                    public void onNext(VersionInfo versionInfo) {
                        dealGetVersionResult(versionInfo);
                    }
                });

        if (NetWorkUtils.isNetworkConnected() &&
                SpUtil.getInstance().getBoolean(SpUtil.KEY_IS_LOGIN)){
            Observable.just("add todo to cloud ")
                    .map(info -> TodoDao.getUnSyncedTodo())
                    .filter(todo -> todo.size() > 0)
                    .map(todo -> Code.encode(Todos.toJson(new Todos(todo)),"123"))
                    .flatMap(bytes -> ServiceFactory.createAuthService(TodoApi.class).addTodos(bytes))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<ApiModel>() {
                        @Override
                        public void onCompleted() {
                            LogUtil.e(TAG," up todo to cloud complete");
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(ApiModel apiModel) {
                            dealAddResult(apiModel);
                        }
                    });

            Observable.just(" update changed todo to cloud")
                    .map(info -> TodoDao.getChangedTodo())
                    .filter(todo -> todo.size() > 0)
                    .map(todo -> Code.encode(Todos.toJson(new Todos(todo)),"123"))
                    .flatMap(bytes -> ServiceFactory.createAuthService(TodoApi.class).updateTodo(bytes))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<ApiModel>() {
                        @Override
                        public void onCompleted() {
                            LogUtil.d(TAG,"update changed todo to cloud complete");
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            LogUtil.e(TAG," update changed todo to cloud failed");
                        }

                        @Override
                        public void onNext(ApiModel apiModel) {
                            dealUpdateResult(apiModel);
                            LogUtil.d(TAG,"code " + apiModel.code + " result" +Code.decode(apiModel.result,"123"));
                        }
                    });
        }else {
            LogUtil.d(TAG," net word error or not login");
        }
    }

    private void dealGetVersionResult(VersionInfo versionInfo) {
        LogUtil.d(TAG, versionInfo.toString());
        if (versionInfo != null && versionInfo.name.equals("yitian")){
            SpUtil spUtil = SpUtil.getInstance();
            spUtil.putInt(SpUtil.KEY_VERSION,versionInfo.version);
            spUtil.putString(SpUtil.VERSION_URL,versionInfo.url);
            spUtil.putString(SpUtil.VERSION_DESC,versionInfo.desc);
        }
    }

    private void dealUpdateResult(ApiModel apiModel) {
        switch (apiModel.code){
            case 201:
                LogUtil.d(TAG," up changed todo to cloud success");
                Observable.just(" synced ")
                        .doOnNext(info -> TodoDao.updateUnSynced(1,2))
                        .subscribeOn(Schedulers.io())
                        .subscribe(s -> {
                            SpUtil.getInstance().putBoolean(SpUtil.KEY_IS_SYNC,true);
                        });
                break;
            case 401:
                LogUtil.e(TAG," up changed todo to cloud failed ");
                SpUtil.getInstance().putBoolean(SpUtil.KEY_IS_SYNC,false);
                break;
        }
    }

    private void dealAddResult(ApiModel apiModel) {
        switch (apiModel.code){
            case 201:
                LogUtil.d(TAG," up todo to cloud success");
                Observable.just(" synced ")
                        .doOnNext(info -> TodoDao.updateUnSynced(1,0))
                        .subscribeOn(Schedulers.io())
                        .subscribe(s -> {
                            SpUtil.getInstance().putBoolean(SpUtil.KEY_IS_SYNC,true);
                        });
                break;
            case 401:
                LogUtil.e(TAG," up todo to cloud failed ");
                SpUtil.getInstance().putBoolean(SpUtil.KEY_IS_SYNC,false);
                break;
        }
    }

    /**
     * start main activity
     */
    private void startMain() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
