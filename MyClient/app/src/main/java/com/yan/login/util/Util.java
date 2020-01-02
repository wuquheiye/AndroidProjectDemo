package com.yan.login.util;


import android.content.Context;

import android.widget.Toast;

public class Util {


//    public static final String ws = "ws://echo.websocket.org";//websocket测试地址
    public static final String ws = "ws://106.54.231.23:43008/websocket/";//websocket测试地址
//    public static final String ip="http://192.168.1.154:43008/";//服务端地址
public static final String ip="http://106.54.231.23:43008/";//服务端地址
//    public static final String ws = "ws://106.54.231.23:8080/websocket/";
    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}
