package com.ai.autohome.spider;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class AutoHomeMain {

	public static void main(String[] args) throws Exception{
		
		FileOutputStream out=new FileOutputStream("E:/auto_home_car_info.txt");
        PrintStream p=new PrintStream(out);
        
        ArrayList<String> urlList = new ArrayList<String>();
		//获取某类车型所有车系的链接，eg:紧凑型车
//		String autoHomeUrl = "http://www.autohome.com.cn/a00/";
        urlList.add("http://www.autohome.com.cn/a00/");
//		String autoHomeUrl_a0 = "http://www.autohome.com.cn/a0/";
        urlList.add("http://www.autohome.com.cn/a0/");
//		String autoHomeUrl_a = "http://www.autohome.com.cn/a/";
        urlList.add("http://www.autohome.com.cn/a/");
//		String autoHomeUrl ="http://www.autohome.com.cn/b/";
        urlList.add("http://www.autohome.com.cn/b/");
//		String autoHomeUrl ="http://www.autohome.com.cn/c/";
        urlList.add("http://www.autohome.com.cn/c/");
//		String autoHomeUrl ="http://www.autohome.com.cn/d/";
        urlList.add("http://www.autohome.com.cn/d/");
//		String autoHomeUrl = "http://www.autohome.com.cn/suv/";
        urlList.add("http://www.autohome.com.cn/suv/");
//		String autoHomeUrl = "http://www.autohome.com.cn/mpv/";
        urlList.add("http://www.autohome.com.cn/mpv/");
//		String autoHomeUrl = "http://www.autohome.com.cn/s/";
        urlList.add("http://www.autohome.com.cn/s/");
//		String autoHomeUrl = "http://www.autohome.com.cn/p/";
        urlList.add("http://www.autohome.com.cn/p/");
//		String autoHomeUrl = "http://www.autohome.com.cn/mb/";
        urlList.add("http://www.autohome.com.cn/mb/");
//		String autoHomeUrl = "http://www.autohome.com.cn/qk/";
		urlList.add("http://www.autohome.com.cn/qk/");
		
		for(int m=0 ; m<urlList.size(); m++){
			
			String autoHomeUrl = urlList.get(m);
			JsoupUtil jsTool = new JsoupUtil();
			ArrayList<String> CSUrl = jsTool.getCarSeriesUrl(autoHomeUrl);
			
			for (int i=0; i< CSUrl.size(); i++){
				p.print(jsTool.getCarSeriesInfo(CSUrl.get(i)));
				p.println("\n");
			}
		}

	}
	
}
