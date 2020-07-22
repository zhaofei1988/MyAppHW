package everlinkintl.com.myappwh.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import everlinkintl.com.myappwh.R;
import everlinkintl.com.myappwh.activity.user.LoginActivity;
import everlinkintl.com.myappwh.common.Cons;
import everlinkintl.com.myappwh.common.SharedPreferencesUtil;
import everlinkintl.com.myappwh.common.Tools;
import everlinkintl.com.myappwh.datatemplate.KeeperData;
import everlinkintl.com.myappwh.view.PictureDialog;

public class KeeperAdapter extends MyBaseAdapter {
    private Context mContext;
    private int mw;

    public KeeperAdapter(Context context, int w) {
        super(context);
        this.mContext = context;
        this.mw = w;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemLayoutId(int getItemViewType) {
        return R.layout.keeper_gv_item;
    }

    @Override
    public void handleItem(int itemViewType, final int position, Object item, ViewHolder holder, boolean isRecycle) {
        final KeeperData it = (KeeperData) item;
        TextView keeperTv = holder.get(R.id.keeper_gv_tv, TextView.class);
        LinearLayout keeperLy = holder.get(R.id.keeper_gv_ly, LinearLayout.class);
        TextView keeperTvDec  = holder.get(R.id.keeper_gv_dec, TextView.class);

        ViewGroup.LayoutParams lp = keeperLy.getLayoutParams();
        lp.height = mw;
        keeperLy.setLayoutParams(lp);
        keeperTv.setText(it.getText());
        if(!Tools.isEmpty(it.getDec())){
            keeperTvDec.setText(it.getDec());
        }else {
            keeperTvDec.setText("");
        }
        keeperLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object org = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGNAME, "");
                Object org1 = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGWHINFO, "");
                if (Tools.isEmpty(org)|| Tools.isEmpty(org1)) {
                    Tools.ToastsShort(mContext, "请选择角色");
                    return;

                }
                if(it.getCl()==null){
                    return;
                }
                if (position == 2) {
                    Intent intent = new Intent(mContext, it.getCl());
                    intent.putExtra("typenum", 0);
                    mContext.startActivity(intent);
                } else if (position == 4) {
                    Intent intent = new Intent(mContext, it.getCl());
                    intent.putExtra("typenum", 1);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, it.getCl());
                    mContext.startActivity(intent);
                }
            }
        });
    }
}
