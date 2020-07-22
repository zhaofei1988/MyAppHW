package everlinkintl.com.myappwh.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import everlinkintl.com.myappwh.R;
import everlinkintl.com.myappwh.activity.user.LoginActivity;
import everlinkintl.com.myappwh.common.Cons;
import everlinkintl.com.myappwh.common.FileRead;
import everlinkintl.com.myappwh.common.PermissionsUtils;
import everlinkintl.com.myappwh.common.SharedPreferencesUtil;
import everlinkintl.com.myappwh.common.Tools;
import everlinkintl.com.myappwh.http.API;
import everlinkintl.com.myappwh.http.Okhttp;
import everlinkintl.com.myappwh.datatemplate.Version;
import everlinkintl.com.myappwh.activity.storekeeper.KeeperActivity;

public class StartActivirty extends AppCompatActivity {
    Thread thread;
    boolean is = true;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 竖屏设置
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.start_layout);
        Cons.sp =getApplicationContext().getSharedPreferences(Cons.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    Object org = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGNAME, "");
                    if (is||Tools.isEmpty(org)) {
                        SharedPreferencesUtil.clearItem(Cons.EVERLINKINT_LOGIN_SP);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Tools tools =new Tools();
                        tools.startService(StartActivirty.this);
                        Intent intent = new Intent(getApplicationContext(), KeeperActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        PermissionsUtils.getInstance().chekPermissions(this, Cons.PERMS_WRITE, permissionsResult);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addDataScheme(getPackageName());
        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String localPkgName = context.getPackageName();//取得MyReceiver所在的App的包名
            Uri data = intent.getData();
            String installedPkgName = data.getSchemeSpecificPart();
            //取得安装的Apk的包名，只在该app覆盖安装后自启动
            if (action.equals(Intent.ACTION_PACKAGE_ADDED) && installedPkgName.equals(localPkgName)) {
                checkoutToken();
            }
        }
    };

    private void checkoutToken() {
        Object loginData = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_SP, "");

        if (Tools.isEmpty(loginData)) {
            is = true;
            star();
        } else {
            getToken((String) loginData);
        }
    }

    private void star() {
        getVersion();
    }

    private void getVersion() {
        Okhttp.getFile(Cons.downUll, this, new Okhttp.DownLoadBack() {
            @Override
            public void onFalia(int code, String errst) {
                thread.start();
            }

            @Override
            public void downLoadInProgress(float progress, long total) {

            }

            @Override
            public void downLoadOnsuccess(File file, String pas) {
                FileRead fileRead = new FileRead();
                String fileurl = fileRead.loadFromSDFile(file);
                Gson gson = new Gson();
                Version version = gson.fromJson(fileurl, Version.class);
                if (Integer.valueOf(version.getVersionCode()) > Tools.getLocalVersion(getApplicationContext())) {
                    getApk(version.getDownUrl());
                } else {
                    thread.start();
                }
            }
        });
    }

    boolean isFirst = true;
    ProgressDialog dialog = null;

    private void getApk(String url) {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle("提示");
        dialog.setMessage("恒联仓库应用下载进度");
        Okhttp.getFile(url, this, new Okhttp.DownLoadBack() {
            @Override
            public void onFalia(int code, String errst) {
                thread.start();
            }

            @Override
            public void downLoadInProgress(float progress, long total) {
                int a2 = (int) (total * progress);
                Message msg = Message.obtain();
                msg.what = 1;
                msg.arg1 = (int) total / 1024;
                msg.arg2 = a2 / 1024;
                handler.sendMessage(msg);
            }

            @Override
            public void downLoadOnsuccess(File file, String pas) {
                dialog.dismiss();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //判读版本是否在7.0以上新的启动方法
                        Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        startActivity(install);
                    } else {
                        //以前的启动方法
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.parse("file://" + pas), "application/vnd.android.package-archive");
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    Handler handler = new Handler() {
        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            // 根据不同线程发送过来的消息，执行不同的UI操作
            // 根据 Message对象的what属性 标识不同的消息
            switch (msg.what) {
                // 定位过来的信息 MapLocation
                case 1:
                    if (isFirst) {
                        isFirst = false;
                        dialog.show();
                    }
                    dialog.setMax(msg.arg1 / 1024);
                    dialog.setProgress(msg.arg2 / 1024);
                    dialog.setProgressNumberFormat("%1d Mb /%2d Mb");
                    break;

            }
        }
    };

    private void getToken(String token) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        API.checkoutToken(map, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {
                is = true;
                star();
            }

            @Override
            public void onsuccess(String object) {
                is = false;
                star();
            }
        });
    }

    //创建监听权限的接口对象
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
            checkoutToken();
        }

        @Override
        public void forbitPermissons() {
            checkoutToken();
        }
    };

    /**
     * 重写onRequestPermissionsResult，用于接受请求结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果传递EasyPermission库处理
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }

        super.onDestroy();
    }
}
