package everlinkintl.com.myappwh.common;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Map;

public class DialogUtile {
    public static void dialog(Context context,String text,DialogInterface.OnClickListener onClickListener,DialogInterface.OnClickListener onClickListener1){
        AlertDialog.Builder builder  = new AlertDialog.Builder(context);
        builder.setTitle("提示" ) ;
        builder.setMessage(text) ;
        builder.setPositiveButton("确定" ,  onClickListener );
        builder.setNegativeButton("取消", onClickListener1);
        builder.show();
    }
    public static void dialog(Map<String,String>map, Context context, String text, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onClickListener1){
        AlertDialog.Builder builder  = new AlertDialog.Builder(context);
        builder.setTitle("提示" ) ;
        builder.setMessage(text) ;
        builder.setPositiveButton(map.get("determine") ,  onClickListener );
        builder.setNegativeButton(map.get("cancel") , onClickListener1);
        builder.show();
    }

}
