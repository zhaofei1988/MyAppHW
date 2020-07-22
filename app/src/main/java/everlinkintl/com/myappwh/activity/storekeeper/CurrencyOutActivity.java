package everlinkintl.com.myappwh.activity.storekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;

import butterknife.OnClick;
import everlinkintl.com.myappwh.R;
import everlinkintl.com.myappwh.activity.MyBsetActivity;
import everlinkintl.com.myappwh.common.Cons;
import everlinkintl.com.myappwh.common.SharedPreferencesUtil;
import everlinkintl.com.myappwh.common.Tools;
import everlinkintl.com.myappwh.datatemplate.DoExpInboundData;
import everlinkintl.com.myappwh.datatemplate.DoNormaOutbound;
import everlinkintl.com.myappwh.datatemplate.OrgWhInfo;
import everlinkintl.com.myappwh.datatemplate.UserData;
import everlinkintl.com.myappwh.http.API;
import everlinkintl.com.myappwh.http.Okhttp;

public class CurrencyOutActivity extends MyBsetActivity {
    @BindView(R.id.currency_out_edt)
    public EditText edit;

    UserData userData;
    @Override
    protected int getContentLayoutId() {
        return R.layout.currency_out_layout;
    }

    @Override
    protected void setData(String string) {

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleName("通用出库");
        setTitleSearchTvShow(this.getString(R.string.shaomiao), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), CaptureActivity.class);
                intent.putExtra("type",1);
                startActivityForResult(intent, 0x005);
            }
        });
        Object org = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGNAME, "");
        Gson gson1 = new Gson();
        userData =gson1.fromJson(org.toString(), UserData.class);



    }
    @OnClick({R.id.currency_out_ok_btn})
    public void onViewClicked(View view) {
        if(!Tools.isFastClick()){
            return;
        }
        switch (view.getId()) {

            case R.id.currency_out_ok_btn:
                getNormalInbound();
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0x005&&resultCode==0x001){
            String resultData=data.getStringExtra("result");
            edit.setText(resultData);
            getNormalInbound();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private void getNormalInbound(){
        if(Tools.isEmpty(edit.getText())){
            Tools.ToastsLong(getApplicationContext(), "请扫描入库单号");
            return;
        }
        DoNormaOutbound doNormaOutbound =new DoNormaOutbound();
        doNormaOutbound.setBiz_no(edit.getText().toString());
        doNormaOutbound.setClient_id(userData.getClientId().replaceAll(" ",""));
        doNormaOutbound.setVc_biz_no("");
        doNormaOutbound.setExp_type("BIZ_NO");
        API.doNormalOutbound(doNormaOutbound, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {
            }

            @Override
            public void onsuccess(String object) {
                if (object.indexOf("code") != -1) {
                    Gson gson =new Gson();
                    DoExpInboundData doExpInboundData =gson.fromJson(object,DoExpInboundData.class);
                    if (doExpInboundData.getCode() == 10200) {
                        Tools.ToastsLong(CurrencyOutActivity.this,"入库成功");
                    }else {
                        Tools.ToastsShort(getApplicationContext(), doExpInboundData.getMessage());
                    }
                }
            }
        });
    }


}