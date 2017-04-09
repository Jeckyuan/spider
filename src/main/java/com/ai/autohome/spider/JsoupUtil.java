package com.ai.autohome.spider;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kopitubruk.util.json.JSONConfig;
import org.kopitubruk.util.json.JSONParser;
import org.kopitubruk.util.json.JSONUtil;
import org.kopitubruk.util.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONStringer;
//import com.ai.autohome.autohome.spider.HttpClientUtils;

public class JsoupUtil {
	
	public static void main(String[] arggs) throws Exception{
//		String url =  "http://www.autohome.com.cn/3170/#levelsource=000000000_0&pvareaid=101594";
//		String url =  "http://car.autohome.com.cn/config/series/3170.html";
//		String url =  "http://www.autohome.com.cn/grade/carhtml/D.html"; //首页全部车型
//		String url =  "http://car.autohome.com.cn/config/series/790.html"; //790系列参数配置
//		String url = "http://www.autohome.com.cn/790/";
//		String url =  "http://www.autohome.com.cn/a/";
//		String url = "http://www.autohome.com.cn/3170/#levelsource=000000000_0&pvareaid=101594";
//		String url = "http://www.autohome.com.cn/1021/#levelsource=000000000_0&pvareaid=101594";
//		String url = "http://www.autohome.com.cn/3427/#levelsource=000000000_0&pvareaid=101594";
//		String url =  "http://car.autohome.com.cn/config/series/2791.html";
//		String url= "http://www.autohome.com.cn/a/";
		String url= "http://car.autohome.com.cn/config/series/3170.html";
		
		
		JsoupUtil jsp = new JsoupUtil();
		
//		System.out.println(jsp.getCarSeriesUrl(url));
//		System.out.println(jsp.getCarSeriesZongshu(url));
//		System.out.println(jsp.getCarSeriesInfo(url));
//		System.out.println(jsp.getCarSeriesZongshu(url));
		System.out.println(jsp.getCarSeriesSetting(url));
		
//		System.out.println(jsp.getCarSeriesUrl(url));
		
		String tUrl = "http://www.autohome.com.cn/370/#levelsource=000000000_0&pvareaid=101594";
//		String tUrl ="http://www.autohome.com.cn/3427/#levelsource=000000000_0&pvareaid=101594";
		
		
		String cot = HttpClientUtils.get(tUrl);
		Document doc = Jsoup.parse(cot);
		Elements es=doc.getElementsMatchingOwnText("参数配置");
		String csUrl = es.get(0).attr("href");
//		System.out.println(csUrl);
		
/*
		String htcont = HttpClientUtils.get(url);
		Document doc = Jsoup.parse(htcont);
		
		//获取车系参数配置
		Map<String,Object> carSerisSetting = new LinkedHashMap<String, Object>();
		
		Elements carSeris = doc.getElementsByTag("script").eq(7);
		
		循环遍历script下面的JS变量  
        for (Element element : carSeris) {  
              
            取得JS变量数组  
            String[] data = element.data().toString().split("var");  
              
            取得单个JS变量  
            for(String variable : data){  
                  
                过滤variable为空的数据  
                if(variable.contains("=")){  
                      
                    取到满足条件的JS变量  
                    if(variable.contains("option") || variable.contains("config")){ 
                          
                        String[]  kvp = variable.split("=");  
                          
                        取得JS变量存入map  
                        if(!carSerisSetting.containsKey(kvp[0].trim()))   
                        	carSerisSetting.put(kvp[0].trim(), kvp[1].trim().substring(0, kvp[1].trim().length()-1).toString());  
                    }  
                }  
            }  
        }  
        System.out.println(carSerisSetting);
    */
		
//		Elements carSettings = carSeris.get(0).getElementsByTag("script");
//		System.out.println(carSettings.size());
//		System.out.println(carSettings.get(0).toString());
		
		
		/*
		//首页全部车型
		ArrayList<String> alty = new ArrayList<String>();
		Elements allCarTypes=doc.getElementsByAttributeValue("class", "rank-list-ul");
		for(Element allCarType: allCarTypes){
			Elements carSeries = allCarType.getElementsByTag("h4");
			for(Element carSerie: carSeries){
//				System.out.println(carSerie.text());
				alty.add(carSerie.text());
			}
		}
		
		System.out.println(alty);
		
		Map<String,Object> myData = new LinkedHashMap<String, Object>();
		myData.put("http://www.autohome.com.cn/grade/carhtml/D.html", alty);
		
		JSONConfig jc = new JSONConfig();
		jc.setValidatePropertyNames(false);
//		System.out.println(JSONUtil.toJSON(myData, jc));
		*/
		
		/*
		//Official example
		Integer x = 5;
		int[] a = {1,1,2,3,5,8,13};
		Map<String,Object> md = new LinkedHashMap<String, Object>();
		md.put("x", x);
		md.put("a", a);
//		myData.put("bundle", ResourceBundle.getBundle("some.bundle.name", locale));
		md.put("status", "success");
		String jsonStr = JSONUtil.toJSON(md);
		System.out.println(jsonStr);
		*/
		/*
		JsonObject jb = new JsonObject(jc);
		jb.add("http://www.autohome.com.cn/grade/carhtml/D.html", "D-Serie");
		jb.add("http://www.autohome.com.cn/grade/carhtml/E.html", "E-Serie");
		System.out.println(jb.toJSON());;
		*/
		
/*		//新车指导价
		Elements divs1=doc.getElementsByAttributeValue("class", "autoseries-info");
		Elements dts=divs1.get(0).getElementsByTag("dt");
		System.out.println(dts.get(0).text());
		*/
		
/*		//2016款用户评分
		Elements divs2=doc.getElementsByAttributeValue("class", "koubei-score");
		Elements as=divs2.get(0).getElementsByAttributeValue("class", "font-score");
		System.out.println(as.get(0).text());
		*/
		
		//当前位置 
		//eg:内容纠错 当前位置：首页>紧凑型车>奥迪A3>参数配置
//		Elements dqwzs=doc.getElementsByAttributeValue("class", "path");
//		System.out.println(dqwzs.get(0).text());
		
		
/*		//JSON
//		String el="{\"name\":\"车型名称\",\"link\":\"http://car.autohome.com.cn/shuyu/detail_18_19_567.html\"}";
		String el="[{\"specid\":25898,\"value\":\"奥迪A3 2016款 Sportback 35 TFSI 进取型\"},{\"specid\":25899,\"value\":\"奥迪A3 2016款 Sportback 35 TFSI 领英型\"}]";
//		JSONObject jsob=(JSONObject)JSONParser.parseJSON(el);
		System.out.println(JSONParser.parseJSON(el));
		
		*/
//		for (Element div: divs){
//			Elements dls = div.getElementsByTag("dl");
//			Element dl = dls.get(0);
//			Elements dts=dl.getElementsByTag("dt");
//			System.out.println(dts.get(0).text());
//			
//		}
		
//		Element content = doc.getElementById("content");
//		Elements links = content.getElementsByTag("新车指导价");
//		for (Element link : links) {
//		  String linkHref = link.attr("href");
//		  String linkText = link.text();
//		  
//		  System.out.println(linkText);
//		}
//	  	String  html="<p><a href=\"a.html\">a</p><p> 文本</p>";
//	  	String html="<div class=\"pop_forum\"><div class=\"pf_inner\"><div class=\"pf_tt\"><h2 class=\"pf_tab\"><a id=\"auto-header-club-tab0\" href=\"javascript:void(0);\" target=\"_self\" tabindex=\"0\" class=\"cur\">找论坛</a><a id=\"auto-header-club-tab1\" href=\"javascript:void(0);\" target=\"_self\" tabindex=\"1\">全部车系</a><a id=\"auto-header-club-tab2\" href=\"javascript:void(0);\" target=\"_self\" tabindex=\"2\">全部地区</a><a id=\"auto-header-club-tab3\" href=\"javascript:void(0);\" target=\"_self\" tabindex=\"3\">全部主题</a><a id=\"auto-header-club-tab4\" href=\"javascript:void(0);\" target=\"_self\" tabindex=\"4\">摩托车论坛</a></h2><a id=\"auto-header-clubclose\" class=\"ico_close\" href=\"javascript:void(0);\" target=\"_self\">关闭</a></div><div id=\"auto-header-clubhtml\" class=\"pf_cont\"></div></div></div>";
////	    Document doc = Jsoup.parse(html);
//	    
//	    Elements test = doc.getElementsByTag("dl");
//	    Elements ele=doc.getElementsByAttributeValue("class", "pf_tab");
////	    Elements ele=doc.getElementsByTag("div");
//	    //ele.attr("class", "").get(0);
//	    for(Element e :ele)
//	    {
//	    	Elements eas = e.getElementsByAttributeValue("tabindex", "1");
//	    	for(Element ea : eas){
//	    		System.out.println(ea.text());
//	    	}
//	          System.out.println(e.text());
//	        
//	    }
	}

//	Map<String, Object> carSeriesInfo  = new LinkedHashMap<String, Object>();
	//获取某系车所有车型的参数配置
	public ArrayList<String> getCarSeriesSetting(String carSeriesSettinggUrl) throws Exception{

		try{		
			String htcont = HttpClientUtils.get(carSeriesSettinggUrl);
		}catch(Exception e){
			e.printStackTrace();
		}
		String htcont = HttpClientUtils.get(carSeriesSettinggUrl);
		Document doc = Jsoup.parse(htcont);
		
//		Map<String,Object> carSerisSetting = new LinkedHashMap<String, Object>();
		ArrayList<String> opts = new ArrayList<String>();
		
		Elements carSeris = doc.getElementsByTag("script").eq(7);
		
		/*循环遍历script下面的JS变量*/  
        for (Element element : carSeris) { 
//              opts.add(element.toString());
            /*取得JS变量数组*/
            String[] data = element.data().toString().split("var ");  
//              System.out.println(data.length);
            /*取得单个JS变量*/
            for(String variable : data){ 
//                  opts.add(variable);
//                过滤variable为空的数据  
                if(variable.contains("=")){ 
                      
//                    取到满足条件的JS变量  
                    if(variable.contains("paramtypeitems") || variable.contains("configtypeitems")){
                        String[]  kvp = variable.split("=");  
                    	opts.add(kvp[1]);
                    }
                }
            }  
        }  
		return opts;
	}
	
