package io.github.buniaowanfeng.todo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.todo.data.model.TodoBean;
import io.github.buniaowanfeng.util.ImageUtil;
import io.github.buniaowanfeng.util.LogUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShareActivity extends AppCompatActivity {

    private static final String ARGS_TODO = "todo";
    private static final String TAG = "shareactivity";
    private TodoBean todo;

    @BindView(R.id.share_toolbar)
    Toolbar toolbar;

    @BindView(R.id.card_share_todo)
    CardView mCardShare;

    @BindView(R.id.tv_share_tag)
    TextView mTvTag;

    @BindView(R.id.tv_share_desc)
    TextView mTvDesc;

    @BindView(R.id.tv_share_detail)
    TextView mTvDetail;

    @BindView(R.id.btn_share_share)
    Button mBtnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        todo = getIntent().getParcelableExtra(ARGS_TODO);
        initToolBar();
        initView();
        mBtnShare.setOnClickListener(view -> share("test"));
    }

    private void initView() {
        if (todo == null){
            Toast.makeText(this,R.string.empty_share,Toast.LENGTH_SHORT).show();
        }else {

            String tag = todo.data.tag;
            if (TextUtils.isEmpty(tag) || tag.equals("null")){
                tag = "";
            }
            mTvTag.setText(String.format(getString(R.string.share_tag),tag,todo.empty.usdTime));
            String location = todo.data.location;
            if (TextUtils.isEmpty(location) || location.equals("null")){
                location = "";
            }

            int month = todo.dateInfo.month;
            int day = todo.dateInfo.day;
            int hour = todo.empty.startHour;
            int minute = todo.empty.startMinute;
            if(minute < 10){
                mTvDetail.setText(String.format(getString(R.string.share_detail),
                        location,month,day,hour,minute));
            }else {
                mTvDetail.setText(String.format(getString(R.string.share_detail_one),
                        location,month,day,hour,minute));
            }

            String desc = todo.data.desc;
            if (TextUtils.isEmpty(desc) || desc.equals("null")){
                desc = "";
            }

            mTvDesc.setText(desc);
        }
    }

    private void share(String fileName) {
        Toast.makeText(this,R.string.making_share,Toast.LENGTH_SHORT).show();
        LogUtil.d(TAG,"share");
        Observable.just(mCardShare)
                .doOnNext(view -> {
                    view.setDrawingCacheEnabled(true);
                    view.buildDrawingCache();})
                .map(view -> {
                    Bitmap bitmap = view.getDrawingCache();
                    bitmap = Bitmap.createBitmap(bitmap,0,0,view.getWidth(),view.getHeight());
                    return bitmap;})
                .map(bitmap -> ImageUtil.saveShare(bitmap,fileName))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Uri>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG," shared");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        LogUtil.e(TAG,"生成分享图偏失败");
                        showError();
                    }

                    @Override
                    public void onNext(Uri uri) {
                        LogUtil.d(TAG,uri.toString());
                        share(uri);
                    }
                });
    }

    private void share(Uri uri) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        startActivity(intent);

    }

    private void showError() {
        Toast.makeText(this,R.string.make_pic_error,Toast.LENGTH_SHORT).show();
    }


    private void initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static void startShareActivity(Context context , TodoBean todo){
        if ( todo == null){
            Toast.makeText(context,R.string.empty_share,Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(context,ShareActivity.class);
            intent.putExtra(ARGS_TODO,todo);
            context.startActivity(intent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}
