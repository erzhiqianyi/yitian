package io.github.buniaowanfeng.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.database.dao.DailyInfoDao;
import io.github.buniaowanfeng.event.ClickEvent;
import io.github.buniaowanfeng.ui.adapter.AppCountPagerAdapter;
import io.github.buniaowanfeng.util.LogUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CountActivity extends AppCompatActivity implements  PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "CountActivity";
    private ViewPager countPager;
    private AppCountPagerAdapter adapter;
    private FloatingActionButton fab;
    private TextView mTvHint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);

        initView();
        initData();
    }

    private void initData() {
        Observable.just("get day")
            .map(info -> DailyInfoDao.getDays(0))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Subscriber<List<Integer>>() {
                @Override
                public void onCompleted() {
                    LogUtil.v(TAG,"completed");
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onNext(List<Integer> days) {
                    adapter.setDays(days);
                    if (adapter.getCount() == 0){
                        mTvHint.setVisibility(View.VISIBLE);
                    }else {
                        mTvHint.setVisibility(View.GONE);
                    }
                    countPager.setAdapter(adapter);
                    countPager.setCurrentItem(days.size());
                }
            });
    }

    private void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fbtn_count);
        countPager = (ViewPager) findViewById(R.id.count_pager);
        mTvHint = (TextView) findViewById(R.id.tv_hint);

        countPager.setOffscreenPageLimit(4);
        adapter = new AppCountPagerAdapter(getSupportFragmentManager());
        fab.setOnClickListener(view -> showPopUpMenu(view));
    }

    private void showPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.count);
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        popupMenu.show();
    }

    public static void startActivity(Context context){
        context.startActivity(new Intent(context,CountActivity.class));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        ClickEvent event = new ClickEvent();
        EventBus.getDefault().post(event);
        switch (item.getItemId()){
            case R.id.sort_by_time:
                event.type = ClickEvent.TYPE_TIME;
                break;
            case R.id.sort_by_times:
                event.type = ClickEvent.TYPE_TIMES;
                break;
        }
        EventBus.getDefault().post(event);

        return false;
    }
}
