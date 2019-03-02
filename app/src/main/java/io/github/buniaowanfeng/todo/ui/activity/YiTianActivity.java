package io.github.buniaowanfeng.todo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.database.dao.TodoDao;
import io.github.buniaowanfeng.event.ActivityEvent;
import io.github.buniaowanfeng.todo.data.model.TodoBean;
import io.github.buniaowanfeng.todo.presenter.TodoPresenter;
import io.github.buniaowanfeng.todo.ui.adapter.ToDoAdapter;
import io.github.buniaowanfeng.todo.ui.view.ITodoView;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.SpUtil;
import rx.Observable;

import static io.github.buniaowanfeng.util.DateUtil.day;

public class YiTianActivity extends AppCompatActivity implements ITodoView{

    private static final String TAG = "yitian";
    private Calendar calendar = Calendar.getInstance();
    private ArrayList<TodoBean> bean ;
    private  int currentDay = day(calendar.getTimeInMillis());
    @BindView(R.id.yitian_toolbar)
    Toolbar toolbar;

    @BindView(R.id.todo_swipe)
    SwipeRefreshLayout mSwipe;

    @BindView(R.id.todo_recycler)
    RecyclerView mTodoRecycler;

    @BindView(R.id.tv_empty)
    TextView mTvEmpty;

    @BindView(R.id.fab_add_todo)
    FloatingActionButton mFabAdd;

    private ToDoAdapter mAdapter;

    private TodoPresenter mPresenter;

