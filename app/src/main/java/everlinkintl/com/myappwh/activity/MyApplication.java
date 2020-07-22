package everlinkintl.com.myappwh.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.util.Log;

import com.vise.log.ViseLog;
import com.vise.log.inner.LogcatTree;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import everlinkintl.com.myappwh.common.Cons;
import everlinkintl.com.myappwh.common.CrashHandler;
import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    //activity集合，用来同意管理activity，方便一键退出
    public List<Activity> activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
        ViseLog.getLogConfig()
                .configAllowLog(Cons.IS_LOG_SHOW)//是否输出日志
                .configShowBorders(true)//是否排版显示
                .configTagPrefix(Cons.VISE_LOG)//设置标签前缀
                .configFormatTag(Cons.FORMAT_TAG)//个性化设置标签，默认显示包名
                .configLevel(Log.VERBOSE);//设置日志最小输出级别，默认Log.VERBOSE
        ViseLog.plant(new LogcatTree());//添加打印日志信息到Logcat的树
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);

    }

    /**
     * 添加activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }
    /**
     * 移除某个activity
     *
     * @param activity
     */
    public void finishSingle(Activity activity) {
        activityList.remove(activity);
        if (!activity.isFinishing()) {
            activity.finish();
        }
    }

    /**
     * 移除所有的activity(通常会在一键退出的时候使用)
     */
    public void finishAll() {
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        am.killBackgroundProcesses("everlinkintl.com.myapp");
    }

    /**
     * 清楚掉除了自己之外的activity
     * 这个方法一般会用在，如果activity任务栈中已经存在了几个activty了，但是你想回到最底层的activty并且销毁掉之前所有的activty
     * 比如说 现在有4个activity A B C D,A是第一个activty，然后进行activity的跳转 A ->B ->C ->D,然后你想从D->跳到A，并且销毁掉B和C，那么可以在进入到A后，在A中调用此方法
     * 举个实际点的例子：你想做一个切换账号的功能，一般此功能都是在 个人中心 ->设置 ->退出当前登录，那么如果你直接进入到登录界面进行登录不做任何处理的话，之前的activty没有finish掉
     * ，gc（垃圾回收机）也不会回收这些内存，这样很容易引起内存泄漏（亲身经历）
     *
     * @param activity
     */
    public void finishAllButNotMe(Activity activity) {
        for (Activity ac : activityList
                ) {
            if (ac == activity) {
                //如果是当前的activty，不做任何处理
            } else if (!ac.isFinishing()) {
                //如果不是，finish掉
                ac.finish();
            }
        }
    }

}
