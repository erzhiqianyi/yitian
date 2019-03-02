package io.github.buniaowanfeng.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.ui.fragment.SettingFragment;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.setting_container,new SettingFragment())
                .commit();
    }

    public static void startActivity(Context context){
        context.startActivity(new Intent(context,SettingActivity.class));
    }
}
