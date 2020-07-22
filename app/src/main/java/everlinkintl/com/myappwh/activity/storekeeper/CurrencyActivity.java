package everlinkintl.com.myappwh.activity.storekeeper;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import everlinkintl.com.myappwh.R;
import everlinkintl.com.myappwh.activity.MyBsetActivity;
import everlinkintl.com.myappwh.adapter.PopoListAdapter;
import everlinkintl.com.myappwh.common.Cons;
import everlinkintl.com.myappwh.common.SharedPreferencesUtil;
import everlinkintl.com.myappwh.common.Tools;
import everlinkintl.com.myappwh.datatemplate.DoExpInboundData;
import everlinkintl.com.myappwh.datatemplate.DoNormalInboundData;
import everlinkintl.com.myappwh.datatemplate.HWData;
import everlinkintl.com.myappwh.datatemplate.NormalInboundData;
import everlinkintl.com.myappwh.datatemplate.OrgWhInfo;
import everlinkintl.com.myappwh.datatemplate.UserData;
import everlinkintl.com.myappwh.http.API;
import everlinkintl.com.myappwh.http.Okhttp;

public class CurrencyActivity extends MyBsetActivity {
    @BindViews({R.id.currency_et_num, R.id.currency_line_num, R.id.currency_pallet_num
            , R.id.currency_position, R.id.currency_into_num, R.id.currency_lot, R.id.currency_pin_size1
            , R.id.currency_pin_size2, R.id.currency_into_date})
    public List<EditText> editList;
    @BindViews({R.id.currency_tv_name, R.id.currency_surplus_num, R.id.currency_code, R.id.currency_code_numbutton, R.id.currency_code1})
    public List<TextView> textList;
    @BindView(R.id.currency_sp)
    Spinner spinner;
    UserData userData;
    NormalInboundData normalInboundData;
    String bizNo;
    String seq;
    String types;
    int mpos = 0;
    ListView listView;
    PopupWindow popupWindow;
    PopoListAdapter adapter;
    List<String> list = new ArrayList<>();
    List<String> listAll = new ArrayList<>();

    @Override
    protected int getContentLayoutId() {
        return R.layout.currency_layout;
    }

    @Override
    protected void setData(String string) {

    }

