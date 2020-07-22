package everlinkintl.com.myappwh.activity.storekeeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import everlinkintl.com.myappwh.R;
import everlinkintl.com.myappwh.activity.MyBsetActivity;
import everlinkintl.com.myappwh.common.Cons;
import everlinkintl.com.myappwh.common.SharedPreferencesUtil;
import everlinkintl.com.myappwh.common.Tools;
import everlinkintl.com.myappwh.datatemplate.BizNoList;
import everlinkintl.com.myappwh.datatemplate.DoExpInboundData;
import everlinkintl.com.myappwh.datatemplate.GetHawbData;
import everlinkintl.com.myappwh.datatemplate.HWData;
import everlinkintl.com.myappwh.datatemplate.OrgWhInfo;
import everlinkintl.com.myappwh.datatemplate.OutboundVcInfoData;
import everlinkintl.com.myappwh.datatemplate.UserData;
import everlinkintl.com.myappwh.http.API;
import everlinkintl.com.myappwh.http.Okhttp;
import everlinkintl.com.myappwh.view.PictureDialog;

public class CurrencyOutActivityOne extends MyBsetActivity {
    @BindViews({R.id.currency_out_et_num})
    public List<EditText> editList;
    @BindViews({R.id.currency_out_tv_numbers, R.id.currency_out_tv_name, R.id.currency_out_tv_piece,
            R.id.currency_out_tv_all_piece, R.id.currency_out_picther, R.id.currency_out_sp_dec})
    public List<TextView> textList;
    GetHawbData getHawbData;
    List<OutboundVcInfoData> vcList = new ArrayList<OutboundVcInfoData>();
    @BindView(R.id.currency_out_sp)
    Spinner spinner;
    @BindView(R.id.currency_out_sp1)
    Spinner spinner1;

    private int mpos = 0;
    private int mpon = 3;
    private String getVc_biz_no;
    private String editListTxt;
    UserData userData;
    OrgWhInfo orgWhInfo;
    boolean iscli = false;

    @Override
    protected int getContentLayoutId() {
        return R.layout.currency_out_layout_one;
    }

    @Override
    protected void setData(String string) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleName("标准出库");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        textList.get(4).setTypeface(iconfont);
        OutboundVcInfoData OutboundVcInfoData = new OutboundVcInfoData();
        OutboundVcInfoData.setVc_biz_no_desc("无车");
        vcList.add(OutboundVcInfoData);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (!Tools.isEmpty(vcList.get(pos).getTrans_route())) {
                    textList.get(5).setText(vcList.get(pos).getTrans_route());
                }
                mpos = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mpon = pos;
                editList.get(0).setHint("请输入" + Cons.types[mpon]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CurrencyOutActivityOne.this, android.R.layout.simple_spinner_item, Cons.types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);

