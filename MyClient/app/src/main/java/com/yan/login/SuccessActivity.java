package com.yan.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

public class SuccessActivity extends Activity {

    private EditText user;
    private EditText password;

    //jsjsBridge start
    private static final String TAG = SuccessActivity.class.getSimpleName();
    /**jsBridgeWebview*/
    private MyBridgeWebView myWebView;
    //jsjsBridge end

    //通知  start
    private Context mContext;
    private JWebSocketClient client;
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;
    private EditText et_content;
    private ListView listView;
    private Button btn_send;

    //通知
    //对应的Activity绑定Service，并获取Service的东西
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MainActivity", "服务与活动成功绑定");
            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
            client = jWebSClientService.client;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("MainActivity", "服务与活动成功断开");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.success);
        mContext= SuccessActivity.this;



        //未加载完
        initViews();
        //判断退出
        initDatas();

        //启动服务
        startJWebSClientService();
        //检测通知是否开启
        checkNotification(mContext);



//        WebView success = findViewById(R.id.success);
//
//        success.getSettings().setJavaScriptEnabled(true);
//        success.getSettings().setAppCacheEnabled(true);
//        success.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        success.getSettings().setDomStorageEnabled(true);
//        success.loadUrl("http://106.54.231.23:43008/use/index.html?utelephonenumber="+inputUser+"&upassword="+inputPassword);
////      success.loadUrl("http://106.54.231.23:43008/alert/index.html");
//        success.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//        });
        
    }

    //未加载完成
    private void initViews() {
        myWebView = findViewById(R.id.web_view);
    }

    private void initDatas() {
//		myWebView.setDefaultHandler(new CustomBridgeHandler());//jsBridge
        //加载网址
//		myWebView.loadLocalUrl("http://106.54.231.23/demo.html");

        //值传递过来
//        Intent intent = getIntent();//声明一个对象，并获得跳转过来的Intent对象
//        String inputUser = intent.getStringExtra("name");//通过键从intent 获取值
//        String inputPassword = intent.getStringExtra("password");
//        System.out.println("登录："+"user:"+inputUser+"password:"+inputPassword);
        CookieManager cookieManager = CookieManager.getInstance();
        Gson gson = new Gson();
        User user = gson.fromJson(cookieManager.getCookie("user"), User.class);
//        System.out.println("获取user-------------"+user.toString());
        String username = user.getName();
        String password = user.getPassword();
        String url = Util.ip+"use/index.html?utelephonenumber="+username+"&upassword="+password;
        System.out.println(url);
        myWebView.loadLocalUrl(url);
//        myWebView.loadLocalUrl("http://192.168.1.154:43008/use/index.html?utelephonenumber=15274935031&upassword=123456");
        initJsBridge();

        //实现webview只可滑动不可点击【项目中需要用到的时候再解开注释】
        //http://blog.csdn.net/mjjmjc/article/details/47105001
        //http://blog.csdn.net/qq_32452623/article/details/52304628
		/*myWebView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return true;
			}
		});*/
    }

    //jsBridge 初始化一些事件监听
    private void initJsBridge(){
        //【js调用Java的方法】必须和js中的调用函数名相同，注册具体执行函数，类似java实现类。
        myWebView.registerHandler("functionInJava", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i(TAG, "接收html发送给Java的数据：" + data);
                String callbackData = "这个是js调用java方法后返回的数据";
                System.out.println("+++++++++++++++++++进入后台，可以删除cookie+++++++++");
                System.out.println("跳转页面");
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                Intent intent = new Intent(SuccessActivity.this, LoginActivity.class);
                System.out.println("流程通过");
                startActivity(intent);
//				function.onCallBack(callbackData);
            }
        });

    }


    /**
     * 启动服务（websocket客户端服务）
     */
    private void startJWebSClientService() {
        Intent intent = new Intent(mContext, JWebSocketClientService.class);
        startService(intent);
    }


    /**
     *  通知
     * 检测是否开启通知
     *
     * @param context
     */
    private void checkNotification(final Context context) {
        if (!isNotificationEnabled(context)) {
            new AlertDialog.Builder(context).setTitle("温馨提示")
                    .setMessage("你还未开启系统通知，将影响消息的接收，要去开启吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setNotification(context);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }
    /**
     * 如果没有开启通知，跳转至设置界面
     *
     * @param context
     */
    private void setNotification(Context context) {
        Intent localIntent = new Intent();
        //直接跳转到应用通知设置的代码：
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", context.getPackageName());
            localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
        }
        context.startActivity(localIntent);
    }

    /**
     * 获取通知权限,监测是否开启了系统通知
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
