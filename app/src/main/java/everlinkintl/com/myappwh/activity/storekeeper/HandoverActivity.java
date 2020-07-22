package everlinkintl.com.myappwh.activity.storekeeper;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.WriterException;
import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import everlinkintl.com.myappwh.R;
import everlinkintl.com.myappwh.activity.MyApplication;
import everlinkintl.com.myappwh.activity.MyBsetActivity;
import everlinkintl.com.myappwh.adapter.KeeperAdapter;
import everlinkintl.com.myappwh.common.Cons;
import everlinkintl.com.myappwh.common.SharedPreferencesUtil;
import everlinkintl.com.myappwh.common.Tools;
import everlinkintl.com.myappwh.datatemplate.HWData;
import everlinkintl.com.myappwh.datatemplate.KeeperData;
import everlinkintl.com.myappwh.datatemplate.OrgWhInfo;
import everlinkintl.com.myappwh.datatemplate.OutboundVcInfoData;
import everlinkintl.com.myappwh.datatemplate.UserData;
import everlinkintl.com.myappwh.http.API;
import everlinkintl.com.myappwh.http.Okhttp;
import everlinkintl.com.myappwh.view.PictureDialog;
import everlinkintl.com.myappwh.zxing.decoding.Intents;
import everlinkintl.com.myappwh.zxing.encoding.EncodingHandler;

public class HandoverActivity extends MyBsetActivity {
    @BindView(R.id.handover_sp)
    Spinner spinner1;
    @BindView(R.id.handover_sp1)
    Spinner spinner;
    private int mpos = 0;
    private int mpon = 0;
    List<OutboundVcInfoData> vcList;
    UserData userData;
    @BindView(R.id.handover_img)
    ImageView imageView;
    @BindView(R.id.handover_txt)
    TextView textView;

    @Override
    protected int getContentLayoutId() {
        return R.layout.handover_layout;
    }

    @Override
    protected void setData(String string) {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleName("交接");
        Object org = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGNAME, "");
        Gson gson1 = new Gson();
        userData = gson1.fromJson(org.toString(), UserData.class);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mpos = pos;
                createQRCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mpon = pos;
                vcInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HandoverActivity.this, android.R.layout.simple_spinner_item, Cons.types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
    }

    @OnClick({R.id.handover_ok})
    public void onViewClicked(View view) {
        if (!Tools.isFastClick()) {
            return;
        }
        switch (view.getId()) {

            case R.id.handover_ok:
                syncVehicle();
                break;


        }
    }
    private void createQRCode(){

        if(vcList!=null&&vcList.size()>0&&vcList.get(mpos).getVeh_sync_status()!=1){
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            try {
                String str=vcList.get(mpos).getVc_biz_no().replaceAll(" ","")+"/"
                        +vcList.get(mpos).getClient_id().replaceAll(" ","");
                Bitmap bitmap=EncodingHandler.createQRCode(str,260);

                imageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }

        }else {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
    }
    private void vcInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("client_id", userData.getClientId().replaceAll(" ", ""));
        map.put("exp_type", Cons.types_vue[mpon]);
        API.getExpOutbound(map, HandoverActivity.this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {

            }

            @Override
            public void onsuccess(String object) {
                ViseLog.e("1=" + object);
                if (object.indexOf("code") != -1) {
                    Gson gson = new Gson();
                    HWData hwData = gson.fromJson(object, HWData.class);
                    if (hwData.getCode() == 10200 && !Tools.isEmpty(hwData.getData())) {
                        String st = gson.toJson(hwData.getData());
                        ViseLog.e("st=" + st);
                        vcList = gson.fromJson(st, new TypeToken<ArrayList<OutboundVcInfoData>>() {
                        }.getType());
                        String[] mItems = new String[vcList.size()];
                        for (int i = 0; i < vcList.size(); i++) {
                            mItems[i] = vcList.get(i).getVc_biz_no_desc();
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HandoverActivity.this, android.R.layout.simple_spinner_item, mItems);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    } else {
                        Tools.ToastsShort(HandoverActivity.this, hwData.getMessage());
                    }
                }

            }
        });
    }
    private  void  syncVehicle(){
        if(vcList.get(mpos).getVeh_sync_status()==1){
            Tools.ToastsShort(HandoverActivity.this, "车辆在发布中无法重新发布");
            return;
        }
        OutboundVcInfoData outboundVcInfoData =new OutboundVcInfoData();
        outboundVcInfoData.setClient_id(userData.getClientId().replaceAll(" ", ""));
        outboundVcInfoData.setVc_biz_no(vcList.get(mpos).getVc_biz_no().replaceAll(" ", ""));
        API.getSyncVehicle(outboundVcInfoData, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {
            }

            @Override
            public void onsuccess(String object) {
                Tools.ToastsShort(HandoverActivity.this, object);
                vcList.get(mpos).setVeh_sync_status(1);
                createQRCode();
            }
        });
    }
}
