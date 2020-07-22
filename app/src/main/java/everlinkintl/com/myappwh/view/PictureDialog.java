package everlinkintl.com.myappwh.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import everlinkintl.com.myappwh.activity.MyBsetActivity;
import everlinkintl.com.myappwh.activity.storekeeper.KeeperActivity;
import everlinkintl.com.myappwh.activity.storekeeper.PhotographActivity;

public class PictureDialog {
    private MyBsetActivity mActivity;
    private Context mContext;
    public PictureDialog(Activity activity, Context context){
        this.mActivity = (MyBsetActivity) activity;
        this.mContext = context;
    }
    public PictureDialog(Context context){
        this.mContext = context;
    }

    public void dialogListV(String[] provinces){
        AlertDialog.Builder bu=   new AlertDialog.Builder(mContext);
        bu.setTitle("选择");
        bu .setItems(provinces, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PhotographActivity photographActivity =(PhotographActivity)mContext;
                photographActivity.TextViewItem(provinces[which]);
            }
        });
        bu.setNegativeButton("重 拍", null);
        bu.create();
        bu.show();

    }
    public void dialogList1(String[] provinces){
        AlertDialog.Builder bu=   new AlertDialog.Builder(mContext);
        bu.setTitle("必须选择角色");
        bu .setItems(provinces, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeeperActivity loginActivity =(KeeperActivity)mContext;
                loginActivity.back(which);
            }
        });
        bu.create();
        bu.show();
    }
    public void diaAll(String title  ,String[] provinces,DialogInterface.OnClickListener listener){
        AlertDialog.Builder bu=   new AlertDialog.Builder(mContext);
        bu.setTitle(title);
        bu .setItems(provinces,listener);
        bu.create();
        bu.show();
    }
}
