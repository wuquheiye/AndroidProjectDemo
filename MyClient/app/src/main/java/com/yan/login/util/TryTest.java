package com.yan.login.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class TryTest {
    private static String test_str;
    public static void Test(final String urlString, final Map<String, Object> param,final Handler mHandler){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                final   String urlString=url;
//                final Map<String, Object> param=new HashMap<String, Object>();
                HttpURLConnectionUtil httpURLConnectionUtil = new HttpURLConnectionUtil();
                String data = httpURLConnectionUtil.get(urlString, param);
               setStr(data);
                Log.d("TryTest  ","str ====== " + getStr());
                Message msg = new Message();
                msg.what = 1;
                //发送消息
                mHandler.sendMessage(msg);
            }
        }).start();
    }
    public static String getStr(){
        return test_str;
    }
    public static void setStr(String test_str) {
        TryTest.test_str = test_str;
    }

}
