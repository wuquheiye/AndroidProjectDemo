
package com.yan.login;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.yan.login.entity.User;
import com.yan.login.im.JWebSocketClient;
import com.yan.login.im.JWebSocketClientService;
import com.yan.login.jsbridgewebview.MyBridgeWebView;
import com.yan.login.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author zhangyan
 * @data 2019/1/10
 */
public class LoginActivity extends Activity {
    private EditText user;
    private EditText password;
    private Button login;
    private Button register;
    private SharedPreferences pref;
    private CheckBox rembemberPass;
    public static final String TAG = "LoginActivity";
    public  String urlLogin = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);


//        自动登录
        if(CookieManager.getInstance().getCookie("user")!=null){

            CookieManager cookieManager = CookieManager.getInstance();

            Gson gson = new Gson();
            User user = gson.fromJson(cookieManager.getCookie("user"), User.class);
                System.out.println("获取user-------------"+user.toString());

            Intent intent = new Intent(LoginActivity.this, SuccessActivity.class);//声明一个Intent对象，构造函数参数为第一个页面与第二个页面
            intent.putExtra("name",user.getName());//给Intent对象绑定数据，类比HashMap 的键-值对形式
            intent.putExtra("password",user.getPassword());
            startActivity(intent);//跳转页面
            LoginActivity.this.finish();

        }
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        //绑定控件
        init();


        //记住密码
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            String user1 = pref.getString("user", "");
            String password1 = pref.getString("password", "");
            user.setText(user1);
            password.setText(password1);
            rembemberPass.setChecked(true);
            System.out.println("记住我："+"user:"+user1+"password:"+password1);
    }

        login.setOnClickListener(new View.OnClickListener() {
            @Override

            //登录按键的响应
            public void onClick(View v) {

                String[] data = null;
                final String inputUser = user.getText().toString();
                final String inputPassword = password.getText().toString();
                System.out.println("登录："+"user:"+inputUser+"password:"+inputPassword);
                //给url设置值
                urlLogin= Util.ip+"dologin?utelephonenumber="+inputUser+"&upassword="+inputPassword;

                if (TextUtils.isEmpty(inputUser)) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(inputPassword)) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    data = new String[]{inputUser, inputPassword};
                    @SuppressLint("HandlerLeak") Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what) {
                                case 0:
                                    Toast.makeText(LoginActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    System.out.println("回调数据"+msg);
                                    Intent intent = new Intent(LoginActivity.this, SuccessActivity.class);//声明一个Intent对象，构造函数参数为第一个页面与第二个页面
                                    intent.putExtra("name",inputUser);//给Intent对象绑定数据，类比HashMap 的键-值对形式
                                    intent.putExtra("password",inputPassword);
                                    startActivity(intent);//跳转页面
                                    LoginActivity.this.finish();
                                    break;
                                case 2:
                                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Log.e("input error", "url为空");
                                    break;
                                case 4:
                                    Toast.makeText(LoginActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                                    break;
                                case 6:
                                    Toast.makeText(LoginActivity.this, "目前未分配角色，请联系管理员", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                            }
                        }
                    };
                    OperateData operateData = new OperateData();
                    String jsonString = operateData.stringTojsonLogin(data);
                    URL url = null;
                    try {
                        url = new URL(urlLogin);
                        System.out.println("连接地址"+url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    operateData.sendData(jsonString, handler, url);

                }

            }
        });


        /**
         * 跳转到注册页面
         */
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }



    /**
     * 初始化
     */
    private void init(){
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        user = findViewById(R.id.user);
        password = findViewById(R.id.password);
        rembemberPass = findViewById(R.id.remember);
    }



}
