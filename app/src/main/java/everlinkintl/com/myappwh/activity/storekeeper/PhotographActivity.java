package everlinkintl.com.myappwh.activity.storekeeper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.vise.log.ViseLog;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import everlinkintl.com.myappwh.R;
import everlinkintl.com.myappwh.activity.MyBsetActivity;
import everlinkintl.com.myappwh.baidu.Base64Util;
import everlinkintl.com.myappwh.common.Cons;
import everlinkintl.com.myappwh.common.Tools;

import everlinkintl.com.myappwh.datatemplate.BaiduImgData;
import everlinkintl.com.myappwh.datatemplate.BaiduToken;
import everlinkintl.com.myappwh.http.API;
import everlinkintl.com.myappwh.http.Okhttp;
import everlinkintl.com.myappwh.view.CameraSurfaceView;
import everlinkintl.com.myappwh.view.PictureDialog;


public class PhotographActivity extends MyBsetActivity {
    private Button button;
    private CameraSurfaceView mCameraSurfaceView;
    byte[] datas;

    @Override
    protected int getContentLayoutId() {
        return R.layout.photograph_layout;
    }

    @Override
    protected void setData(String string) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleName("拍照上传");
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        button = (Button) findViewById(R.id.takePic);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraSurfaceView.takePicture();
            }
        });
    }

    public void ph(byte[] data) {
        String url = "https://aip.baidubce.com/oauth/2.0/token";
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "client_credentials");
        map.put("client_id", "7bFO8nZnGMR7XDiVeK4PT9wR");
        map.put("client_secret", "ncK748Xwy2GvyWNEXebayQhN8BsANDlz");
        API.baidutoken(url, map, PhotographActivity.this, new Okhttp.BasicsBack() {
            @Override
            public void onFalia(String errst) {

            }

            @Override
            public void onsuccess(String object) {
                Gson gson = new Gson();
                BaiduToken baiduToken = gson.fromJson(object, BaiduToken.class);
                sss(data, baiduToken.getAccess_token());
            }
        });

/**
 new Thread(new Runnable() {
@Override public void run() {
String imgBase64 = Base64.encodeToString(data, Base64.DEFAULT);
String obt1 = "";
try {
JSONObject object = new JSONObject();
object.put("min_size", 16);
object.put("output_prob", true);
object.put("output_keypoints", false);
object.put("skip_detection", false);
object.put("without_predicting_direction", false);
String config_str = object.toString();
JSONObject object1 = new JSONObject();
object1.put("image", imgBase64);
object1.put("configure", config_str);
obt1 = object1.toString();

} catch (JSONException e) {
e.printStackTrace();
}

AlBaBaAPI.GPUHttpTest(obt1, new ApiCallback() {
@Override public void onFailure(ApiRequest apiRequest, Exception e) {

}

@Override public void onResponse(ApiRequest apiRequest, ApiResponse apiResponse) {
if (apiResponse.getCode() == 200) {
String body = new String(apiResponse.getBody(), SdkConstant.CLOUDAPI_ENCODING);
Message message = handler.obtainMessage(2);     // Message
message.obj = body;
handler.sendMessage(message);
} else {
String errorMessage = apiResponse.getHeaders().get("X-Ca-Error-Message").toString();
Tools.ToastsShort(PhotographActivity.this, errorMessage);
}
}
});
}
}).start();
 */
    }

    private void sss(byte[] data, String token) {
        try {


            String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";

            String imgStr = Base64Util.encode(data);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = token;
            url = url + "?access_token=" + accessToken;
            API.baidutimg(url, "image=" + imgParam, this, new Okhttp.BasicsBack() {
                @Override
                public void onFalia(String errst) {
                    ViseLog.e(errst);
                }

                @Override
                public void onsuccess(String object) {
                    datas = data;
                    Gson gson = new Gson();
                    BaiduImgData baiduImgData = gson.fromJson(object, BaiduImgData.class);

                    String[] provinces = new String[baiduImgData.getWords_result().size()];
                    for (int s = 0; s < baiduImgData.getWords_result().size(); s++) {
                        provinces[s] = baiduImgData.getWords_result().get(s).getWords().replaceAll("o", "0").replaceAll("-","");
                    }
                    PictureDialog pictureDialog = new PictureDialog(PhotographActivity.this);
                    pictureDialog.dialogListV(provinces);

                }
            });
        } catch (Exception e) {

        }

    }

    public Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 保存方法
     */
    public String saveBitmap(Bitmap bitmap, String st) {
        String name = "/sdcard/everlinkintl/" + Tools.timeFormat1() + "_hawb_" + st + ".png";
        File f = new File(name);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return name;
    }

    public void TextViewItem(String text) {
        text = text.replaceAll("-", "");
        Bitmap bitmap = getBitmapFromByte(datas);
        String url = saveBitmap(bitmap, text);
        Intent intent1 = new Intent(Cons.RECEIVER_ACTION_SERVER1);
        intent1.putExtra(Cons.RECEIVER_PUT_RSULT_URL, url);
        this.sendBroadcast(intent1);
        //创建Intent对象
        Intent intent = new Intent();
        //将求和的结果放进intent中
        intent.putExtra("result", text);
        //返回结果
        setResult(0x001, intent);
        //关闭当前界面
        finish();
    }
}
