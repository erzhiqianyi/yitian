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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.buniaowanfeng.R;
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

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "sign";
    private ProgressDialog dialog;
    @BindView(R.id.input_name)
    EditText mEditName;

    @BindView(R.id.input_email)
    EditText mEditEmail;

    @BindView(R.id.input_password)
    EditText mEditPassword;

    @BindView(R.id.btn_signup)
    AppCompatButton mBtnSignup;

    @BindView(R.id.link_login)
    TextView mTvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        ButterKnife.bind(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("正在注册");
        mBtnSignup.setOnClickListener(view -> signUp());
        mTvLogin.setOnClickListener(view -> login());
    }

    private void login() {
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    private void signUp() {
        String name = mEditName.getText().toString();
        String password =mEditPassword.getText().toString();
        String email = mEditEmail.getText().toString();

        if (!checkUser(name,password,email)){
            mBtnSignup.setEnabled(true);
            return;
        }else {
            dialog.show();
            mBtnSignup.setEnabled(false);
            User user = new User();
            user.username = name;
            user.password = password;
            user.email = email;
            sign(user);
        }
    }

    private void sign(User user) {
        String json = User.toJson(user);
        byte[] bytes =  Code.encode(json,"123");
        ServiceFactory.createService(TodoApi.class)
                .createUser(bytes)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ApiModel>() {
                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                        mBtnSignup.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        mBtnSignup.setEnabled(true);
                        Snackbar.make(mTvLogin,R.string.service_error,Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ApiModel apiModel) {
                        checkSignResult(apiModel);
                    }
                });
    }

    private void checkSignResult(ApiModel signResult) {
        byte[] result = signResult.result;
        String message = Code.decode(result,"123");
        switch (signResult.code){
            case 100:
                LogUtil.d(TAG,"sign up success");
                User user = User.fromJson(message);
                signSuccess(user);
                break;
            case 401:
                LogUtil.d(TAG,message);
                Snackbar.make(mTvLogin,getString(R.string.sign_faild),Snackbar.LENGTH_SHORT).show();
                break;
            case 601:
                LogUtil.d(TAG,message);
                mEditName.setError(getString(R.string.name_empty));
                break;
            case 602:
                LogUtil.d(TAG,message);
                mEditPassword.setError(getString(R.string.password_empty));
                break;
            case 603:
                LogUtil.d(TAG,message);
                mEditEmail.setError(getString(R.string.email_empty));
                break;
            case 701:
                LogUtil.d(TAG,message);
                mEditName.setError(getString(R.string.name_exists));
                break;
            case 702:
                LogUtil.d(TAG,message);
                mEditEmail.setError(getString(R.string.email_exists));
                break;
        }
    }

    private void signSuccess(User user) {
        SpUtil spUtil = SpUtil.getInstance();
        spUtil.putBoolean(SpUtil.KEY_IS_SIGN,true);
        spUtil.putBoolean(SpUtil.KEY_IS_LOGIN,true);
        spUtil.putString(SpUtil.KEY_NAME,user.username);
        spUtil.putString(SpUtil.KEY_TOKEN,user.token);
        spUtil.putInt(SpUtil.KEY_USER_ID,user.userId);
        Toast.makeText(this,R.string.sign_success,Toast.LENGTH_LONG).show();
        finish();
    }

    private boolean checkUser(String name,String password,String email) {
        boolean isCorrectName,isCorrectPassword,isCorrectEmail;
        if (TextUtils.isEmpty(name) || name.length() < 3){
            mEditName.setError(getString(R.string.name_error));
            isCorrectName = false;
        }else {
            mEditName.setError(null);
            isCorrectName = true;
        }

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEditEmail.setError(getString(R.string.email_error));
            isCorrectEmail = false;
        }else {
            mEditName.setError(null);
            isCorrectEmail = true;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6){
            mEditPassword.setError(getString(R.string.password_format));
            isCorrectPassword =false;
        }else {
            mEditName.setError(null);
            isCorrectPassword =true;

        }
        boolean valid = isCorrectName && isCorrectEmail && isCorrectPassword;
        return valid;
    }

    @Override
    public void onBackPressed() {
        if (!SpUtil.getInstance().getBoolean(SpUtil.KEY_IS_SIGN)){
            Snackbar.make(mTvLogin,R.string.not_singup,Snackbar.LENGTH_LONG)
                    .setAction(R.string.cancle,view ->{
                        super.onBackPressed();
                    })
                    .show();
        }else {
            super.onBackPressed();
        }
    }
}