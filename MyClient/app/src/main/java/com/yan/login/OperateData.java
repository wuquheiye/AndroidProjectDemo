package com.yan.login;

import android.content.Intent;
import android.renderscript.Type;
import android.util.Log;
import android.webkit.CookieManager;

import com.google.gson.Gson;
import com.yan.login.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author zhangyan
 * 主要完成 字符串数组转json字符串
 * 以及对json数据的发送和接收
 */
public class OperateData {

    /**
     *
     * @param stringArray  将string数组转成json格式字符串
     * @return
     */
    public String stringTojson(String stringArray[])
    {    JSONObject jsonObject = null;
        if(stringArray == null) {
            return "";
        }
        jsonObject = new JSONObject();
        try {
            jsonObject.put("uusername",stringArray[0]);
            jsonObject.put("upassword",stringArray[1]);
            jsonObject.put("uinvitationCode",stringArray[2]);
            jsonObject.put("did",stringArray[3]);
            jsonObject.put("artsVision",stringArray[4]);
            jsonObject.put("utelephonenumber",stringArray[5]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = String.valueOf(jsonObject);
        return jsonString;
    }

    public String stringTojsonLogin(String stringArray[])
    {    JSONObject jsonObject = null;
        if(stringArray == null) {
            return "";
        }
        jsonObject = new JSONObject();
        try {
            jsonObject.put("uusername",stringArray[0]);
            jsonObject.put("upassword",stringArray[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = String.valueOf(jsonObject);
        return jsonString;
    }

    /**
     * 判断登录是否成功
     * 功能：json字符串转字符串
     * @param jsonString
     * @return String
     */
    public int jsonToint(String jsonString)
    {
        int type=1;
        try {
            JSONObject responseJson = new JSONObject(jsonString);
            type = responseJson.getInt("status");
            Log.i("status",""+type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return type;
    }

    /**
     * 存入cookie
     */
    public void setCookie(User user ){
//        System.out.println(user.toString());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        Gson gson = new Gson();
        cookieManager.setCookie("user",gson.toJson(user));
//        Object o = gson.fromJson(, User.class);
//        System.out.println("存储cookie到本地"+cookieManager.getCookie("user"));
//        System.out.println("本地cookie PAssword="+gson.fromJson(
//                cookieManager.getCookie("qwer"),User.class).getPassword());
    }


    /**
     *功能：发送jsonString到服务器并解析回应
     * @param jsonString mh url
     * handler 参数规定
     *  msg.what:
     *  0：服务器连接失败
     *  1：注册/登录成功 跳转页面
     *  2：用户已存在/登录失败
     *  3：地址为空
     *  4： 连接超时
     *
     */
    public void sendData(final  String jsonString, final android.os.Handler mh,final URL url) {

        if (url==null){
            mh.sendEmptyMessage(3);
        }else{
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    
                    HttpURLConnection httpURLConnection = null;
                    BufferedReader bufferedReader = null;
                    try {
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        // 设置连接超时时间
                        httpURLConnection.setConnectTimeout(1000 * 1000);
                        //设置从主机读取数据超时
                        httpURLConnection.setReadTimeout(1000 * 1000);
                        // Post请求必须设置允许输出 默认false
                        httpURLConnection.setDoOutput(true);
                        //设置请求允许输入 默认是true
                        httpURLConnection.setDoInput(true);
                        // Post请求不能使用缓存
                        httpURLConnection.setUseCaches(false);
                        // 设置为Post请求
                        httpURLConnection.setRequestMethod("POST");
                        //设置本次连接是否自动处理重定向
                        httpURLConnection.setInstanceFollowRedirects(true);
                        // 配置请求Content-Type
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        //开始连接
                        httpURLConnection.connect();


                        //发送数据
                        Log.i("JSONString+++",jsonString);
                        DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
                        os.writeBytes(jsonString);
                        os.flush();
                        os.close();
                        Log.i("状态码：","" + httpURLConnection.getResponseCode());
                        
                        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String temp;

                            while ((temp = bufferedReader.readLine()) != null) {
                                response.append(temp);
                                Log.i("Main", response.toString());
                                System.out.println("url"+url.getFile());
                                if(url.getFile().indexOf("utelephonenumber")>0 ){
                                    if(jsonToint(response.toString())==200) {
                                        String utelephonenumber = url.getFile().split("=")[1].split("&")[0];
                                        String password = url.getFile().split("=")[2];
                                        int uid = Integer.parseInt(response.toString().split("uid")[1].split(",")[0].split(":")[1]);
                                        User user = new User();
                                        user.setName(utelephonenumber);
                                        user.setPassword(password);
                                        user.setUid(uid);
                                        setCookie(user);
                                        System.out.println("进入成功页面");
                                    }
                                }

                            }
                            int type = jsonToint(response.toString());


                            //根据
                            switch (type)
                            {
                                case 200:mh.sendEmptyMessage(1);
                                    break;
                                case 201:mh.sendEmptyMessage(2);
                                break;
                                case 202:mh.sendEmptyMessage(6);
                                    break;
                                case 203:mh.sendEmptyMessage(5);
                                    break;
                                default:
                            }
                        } else {
                            mh.sendEmptyMessage(0);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        mh.sendEmptyMessage(4);
                    } finally {
                        //关闭bufferedreader
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                        }
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    }
                }
            }).start();
        }
    }
}
