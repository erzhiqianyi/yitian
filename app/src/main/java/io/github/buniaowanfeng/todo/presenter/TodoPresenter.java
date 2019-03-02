package io.github.buniaowanfeng.todo.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.database.dao.TodoDao;
import io.github.buniaowanfeng.todo.data.cloud.ServiceFactory;
import io.github.buniaowanfeng.todo.data.cloud.TodoApi;
import io.github.buniaowanfeng.todo.data.model.ApiModel;
import io.github.buniaowanfeng.todo.data.model.DateInfo;
import io.github.buniaowanfeng.todo.data.model.Todo;
import io.github.buniaowanfeng.todo.data.model.TodoBean;
import io.github.buniaowanfeng.todo.data.model.TodoEmpty;
import io.github.buniaowanfeng.todo.data.model.TodoSave;
import io.github.buniaowanfeng.todo.data.model.Todos;
import io.github.buniaowanfeng.todo.data.model.UserInfo;
import io.github.buniaowanfeng.todo.ui.view.ITodoView;
import io.github.buniaowanfeng.util.Code;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.SpUtil;
import io.github.buniaowanfeng.util.TodoBeanCompare;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by caofeng on 16-9-19.
 */
public class TodoPresenter  implements ITodoPresenter{
    private static final String TAG = "todopresenter";

    private ITodoView mView;

    public TodoPresenter(ITodoView mView) {
        this.mView = mView;
    }

    public TodoPresenter() {
        this(null);
    }

    private void showTodos(ArrayList<TodoSave> todoSaves) {
        Observable.just(todoSaves)
                .map(todos -> toTodoBeans(todos))
                .doOnNext(bean -> Collections.sort(bean,new TodoBeanCompare()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ArrayList<TodoBean>>() {
                    @Override
                    public void onCompleted() {
                        mView.hideRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.hideRefresh();
                        LogUtil.d(TAG," show data error ");
                    }

                    @Override
                    public void onNext(ArrayList<TodoBean> todoBeen) {
                        mView.newData(todoBeen);
                    }
                });
    }

    private ArrayList<TodoBean> toTodoBeans(ArrayList<TodoSave> todoSaves) {
        ArrayList<TodoBean> beans = new ArrayList<>();
        for (int i = 0 ; i < todoSaves.size() ; i++){
            TodoBean bean = new TodoBean();

            bean.type = TodoBean.EMPTY;
            bean.id = todoSaves.get(i).id;
            long startTime = todoSaves.get(i).startTime;
            long endTime = todoSaves.get(i).endTime;
            int userId = todoSaves.get(i).userId;
            int level = todoSaves.get(i).level;

            UserInfo userInfo = new UserInfo();
            userInfo.userId = userId;
            userInfo.permissionLevel = level;

            bean.userInfo = userInfo;

            DateInfo dateInfo = new DateInfo();
            Date startDate = new Date(startTime);
            Date endDate = new Date(endTime);

            dateInfo.year = DateUtil.getYear(startDate);
            dateInfo.month = DateUtil.getMonth(startDate);
            dateInfo.day = startDate.getDate();
            dateInfo.date = new String[]{
                    "周日","周一","周二","周三","周四","周五","周六"}[startDate.getDay()];
            bean.dateInfo =dateInfo;

            TodoEmpty empty = new TodoEmpty();
            empty.startHour = startDate.getHours();
            empty.startMinute = startDate.getMinutes();
            empty.endHour = endDate.getHours();
            empty.endMinute = endDate.getMinutes();
            empty.usdTime =DateUtil.minuteToString((empty.endHour* 60
                    + empty.endMinute) - ( empty.startHour*60 +
                    empty.startMinute));
            if (empty.endHour <= 12 && empty.endHour >= 6){
                empty.iconId = R.drawable.ic_timeofday_morning;
            }

            if (empty.endHour <=18 && empty.endHour >= 12){
                empty.iconId = R.drawable.ic_timeofday_afternoon;
            }

            if (empty.endHour <= 24 && empty.endHour >= 18 || empty.endHour < 6){
                empty.iconId = R.drawable.ic_timeofday_night;
            }

            bean.empty = empty;

            Todo todo = new Todo();
            todo.tag = todoSaves.get(i).tag;
            todo.desc = todoSaves.get(i).description;
            todo.location = todoSaves.get(i).location;

            if (todo.isNotNone()){
                bean.data = todo;
                bean.type = TodoBean.DATA;
            }
            beans.add(bean);
        }
        return beans;
    }

    @Override
    public void getTodoFromLocal(int day) {
        LogUtil.d(TAG," get todo from local " + day);
        if (day == DateUtil.day(System.currentTimeMillis())){
            mView.showRefresh();
        }

        Observable.just(day)
                .map(info -> TodoDao.getTodo(day))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ArrayList<TodoSave>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG," get todo from local complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.hideRefresh();
                        LogUtil.e(TAG," get todo from local failed");
                    }

                    @Override
                    public void onNext(ArrayList<TodoSave> todoSaves) {
                        if (todoSaves.size() == 0){
                            getTodoFromCloud(day);
                        }else {
                            showTodos(todoSaves);
                        }
                    }
                })
            ;

    }

    @Override
    public void getTodoFromCloud(int day) {
        LogUtil.d(TAG," get todo from cloud " + day);
        if (day == DateUtil.day(System.currentTimeMillis())){
            mView.showRefresh();
        }
        Observable.just(day)
                .flatMap(info -> ServiceFactory.createAuthService(TodoApi.class)
                        .getTodoByDay(day))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ApiModel>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG," get data from cloud success");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.hideRefresh();
                        LogUtil.e(TAG,"获取数据失败");
