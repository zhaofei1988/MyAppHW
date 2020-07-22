package everlinkintl.com.myappwh.activity.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.List;
import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import everlinkintl.com.myappwh.R;
import everlinkintl.com.myappwh.activity.MyBsetActivity;
import everlinkintl.com.myappwh.common.CountDown;
import everlinkintl.com.myappwh.common.Tools;
import everlinkintl.com.myappwh.datatemplate.BasicData;
import everlinkintl.com.myappwh.datatemplate.CodeData;
import everlinkintl.com.myappwh.datatemplate.RegisterData;

public class ForgetPasswordActivity extends MyBsetActivity {
    @BindViews({R.id.forget_password_code_et, R.id.forget_password_phone_et})
    public List<EditText> editTextList;
    @BindView(R.id.forget_password_code_btn)
    Button registerCodeBtn;
    @BindArray(R.array.register_string)
    String [] registerString ;
    private boolean isCodeBtnCLick = true;
    private CountDown countDown1;
    @BindString(R.string.retrieve_password)
    String retrievePassword;
    CodeData codeDatas;
    @Override
    protected int getContentLayoutId() {
        return R.layout.forget_password_layout;
    }

    @Override
    protected void setData(String string) {
        Gson gson = new Gson();
        BasicData basicData1 = gson.fromJson(string, BasicData.class);
        if (basicData1.getCode() == Tools.code().get("code")) {
            codeDatas = gson.fromJson(string, CodeData.class);
            Tools.ToastsShort(getApplicationContext(), codeDatas.getCode());
        }
        if (basicData1.getCode() == Tools.code().get("forgetPassword")) {
//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleName(retrievePassword);
    }
    @OnClick({R.id.forget_password_btn, R.id.forget_password_code_btn})
    public void onViewClicked(View view) {
        if(!Tools.isFastClick()){
            return;
        }
        switch (view.getId()) {
            case R.id.forget_password_btn:
                forgetPassword();
                break;
            case R.id.forget_password_code_btn:
                countDown();
                break;
        }
    }
    private void forgetPassword(){
        if(Tools.isEmpty(editTextList.get(1).getText().toString().trim())){
            Tools.ToastsShort(getApplicationContext(),registerString[2]);
            return;
        }
        if (!Tools.isMobileNO(editTextList.get(1).getText().toString().trim())) {
            Tools.ToastsShort(getApplicationContext(), registerString[5]);
            return;
        }
        if (!Tools.isEmpty(codeDatas)&&editTextList.get(1).getText().toString().trim() != codeDatas.getPhone()) {
            Tools.ToastsShort(getApplicationContext(), registerString[8]);
            return;
        }
        if(Tools.isEmpty(editTextList.get(0).getText().toString().trim())){
            Tools.ToastsShort(getApplicationContext(),registerString[0]);
            return;
        }
        if (!Tools.isCheckCode(editTextList.get(0).getText().toString().trim())) {
            Tools.ToastsShort(getApplicationContext(), registerString[4]);
            return;
        }
        if (!Tools.isEmpty(codeDatas)&&editTextList.get(0).getText().toString().trim() != codeDatas.getCode()) {
            Tools.ToastsShort(getApplicationContext(), registerString[9]);
            return;
        }
        RegisterData registerData = new RegisterData();
        Gson gson = new Gson();
        BasicData basicData = new BasicData();
        basicData.setCode(Tools.code().get("forgetPassword"));
        basicData.setErrorMessage("忘记密码");
        registerData.setCode(editTextList.get(0).getText().toString().trim());
        registerData.setPhone(editTextList.get(1).getText().toString().trim());
        String stCodeData = gson.toJson(registerData);
        basicData.setObject(stCodeData);


    }
    private void countDown() {
        if(Tools.isEmpty(editTextList.get(1).getText().toString().trim())){
            Tools.ToastsShort(getApplicationContext(),registerString[2]);
            return;
        }
        if (!Tools.isMobileNO(editTextList.get(1).getText().toString().trim())) {
            Tools.ToastsShort(getApplicationContext(), registerString[5]);
            return;
        }
        if (!isCodeBtnCLick) {
            return;
        }
        countDown1 = new CountDown(registerCodeBtn);
        countDown1.countDown(new CountDown.Back() {
            @Override
            public void back(boolean b) {
                isCodeBtnCLick = b;
            }
        });
        CodeData codeData = new CodeData();
        Gson gson = new Gson();
        BasicData basicData = new BasicData();
        basicData.setCode(Tools.code().get("code"));
        basicData.setErrorMessage("忘记密码code");
        codeData.setCode("123456");
        codeData.setPhone(editTextList.get(1).getText().toString().trim());
        String stCodeData = gson.toJson(codeData);
        basicData.setObject(stCodeData);

    }
    @Override
    protected void onStop() {
        super.onStop();
        if(countDown1!=null){
            countDown1.releaseSocket();
        }
    }
}