        Message m = handler1.obtainMessage();
        m.what = 1;
        handler1.sendMessage(m);


    }

    Handler handler1 = new Handler() {
        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Object org = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGNAME, "");
                    Gson gson1 = new Gson();
                    userData = gson1.fromJson(org.toString(), UserData.class);
                    spinner1.setSelection(3, true);
                    Object org1 = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGWHINFO, "");
                    orgWhInfo = gson1.fromJson(org1.toString(), OrgWhInfo.class);
                    vcInfo();
                    break;
            }
        }
    };

    @OnClick({R.id.currency_out_picther, R.id.currency_out_get_detile, R.id.currency_out_ok_btn_all})
    public void onViewClicked(View view) {
        if (!Tools.isFastClick()) {
            return;
        }
        switch (view.getId()) {

            case R.id.currency_out_picther:
                Intent intent = new Intent(CurrencyOutActivityOne.this, CaptureActivity.class);
                startActivityForResult(intent, 0x005);
                break;
            case R.id.currency_out_get_detile:
                getDetile();
                break;
            case R.id.currency_out_ok_btn_all:
                posData();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x005 && resultCode == 0x001) {
            String resultData = data.getStringExtra("result");
            editList.get(0).setText(resultData);
            getHawbData = null;
            getDetile();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getDetile() {
        Map<String, String> map = new HashMap<>();

        if (!Tools.isEmpty(editList.get(0).getText().toString())) {
            map.put("hawb", editList.get(0).getText().toString().trim().replaceAll(" ", ""));
        } else {
            Tools.ToastsShort(CurrencyOutActivityOne.this, Cons.types[mpon] + "不能为空");
            return;
        }
        map.put("exp_type", Cons.types_vue[mpon]);
        map.put("client_id", userData.getClientId().replaceAll(" ", ""));
        map.put("warehouse_id", orgWhInfo.getWarehouse_id().replaceAll(" ", ""));
        API.getOutbound(map, CurrencyOutActivityOne.this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {
                Tools.ToastsShort(CurrencyOutActivityOne.this, errst);
            }

            @Override
            public void onsuccess(String object) {
                show(object);
                if (vcList != null && vcList.size() > 0) {
                    if (Tools.isEmpty(vcList.get(mpos).getVc_biz_no())) {
                        getVc_biz_no = "";
                    } else {
                        getVc_biz_no = vcList.get(mpos).getVc_biz_no().replaceAll(" ", "");
                    }

                }
                editListTxt = editList.get(0).getText().toString().replaceAll(" ", "");
            }
        });
    }

    private void vcInfo() {

        Map<String, String> map = new HashMap<>();
        map.put("client_id", userData.getClientId().replaceAll(" ", ""));
        map.put("exp_type", Cons.types_vue[mpon]);
        API.getExpOutbound(map, CurrencyOutActivityOne.this, new Okhttp.BasicsBack() {
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
                        List<OutboundVcInfoData> vcList1 = gson.fromJson(st, new TypeToken<ArrayList<OutboundVcInfoData>>() {
                        }.getType());
                        vcList.addAll(vcList1);
                    } else {
                        Tools.ToastsShort(CurrencyOutActivityOne.this, hwData.getMessage());
                    }
                    String[] mItems = new String[vcList.size()];

                    for (int i = 0; i < vcList.size(); i++) {
                        mItems[i] = vcList.get(i).getVc_biz_no_desc();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(CurrencyOutActivityOne.this, android.R.layout.simple_spinner_item, mItems);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    if (!Tools.isEmpty(vcList.get(0).getTrans_route())) {
                        textList.get(5).setText(vcList.get(0).getTrans_route());
                    }

                }

            }
        });
    }

    private void posData() {

        if (getHawbData == null) {
            return;
        }
        BizNoList bizNoList = dataLi();
        if (bizNoList == null) {
            return;
        }

        API.doNormalOutbound(bizNoList, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {
                Tools.ToastsShort(CurrencyOutActivityOne.this, errst);
            }

            @Override
            public void onsuccess(String object) {
                if (object.indexOf("code") != -1) {
                    Gson gson = new Gson();
                    DoExpInboundData doExpInboundData = gson.fromJson(object, DoExpInboundData.class);
                    if (doExpInboundData.getCode() == 10200) {
                        Tools.ToastsShort(CurrencyOutActivityOne.this, "出库完成");
                    } else {
                        Tools.ToastsShort(CurrencyOutActivityOne.this, doExpInboundData.getMessage());
                    }
                }
            }
        });
    }

    private BizNoList dataLi() {

        BizNoList bizNoList = new BizNoList();
        bizNoList.setBiz_no(getHawbData.getBIZ_NO().replaceAll(" ", ""));
        bizNoList.setClient_id(getHawbData.getCLIENT_ID().replaceAll(" ", ""));
        bizNoList.setVc_biz_no(getVc_biz_no);
        if (!Tools.isEmpty(editListTxt)) {
            bizNoList.setHawb_no(editListTxt);
        } else {
            Tools.ToastsShort(CurrencyOutActivityOne.this, Cons.types[mpon] + "不能为空");
            return null;
        }
        bizNoList.setExp_type(Cons.types_vue[mpon]);
        return bizNoList;
    }

    private void show(String object) {
        if (object.indexOf("code") != -1) {
            Gson gson = new Gson();
            HWData hwData = gson.fromJson(object, HWData.class);
            if (hwData.getCode() == 10200 && !Tools.isEmpty(hwData.getData())) {
                String json = gson.toJson(hwData.getData());
                List<GetHawbData> li = gson.fromJson(json, new TypeToken<ArrayList<GetHawbData>>() {
                }.getType());
                if (li.size() > 1) {
                    String[] provinces = new String[li.size()];
                    for (int s = 0; s < li.size(); s++) {
                        provinces[s] = li.get(s).getHAWB_NO();
                    }
                    PictureDialog pictureDialog = new PictureDialog(CurrencyOutActivityOne.this);
                    pictureDialog.diaAll("选择" + Cons.types[mpon], provinces, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            iscli = true;
                            getHawbData = li.get(which);
                            editListTxt = getHawbData.getHAWB_NO();
                            show1();
                        }
                    });
                } else {
                    iscli = false;
                    getHawbData = li.get(0);
                    show1();
                }

            } else {
                Tools.ToastsShort(CurrencyOutActivityOne.this, hwData.getMessage());
            }
        } else {
            Tools.ToastsShort(CurrencyOutActivityOne.this, object);
        }
    }

    private void show1() {
        textList.get(0).setText(getHawbData.getBIZ_NO());
        if (!Tools.isEmpty(getHawbData.getGOODS_NAME())) {
            textList.get(1).setText(getHawbData.getGOODS_NAME());
        }
        if (!Tools.isEmpty(getHawbData.getPKG_NO())) {
            textList.get(2).setText(getHawbData.getPKG_NO() + "");
        }
        if (!Tools.isEmpty(getHawbData.getOUT_QTY())) {
            textList.get(3).setText(getHawbData.getOUT_QTY() + "");
        }
        if (iscli) {
            editList.get(0).setText(getHawbData.getHAWB_NO());
            editList.get(0).setTextColor(this.getResources().getColor(R.color.c0076ff));
        }

    }

}
