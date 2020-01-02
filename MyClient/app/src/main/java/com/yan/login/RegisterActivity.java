package com.yan.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.yan.login.util.Department;
import com.yan.login.util.HttpURLConnectionUtil;
import com.yan.login.util.ReturnMsg;
import com.yan.login.util.SpinnerOption;
import com.yan.login.util.TryTest;
import com.yan.login.util.Util;



import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @author zhangyan
 * @date 2019/1/10
 */
public class RegisterActivity extends Activity implements View.OnClickListener {

    private Button loginRegister;
    private EditText loginUser;
    private EditText loginPassword;
    private EditText loginPassword1;
    private EditText loginPhone;
    private EditText loginVsCode;
    private int sectionGetValue;
    private int unGetValue;
    private String[] str = null;
    private TextView textView;
    private Spinner spinner;
    private TextView section;
    private Spinner sectionValue;
    private ArrayAdapter<SpinnerOption> adapter;

    private static final String URLREGISTER = Util.ip+"doregist";

    private String stra = "nothing";

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);

       //控件绑定
        init();

        //注册按钮
        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = loginUser.getText().toString();
                String password = loginPassword.getText().toString();
                String password1 = loginPassword1.getText().toString();
                String phone = loginPhone.getText().toString();
                String vsCode = loginVsCode.getText().toString();
                        if (TextUtils.isEmpty(user)) {
                            //用户名为空
                            Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(password)) {
                            Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(password1)) {
                            Toast.makeText(RegisterActivity.this, "请确认密码",Toast.LENGTH_SHORT).show();
                        } else if (!password.equals(password1)) {
                            Toast.makeText(RegisterActivity.this, "两次密码不一样，请验证", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(phone)) {
                            Toast.makeText(RegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(vsCode)) {
                            Toast.makeText(RegisterActivity.this, "请输入激活码", Toast.LENGTH_SHORT).show();
                        }else {
                            str= new String[]{user, password1,vsCode,Integer.toString(unGetValue),Integer.toString(sectionGetValue),phone};
                            System.out.println("str +++++"+str);

                            Handler handler = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    switch (msg.what) {
                                        case 0:
                                            Toast.makeText(RegisterActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 1: Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                            //注册成功跳转到登录页面
                                            startActivity( new Intent(RegisterActivity.this, LoginActivity.class));
                                            RegisterActivity.this.finish();
                                            break;
                                        case 2:
                                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 3:
                                            Log.e("input error", "url为空");
                                            break;
                                        case 4:Toast.makeText(RegisterActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 5:
                                            Toast.makeText(RegisterActivity.this, "激活码不正确", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 6:
                                            Toast.makeText(RegisterActivity.this, "用户名或电话号码重复", Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                    }
                                }
                            };
                            OperateData operateData = new OperateData();
                            String jsonString = operateData.stringTojson(str);
                            System.out.println("jsonString++"+jsonString);
                            URL url = null;
                            try {
                                url = new URL(URLREGISTER);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            operateData.sendData(jsonString, handler, url);
                        }

                //保存数据到SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("register", MODE_PRIVATE).edit();
                editor.putString("User", user);
                editor.putString("password", password);
                editor.putString("password1", password1);
                editor.putString("phone", phone);
                editor.putString("section", Integer.toString(sectionGetValue));
                editor.putString("un",Integer.toString(unGetValue));
//                editor.putString("")
                editor.commit();
            }
        });




        //Spinner，这里最好写在配置文件中
        spinner = (Spinner) findViewById(R.id.spinner);
        textView = (TextView) findViewById(R.id.un);

        //Spinner，这里最好写在配置文件中
        sectionValue = (Spinner) findViewById(R.id.sectionValue);
        section = (TextView) findViewById(R.id.section);

      //获取 公司名称

//        param.put("userId", "123456");
//        param.put("pa", "hello");

        @SuppressLint("HandlerLeak") Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //msg.what对应子线程中msg的标签，在子线程中进行赋值
                if(msg.what==1){
                    stra = TryTest.getStr();
                    Log.d("TryMain", "Handle str = " + stra);

                    ReturnMsg returnMsg;
                    returnMsg = gson.fromJson(stra, ReturnMsg.class);
                    System.out.println("res adfasfa"+returnMsg.getMsg().get(1).getDname());
                    List<SpinnerOption> spinnerOptions =new ArrayList< SpinnerOption>();
                    for (Department department:returnMsg.getMsg()){
                        System.out.println(department);
                        System.out.println(department.getDname());
                        SpinnerOption spinnerOption = new SpinnerOption();
                        spinnerOption.setText(department.getDname());
                        spinnerOption.setValue(department.getDid());
                        spinnerOptions.add(spinnerOption);
                    }
                    /*为spinner定义适配器，也就是将数据源存入adapter，这里需要三个参数
                    1. 第一个是Context（当前上下文），这里就是this
                    2. 第二个是spinner的布局样式，这里用android系统提供的一个样式
                    3. 第三个就是spinner的数据源，这里就是dataList*/
                    adapter = new ArrayAdapter<SpinnerOption>(RegisterActivity.this, android.R.layout.simple_spinner_item, spinnerOptions);

                    //style
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    //为spinner绑定我们定义好的数据适配器
                    sectionValue.setAdapter(adapter);


                    //部门选择
                    sectionValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            section.setText("部门：" + adapter.getItem(position));
                            sectionGetValue = ((SpinnerOption)spinner.getSelectedItem()).getValue();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            section.setText("请选择您的部门");
                        }
                    });

                }
            }
        };
        String urlString=Util.ip+"department/selectAllDepartment";
        Map<String, Object> param=new HashMap<String, Object>();

        Log.d("TryMain", "Main str = " + stra);
        String data = getData(urlString, param, mHandler);