//                        mView.showError(YiTian.mContext.getString(R.string.get_data_error));
                    }

                    @Override
                    public void onNext(ApiModel apiModel) {
                        dealGetResult(apiModel);
                    }
                });

    }

    @Override
    public void syncTodoToCloud() {
        mView.showRefresh();
        Observable.just("sync changed todo ")
                .map(info -> TodoDao.getChangedTodo())
                .map(todo -> Code.encode(Todos.toJson(new Todos(todo)),"123"))
                .flatMap(bytes -> ServiceFactory.createAuthService(TodoApi.class).updateTodo(bytes))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ApiModel>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG," up changed todo to cloud complete");
                        mView.hideRefresh();
                        mView.showError(YiTian.mContext.getString(R.string.sync_success));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError(YiTian.mContext.getString(R.string.sync_error));
                        mView.hideRefresh();
                        LogUtil.e(TAG," up changed todo to cloud failed");
                    }

                    @Override
                    public void onNext(ApiModel apiModel) {
                        dealUpdateResult(apiModel);
                    }
                });


    }

    private void dealUpdateResult(ApiModel apiModel) {
        switch (apiModel.code){
            case 401:
                LogUtil.e(TAG," up changed todo to cloud failed");
                mView.showError(YiTian.mContext.getString(R.string.sync_error));
                break;
            case 201:
                LogUtil.d(TAG," up changed todo to cloud success");
                mView.hideRefresh();
                Observable.just(" up changed todo to cloud sucess")
                        .doOnNext(info -> TodoDao.updateUnSynced(1,2))
                        .observeOn(Schedulers.io())
                        .subscribe();
                break;
        }
    }

    @Override
    public void todoChanged(TodoBean bean) {
        LogUtil.d(TAG," changed " + bean.toString());
        Observable.just(bean)
                .map(todoBean ->toTodoSaves(todoBean))
                .doOnNext(todo -> TodoDao.todoChanged(todo))
                .map(todo -> {
                    ArrayList<TodoSave> todoSaves = new ArrayList<TodoSave>();
                    todoSaves.add(todo);
                    return new Todos(todoSaves);})
                .map(todos -> Code.encode(Todos.toJson(todos),"123"))
                .flatMap(bytes -> ServiceFactory.createAuthService(TodoApi.class).updateTodo(bytes))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ApiModel>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG,"update changed todo complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogUtil.e(TAG," update todo failed");

                    }

                    @Override
                    public void onNext(ApiModel apiModel) {
                        dealUpdateResult(apiModel);
                    }
                });
    }

    @Override
    public void addNewTodo(TodoBean bean) {
        Observable.just(bean)
                .map(todoBean -> toTodoSaves(todoBean))
                .doOnNext(todo -> TodoDao.insertTodo(todo))
                .map(todo -> {
                    ArrayList<TodoSave> todoSaves = new ArrayList<TodoSave>();
                    todoSaves.add(todo);
                    return new Todos(todoSaves);})
                .map(todos -> Code.encode(Todos.toJson(todos),"123"))
                .flatMap(bytes -> ServiceFactory.createAuthService(TodoApi.class).addTodos(bytes))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ApiModel>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG,"add todo to cloud complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogUtil.e(TAG," add todo to cloud failed");
                    }

                    @Override
                    public void onNext(ApiModel apiModel) {
                        dealAddResult(apiModel);
                    }
                });
    }

    private void dealAddResult(ApiModel apiModel) {
        switch (apiModel.code){
            case 401:
                LogUtil.e(TAG," up changed todo to cloud failed");
                break;
            case 201:
                LogUtil.d(TAG," up changed todo to cloud success");
                Observable.just(" up changed todo to cloud sucess")
                        .doOnNext(info -> TodoDao.updateUnSynced(1,0))
                        .observeOn(Schedulers.io())
                        .subscribe();
                break;
        }
    }

    @Override
    public void filterData(ArrayList<TodoBean> beans) {
        long timeFiler = SpUtil.getInstance().getLong(SpUtil.KEY_IDLE_TIME);
        LogUtil.d(TAG," filter " + timeFiler);
        ArrayList<TodoBean> newBean = new ArrayList<>();
        Observable.from(beans)
                .filter(bean ->( ( bean.empty.endHour * 60 + bean.empty.endMinute) -
                                (bean.empty.startHour * 60 + bean.empty.startMinute)) * 60 * 1000 >= timeFiler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<TodoBean>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG,"complete filter ");
                        LogUtil.d(TAG,"size " + newBean.size());
                        Collections.sort(newBean,new TodoBeanCompare());
                        for (TodoBean bean : newBean)
                            LogUtil.d(TAG,bean.toString());
                        mView.showAfterFilter(newBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(TodoBean bean) {
                        LogUtil.d(TAG,"after filter "+bean.toString());
                        newBean.add(bean);
                    }
                });
    }

    private TodoSave toTodoSaves(TodoBean bean){
        TodoSave todo = new TodoSave();
        LogUtil.d(TAG,bean.toString());

        todo.id = bean.id;
        todo.tag = bean.data.tag;
        todo.description = bean.data.desc;
        todo.location = bean.data.location;
        todo.level = bean.userInfo.permissionLevel;

        LogUtil.d(TAG," todo save year " + bean.dateInfo.year
                + " month " + bean.dateInfo.month
                + " day " + bean.dateInfo.day);

        todo.startTime = bean.id;

        todo.day = DateUtil.day(todo.startTime);
        LogUtil.d(TAG, " day " + todo.day);
        todo.endTime = todo.startTime +
                ((bean.empty.endHour * 60 + bean.empty.endMinute) -
                (bean.empty.startHour * 60 + bean.empty.startMinute))
                        * 60000;
        todo.userId = bean.userInfo.userId;
        LogUtil.d(TAG,todo.toString());
        return todo;
    }

