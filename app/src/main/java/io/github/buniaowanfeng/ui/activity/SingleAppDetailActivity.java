package io.github.buniaowanfeng.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.data.SingleDetail;
import io.github.buniaowanfeng.presententer.SingleDetailPresenter;
import io.github.buniaowanfeng.ui.adapter.SingleDetailAdapter;
import io.github.buniaowanfeng.view.ISingleDetailActivity;
import io.github.buniaowanfeng.view.ISingleDetailPresenter;

public class SingleAppDetailActivity extends AppCompatActivity implements View.OnClickListener ,ISingleDetailActivity {
    private static final String TAG = "single activity";
    public static final String WHOLE = "whole";
    private static final String PACKAGE = "package";

    private String packageName;

    private ImageView mImageIcon;
    private TextView mTvName;
    private TextView mTvCount;
    private TextView mTvTotalTime;
    private TextView mTvTotalTimes;
    private TextView mTvToday;
    private RelativeLayout head;
    private RecyclerView singleDetailRecycler;
    private SingleDetailAdapter adapter;
    private ProgressBar mProgress;

    private ISingleDetailPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_app_detail);
        packageName = getIntent().getStringExtra(PACKAGE);

        initView();
        initData();
    }

    private void initData() {
        presenter.getSingleDetail(packageName);
    }

    private void initView() {
        head = (RelativeLayout) findViewById(R.id.head);
        mImageIcon = (ImageView) findViewById(R.id.detail_icon);
        mTvName = (TextView) findViewById(R.id.detail_name);
        mTvCount = (TextView) findViewById(R.id.tv_detail_total_count);
        mTvTotalTime = (TextView) findViewById(R.id.tv_detail_total_time);
        mTvTotalTimes = (TextView) findViewById(R.id.tv_detail_total_times);
        mTvToday = (TextView) findViewById(R.id.tv_detail_today);
        singleDetailRecycler = (RecyclerView) findViewById(R.id.single_detail_recycler);
        singleDetailRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SingleDetailAdapter();
        head.setOnClickListener(this);
        mProgress = (ProgressBar) findViewById(R.id.load_progress);

        presenter = new SingleDetailPresenter(this);
    }

    public static void startActivity(Context context,String packageName){
        Intent intent = new Intent(context,SingleAppDetailActivity.class);
        intent.putExtra(PACKAGE,packageName);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this,"选择应用",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setHeadMessage(SingleDetail head) {
        if (head.packageName.equals("whole")){
            Glide.with(this)
                .load(R.drawable.ic_phone)
                .into(mImageIcon) ;
        }else {
            Glide.with(this)
                    .load(new File(YiTian.mIconPath,head.packageName+".png"))
                    .into(mImageIcon);
        }
        mTvName.setText(head.appName);
        mTvCount.setText(head.countDay);
        mTvTotalTime.setText(head.totalTime);
        mTvTotalTimes.setText(head.totalTimes);
        mTvToday.setText(head.today);

    }

    @Override
    public void setAdater(List<SingleDetail> datas) {
        adapter.setDatas(datas);
        singleDetailRecycler.setAdapter(adapter);
    }

    @Override
    public void showRefresh() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRefresh() {
        mProgress.setVisibility(View.GONE);
    }

}
