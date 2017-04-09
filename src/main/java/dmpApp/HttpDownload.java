package dmpApp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HttpDownload {
	/**http下载*/
	public static boolean httpDownload(String httpUrl,String saveFile){
	       // 下载网络文件
	       int bytesum = 0;
	       int byteread = 0;

	       URL url = null;
	    try {
	        url = new URL(httpUrl);
	    } catch (MalformedURLException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
	        return false;
	    }

	       try {
	           URLConnection conn = url.openConnection();
	           InputStream inStream = conn.getInputStream();
	           FileOutputStream fs = new FileOutputStream(saveFile);

	           byte[] buffer = new byte[1204];
	           while ((byteread = inStream.read(buffer)) != -1) {
	               bytesum += byteread;
	               System.out.println(bytesum);
	               fs.write(buffer, 0, byteread);
	           }
	           return true;
	       } catch (FileNotFoundException e) {
	           e.printStackTrace();
	           return false;
	       } catch (IOException e) {
	           e.printStackTrace();
	           return false;
	       }
	   }


	public static void main(String[] args){
//		String url="http://shouji.360tpcdn.com/170123/96bc65ef9a8866a3fad3f2d5a3fae710/com.qihoo360.mobilesafe_251.apk";
		String url="http://f2.market.xiaomi.com/download/AppStore/06c494cc21924e8aede5b778fe27aa290e1401881/com.amazing.gamez.airport.flight.staff.simulator.apk";
//		String fileName="E:\\work\\dmp_app\\apk_download\\com.qihoo360.mobilesafe_251.apk";
		String fileName="E:\\work\\dmp_app\\apk_download\\com.amazing.gamez.airport.flight.staff.simulator.apk";
		httpDownload(url, fileName);
	}
}
