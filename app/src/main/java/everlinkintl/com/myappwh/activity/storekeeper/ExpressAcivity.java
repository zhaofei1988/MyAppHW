package everlinkintl.com.myappwh.activity.storekeeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import everlinkintl.com.myappwh.datatemplate.UserData;
import everlinkintl.com.myappwh.http.API;
import everlinkintl.com.myappwh.http.Okhttp;
import everlinkintl.com.myappwh.datatemplate.BizNoList;
import everlinkintl.com.myappwh.view.PictureDialog;

public class ExpressAcivity extends MyBsetActivity {
    @BindViews({R.id.express_et_num, R.id.express_position})
    public List<EditText> editList;
    @BindViews({R.id.express_tv_numbers, R.id.express_tv_name, R.id.express_tv_piece,
            R.id.express_tv_all_piece, R.id.express_picther})
    public List<TextView> textList;
    UserData userData;
    OrgWhInfo orgWhInfo;
    GetHawbData getHawbData;
    boolean iscli = false;

    @BindView(R.id.express_sp)
    Spinner spinner;
    int mpon = 0;

    @Override
    protected int getContentLayoutId() {
        return R.layout.express_layout;
    }

    @Override
    protected void setData(String string) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setTitleName("快速入库");
        textList.get(4).setTypeface(iconfont);
        Object org = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGNAME, "");
        Gson gson1 = new Gson();
        userData = gson1.fromJson(org.toString(), UserData.class);
        Object org1 = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGWHINFO, "");
        orgWhInfo = gson1.fromJson(org1.toString(), OrgWhInfo.class);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mpon = pos;
                editList.get(0).setHint("请输入" + Cons.types[mpon]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExpressAcivity.this, android.R.layout.simple_spinner_item, Cons.types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @OnClick({R.id.express_picther, R.id.express_ok_btn,
            R.id.express_get_detile, R.id.express_ok_btn_all})
    public void onViewClicked(View view) {
        if (!Tools.isFastClick()) {
            return;
        }
        switch (view.getId()) {

            case R.id.express_picther:
                Intent intent = new Intent(getApplicationContext(), PhotographActivity.class);
                startActivityForResult(intent, 0x002);
                break;
            case R.id.express_ok_btn:
                posData(1);
                break;
            case R.id.express_get_detile:
                getDetile();
                break;
            case R.id.express_ok_btn_all:
                posData(2);
                break;

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x002 && resultCode == 0x001) {
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
            map.put("hawb", editList.get(0).getText().toString().replaceAll(" ", ""));
        } else {
            Tools.ToastsShort(getApplicationContext(), Cons.types[mpon] + "不能为空");
            return;
        }
        map.put("client_id", userData.getClientId().replaceAll(" ", ""));
        map.put("exp_type", Cons.types_vue[mpon]);
        map.put("warehouse_id",orgWhInfo.getWarehouse_id().replaceAll(" ",""));
        API.getWH(map, ExpressAcivity.this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {
                Tools.ToastsShort(getApplicationContext(), errst);
            }

            @Override
            public void onsuccess(String object) {
                show(object);

            }
        });
    }

    private void posData(int i) {
        if (getHawbData == null || getHawbData.getIN_QTY() >= getHawbData.getPKG_NO()) {
            Tools.ToastsShort(getApplicationContext(), "已经入库完");
            return;
        }
        List<BizNoList> lists = new ArrayList<>();
        if (i == 1) {
            BizNoList bizNoList = dataLi();
            if (bizNoList != null) {
                lists.add(bizNoList);
            }
        } else {
            if (!Tools.isEmpty(textList.get(2).getText())) {
                String txt = textList.get(2).getText().toString();
                String txt1 = textList.get(3).getText().toString();
                int txtInt = Integer.parseInt(txt) - Integer.parseInt(txt1);
                for (int i1 = 0; i1 < txtInt; i1++) {
                    BizNoList bizNoList = dataLi();
                    if (bizNoList != null) {
                        lists.add(bizNoList);
                    }

                }
            }

        }

        API.getDoExpInbound(lists, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {
                Tools.ToastsShort(getApplicationContext(), errst);
            }

            @Override
            public void onsuccess(String object) {
                if (object.indexOf("code") != -1) {
                    Gson gson = new Gson();
                    DoExpInboundData doExpInboundData = gson.fromJson(object, DoExpInboundData.class);
                    if (doExpInboundData.getCode() == 10200) {
                        getDetile();
                    } else {
                        Tools.ToastsShort(getApplicationContext(), doExpInboundData.getMessage());
                    }
                }
            }
        });
    }

    private BizNoList dataLi() {
        BizNoList bizNoList = new BizNoList();
        bizNoList.setBiz_no(getHawbData.getBIZ_NO());
        if (!Tools.isEmpty(editList.get(0).getText().toString())) {
            bizNoList.setHawb_no(editList.get(0).getText().toString().replaceAll(" ", ""));
        } else {

            Tools.ToastsShort(getApplicationContext(), Cons.types[mpon] + "不能为空");
            return null;
        }
        if (!Tools.isEmpty(editList.get(1).getText().toString())) {
            bizNoList.setLocation_no(editList.get(1).getText().toString().replaceAll(" ", ""));
        } else {
            bizNoList.setLocation_no("STAGE");
        }
        bizNoList.setClient_id(userData.getClientId().replaceAll(" ", ""));
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
                List<GetHawbData> listq = gson.fromJson(json, new TypeToken<ArrayList<GetHawbData>>() {
                }.getType());
                ViseLog.e("List<GetHawbData>.size=" + listq.size());
                if (listq.size() > 1) {
                    String[] provinces = new String[listq.size()];
                    for (int s = 0; s < listq.size(); s++) {
                        provinces[s] = listq.get(s).getHAWB_NO();
                    }

                    PictureDialog pictureDialog = new PictureDialog(ExpressAcivity.this);

                    pictureDialog.diaAll("选择" + Cons.types[mpon], provinces, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            iscli = true;
                            getHawbData = listq.get(which);
                            show1();
                        }
                    });
                } else {
                    iscli = false;
                    getHawbData = listq.get(0);
                    show1();
                }
            } else {
                Tools.ToastsShort(getApplicationContext(), hwData.getMessage());
            }
        } else {
            Tools.ToastsShort(getApplicationContext(), object);
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
        if (!Tools.isEmpty(getHawbData.getIN_QTY())) {
            textList.get(3).setText(getHawbData.getIN_QTY() + "");
        }
        if (!Tools.isEmpty(getHawbData.getLOCATION_NO())) {
            editList.get(1).setText(getHawbData.getLOCATION_NO() + "");
        }
        if (iscli) {
            editList.get(0).setText(getHawbData.getHAWB_NO());
            editList.get(0).setTextColor(this.getResources().getColor(R.color.c0076ff));
        }

    }
}
