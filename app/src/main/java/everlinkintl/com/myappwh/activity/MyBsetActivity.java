package everlinkintl.com.myappwh.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import everlinkintl.com.myappwh.R;
import everlinkintl.com.myappwh.common.Cons;
import everlinkintl.com.myappwh.common.Tools;

public abstract class MyBsetActivity extends AppCompatActivity {
    protected Unbinder mRootUnBinder;
    private boolean mReceiverTag = false;   //广播接受者标识
    public Typeface iconfont;

    private TextView toolbarBreak;
    private TextView toolbarTitle;
    private TextView toolbarRigthTv;
    private TextView toolbarSearchTv;
    private LinearLayout toolbarSearchLy;
    public EditText editTextSearch;
    ActivityManager activityManager = null;
   public Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Cons.RECEIVER_ACTION)) {
                String locationResult = intent.getStringExtra(Cons.RECEIVER_PUT_RSULT);
                if (null != locationResult && !locationResult.trim().equals("")) {
                    setData(locationResult);
                }

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int layId = getContentLayoutId();
        setContentView(layId);
        initToolbar();
        initWidows();
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
        }
        // 竖屏设置
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 得到界面Id并设置到Activity界面中
     Cons.sp =getApplicationContext().getSharedPreferences(Cons.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        initWidget();

        startService();
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:


                break;
        }
        return super.dispatchTouchEvent(ev);
    }




    private void initWidows() {
        Window window = getWindow();
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.parseColor("#50000000"));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    protected abstract int getContentLayoutId();

    protected abstract void setData(String string);

    private int getStatusBarHeight() {
        int result = 20;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    protected void initToolbar() {
        iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        toolbar = findViewById(R.id.toolbar);

        if (!Tools.isEmpty(toolbar)) {
            toolbar.setPadding(0,getStatusBarHeight()+15,0,0);
            toolbar.setTitle("");
            toolbar.setTitleTextColor(Color.WHITE);
        }
        toolbarBreak = findViewById(R.id.toolbar_break_tv);
        toolbarTitle = findViewById(R.id.toolbar_title_tv);
        toolbarRigthTv = findViewById(R.id.toolbar_rigth_tv);
        toolbarSearchTv = findViewById(R.id.toolbar_search_tv);
        TextView toolbarSearchIcon = findViewById(R.id.toolbar_search_icon);
        toolbarSearchLy = findViewById(R.id.toolbar_search_ly);
        editTextSearch = findViewById(R.id.toolbar_search_et);
        if (!Tools.isEmpty(toolbarSearchLy)) {
            toolbarSearchIcon.setTypeface(iconfont);
        }
        if (!Tools.isEmpty(toolbarSearchTv)) {
            toolbarSearchTv.setTypeface(iconfont);
        }
        if (!Tools.isEmpty(toolbarBreak)) {
            toolbarBreak.setTypeface(iconfont);
            toolbarBreak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    /**
     * 设置title
     *
     * @param title ：title
     */
    protected void setTitleName(String title) {
        if (!Tools.isEmpty(toolbarTitle)) {
            toolbarTitle.setText(title);
        }
    }
    protected void setToolbar(int letf,int top ,int right,int bottom) {
        toolbar.setPadding(0,top,0,0);
    }
    protected void setTitleGone() {
        if (!Tools.isEmpty(toolbarTitle)) {
            toolbarTitle.setVisibility(View.GONE);
        }
    }

    protected void setTitleRigthTvShow(String title, View.OnClickListener onClickListener) {
        if (!Tools.isEmpty(toolbarRigthTv)) {
            toolbarRigthTv.setVisibility(View.VISIBLE);
            toolbarRigthTv.setText(title);
            toolbarRigthTv.setOnClickListener(onClickListener);
        }
    }

    protected void setTitleRigthTvShow(String title) {
        if (!Tools.isEmpty(toolbarRigthTv)) {
            toolbarRigthTv.setVisibility(View.VISIBLE);
            toolbarRigthTv.setText(title);
        }
    }

    protected void setTitleSearchTvShow(View.OnClickListener onClickListener) {
        if (!Tools.isEmpty(toolbarSearchTv)) {
            toolbarSearchTv.setVisibility(View.VISIBLE);
            toolbarSearchTv.setOnClickListener(onClickListener);
        }
    }
    protected void setTitleSearchTvShow(String st,View.OnClickListener onClickListener) {
        if (!Tools.isEmpty(toolbarSearchTv)) {
            toolbarSearchTv.setVisibility(View.VISIBLE);
            toolbarSearchTv.setText(st);
            toolbarSearchTv.setOnClickListener(onClickListener);
        }
    }
    protected void setTitleSearchShow() {
        if (!Tools.isEmpty(toolbarSearchLy)) {
            toolbarSearchLy.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置title
     */
    protected void setGoneBreak() {
        if (!Tools.isEmpty(toolbarBreak)) {
            toolbarBreak.setVisibility(View.GONE);
        }
    }

    /**
     * 发送sendBroadcast 到service
     *
     * @param st
     */


    /**
     * 初始化控件
     */
    protected void initWidget() {
        mRootUnBinder = ButterKnife.bind(this);
        ((MyApplication) getApplication()).addActivity(this);
    }

    /**
     * 初始化Service
     */
    protected void startService() {
        //在注册广播接受者的时候 判断是否已被注册,避免重复多次注册广播
        if (!mReceiverTag) {
            IntentFilter filter = new IntentFilter();
            mReceiverTag = true;
            filter.addAction(Cons.RECEIVER_ACTION);
            registerReceiver(broadcastReceiver1, filter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // 当点击界面导航返回时，Finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {   //判断广播是否注册
            mReceiverTag = false;   //Tag值 赋值为false 表示该广播已被注销
            unregisterReceiver(broadcastReceiver1);
        }
        if (mRootUnBinder != null) {
            mRootUnBinder.unbind();
        }
        //当被销毁后，将其移除
        ((MyApplication) getApplication()).finishSingle(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}