package per.lijuan.meituan.HttpThread;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import per.lijuan.meituan.Interface.ErrorCallBack;
import per.lijuan.meituan.Interface.PostCallBack;
import static android.content.ContentValues.TAG;

/**
 * Created by admin on 2017/3/29.
 */

public class SendText {
    public static void send(final String str, final PostCallBack callback, final ErrorCallBack error){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String strUrlPath = "http://192.168.6.124:3000/sendText";
                    URL url = new URL(strUrlPath);
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("content",str);
                    byte[] data = getRequestData(params, "utf-8").toString().getBytes();//获得请求体
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setConnectTimeout(3000);     //设置连接超时时间
                    httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
                    httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
                    httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
                    httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
                    //设置请求体的类型是文本类型
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //设置请求体的长度
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
                    //获得输出流，向服务器写入数据
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(data);
                    int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
                    if(response == HttpURLConnection.HTTP_OK) {
                        InputStream in = httpURLConnection.getInputStream();
                        String resultData = null;      //存储处理结果
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] dataS = new byte[1024];
                        int len = 0;
                        try {
                            while((len = in.read(dataS)) != -1) {
                                byteArrayOutputStream.write(dataS, 0, len);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        resultData = new String(byteArrayOutputStream.toByteArray());
                        Log.e(TAG, resultData+ "=======");
                        if(callback!=null){
                            callback.excute(resultData);
                        }
                    }
                }catch(Exception ex){
                    Log.e("fail","post failed");
                    error.excute( );
                }
            }
        }).start();
    }


   static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
}