	//获取车系综述信息
	//名称，新车指导价，用户评分，当前位置
	public Map<String, Object> getCarSeriesZongshu(String carSeriesUrl) throws Exception {
		 
		System.out.println(carSeriesUrl);
		
		Map<String, Object> carSeriesInfo  = new LinkedHashMap<String, Object>();
		
		String htcont = HttpClientUtils.get(carSeriesUrl);
		Document doc = Jsoup.parse(htcont);
		
		Elements es = new Elements();
	
		//车系ID和名称
//		Elements es=doc.getElementsByAttributeValue("class", "uibox uibox-car-pic");
		es=doc.getElementsByAttributeValue("class", "subnav-title-name");
		String csID = es.get(0).getElementsByTag("a").get(0).attr("href");
//		zs.add(csID.replace("/", ""));
		carSeriesInfo.put("车系ID", csID.replace("/", ""));
		
		String csName = es.get(0).getElementsByTag("a").get(0).text();
//		zs.add(csName);
		carSeriesInfo.put("车系名称", csName);
		
		//车系URL
		String csUl=carSeriesUrl;
		carSeriesInfo.put("车系URL", csUl);
		
		//判断该车系是否在售
		es=doc.getElementsByAttributeValue("class", "other-car");
		int aTagNum = es.get(0).getElementsByTag("a").size();
		
		if(aTagNum>1){
			
			//当前位置
			es=doc.getElementsByAttributeValue("class", "path fn-clear");
			String csLocation = es.get(0).text();
//			zs.add(csLocation);
			carSeriesInfo.put("当前位置", csLocation.replaceAll("当前位置：", ""));
			
			//新车指导价
			es=doc.getElementsByAttributeValue("class", "autoseries-info");
			String csPrice = es.get(0).getElementsByTag("dt").get(0).text();
//			zs.add(csPrice);
			carSeriesInfo.put("新车指导价", csPrice.replaceAll("\\(.*\\)", "").replaceAll("新车指导价：", ""));
			
			//用户评分
			es=doc.getElementsByAttributeValue("class", "koubei-score");
			Element e=es.get(0);
			Elements ts=e.getElementsByTag("div");
			String csScore=ts.get(1).text();
//			zs.add(csScore);
			carSeriesInfo.put("评分", csScore);
			
			//参数配置URL
			es=doc.getElementsMatchingOwnText("参数配置");
			String csUrl = es.get(0).attr("href");
//			zs.add(csUrl);
			carSeriesInfo.put("参数配置URL", csUrl);
		}else{
			carSeriesInfo.put("当前位置", "已停售");
			carSeriesInfo.put("新车指导价", "已停售");
			carSeriesInfo.put("评分", "已停售");
			carSeriesInfo.put("参数配置URL", new String());
		}

		return carSeriesInfo;
	}
	
