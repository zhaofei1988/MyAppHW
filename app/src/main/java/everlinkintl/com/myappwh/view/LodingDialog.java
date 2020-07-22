package everlinkintl.com.myappwh.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import everlinkintl.com.myappwh.R;

public class LodingDialog {
    private static AlertDialog customAlert;
    public static AlertDialog dialogIndex(Activity context){
        AlertDialog.Builder setDeBugDialog = new AlertDialog.Builder(context);
        //获取界面
        View dialogView = LayoutInflater.from(context).inflate(R.layout.loding_layout, null);

        //将界面填充到AlertDiaLog容器
        setDeBugDialog.setView(dialogView);
        //初始化控件
        //取消点击外部消失弹窗
        setDeBugDialog.setCancelable(false);
        //创建AlertDiaLog
        setDeBugDialog.create();
        customAlert = setDeBugDialog.show();
        customAlert.getWindow().setDimAmount(0.2f);
        customAlert.getWindow().setBackgroundDrawableResource(R.color.c00000000);
        customAlert.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
                {
                    customAlert.dismiss();
                }
                return false;
            }
        });
        return customAlert;
    }
}
