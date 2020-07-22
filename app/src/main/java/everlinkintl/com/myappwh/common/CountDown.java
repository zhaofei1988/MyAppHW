package everlinkintl.com.myappwh.common;

import android.os.Handler;
import android.os.Message;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindString;
import everlinkintl.com.myappwh.R;

public class CountDown {
    private int count = 60;
    private boolean isCodeBtnCLick = true;
    private Timer timer;
    private TimerTask task;
    private Button mView;
    @BindString(R.string.second)
    String second;
    @BindString(R.string.get_code)
    String getCode;
     public CountDown(Button view){
         this.mView = view;
     }
    public  void countDown(final Back back) {
        /*定时发送数据*/
        if (timer == null) {
            timer = new Timer();
        }
        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);
                    back.back(isCodeBtnCLick);
                }
            };
        }
        timer.schedule(task, 0, 1000);
    }
    /*释放资源*/
    public void releaseSocket() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }
    Handler handler = new Handler() {
        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (count == 0) {
                        isCodeBtnCLick = true;
                        releaseSocket();
                        count =60;
                        mView.setText(getCode);
                    } else {
                        isCodeBtnCLick = false;
                        mView.setText(count + second);
                    }
                    count--;
                    break;

            }
        }
    };
    public interface Back{
        void back(boolean b);
    }
}