//    private void upToCloud(ArrayList<TodoSave> todos) {
//        Observable.just(todos)
//                .map(info ->new Todos(info))
//                .map(todo -> Todos.toJson(todo))
//                .map(json -> Code.encode(json,"123"))
//                .flatMap(bytes -> ServiceFactory.createAuthService(TodoApi.class).addTodos(bytes))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Subscriber<ApiModel>() {
//                    @Override
//                    public void onCompleted() {
//                        LogUtil.d(TAG," up date to cloud success");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        mView.showError(YiTian.mContext.getString(R.string.sync_error));
//                        mView.hideRefresh();
//                        LogUtil.e(TAG,"up date to cloud failed");
//                    }
//
//                    @Override
//                    public void onNext(ApiModel apiModel) {
//                        dealGetResult(apiModel);
//                    }
//                });
//
//    }

    private void dealGetResult(ApiModel apiModel) {
        switch (apiModel.code){
            case 401:
                mView.hideRefresh();
//                mView.showError(YiTian.mContext.getString(R.string.get_data_error));
                LogUtil.e(TAG,"up date to cloud failed");
                break;
            case 201:
                String json = Code.decode(apiModel.result,"123");
                LogUtil.d(TAG,json);
                ArrayList<TodoSave> todos = Todos.fromJson(json).todos;
                LogUtil.d(TAG,"size " + todos.size());
                for (TodoSave todo : todos){
                    LogUtil.d(TAG,todo.toString());
                }
                showTodos(todos);
                break;
        }
    }

}
