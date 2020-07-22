package everlinkintl.com.myappwh.activity.storekeeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
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
import everlinkintl.com.myappwh.datatemplate.DoExpInboundData;
import everlinkintl.com.myappwh.datatemplate.GetHawbData;
import everlinkintl.com.myappwh.datatemplate.HWData;
import everlinkintl.com.myappwh.datatemplate.OrgWhInfo;
import everlinkintl.com.myappwh.datatemplate.OutboundVcInfoData;
import everlinkintl.com.myappwh.datatemplate.UserData;
import everlinkintl.com.myappwh.http.API;
import everlinkintl.com.myappwh.http.Okhttp;
import everlinkintl.com.myappwh.datatemplate.BizNoList;
import everlinkintl.com.myappwh.view.PictureDialog;

public class ExpressOutAcivity extends MyBsetActivity {
    @BindViews({R.id.express_out_et_num})
    public List<EditText> editList;
    @BindViews({R.id.express_out_tv_numbers, R.id.express_out_tv_name, R.id.express_out_tv_piece,
            R.id.express_out_tv_all_piece, R.id.express_out_picther, R.id.express_out_sp_dec})
    public List<TextView> textList;
    GetHawbData getHawbData;
    List<OutboundVcInfoData> vcList;
    @BindView(R.id.express_out_sp)
    Spinner spinner;
    @BindView(R.id.express_out_sp1)
    Spinner spinner1;

    private int mpos = 0;
    private int mpon = 0;
    private String getVc_biz_no;
    private String editListTxt;
    UserData userData;
    boolean iscli = false;
    OrgWhInfo orgWhInfo;
    @Override
    protected int getContentLayoutId() {
        return R.layout.express_out_layout;
    }

    @Override
    protected void setData(String string) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setTitleName("快速出库");
        textList.get(4).setTypeface(iconfont);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                textList.get(5).setText(vcList.get(pos).getTrans_route());
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
                vcInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExpressOutAcivity.this, android.R.layout.simple_spinner_item, Cons.types);
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
                    Object org1 = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGWHINFO, "");
                    orgWhInfo = gson1.fromJson(org1.toString(), OrgWhInfo.class);
                    vcInfo();
                    break;
            }
        }
    };

    @OnClick({R.id.express_out_picther, R.id.express_out_ok_btn,
            R.id.express_out_get_detile, R.id.express_out_ok_btn_all})
    public void onViewClicked(View view) {
        if (!Tools.isFastClick()) {
            return;
        }
        switch (view.getId()) {

            case R.id.express_out_picther:
                Intent intent = new Intent(ExpressOutAcivity.this, PhotographActivity.class);
                startActivityForResult(intent, 0x003);
                break;
            case R.id.express_out_ok_btn:
                posData(1);
                break;
            case R.id.express_out_get_detile:
                getDetile();
                break;
            case R.id.express_out_ok_btn_all:
                posData(2);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x003 && resultCode == 0x001) {
            String resultData = data.getStringExtra("result");
            editList.get(0).setText(resultData);
            getHawbData = null;
            for (int i = 0; i < textList.size(); i++) {
                if (i != 4) {
                    textList.get(i).setText("");
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getDetile() {
        Map<String, String> map = new HashMap<>();

        if (!Tools.isEmpty(editList.get(0).getText().toString())) {
            map.put("hawb", editList.get(0).getText().toString().trim().replaceAll(" ", ""));
        } else {
            Tools.ToastsShort(ExpressOutAcivity.this, Cons.types[mpon] + "不能为空");
            return;
        }
        map.put("exp_type", Cons.types_vue[mpon]);
        if (vcList == null || vcList.size() == 0) {
            Tools.ToastsShort(ExpressOutAcivity.this, "没有车辆无法查询");
            return;
        }
        map.put("client_id", vcList.get(mpos).getClient_id().replaceAll(" ", ""));
        map.put("warehouse_id",orgWhInfo.getWarehouse_id().replaceAll(" ",""));
        API.getOutbound(map, ExpressOutAcivity.this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {
                Tools.ToastsShort(ExpressOutAcivity.this, errst);
            }

            @Override
            public void onsuccess(String object) {
                show(object);
                getVc_biz_no = vcList.get(mpos).getVc_biz_no().replaceAll(" ", "");
                editListTxt = editList.get(0).getText().toString().replaceAll(" ", "");
            }
        });
    }

    private void vcInfo() {

        Map<String, String> map = new HashMap<>();
        map.put("client_id", userData.getClientId().replaceAll(" ", ""));
        map.put("exp_type", Cons.types_vue[mpon]);
        API.getExpOutbound(map, ExpressOutAcivity.this, new Okhttp.BasicsBack() {
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExpressOutAcivity.this, android.R.layout.simple_spinner_item, mItems);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        textList.get(5).setText(vcList.get(0).getTrans_route());
                    } else {
                        Tools.ToastsShort(ExpressOutAcivity.this, hwData.getMessage());
                    }
                }

            }
        });
    }

    private void posData(int i) {
        if (vcList == null || vcList.size() == 0) {
            return;
        }
        if (getHawbData == null) {
            return;
        }
        if (getHawbData.getIN_QTY() != getHawbData.getPKG_NO()) {
            Tools.ToastsShort(ExpressOutAcivity.this, "此单还没有完全入库");
            return;
        }
        if (getHawbData.getOUT_QTY() >= getHawbData.getIN_QTY()) {
            Tools.ToastsShort(ExpressOutAcivity.this, "此单已经出库完成");
            return;
        }
        List<BizNoList> lists = new ArrayList<>();
        if (i == 1) {
            BizNoList bizNoList = dataLi();
            if (bizNoList == null) {
                return;
            } else {
                lists.add(bizNoList);
            }
        } else {
            if (!Tools.isEmpty(textList.get(2).getText())) {
                String txt = textList.get(2).getText().toString();
                String txt1 = textList.get(3).getText().toString();
                int txtInt = Integer.parseInt(txt) - Integer.parseInt(txt1);
                for (int i1 = 0; i1 < txtInt; i1++) {
                    BizNoList bizNoList = dataLi();
                    if (bizNoList == null) {
                        return;
                    } else {
                        lists.add(bizNoList);
                    }

                }
            }

        }

        API.getDoExpoutbound(lists, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {
                Tools.ToastsShort(ExpressOutAcivity.this, errst);
            }

            @Override
            public void onsuccess(String object) {
                if (object.indexOf("code") != -1) {
                    Gson gson = new Gson();
                    DoExpInboundData doExpInboundData = gson.fromJson(object, DoExpInboundData.class);
                    if (doExpInboundData.getCode() == 10200) {
                        getDetile();
                    } else {
                        Tools.ToastsShort(ExpressOutAcivity.this, doExpInboundData.getMessage());
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
            Tools.ToastsShort(ExpressOutAcivity.this, Cons.types[mpon] + "不能为空");
            return null;
        }
        bizNoList.setExp_type(Cons.types_vue[mpon]);
        bizNoList.setWarehouse_id(orgWhInfo.getWarehouse_id().replaceAll(" ",""));
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
                    PictureDialog pictureDialog = new PictureDialog(ExpressOutAcivity.this);
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
                Tools.ToastsShort(ExpressOutAcivity.this, hwData.getMessage());
            }
        } else {
            Tools.ToastsShort(ExpressOutAcivity.this, object);
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
