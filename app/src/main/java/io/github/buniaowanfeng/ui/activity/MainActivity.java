package io.github.buniaowanfeng.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.database.dao.DailyInfoDao;
import io.github.buniaowanfeng.service.YiTianService;
import io.github.buniaowanfeng.todo.ui.activity.YiTianActivity;
import io.github.buniaowanfeng.ui.adapter.AppUsaegDetailPagerAdapter;
import io.github.buniaowanfeng.util.Androids;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.SpUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class
MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "main activity";
    private ViewPager mDetailViewpager;
    private AppUsaegDetailPagerAdapter adapter;
    private TextView mTvHint;
    private FloatingActionButton fbt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initData() {
        Observable.just("get data")
                .map(info -> DailyInfoDao.getDays(0))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.v(TAG,"get data competed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mTvHint.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        LogUtil.d(TAG," set adpter " + integers.size() );
                        if (integers.size() == 0){
                            mTvHint.setVisibility(View.VISIBLE);
                        }else {
                            adapter.setDays(integers);
                            mDetailViewpager.setAdapter(adapter);
                            mDetailViewpager.setCurrentItem(integers.size());
                            mTvHint.setVisibility(View.GONE);
                        }
                    }
                });
        startService();
    }

    private void initView() {
        fbt = (FloatingActionButton) findViewById(R.id.btn_yitian);
        fbt.setOnClickListener(btn -> showPopUpMenu(btn));
        mDetailViewpager = (ViewPager) findViewById(R.id.daily_detail_pager);
        mDetailViewpager.setOffscreenPageLimit(4);
        adapter = new AppUsaegDetailPagerAdapter(getSupportFragmentManager());
        mTvHint = (TextView) findViewById(R.id.tv_hint);

    }


    private void showPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.ations);

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

    /**
     * after finish get data then start TiTianService
     */
    private void startService(){
        if (Androids.isYiTianServiceRunning(this))
            return;
        if (Build.VERSION.SDK_INT >= 21 && !Androids.hasPermissionForUsage()){
            new AlertDialog.Builder(this)
                    .setTitle("设置")
                    .setMessage("您的手机版本太高了， 需要设置一下才能用，请允许一天查看应用使用情况")
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivityForResult(intent,0);
                            startActivity(intent);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }else {
            YiTianService.startService(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        YiTianService.startService(this);
        initData();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()){
            case R.id.count:
                CountActivity.startActivity(this);
                break;
            case R.id.share:
                share(item);
                break;
            case R.id.trend:
                TrendActivity.startActivity(this,TrendActivity.WHOLE);
                break;
            case R.id.buniaowanfeng:
                startActivity(new Intent(this, YiTianActivity.class));
                break;
            case R.id.setting:
                SettingActivity.startActivity(this);
                break;
        }
        return true;
    }

    private void share(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String url = SpUtil.getInstance().getString(SpUtil.VERSION_URL);
        String shareText = getString(R.string.share_desc)+url;
        intent.putExtra(Intent.EXTRA_TEXT,shareText);
        startActivity(intent);
    }

    public static void startActivity(Context context){
        context.startActivity(new Intent(context,MainActivity.class));
    }
}