    private ToDoAdapter.OnMoreItemClickListener onMoreItemClickListener =
            (bean, view, position) -> {
                PopupMenu menu = new PopupMenu(view.getContext(),view);
                menu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()){
                        case R.id.action_edit_item:
                            AddTodoActivity.startAddTodoActivity(view.getContext(),bean,position);
                            break;
                        case R.id.action_share_item:
                            ShareActivity.startShareActivity(view.getContext(),bean);

                            break;
                    }
                    return false;
                });
                menu.inflate(R.menu.todo_pop_menu);
                try {
                    Field field = menu.getClass().getDeclaredField("mPopup");
                    field.setAccessible(true);
                    MenuPopupHelper helper = (MenuPopupHelper) field.get(menu);
                    helper.setForceShowIcon(true);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                menu.show();
            };

    private ToDoAdapter.OnEmptyItemClickListener onEmptyItemClickListener =
            (bean, position) -> {
                LogUtil.d(TAG," position " + position);
                AddTodoActivity.startAddTodoActivity(mFabAdd.getContext(),bean,position);
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yi_tian);
        ButterKnife.bind(this);

        initToolBar();
        EventBus.getDefault().register(this);

        mPresenter = new TodoPresenter(this);

        initSwipe();
        initRecycler();
        bean = new ArrayList<>();
        mFabAdd.setOnClickListener(view -> AddTodoActivity
                .startAddTodoActivity(this,null,0));

        if (SpUtil.getInstance().getBoolean(SpUtil.KEY_IS_LOGIN)) {
//            mPresenter.getTodoFromCloud(currentDay);
            mPresenter.getTodoFromLocal(currentDay);
        }else {
            startLogin();
        }
    }

    private void initRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mTodoRecycler.setLayoutManager(manager);
        mAdapter = new ToDoAdapter();
        mAdapter.setOnEmptyItemClickListener(onEmptyItemClickListener);
        mAdapter.setOnMoreItemClickListener(onMoreItemClickListener);
        mTodoRecycler.setAdapter(mAdapter);
        shouldHideEmptyTv();

        mTodoRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int previousTotal = 0;
            boolean loading = true;
            int firstVisibleItem, visibleItemCount, totalItemCount;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = mTodoRecycler.getChildCount();
                totalItemCount = manager.getItemCount();
                firstVisibleItem = manager.findFirstVisibleItemPosition();

                if (loading){
                    if (totalItemCount > previousTotal){
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }

                if (!loading && (totalItemCount - visibleItemCount) <= firstVisibleItem ){
                    if (currentDay != DateUtil.day(System.currentTimeMillis())) {
                        LogUtil.d(TAG," refresh day " + day(calendar.getTimeInMillis()));
                        LogUtil.e(TAG," load more");
                        mPresenter.getTodoFromLocal(currentDay);
                        loading = true;
                    }
                }

                if (dy > 0){
                    mFabAdd.setVisibility(View.GONE);
                }else {
//                    loading = false;
                    mFabAdd.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void initSwipe() {
        int[] color ={R.color.material_red,R.color.material_purple};
        mSwipe.setColorSchemeResources(color);
        mSwipe.setOnRefreshListener(() -> {
                if (SpUtil.getInstance().getBoolean(SpUtil.KEY_IS_LOGIN)){
                    if (mAdapter.getLists().size() < 4){
                        calendar.add(Calendar.DATE,-1);
                        mPresenter.getTodoFromLocal(currentDay);
                        currentDay = day(calendar.getTimeInMillis());
                        LogUtil.d(TAG," refresh day " + currentDay );
                    }else {
                        LogUtil.d(TAG," up changed todo to cloud");
                        mPresenter.syncTodoToCloud();
                    }
                }else {
                    hideRefresh();
                    startLogin();
                }
            shouldHideEmptyTv();
        });
    }

    private void shouldHideEmptyTv() {
        if (mAdapter.getLists().size() == 0){
            mTvEmpty.setVisibility(View.VISIBLE);
        }else {
            mTvEmpty.setVisibility(View.GONE);
        }
    }

    private void initToolBar() {
        toolbar.setTitle(R.string.yitian);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_manager_account,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();;
                break;
            case R.id.logout:
                logOut();
                break;
            case R.id.action_idle_filter:
                setIdleFilter();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setIdleFilter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.set_idle_tile);
        long[] minute = new long[]{300000,600000,1200000,1800000,2400000,3000000,3600000,7200000};
        String[] arrays= new String[]{"5分钟","10分钟","20分钟","30分钟","40分钟","50分钟","1小时","2小时+"};
        builder.setItems(arrays,(dialogInterface, i)->{
            LogUtil.d(TAG,arrays[i] + minute[i] );
            SpUtil.getInstance().putLong(SpUtil.KEY_IDLE_TIME,minute[i]);
            mPresenter.filterData(bean);
        });
        builder.create().show();

    }

    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.logout_title);
        builder.setMessage(R.string.lgout_detail);
        builder.setPositiveButton(R.string.sure, (dialogInterface, i) -> {
            SpUtil spUtil = SpUtil.getInstance();
            spUtil.putString(SpUtil.KEY_TOKEN,"");
            spUtil.putBoolean(SpUtil.KEY_IS_LOGIN,false);
            spUtil.putBoolean(SpUtil.KEY_IS_SIGN,false);
            mAdapter.clear();
            Observable.just("clear tod db")
                    .doOnNext(info -> TodoDao.clear())
                    .subscribe();
        });
        builder.setNegativeButton(R.string.cancle, (dialogInterface, i) -> {

        });
        if (SpUtil.getInstance().getBoolean(SpUtil.KEY_IS_LOGIN)){
            builder.create().show();
        }else {
            startLogin();
        }
    }

    private void startLogin() {
        Snackbar.make(mSwipe,R.string.login_hint,Snackbar.LENGTH_LONG)
                .setAction(R.string.login, view -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                })
                .show();
        hideRefresh();
    }

    @Override
    public void newData(ArrayList<TodoBean> beans) {
        if (beans.size() > 0){
            int position = beans.size() +1;
            bean.addAll(beans);
            mAdapter.addNewData(beans);
            calendar.add(Calendar.DATE,-1);
            currentDay = day(calendar.getTimeInMillis());
//            mTodoRecycler.scrollToPosition(position);
            LogUtil.d(TAG,"current day new data " + currentDay);
        }else {
            showError(getString(R.string.no_more_data));
        }

        if (mAdapter.getLists().size() == 0){
            mTvEmpty.setVisibility(View.VISIBLE);
            mTvEmpty.setText(R.string.no_record);
        }else {
            mTvEmpty.setVisibility(View.GONE);
        }


    }

    @Override
    public void showError(String message) {
        Snackbar.make(mSwipe,message,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showAfterFilter(ArrayList<TodoBean> todo) {
        mAdapter.setNewData(todo);
        if (todo.size() > 0){
            mTvEmpty.setVisibility(View.GONE);
        }else {
            mTvEmpty.setVisibility(View.VISIBLE);
            mTvEmpty.setText(R.string.selete_anohter);
        }
    }

    @Override
    public void showRefresh() {
        mSwipe.postDelayed(() -> mSwipe.setRefreshing(true),50);
    }

    @Override
    public void hideRefresh() {
        mSwipe.postDelayed(() -> mSwipe.setRefreshing(false),500);
    }

    @Subscribe
    public void handleEvent(ActivityEvent<TodoBean> event){
        TodoBean bean = event.data;
        int position = event.position;
        switch (event.type){
            case ActivityEvent.EDIT_EMPTY_TODO_EVENT:
                LogUtil.d(TAG," edit exists todo");
                mAdapter.getLists().remove(position);
                mAdapter.getLists().add(position,bean);
                mAdapter.notifyItemChanged(position);
                mPresenter.todoChanged(bean);
                break;
            case ActivityEvent.NEW_TODO_EVENT:
                LogUtil.d(TAG," create new todo ");
                mAdapter.getLists().add(position,bean);
                mAdapter.notifyItemChanged(position);
                LogUtil.d(TAG,"new todo " +bean.toString());
                mPresenter.addNewTodo(bean);
                break;
            case ActivityEvent.LOGIN_EVENT:
                LogUtil.d(TAG," login ");
                mPresenter.getTodoFromLocal(currentDay);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        calendar = null;
        super.onDestroy();
    }
}
