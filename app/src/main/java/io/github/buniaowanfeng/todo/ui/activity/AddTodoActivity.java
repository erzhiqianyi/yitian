package io.github.buniaowanfeng.todo.ui.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.event.ActivityEvent;
import io.github.buniaowanfeng.todo.data.model.Tag;
import io.github.buniaowanfeng.todo.data.model.Tags;
import io.github.buniaowanfeng.todo.data.model.Todo;
import io.github.buniaowanfeng.todo.data.model.TodoBean;
import io.github.buniaowanfeng.util.DateUtil;
import io.github.buniaowanfeng.util.InitUtil;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.SpUtil;

public class AddTodoActivity extends AppCompatActivity {
    public static final String ARGS_BEAN = "bean";
    private static final String TAG = "add";
    private static final String ARGS_POSITION = "position";

    @BindView(R.id.edit_todo_toolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_done)
    TextView mTvDone;

    @BindView(R.id.tv_date)
    TextView mTvDate;

    @BindView(R.id.tv_start_time)
    TextView mTvStartTime;

    @BindView(R.id.tv_end_time)
    TextView mTvEndTime;

    @BindView(R.id.tv_add_tag)
    TextView mTvTag;

    @BindView(R.id.edit_desc)
    EditText mEditDesc;

    @BindView(R.id.add_location)
    TextView mTvLocation;

    @BindView(R.id.tv_permission)
    TextView mTvPermission;

    @BindView(R.id.tv_permission_level)
    TextView mTvPermissionLevel;

