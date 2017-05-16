package per.lijuan.meituan.HttpThread;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import per.lijuan.meituan.Interface.ErrorCallBack;
import per.lijuan.meituan.Interface.PostCallBack;


import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/3/29.
 */

public class SendWav {
      public static void send(final File file, final PostCallBack callBack, final ErrorCallBack error){
          new Thread(new Runnable() {
              @Override
              public void run() {
                  String urlStr = "http://192.168.6.124:3000/sendRecord";
//                  File file = new File(AudioFileFunc.getWavFilePath());
                  try{
                      String end = "\r\n";
                      String hyphens = "--";
                      String boundary = "*****";
                      URL url = new URL(urlStr);
                      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			/* 允许使用输入流，输出流，不允许使用缓存*/
                      conn.setDoInput(true);
                      conn.setDoOutput(true);
                      conn.setUseCaches(false);
			/* 请求方式*/
                      conn.setRequestMethod("POST");
                      conn.setRequestProperty("Charset", "UTF-8");
                      conn.setRequestProperty("Connection", "Keep-Alive");
                      conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
                      if(file != null){
                          DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
				/* name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的   比如:abc.png*/
                          ds.writeBytes(hyphens + boundary + end);
                          ds.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" +
                                  file.getName() +"\"" + end);
                          ds.writeBytes(end);

                          InputStream input = new FileInputStream(file);
                          int size = 1024;
                          byte[] buffer = new byte[size];
                          int length = -1;
				/* 从文件读取数据至缓冲区*/
                          while((length = input.read(buffer)) != -1){
                              ds.write(buffer, 0, length);
                          }
                          input.close();
                          ds.writeBytes(end);
                          ds.writeBytes(hyphens + boundary + hyphens + end);
                          ds.flush();
                          String result = null;
				/* 获取响应码*/
                          Log.e(TAG, conn.getResponseCode() + "=======");
                          if(conn.getResponseCode() == 200){
                              InputStream in = conn.getInputStream();
                              String resultData = null;      //存储处理结果
                              ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                              byte[] data = new byte[1024];
                              int len = 0;
                              try {
                                  while((len = in.read(data)) != -1) {
                                      byteArrayOutputStream.write(data, 0, len);
                                  }
                              } catch (IOException e) {
                                  e.printStackTrace();
                              }
                              resultData = new String(byteArrayOutputStream.toByteArray());
                              Log.e(TAG, resultData+ "=======");
                              if(callBack!=null){
                                   callBack.excute(resultData);
                               }
                          }
                      }

                  }catch (Exception e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                      Log.e("fail","post failed");
                      error.excute();
                  }

              }
          }).start();
      }
}
