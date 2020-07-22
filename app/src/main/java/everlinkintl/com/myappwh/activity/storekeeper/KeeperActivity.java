package everlinkintl.com.myappwh.activity.storekeeper;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import everlinkintl.com.myappwh.datatemplate.KeeperData;
import everlinkintl.com.myappwh.datatemplate.OrgWhInfo;
import everlinkintl.com.myappwh.datatemplate.OutboundVcInfoData;
import everlinkintl.com.myappwh.datatemplate.UserData;
import everlinkintl.com.myappwh.http.API;
import everlinkintl.com.myappwh.http.Okhttp;
import everlinkintl.com.myappwh.view.PictureDialog;

public class KeeperActivity extends MyBsetActivity {
    @BindView(R.id.keeper_gv)
    GridView gridView;
    private long exitTime = 0;
    @BindString(R.string.exit)
    String exit;
    List<UserData> list;

    @Override
    protected int getContentLayoutId() {
        return R.layout.keeper_layout;
    }

    @Override
    protected void setData(String string) {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleName("选择操作");
        setGoneBreak();
        setTitleRigthTvShow("选择角色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zhuzhixuanze();

            }
        });
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        int widthPixel = outMetrics.widthPixels;
        int itemW = (widthPixel - Tools.dp2px(this, 22)) / 2;
        List<KeeperData> list = new ArrayList<>();
        KeeperData keeperData = new KeeperData();
        keeperData.setText("快速入库");
        keeperData.setCl(ExpressAcivity.class);
        list.add(keeperData);
        keeperData = new KeeperData();
        keeperData.setText("快速出库");
        keeperData.setCl(ExpressOutAcivity.class);
        list.add(keeperData);
        keeperData = new KeeperData();
        keeperData.setText("标准入库");
        keeperData.setDec("(按托入库)");
        keeperData.setCl(CurrencyActivity.class);
        list.add(keeperData);
        keeperData = new KeeperData();
        keeperData.setText("标准出库");
        keeperData.setCl(CurrencyOutActivityOne.class);
        list.add(keeperData);

        keeperData = new KeeperData();
        keeperData.setText("标准入库");
        keeperData.setDec("(按箱入库)");
        keeperData.setCl(CurrencyActivity.class);
        list.add(keeperData);
        keeperData = new KeeperData();
        keeperData.setText("与司机交接");
        keeperData.setCl(HandoverActivity.class);
        list.add(keeperData);

        KeeperAdapter keeperAdapter = new KeeperAdapter(KeeperActivity.this, itemW);
        keeperAdapter.setData(list);
        gridView.setAdapter(keeperAdapter);
        keeperAdapter.notifyDataSetChanged();

    }

    @OnClick({R.id.keeper_bt})
    public void onViewClicked(View view) {
        if (!Tools.isFastClick()) {
            return;
        }
        switch (view.getId()) {

            case R.id.keeper_bt:
                SharedPreferencesUtil.clearAll();
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
                break;


        }
    }

    private void zhuzhixuanze() {
        Map<String, String> map = new HashMap<>();
        API.getUserOrg(map, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {

            }

            @Override
            public void onsuccess(String object) {
                if (object.indexOf("userId") != -1) {
                    Gson gson = new Gson();
                    list = gson.fromJson(object, new TypeToken<ArrayList<UserData>>() {
                    }.getType());
                    String[] mItems = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        mItems[i] = list.get(i).getOrgName();
                    }
                    PictureDialog pictureDialog = new PictureDialog(KeeperActivity.this);
                    pictureDialog.dialogList1(mItems);
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void back(int i) {
        if (list != null && list.size() > 0 && !Tools.isEmpty(list.get(i).getClientId())) {
            Gson gson = new Gson();
            String st = gson.toJson(list.get(i));
            SharedPreferencesUtil.clearItem(Cons.EVERLINKINT_LOGIN_ORGNAME);
            SharedPreferencesUtil.setParam(Cons.EVERLINKINT_LOGIN_ORGNAME, st);
            getOrgWhInfo(list.get(i).getClientId());
        }
    }

    private void getOrgWhInfo(String client_id) {
        String mpclient_id = client_id;
        Map<String, String> map = new HashMap<>();
        map.put("client_id", mpclient_id.replaceAll(" ", ""));
        API.getOrgWhInfo(map, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {

            }

            @Override
            public void onsuccess(String object) {

                if (object.indexOf("warehouse_id") != -1) {
                    Gson gson = new Gson();
                    List<OrgWhInfo> listWhInfo = gson.fromJson(object, new TypeToken<ArrayList<OrgWhInfo>>() {
                    }.getType());
                    if (listWhInfo.size() > 1) {
                        String[] string = new String[listWhInfo.size()];

                        for (int i = 0; i < listWhInfo.size(); i++) {
                            string[i] = listWhInfo.get(i).getWarehouse_name();
                        }
                        PictureDialog pictureDialog = new PictureDialog(KeeperActivity.this);
                        pictureDialog.diaAll("选着仓库", string, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferencesUtil.clearItem(Cons.EVERLINKINT_LOGIN_ORGWHINFO);
                                SharedPreferencesUtil.setParam(Cons.EVERLINKINT_LOGIN_ORGWHINFO, gson.toJson(listWhInfo.get(which)));
                                getOrgLoc(mpclient_id, listWhInfo.get(which).getWarehouse_id());
                            }
                        });
                    } else {
                        SharedPreferencesUtil.clearItem(Cons.EVERLINKINT_LOGIN_ORGWHINFO);
                        SharedPreferencesUtil.setParam(Cons.EVERLINKINT_LOGIN_ORGWHINFO, gson.toJson(listWhInfo.get(0)));
                        getOrgLoc(mpclient_id, listWhInfo.get(0).getWarehouse_id());
                    }
                } else {
                    Tools.ToastsShort(KeeperActivity.this, object);
                }
            }
        });
    }

    private void getOrgLoc(String client_id, String warehouse_id) {
        Map<String, String> map = new HashMap<>();
        map.put("client_id", client_id.replaceAll(" ", ""));
        map.put("warehouse_id", warehouse_id.replaceAll(" ", ""));
        API.getOrgLoc(map, this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {

            }

            @Override
            public void onsuccess(String object) {
                if (object.indexOf("warehouse_id") != -1) {
//                    Gson gson=new Gson();
//                    List<OrgWhInfo> listOrgLoc =gson.fromJson(object,new TypeToken<ArrayList<OrgWhInfo>>() {
//                    }.getType());
                    SharedPreferencesUtil.clearItem(Cons.EVERLINKINT_LOGIN_ORGLOC);
                    SharedPreferencesUtil.setParam(Cons.EVERLINKINT_LOGIN_ORGLOC, object);
                } else {
                    Tools.ToastsShort(KeeperActivity.this, object);
                }
            }
        });
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Tools.ToastsShort(getApplicationContext(), exit);
            exitTime = System.currentTimeMillis();
        } else {
            ((MyApplication) getApplication()).finishAll();
        }
    }
}
