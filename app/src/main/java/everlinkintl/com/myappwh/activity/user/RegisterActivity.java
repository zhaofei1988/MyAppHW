package everlinkintl.com.myappwh.activity.user;

import android.content.Intent;
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
import everlinkintl.com.myappwh.common.Cons;
import everlinkintl.com.myappwh.common.CountDown;
import everlinkintl.com.myappwh.common.SharedPreferencesUtil;
import everlinkintl.com.myappwh.common.Tools;
import everlinkintl.com.myappwh.datatemplate.BasicData;
import everlinkintl.com.myappwh.datatemplate.CodeData;
import everlinkintl.com.myappwh.datatemplate.RegisterData;

/**
 * 注册 / 修改密码
 */
public class RegisterActivity extends MyBsetActivity {
    @BindViews({R.id.register_code_et, R.id.register_password_et,
            R.id.register_phone_et, R.id.register_repetition_password_et})
    public List<EditText> editTextList;
    @BindView(R.id.register_code_btn)
    Button registerCodeBtn;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindArray(R.array.register_string)
    String[] registerString;
    CountDown countDown1;
    private boolean isCodeBtnCLick = true;
    private boolean isChangePassword = false;
    @BindArray(R.array.modification_password_default)
    String[] modificationPasswordDefault;
    @BindString(R.string.modification_password_title)
    String modificationPasswordTitle;
    @BindString(R.string.register_title)
    String registerTitle;
    @BindString(R.string.modification_password)
    String modificationPassword;
    CodeData codeDatas;

    @Override
    protected int getContentLayoutId() {
        return R.layout.register_layout;
    }

    @Override
    protected void setData(String string) {
        Gson gson = new Gson();
        BasicData basicData1 = gson.fromJson(string, BasicData.class);
        if (basicData1.getCode() == Tools.code().get("code")) {
            codeDatas = gson.fromJson(string, CodeData.class);
            Tools.ToastsShort(getApplicationContext(), codeDatas.getCode());
        }
        if (basicData1.getCode() == Tools.code().get("register") || basicData1.getCode() == Tools.code().get("changePassword")) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(intent);
            SharedPreferencesUtil.clearItem(Cons.EVERLINKINT_LOGIN_SP);
        }
    }

    @OnClick({R.id.register_btn, R.id.register_code_btn})
    public void onViewClicked(View view) {
        if(!Tools.isFastClick()){
            return;
        }
        switch (view.getId()) {
            case R.id.register_btn:
                register();
                break;
            case R.id.register_code_btn:
                countDown();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleName(registerTitle);
        Intent intent = getIntent();
        if (!Tools.isEmpty(intent.getExtras())) {
            Bundle bundle = intent.getExtras();
            String phone = bundle.getString(Cons.PHONE);
            isChangePassword = true;
            init(phone);
        }
    }

    private void init(String phone) {
        editTextList.get(2).setText(phone);
        editTextList.get(1).setHint(modificationPasswordDefault[0]);
        editTextList.get(3).setHint(modificationPasswordDefault[1]);
        setTitleName(modificationPasswordTitle);
        registerBtn.setText(modificationPassword);
    }

    private void register() {
        if (Tools.isEmpty(editTextList.get(2).getText().toString().trim())) {
            Tools.ToastsShort(getApplicationContext(), registerString[2]);
            return;
        }
        if (!Tools.isMobileNO(editTextList.get(2).getText().toString().trim())) {
            Tools.ToastsShort(getApplicationContext(), registerString[5]);
            return;
        }
        if (!Tools.isEmpty(codeDatas)&&editTextList.get(2).getText().toString().trim() != codeDatas.getPhone()) {
            Tools.ToastsShort(getApplicationContext(), registerString[8]);
            return;
        }
        if (Tools.isEmpty(editTextList.get(0).getText().toString().trim())) {
            Tools.ToastsShort(getApplicationContext(), registerString[0]);
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
        if (!Tools.isCheckCode(editTextList.get(2).getText().toString().trim())) {
            Tools.ToastsShort(getApplicationContext(), registerString[4]);
            return;
        }
        if (Tools.isEmpty(editTextList.get(1).getText().toString().trim())) {
            Tools.ToastsShort(getApplicationContext(), registerString[1]);
            return;
        }
        if (!Tools.isPassword(editTextList.get(1).getText().toString().trim())) {
            Tools.ToastsShort(getApplicationContext(), registerString[6]);
            return;
        }
        if (Tools.isEmpty(editTextList.get(3).getText().toString().trim())) {
            Tools.ToastsShort(getApplicationContext(), registerString[3]);
            return;
        }
        if (editTextList.get(3).getText().toString().trim() != editTextList.get(1).getText().toString().trim()) {
            Tools.ToastsShort(getApplicationContext(), registerString[7]);
            return;
        }
        RegisterData registerData = new RegisterData();
        Gson gson = new Gson();
        BasicData basicData = new BasicData();
        if (isChangePassword) {
            basicData.setCode(Tools.code().get("changePassword"));
            basicData.setErrorMessage("修改密码");
        } else {
            basicData.setCode(Tools.code().get("register"));
            basicData.setErrorMessage("注册");
        }
        registerData.setCode(editTextList.get(0).getText().toString().trim());
        registerData.setPhone(editTextList.get(2).getText().toString().trim());
        registerData.setPassword(editTextList.get(1).getText().toString().trim());
        String stCodeData = gson.toJson(registerData);
        basicData.setObject(stCodeData);
    }

    private void countDown() {
        if (Tools.isEmpty(editTextList.get(2).getText().toString().trim())) {
            Tools.ToastsShort(getApplicationContext(), registerString[2]);
            return;
        }
        if (!Tools.isMobileNO(editTextList.get(2).getText().toString().trim())) {
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
        basicData.setErrorMessage("注册验证码");
        codeData.setCode("123456");
        codeData.setPhone(editTextList.get(2).getText().toString().trim());
        String stCodeData = gson.toJson(codeData);
        basicData.setObject(stCodeData);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDown1 != null) {
            countDown1.releaseSocket();
        }
    }
}