	//获取车系URL
	public ArrayList<String> getCarSeriesUrl(String autoHomeUrl) throws Exception {
		String htcont = HttpClientUtils.get(autoHomeUrl);
		Document doc = Jsoup.parse(htcont);
		
//		Map<String,Object> carSerisZongshu = new LinkedHashMap<String, Object>();
		ArrayList<String> zsUrl = new ArrayList<String>();
		ArrayList<String> urls  = new ArrayList<String>();
		
		Elements es = new Elements();
		es = doc.getElementsByTag("h4");
		for(Element e: es){
			Elements esi=e.getElementsByTag("a");
			zsUrl.add(esi.get(0).attr("href"));
		}
		
		for(int i=0; i<zsUrl.size(); i++){
			String ul=zsUrl.get(i).trim();
			if(ul.substring(ul.length()-6).equals("101594")){
				urls.add(ul);
			}
		}
		return urls;
	}
	
	//获取车系全部信息
	public String getCarSeriesInfo(String carSeriesUrl) throws Exception{
		
		Map<String, Object> zsMap=getCarSeriesZongshu(carSeriesUrl);
		
		String cssUrl = (String)zsMap.get("参数配置URL");
		
		Map<String, Object> stMap=new LinkedHashMap<String, Object>();
		
		if(cssUrl != null & !cssUrl.equals("")){
//			String carSetting = JSOUtils.getJSText(getCarSeriesSetting(cssUrl));
			LinkedHashMap<String, Map<String, String>> carSetting = JSOUtils.getJSText(getCarSeriesSetting(cssUrl));
			zsMap.put("参数配置", carSetting);
		}

		JSONObject job = JSONObject.fromObject(zsMap);
		String zsText = job.toString();
		
		return zsText; 
//		return js.toString();
	}
	
	
}
