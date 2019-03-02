package io.github.buniaowanfeng.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.ui.activity.AboutActivity;
import io.github.buniaowanfeng.util.Androids;
import io.github.buniaowanfeng.util.SpUtil;

/**
 * Created by caofeng on 16-7-26.
 */
public class SettingFragment extends Fragment{
    private static final String TAG = "settigfragment";

    private CardView checkPermission;
    private CardView checkVersion;
    private CardView about;

    private ProgressDialog dialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkPermission = (CardView) view.findViewById(R.id.check_permission);
        checkVersion = (CardView) view.findViewById(R.id.check_version);
        about = (CardView) view.findViewById(R.id.about);

        checkPermission.setOnClickListener(click -> checkPermision());
        checkVersion.setOnClickListener(click -> checkVersion());
        about.setOnClickListener(click -> about());
    }

    private void about() {
        getActivity().startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    private void checkVersion() {
        boolean newVersion = newVersion();
        if (newVersion){
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.new_version))
                    .setMessage(SpUtil.getInstance().getString(SpUtil.VERSION_DESC))
                    .setPositiveButton("去更新", (dialogInterface, i) -> {
                        String address = SpUtil.getInstance().getString(SpUtil.VERSION_URL);
                        if (TextUtils.isEmpty(address))
                            return;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(address));
                        startActivity(intent);
                    })
                    .setNegativeButton("残忍拒绝",(DialogInterface,i)->{
                        Toast.makeText(YiTian.mContext,"取消更新",Toast.LENGTH_LONG).show();
                    })
                    .show();
        }else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("oh.oh已经是最新版本了")
                    .setPositiveButton("知道了", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    })
                    .show();
        }
    }

    private boolean newVersion() {
        PackageManager packageManager = getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo("io.github.buniaowanfeng",0);
            if (SpUtil.getInstance().getInt(SpUtil.KEY_VERSION) > packageInfo.versionCode)
                return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void checkPermision() {
        boolean permision = false;
        if (Build.VERSION.SDK_INT >= 21){
            permision = Androids.hasPermissionForUsage();
        }else {
            permision = true;
        }

        if (permision){
            new AlertDialog.Builder(getActivity())
                    .setTitle("恭喜")
                    .setMessage("一天正常运行,享受美好的一天吧")
                    .setPositiveButton("知道了", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    }).show();
        }else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("设置")
                    .setMessage("oh,oh您的手机版本好高,请允许一天查看应用使用情况,一天就能正常工作了")
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
        }
    }

}