//        System.out.println("data========="+data);
//        ReturnMsg returnMsg;
//        returnMsg = gson.fromJson(data, ReturnMsg.class);
//        System.out.println("res adfasfa"+returnMsg.getMsg().get(1).getDname());
//        List<String> spinnerOptions =new ArrayList<String>();
//        for (Department department:returnMsg.getMsg()){
//            System.out.println(department);
//            System.out.println(department.getDname());
//            SpinnerOption spinnerOption = new SpinnerOption();
//            spinnerOption.setText(department.getDname());
//            spinnerOption.setValue(department.getDid());
//            spinnerOptions.add(department.getDname());
//        }



        List<SpinnerOption>  dataList = new ArrayList<SpinnerOption>();
        dataList.add(new SpinnerOption(0,"广州广空"));
        dataList.add(new SpinnerOption(1,"广州利捷"));
        /*为spinner定义适配器，也就是将数据源存入adapter，这里需要三个参数
        1. 第一个是Context（当前上下文），这里就是this
        2. 第二个是spinner的布局样式，这里用android系统提供的一个样式
        3. 第三个就是spinner的数据源，这里就是dataList*/
        adapter = new ArrayAdapter<SpinnerOption>(this, android.R.layout.simple_spinner_item, dataList);

        //style
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //为spinner绑定我们定义好的数据适配器
        spinner.setAdapter(adapter);

        //城市选择
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textView.setText("公司：" + adapter.getItem(position));//
                unGetValue =((SpinnerOption)spinner.getSelectedItem()).getValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textView.setText("请选择您的城市");
            }
        });
    }

    public String getData(String url, Map<String, Object> param,Handler mHandler){
        //调用Test方法
        TryTest.Test(url,param,mHandler);
        //获取Test中的数据
        stra = TryTest.getStr();
        Log.d("TryMain getData", "str ============== " + stra);
        return stra;
    }

    /**
     *
     * 字符数组转json
     */
    private String stringArraytoJson(String[] strings) {

        if (strings == null){return "";}
        String js = "[{"+"uusername:"+strings[0]+"upassword:"+strings[1]+"uinvitationCode:"+strings[2]+"did:"+strings[3]+"artsVision:"+strings[4];
        return js;
    }
    
    /**
     * 绑定控件
     */
    private void init() {
        loginRegister = findViewById(R.id.L_register);
        loginUser = findViewById(R.id.L_user);
        loginPassword = findViewById(R.id.L_password);
        loginPassword1 = findViewById(R.id.L_password1);
        loginPhone = findViewById(R.id.L_phone);
        loginVsCode =findViewById(R.id.L_VsCode);
    }


    @Override
    public void onClick(View v) {

    }

    // 带编码的
    public static String is2String(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        InputStreamReader inread = new InputStreamReader(in, "UTF-8");
        char[] b = new char[4096];
        for (int n; (n = inread.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

}





