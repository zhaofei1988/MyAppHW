package everlinkintl.com.myappwh.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import com.vise.log.ViseLog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import everlinkintl.com.myappwh.common.Cons;
import everlinkintl.com.myappwh.http.API;
import everlinkintl.com.myappwh.http.Okhttp;

public class UpService extends Service {
    private boolean mReceiverTag = false;   //广播接受者标识
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
             switch (msg.what){
                 case 1:
                     String st= (String)msg.obj;
                     posts(st);
                     break;
             }
        }
    };

    private void posts(String url){
        ViseLog.e("url");
        Map<String,String> map=new HashMap<>();
        API.addFile1(map, url, getApplicationContext(), new Okhttp.FileBack() {
            @Override
            public void onFalia(int code, String errst) {

            }

            @Override
            public void fileOnsuccess(Object object) {
                ViseLog.e(object.toString());
                File file =new File(url);
                if(file.exists()){
                    file.delete();
                }
            }
        });
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ViseLog.e("broadcastReceiver");
            if (intent != null && intent.getAction() != null) {
                String action = intent.getAction();
                if (Cons.RECEIVER_ACTION_SERVER1.equals(action)) {
                    String locationResult = intent.getStringExtra(Cons.RECEIVER_PUT_RSULT_URL);
                    if (null != locationResult) {
                        Message message = handler.obtainMessage(1);     // Message
                        message.obj=locationResult;
                        handler.sendMessage(message);
                    }
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ViseLog.e("server");
        if (!mReceiverTag) {     //在注册广播接受者的时候 判断是否已被注册,避免重复多次注册广播
            IntentFilter filter = new IntentFilter();
            mReceiverTag = true;
            filter.addAction(Cons.RECEIVER_ACTION_SERVER1);
            registerReceiver(broadcastReceiver, filter);
        }
        flags = START_STICKY;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_app1";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "恒联仓库", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("恒联仓库")
                    .setContentText("恒联仓库").build();
            startForeground(1, notification);
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        if (mReceiverTag) {   //判断广播是否注册
            mReceiverTag = false;   //Tag值 赋值为false 表示该广播已被注销
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

}