    private TodoBean bean;
    private int position;
    private boolean editTime;
    private boolean editData;
    private int eventType = ActivityEvent.EDIT_EMPTY_TODO_EVENT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        ButterKnife.bind(this);
        initToolBar();
        getArgs();
        initView();
    }

    private void initView() {
        if (bean.empty.startMinute < 10){
            mTvStartTime.setText(String.format(getString(R.string.start_format_one),
                    bean.empty.startHour, bean.empty.startMinute));
        }else {
            mTvStartTime.setText(String.format(getString(R.string.start_format),
                    bean.empty.startHour, bean.empty.startMinute));
        }

        if (bean.empty.endMinute < 10){
            mTvEndTime.setText(String.format(getString(R.string.end_format_one),
                    bean.empty.endHour, bean.empty.endMinute));
        }else {
            mTvEndTime.setText(String.format(getString(R.string.end_format),
                    bean.empty.endHour, bean.empty.endMinute));
        }
        mTvDate.setText(String.format(getString(R.string.date_format),
                bean.dateInfo.month, bean.dateInfo.day));


        if (!TextUtils.isEmpty(bean.data.tag)) {
            mTvTag.setText(bean.data.tag);
        }

        if (!TextUtils.isEmpty(bean.data.desc)) {
            mEditDesc.setText(bean.data.desc);
        }

        if (!TextUtils.isEmpty(bean.data.location)) {
            mTvLocation.setText(bean.data.location);
        }

        mTvPermissionLevel.setText(bean.userInfo.permissionLevel == 1 ? "公开" : "仅自己可见");
        setClickListener();
    }

    private void setClickListener() {
        mTvDate.setOnClickListener(view -> setDate());
        mTvStartTime.setOnClickListener(view -> setStartTime());
        mTvEndTime.setOnClickListener(view -> setEndTime());
        mTvTag.setOnClickListener(view -> setTag());
        mEditDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                bean.data.desc = mEditDesc.getText().toString();
                editData = true;
            }
        });
        mTvLocation.setOnClickListener(view -> setLocation());
        mTvPermission.setOnClickListener(view -> setPermission());
        mTvDone.setOnClickListener(view -> done());
    }

    private void done() {
        if ((editTime && editData) || editData) {
            int startTime = bean.empty.startHour * 60 + bean.empty.startMinute;
            int endTime = bean.empty.endHour * 60 + bean.empty.endMinute;
            bean.empty.usdTime = DateUtil.minuteToString(endTime - startTime);

            bean.type = TodoBean.DATA;
            String tag =  mTvTag.getText().toString();
            if (tag.equals("添加爱标签")){
                tag = null;
            }
            bean.data.tag = tag;

            String location =  mTvLocation.getText().toString();
            if (location.equals("添加地点")){
                location = null;
            }
            bean.data.location = location;

            String desc = mEditDesc.getText().toString();
            if (desc.equals("添加描述")){
                desc = null;
            }
            bean.data.desc = desc;
            if (eventType == ActivityEvent.NEW_TODO_EVENT){
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE,bean.empty.startMinute);
                calendar.set(Calendar.HOUR_OF_DAY,bean.empty.startHour);
                bean.id = calendar.getTimeInMillis();
            }
            ActivityEvent<TodoBean> event = new ActivityEvent<>();
            event.data = bean;
            event.position = position;
            event.type = eventType;
            EventBus.getDefault().post(event);
        }
        finish();
    }

    private void setPermission() {
        String[] arrays = {getString(R.string.one), getString(R.string.zero)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.tv_permission));
        builder.setItems(arrays, (dialogInterface, i) -> {
            bean.userInfo.permissionLevel = i;
            mTvPermissionLevel.setText(arrays[i]);
        });
        editData = true;
        builder.create().show();
    }

    private void setLocation() {
        String json = SpUtil.getInstance().getString(SpUtil.KEY_LOCATION);
        List<Tag> tags;
        if (TextUtils.isEmpty(json)) {
            tags = InitUtil.makeLocation();
        } else {
            tags = Tags.fromJson(json).tags;
        }

        String[] arrays = new String[tags.size()];
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = tags.get(i).value;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.location));
        builder.setItems(arrays, (dialogInterface, i) -> {
            if (i < arrays.length - 1) {
                bean.data.location = arrays[i];
                mTvLocation.setText(arrays[i]);
                editData = true;
            } else {
                showCustomDialog(mTvLocation, SpUtil.KEY_LOCATION);
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
        editData = true;
    }

    private void setTag() {
        String json = SpUtil.getInstance().getString(SpUtil.KEY_TAG);
        List<Tag> tags;
        if (TextUtils.isEmpty(json)) {
            tags = InitUtil.makeTag();
        } else {
            tags = Tags.fromJson(json).tags;
        }
        String[] arrays = new String[tags.size()];
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = tags.get(i).value;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Tags));
        builder.setItems(arrays, (dialogInterface, i) -> {
            if (i < arrays.length - 1) {
                bean.data.tag = arrays[i];
                mTvTag.setText(arrays[i]);
            } else {
                showCustomDialog(mTvTag, SpUtil.KEY_TAG);
            }
            editData = true;
        });
        Dialog dialog = builder.create();
        dialog.show();
        editData = true;
    }

    private void showCustomDialog(TextView view, String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.custom_title));
        EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
            String text = editText.getText().toString();
            if (TextUtils.isEmpty(text)) {

            } else {
                addTag(key, text);
                view.setText(text);
            }
        });

        builder.create().show();
    }

    private void addTag(String key, String value) {
        String json = SpUtil.getInstance().getString(key);
        Tags saved = Tags.fromJson(json);
        ArrayList<Tag> tags = saved.tags;
        Tag tag = new Tag();
        tag.id = tags.size();
        tag.value = value;
        tags.add(tags.size() - 1, tag);
        saved.tags = tags;
        SpUtil.getInstance().putString(key, Tags.toJson(saved));
    }

    private void setEndTime() {
        shouldChangeTime(2);
        editTime = true;
    }

    private void setStartTime() {
        shouldChangeTime(1);
        editTime = true;
    }

    private void setDate() {
        shouldChangeTime(0);
        editTime = true;
    }

    private void shouldChangeTime(int type) {
        if (bean.data.canChangeTime == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.edit_time);
            builder.setMessage(R.string.edit_time_detail);
            builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                return;
            });
            builder.setPositiveButton(R.string.edit, (dialogInterface, i) -> {
                editTime(type);
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            editTime(type);
        }
    }

    private void editTime(int type) {
        switch (type) {
            case 0:
                showDatePicker();
                break;
            case 1:
                showTimePicker(1);
                break;
            case 2:
                showTimePicker(2);
                break;
        }
    }

    private void showTimePicker(int type) {
        int hour = type == 1 ? bean.empty.startHour : bean.empty.endHour;
        int minute = type == 1 ? bean.empty.endMinute : bean.empty.endMinute;

        TimePickerDialog dialog = new TimePickerDialog(this, (timePicker, i, i1) -> {
            if (type == 1) {
                bean.empty.startHour = i;
                bean.empty.startMinute = i1;
                mTvStartTime.setText(String.format(getString(
                        R.string.start_format), i, i1));
            } else {
                bean.empty.endHour = i;
                bean.empty.endMinute = i1;
                if (bean.empty.endHour < bean.empty.startHour) {
                    Snackbar.make(mTvDate, getString(R.string.time_error), Snackbar.LENGTH_LONG)
                            .setAction(R.string.edit, view -> {
                                showTimePicker(type);
                            })
                            .show();
                } else {
                    mTvEndTime.setText(String.format(getString(R.string.end_format), i, i1));
                }
            }
        }, hour, minute, false);
        dialog.show();
    }

    private void showDatePicker() {
        LogUtil.d(TAG," year " + bean.dateInfo.year + " month " +bean.dateInfo.month + " day " + bean.dateInfo.day );
        DatePickerDialog dialog = new DatePickerDialog(this, (datePicker, i, i1, i2) -> {
            bean.dateInfo.year = i;
            bean.dateInfo.month = i1;
            bean.dateInfo.day = i2;
            LogUtil.d(TAG," year " + i + " month " + i1 + " day "+ i2);
            mTvDate.setText(String.format(getString(R.string.date_format), i1, i2));
        }, bean.dateInfo.year, bean.dateInfo.month, bean.dateInfo.day);
        dialog.show();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getArgs() {
        Intent intent = getIntent();
        bean = intent.getParcelableExtra(ARGS_BEAN);
        position = intent.getIntExtra(ARGS_POSITION, 0);
        LogUtil.d(TAG,"is null" +(bean == null));
        if (bean == null) {
            bean = new TodoBean();
            bean.id = 0;
            bean.type = TodoBean.DATA;
            InitUtil.makeTodoBean(bean);
        } else if (bean.data == null) {
            bean.data = new Todo();
        }
        if (bean.id == 0 ){
            eventType = ActivityEvent.NEW_TODO_EVENT;
        }

        LogUtil.d(TAG,bean.toString());

    }

    public static void startAddTodoActivity(Context context, TodoBean bean, int position) {
        Intent intent = new Intent(context, AddTodoActivity.class);
        intent.putExtra(ARGS_BEAN, bean);
        intent.putExtra(ARGS_POSITION, position);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(mTvPermissionLevel, R.string.not_save,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.cancel, view -> {
                    super.onBackPressed();
                })
                .show();

    }
}