    private void kuwei(Gson gson) {
        Object org2 = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGLOC, "");
        List<OrgWhInfo> orgWhInfoList = gson.fromJson(org2.toString(), new TypeToken<ArrayList<OrgWhInfo>>() {
        }.getType());
        if (orgWhInfoList != null && orgWhInfoList.size() > 0) {
            for (OrgWhInfo orgWhInfo : orgWhInfoList) {
                listAll.add(orgWhInfo.getLoc_no());
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setTitleName("标准入库");
        textList.get(2).setTypeface(iconfont);
        textList.get(3).setTypeface(iconfont);
        textList.get(4).setTypeface(iconfont);
        int type = (int) getIntent().getExtras().get("typenum");
        if (type == 0) {
            types = "PLT";
            editList.get(2).setHint("请输入托盘号");
        } else {
            editList.get(2).setHint("请输入箱号");
            types = "CTN";
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mpos = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        String[] mItems = {"pan_size X 箱数 = 入库数量", "入库数量 / 箱数 = pan_size", "入库数量 / pan_size = 箱数"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CurrencyActivity.this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        editList.get(8).setText(Tools.timeForm());
        Object org = SharedPreferencesUtil.getParam(Cons.EVERLINKINT_LOGIN_ORGNAME, "");
        Gson gson1 = new Gson();
        userData = gson1.fromJson(org.toString(), UserData.class);
        kuwei(gson1);
        edittextLison();
    }

    @OnClick({R.id.currency_code, R.id.currency_ok_btn, R.id.currency_get_detile, R.id.currency_code_numbutton, R.id.currency_code1})
    public void onViewClicked(View view) {
        if (!Tools.isFastClick()) {
            return;
        }
        switch (view.getId()) {

            case R.id.currency_code:
                Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, 0x003);
                break;
            case R.id.currency_code_numbutton:
                Intent intent1 = new Intent(getApplicationContext(), CaptureActivity.class);
                intent1.putExtra("type", 3);
                startActivityForResult(intent1, 0x005);
                break;

            case R.id.currency_ok_btn:
                postOk();
                break;
            case R.id.currency_get_detile:
                getNormalInbound();
                break;
            case R.id.currency_code1:
                Intent intent2 = new Intent(getApplicationContext(), CaptureActivity.class);
                intent2.putExtra("type", 2);
                startActivityForResult(intent2, 0x004);
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x003 && resultCode == 0x001) {
            String resultData = data.getStringExtra("result");
            editList.get(0).setText(resultData);
            Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
            intent.putExtra("type", 2);
            startActivityForResult(intent, 0x004);
        }
        if (requestCode == 0x004 && resultCode == 0x001) {
            String resultData = data.getStringExtra("result");
            String[] st = resultData.split("/");
            String num = st[st.length - 1];
            editList.get(1).setText(num);
            getNormalInbound();
        }
        if (requestCode == 0x005 && resultCode == 0x001) {
            String resultData = data.getStringExtra("result");
            editList.get(2).setText(resultData);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getNormalInbound() {
        if (Tools.isEmpty(editList.get(0).getText())) {
            Tools.ToastsLong(getApplicationContext(), "请扫描入库单号");
            return;
        }
        if (Tools.isEmpty(editList.get(1).getText())) {
            Tools.ToastsLong(getApplicationContext(), "请扫描入库行号");
            return;
        }
        Map<String, String> map = new HashMap<>();

        map.put("biz_no", editList.get(0).getText().toString());
        map.put("seq", editList.get(1).getText().toString());
        map.put("client_id", userData.getClientId().replaceAll(" ", ""));

        API.getNormalInbound(map, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {

            }

            @Override
            public void onsuccess(String object) {
                show(object);
                bizNo = editList.get(0).getText().toString();
                seq = editList.get(1).getText().toString();
                dingwei(editList.get(2));
            }
        });
    }

    private void show(String object) {
        if (object.indexOf("code") != -1) {
            Gson gson = new Gson();
            HWData hwData = gson.fromJson(object, HWData.class);
            if (hwData.getCode() == 10200 && !Tools.isEmpty(hwData.getData())) {
                String json = gson.toJson(hwData.getData());
                List<NormalInboundData> li = gson.fromJson(json, new TypeToken<ArrayList<NormalInboundData>>() {
                }.getType());
                normalInboundData = li.get(0);
                if (!Tools.isEmpty(normalInboundData.getLot1())) {
                    editList.get(5).setText(normalInboundData.getLot1());
                }
                if (!Tools.isEmpty(normalInboundData.getPan_size())) {
                    editList.get(6).setText(normalInboundData.getPan_size());
                }
                if (!Tools.isEmpty(normalInboundData.getGoods_name())) {
                    textList.get(0).setText(normalInboundData.getGoods_name());
                }
                if (!Tools.isEmpty(normalInboundData.getRemain_in_qty())) {
                    textList.get(1).setText(normalInboundData.getRemain_in_qty());
                    int ta = Integer.valueOf(normalInboundData.getRemain_in_qty());
                    int ta2 = Integer.valueOf(textList.get(1).getText().toString());
                    if (ta < ta2) {
                        editList.get(6).setText("");
                        editList.get(4).setText(normalInboundData.getRemain_in_qty());
                    }
                }

            } else {
                Tools.ToastsShort(getApplicationContext(), hwData.getMessage());
            }
        } else {
            Tools.ToastsShort(getApplicationContext(), object);
        }
    }

    private void postOk() {
        DoNormalInboundData doNormalInboundData = new DoNormalInboundData();
        doNormalInboundData.setBiz_no(bizNo);
        doNormalInboundData.setSeq(seq);
        doNormalInboundData.setClient_id(userData.getClientId().replaceAll(" ", ""));

        if (!Tools.isEmpty(editList.get(8).getText().toString())) {
            doNormalInboundData.setInb_date(editList.get(8).getText().toString());
        } else {
            dingwei(editList.get(8));
            Tools.ToastsShort(getApplicationContext(), "入库日期不能为空");
            return;
        }
        doNormalInboundData.setInb_mode(types);
        if (!Tools.isEmpty(editList.get(3).getText().toString())) {
            doNormalInboundData.setLoc_no(editList.get(3).getText().toString());
        } else {
            doNormalInboundData.setLoc_no("STAGE");
        }

        if (!Tools.isEmpty(editList.get(4).getText().toString()) && Tools.isNumeric(editList.get(4).getText().toString())) {
            doNormalInboundData.setQty(editList.get(4).getText().toString());
        } else {
            dingwei(editList.get(4));
            Tools.ToastsShort(getApplicationContext(), "请输入正确数量");
            return;
        }
        if (!Tools.isEmpty(editList.get(6).getText().toString()) && Tools.isNumeric(editList.get(6).getText().toString())) {
            doNormalInboundData.setPan_size(editList.get(6).getText().toString());
        } else {
            dingwei(editList.get(6));
            Tools.ToastsShort(getApplicationContext(), "请输入正确pan_size数量");
            return;
        }
        if (Tools.isEmpty(editList.get(7).getText().toString()) || !Tools.isNumeric(editList.get(7).getText().toString())) {
            dingwei(editList.get(7));
            Tools.ToastsShort(getApplicationContext(), "请输入正确箱数数量");
            return;
        } else {
            int et7 = Integer.valueOf(editList.get(7).getText().toString());
            int et6 = Integer.valueOf(editList.get(6).getText().toString());
            int et4 = Integer.valueOf(editList.get(4).getText().toString());
            if (et6 * et7 != et4) {
                dingwei(editList.get(4));
                Tools.ToastsShort(getApplicationContext(), "请调整入库数量/pan_size数量/箱数数量");
                return;
            }
        }
        if (!Tools.isEmpty(editList.get(5).getText().toString())) {
            doNormalInboundData.setLot1(editList.get(5).getText().toString());
        } else {
            doNormalInboundData.setLot1("");
        }
        doNormalInboundData.setProd_date("");
        doNormalInboundData.setPhone_no("");
        int ed4 = Integer.valueOf(editList.get(4).getText().toString());
        if (Integer.valueOf(normalInboundData.getRemain_in_qty()) < ed4) {
            dingwei(editList.get(4));
            Tools.ToastsShort(getApplicationContext(), "入库数量超过实际数量");
            return;
        }
        if (!Tools.isEmpty(editList.get(2).getText().toString())) {
            doNormalInboundData.setLpn_no(editList.get(2).getText().toString());
        } else {
            if (!Tools.isEmpty(no)) {
                doNormalInboundData.setLpn_no(no);
            } else {
                getRndPltNo();
                return;
            }

        }
        API.DoNormalInbound(doNormalInboundData, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {
                ViseLog.e(errst);
            }

            @Override
            public void onsuccess(String object) {
                if (object.indexOf("code") != -1) {
                    Gson gson = new Gson();
                    DoExpInboundData doExpInboundData = gson.fromJson(object, DoExpInboundData.class);
                    if (doExpInboundData.getCode() == 10200) {
                        Tools.ToastsLong(CurrencyActivity.this, "入库成功");
                        no = null;
                        getNormalInbound();
                    } else {
                        Tools.ToastsShort(getApplicationContext(), doExpInboundData.getMessage());
                    }
                }

            }
        });
    }

    String no = null;

    private void getRndPltNo() {
        no = null;
        Map<String, String> map = new HashMap<>();
        map.put("biz_no", editList.get(0).getText().toString());

        API.getRndPltNo(map, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {

            }

            @Override
            public void onsuccess(String object) {
                if (object.indexOf("code") != -1) {
                    Gson gson = new Gson();
                    DoExpInboundData doExpInboundData = gson.fromJson(object, DoExpInboundData.class);
                    if (doExpInboundData.getCode() == 10200 && !Tools.isEmpty(doExpInboundData.getData())) {
                        no = doExpInboundData.getData();
                        postOk();
                    } else {
                        Tools.ToastsShort(getApplicationContext(), doExpInboundData.getMessage());
                    }
                }

            }
        });
    }

    private void dingwei(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    private void edittextLison() {
        editList.get(6).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mpos == 0 && !Tools.isEmpty(editList.get(7).getText().toString()) &&
                        Tools.isNumeric(editList.get(7).getText().toString())) {
                    if (!Tools.isEmpty(s.toString()) &&
                            Tools.isNumeric(s.toString())) {
                        int et6 = Integer.valueOf(editList.get(4).getText().toString());
                        int et7 = Integer.valueOf(s.toString());
                        editList.get(4).setText(String.valueOf(et6 * et7));
                    }
                }
                if (mpos == 2 && !Tools.isEmpty(editList.get(4).getText().toString()) &&
                        Tools.isNumeric(editList.get(4).getText().toString())) {
                    if (!Tools.isEmpty(s.toString()) &&
                            Tools.isNumeric(s.toString())) {
                        int et6 = Integer.valueOf(editList.get(4).getText().toString());
                        int et7 = Integer.valueOf(s.toString());
                        if (isPureDigital(String.valueOf(et6 / et7))) {
                            editList.get(7).setText(String.valueOf(et6 / et7));
                        }

                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editList.get(7).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mpos == 0 && !Tools.isEmpty(editList.get(6).getText().toString()) &&
                        Tools.isNumeric(editList.get(6).getText().toString())) {
                    if (!Tools.isEmpty(s.toString()) &&
                            Tools.isNumeric(s.toString())) {
                        int et6 = Integer.valueOf(editList.get(6).getText().toString());
                        int et7 = Integer.valueOf(s.toString());
                        editList.get(4).setText(String.valueOf(et6 * et7));

                    }

                }
                if (mpos == 1 && !Tools.isEmpty(editList.get(4).getText().toString()) &&
                        Tools.isNumeric(editList.get(4).getText().toString())) {
                    if (!Tools.isEmpty(s.toString()) &&
                            Tools.isNumeric(s.toString())) {
                        int et6 = Integer.valueOf(editList.get(4).getText().toString());
                        int et7 = Integer.valueOf(s.toString());
                        if (isPureDigital(String.valueOf(et6 / et7))) {
                            editList.get(6).setText(String.valueOf(et6 / et7));
                        }

                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editList.get(4).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mpos == 1 && !Tools.isEmpty(editList.get(7).getText().toString()) &&
                        Tools.isNumeric(editList.get(7).getText().toString())) {
                    if (!Tools.isEmpty(s.toString()) &&
                            Tools.isNumeric(s.toString())) {
                        int et6 = Integer.valueOf(editList.get(7).getText().toString());
                        int et7 = Integer.valueOf(s.toString());
                        if (isPureDigital(String.valueOf(et7 / et6))) {
                            editList.get(6).setText(String.valueOf(et7 / et6));
                        }

                    }

                }
                if (mpos == 2 && !Tools.isEmpty(editList.get(6).getText().toString()) &&
                        Tools.isNumeric(editList.get(6).getText().toString())) {
                    if (!Tools.isEmpty(s.toString()) &&
                            Tools.isNumeric(s.toString())) {
                        int et6 = Integer.valueOf(editList.get(6).getText().toString());
                        int et7 = Integer.valueOf(s.toString());
                        if (isPureDigital(String.valueOf(et7 / et6))) {
                            editList.get(7).setText(String.valueOf(et7 / et6));
                        }

                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editList.get(3).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Tools.isEmpty(s.toString().trim())) {
                    if (listAll != null && listAll.size() > 0) {
                        list.clear();
                        for (String st : listAll) {
                            if (st.indexOf(s.toString().trim()) != -1) {
                                list.add(st);
                            }
                        }
                        initPopWindow();
                        ad(list);
                    }
                } else {
                    if (list != null && list.size() > 0) {
                        list.clear();
                        ad(list);
                        popupWindow.dismiss();
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void ad(List<String> list) {
        adapter.setData(list);
        adapter.notifyDataSetChanged();
    }

    public static boolean isPureDigital(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }

        Pattern p;
        Matcher m;
        p = Pattern.compile("[0-9]*");
        m = p.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public void popoBack(String st) {
        editList.get(3).setText(st);
        list.clear();
        ad(list);
        popupWindow.dismiss();
    }

    private void initPopWindow() {
        int[] lis = Tools.getLocation(editList.get(3));
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popo_main, null);
        contentView.setBackgroundColor(Color.WHITE);
        popupWindow = new PopupWindow(findViewById(R.id.currency_lau),
                ViewGroup.LayoutParams.WRAP_CONTENT, lis[1] - lis[3]);
        popupWindow.setContentView(contentView);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        listView = (ListView) contentView.findViewById(R.id.list);
        adapter = new PopoListAdapter(this);
        listView.setAdapter(adapter);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(editList.get(3), Gravity.TOP, 0, 50);

    }


}