package io.github.buniaowanfeng.todo.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.event.ActivityEvent;
import io.github.buniaowanfeng.todo.data.cloud.ServiceFactory;
import io.github.buniaowanfeng.todo.data.cloud.TodoApi;
import io.github.buniaowanfeng.todo.data.model.ApiModel;
import io.github.buniaowanfeng.todo.data.model.User;
import io.github.buniaowanfeng.util.Code;
import io.github.buniaowanfeng.util.LogUtil;
import io.github.buniaowanfeng.util.SpUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "login";
    @BindView(R.id.input_email)
    EditText mEmailInput;

    @BindView(R.id.input_password)
    EditText mPasswordInput;

    @BindView(R.id.btn_login)
    AppCompatButton mBtnLogin;

    @BindView(R.id.link_signup)
    TextView mTvSingupLink;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.logining));
        mBtnLogin.setOnClickListener(view -> login());
        mTvSingupLink.setOnClickListener(view -> signUp());
    }

    private void signUp() {
        startActivity(new Intent(this,SignupActivity.class));
    }

    private void login() {
        String userName = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();
        if (!checkInput(userName,password)){
            mBtnLogin.setEnabled(true);
        }else{
            dialog.show();
            mBtnLogin.setEnabled(false);
            User user = new User();
            user.username = userName;
            user.password = password;
            login(user);
        }
    }

    private void login(User user) {
        String json = User.toJson(user);
        byte[] bytes = Code.encode(json,"123");
        ServiceFactory.createService(TodoApi.class)
                .getUser(bytes)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ApiModel>() {
                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                        mBtnLogin.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        mBtnLogin.setEnabled(true);
                        Snackbar.make(mTvSingupLink,R.string.service_error,Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ApiModel apiModel) {
                        checkLoginResult(apiModel);
                    }
                });
    }

    private void checkLoginResult(ApiModel loginResult) {
        byte[] result = loginResult.result;
        String message = Code.decode(result,"123");
        LogUtil.e(TAG,"code " + loginResult.code + " message " + message);
        switch (loginResult.code){
            case 101:
                LogUtil.d(TAG,"sign up success");
                User user = User.fromJson(message);
                LogUtil.e(TAG,user.toString());
                loginSuccess(user);
                break;
            case 401:
                Snackbar.make(mTvSingupLink,getString(R.string.sign_faild),Snackbar.LENGTH_SHORT).show();
                break;
            case 601:
                mEmailInput.setError(getString(R.string.name_empty));
                break;
            case 602:
                mPasswordInput.setError(getString(R.string.password_empty));
                break;
            case 603:
                mPasswordInput.setError(getString(R.string.password_error));
                break;
            case 801:
                mEmailInput.setError(getString(R.string.name_not_exists));
                break;
        }
    }

    private void loginSuccess(User user) {
        SpUtil spUtil = SpUtil.getInstance();
        spUtil.putBoolean(SpUtil.KEY_IS_SIGN,true);
        spUtil.putBoolean(SpUtil.KEY_IS_LOGIN,true);
        spUtil.putString(SpUtil.KEY_NAME,user.username);
        spUtil.putString(SpUtil.KEY_TOKEN,user.token);
        spUtil.putInt(spUtil.KEY_USER_ID,user.userId);
        LogUtil.e(TAG,user.username + user.userId);
        SpUtil.getInstance().putLong(SpUtil.KEY_IDLE_TIME,300000);
        ActivityEvent<String> event = new ActivityEvent<String>();
        event.type = ActivityEvent.LOGIN_EVENT;
        EventBus.getDefault().post(event);
        finish();
        Toast.makeText(this,R.string.login_success,Toast.LENGTH_LONG).show();

    }
    public boolean checkInput(String username,String password){
        boolean isNameCorrect,isPasswordCorrect;
        if (TextUtils.isEmpty(username)){
            mEmailInput.setError(getString(R.string.name_empty));
            isNameCorrect = false;
        }else {
            mEmailInput.setError(null);
            isNameCorrect = true;
        }

        if (TextUtils.isEmpty(password)){
            mPasswordInput.setError(getString(R.string.password_empty));
            isPasswordCorrect = false;
        }else {
            mPasswordInput.setError(null);
            isPasswordCorrect = true;
        }

        return  isNameCorrect && isPasswordCorrect;
    }

    @Override
    public void onBackPressed() {
        if (!SpUtil.getInstance().getBoolean(SpUtil.KEY_IS_LOGIN)){
            Snackbar.make(mTvSingupLink,R.string.not_login_back,Snackbar.LENGTH_LONG)
                    .setAction(R.string.cancle,view ->{
                        super.onBackPressed();
                    })
                    .show();
        }else {
            super.onBackPressed();
        }

    }
}
