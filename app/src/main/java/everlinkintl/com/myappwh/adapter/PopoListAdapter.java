package everlinkintl.com.myappwh.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.vise.log.ViseLog;

import everlinkintl.com.myappwh.R;
import everlinkintl.com.myappwh.activity.storekeeper.CurrencyActivity;


public class PopoListAdapter extends MyBaseAdapter {
    private Context mContext;

    public PopoListAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemLayoutId(int getItemViewType) {
        return R.layout.popo_item;
    }

    @Override
    public void handleItem(int itemViewType, final int position, Object item, ViewHolder holder, boolean isRecycle) {
        final String it = (String) item;
        TextView keeperTv = holder.get(R.id.popo_item_txt, TextView.class);
        keeperTv.setText(it);
        keeperTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrencyActivity currencyActivity=(CurrencyActivity)mContext;
                currencyActivity.popoBack(it);
            }
        });
        }
    }
