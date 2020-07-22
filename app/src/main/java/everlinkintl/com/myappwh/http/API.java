package everlinkintl.com.myappwh.http;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

/***   API 函数调用
 *             Map <String ,String>  map= new HashMap<>();
 *         API.loging(map,this.getApplicationContext(),new Okhttp.Objectcallback() {
 *             @Override
 *             public void onsuccess(Object object) {
 *
 *             }
 *
 *             @Override
 *             public void fileOnsuccess(Object object) {
 *
 *             }
 *
 *             @Override
 *             public void onFalia(int code, String errst) {
 *
 *             }
 *
 *             @Override
 *             public void downLoadInProgress(float progress, long total) {
 *
 *             }
 *
 *             @Override
 *             public void downLoadOnsuccess(File file) {
 *
 *             }
 *         });
 */
public class API {
    //登陆
    public static void loging(Map<String,String> map, Activity activity, Okhttp.BasicsBack handler){
       Okhttp.post("/login",map,activity,true,handler);
    }
    //校验token
    public static void checkoutToken(Map<String,String> map,  Activity activity, Okhttp.BasicsBack handler ){
        Okhttp.get("/users/current",map,activity,false,handler);
    }

    //获取单子数量
    public static void list(Map<String,String> map,  Activity activity, boolean isLoding ,Okhttp.BasicsBack handler ){
        Okhttp.post("/v1/VehTasks/list",map,activity,isLoding,handler);
    }

    public static void getWH(Map<String,String> map,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.get("/wh/get_exp_inbound_info",map,activity,true,handler);
    }
    public static void getDoExpInbound(Object object,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.post("/wh/do_exp_inbound",object,activity,true,handler);
    }
    public static void getOutbound(Map<String,String> map,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.get("/wh/get_exp_outbound_info",map,activity,true,handler);
    }
    public static void getExpOutbound(Map<String,String> map,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.get("/wh/get_exp_outbound_vc_info",map,activity,true,handler);
    }
    public static void getDoExpoutbound(Object object,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.post("/wh/do_exp_outbound",object,activity,true,handler);
    }
    public static void getSyncVehicle (Object obj,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.post("/wh/post_vch_biz_no",obj,activity,true,handler);
    }
    public static void getOrgWhInfo(Map<String,String> map,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.get("/wh/getOrgWhInfo",map,activity,true,handler);
    }
    public static void getOrgLoc(Map<String,String> map,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.get("/wh/getOrgLoc",map,activity,true,handler);
    }
    public static void getUserOrg(Map<String,String> map,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.get("/wh/getUserOrg",map,activity,true,handler);
    }
    public static void getNormalInbound(Map<String,String> map,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.get("/wh/get_normal_inbound",map,activity,true,handler);
    }
    public static void DoNormalInbound(Object object,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.post("/wh/do_normal_inbound",object,activity,true,handler);
    }
    public static void getRndPltNo(Map<String,String> map,  Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.get("/wh/get_rnd_plt_no",map,activity,true,handler);
    }
     public static void doNormalOutbound(Object object,   Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.post("/wh/do_normal_outbound",object,activity,true,handler);
    }
    public static void baidutoken(String url ,Map<String,String> map,   Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.get1(url,map,activity,true,handler);
    }
    public static void baidutimg(String url ,String img,   Activity activity,  Okhttp.BasicsBack handler ){
        Okhttp.post1(url,img,activity,true,handler);
    }
    public static void addFile1(Map<String,String> map,String fileUrl, Context context, Okhttp.FileBack handler ){
        Okhttp.postFile1("/files ",map,fileUrl,context,handler);
    }
}
