package com.ai.autohome.autohome.spider;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import net.sf.json.JSONObject;
import weibo.HttpClientWeibo;

import org.jsoup.nodes.Element;

public class JsoupTest {

	public static void main(String[] args){
		String h="<html> <head></head> <body>  <div class=\"WB_cardwrap S_bg2\">   <div class=\"PCD_text_b PCD_text_b2\">    <div class=\"WB_cardtitle_b S_line2\">     <div class=\"obj_name\">      <h2 class=\"main_title W_fb W_f14\">基本信息</h2>     </div>    </div>    <div class=\"WB_innerwrap\">     <div class=\"m_wrap clearfix\">      <ul class=\"clearfix\">       <li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">昵称：</span><span class=\"pt_detail\">冷笑话彡</span></li>       <li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">所在地：</span><span class=\"pt_detail\">北京 东城区</span></li>       <li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">性别：</span><span class=\"pt_detail\">男</span></li>       <li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">个性域名：</span><span class=\"pt_detail\"><a href=\"http://weibo.com/uc87983677?from=inf&amp;wvr=5&amp;loc=infdomain\">http://weibo.com/uc87983677</a></span></li>       <li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">简介：</span><span class=\"pt_detail\">微博搞笑中心！分享新鲜、时尚、搞笑信息~关注我，获得每日新鲜笑料！↖(^ω^)↗</span></li>       <li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">注册时间：</span><span class=\"pt_detail\">2012-02-07</span></li>       </ul>     </div>    </div>   </div>  </div>  "
				+ "<!--//模块-->  <!--模块-->  <!--//模块-->  <!--模块-->  <!--模块-->   <div class=\"WB_cardwrap S_bg2\">   <div class=\"PCD_text_b PCD_text_b2\">    <div class=\"WB_cardtitle_b S_line2\">     <div class=\"obj_name\">      "
				+ "<h2 class=\"main_title W_fb W_f14\">标签信息</h2>     </div>    </div>    <div class=\"WB_innerwrap\">     <div class=\"m_wrap clearfix\">      <ul class=\"clearfix\">       <li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">标签：</span><span class=\"pt_detail\"><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&amp;tag=%E5%86%B7%E7%AC%91%E8%AF%9D\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>冷笑话</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&amp;tag=%E5%AE%85%E5%A5%B3\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>宅女</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&amp;tag=%E6%90%9E%E7%AC%91\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>搞笑</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&amp;tag=%E5%B9%BD%E9%BB%98\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>幽默</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&amp;tag=%E5%B0%8F%E8%AF%B4\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>小说</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&amp;tag=%E7%AC%91%E8%AF%9D\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>笑话</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&amp;tag=90%E5%90%8E%E9%9D%9E%E8%84%91%E6%AE%8B\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>90后非脑残</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&amp;tag=%E7%BE%8E%E5%A5%B3\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>美女</a></span></li>       </ul>     </div>    </div>   </div>  </div>  </body></html>";
		
		String body="<div class=\"WB_cardwrap S_bg2\"><div class=\"PCD_text_b PCD_text_b2\"><div class=\"WB_cardtitle_b S_line2\"><div class=\"obj_name\"><h2 class=\"main_title W_fb W_f14\">基本信息</h2></div></div><div class=\"WB_innerwrap\"><div class=\"m_wrap clearfix\"><ul class=\"clearfix\"><li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">昵称：</span><span class=\"pt_detail\">冷笑话彡</span></li><li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">所在地：</span><span class=\"pt_detail\">北京 东城区</span></li><li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">性别：</span><span class=\"pt_detail\">男</span></li><li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">个性域名：</span><span class=\"pt_detail\"><a href=\"http://weibo.com/uc87983677?from=inf&wvr=5&loc=infdomain\">http://weibo.com/uc87983677</a></span></li><li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">简介：</span><span class=\"pt_detail\">微博搞笑中心！分享新鲜、时尚、搞笑信息~关注我，获得每日新鲜笑料！↖(^ω^)↗</span></li><li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">注册时间：</span><span class=\"pt_detail\">2012-02-07</span></li>                </ul></div></div></div></div>"
				+ "<!--//模块--><!--模块--><!--//模块--><!--模块--><!--模块-->          <div class=\"WB_cardwrap S_bg2\"><div class=\"PCD_text_b PCD_text_b2\"><div class=\"WB_cardtitle_b S_line2\"><div class=\"obj_name\">"
				+ "<h2 class=\"main_title W_fb W_f14\">标签信息</h2></div></div><div class=\"WB_innerwrap\"><div class=\"m_wrap clearfix\"><ul class=\"clearfix\"><li class=\"li_1 clearfix\"><span class=\"pt_title S_txt2\">标签：</span><span class=\"pt_detail\"><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&tag=%E5%86%B7%E7%AC%91%E8%AF%9D\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>冷笑话</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&tag=%E5%AE%85%E5%A5%B3\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>宅女</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&tag=%E6%90%9E%E7%AC%91\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>搞笑</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&tag=%E5%B9%BD%E9%BB%98\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>幽默</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&tag=%E5%B0%8F%E8%AF%B4\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>小说</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&tag=%E7%AC%91%E8%AF%9D\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>笑话</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&tag=90%E5%90%8E%E9%9D%9E%E8%84%91%E6%AE%8B\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>90后非脑残</a><a target=\"_blank\" node-type=\"tag\" href=\"http://s.weibo.com/user/&tag=%E7%BE%8E%E5%A5%B3\" class=\"W_btn_b W_btn_tag\"><span class=\"W_arrow_bor W_arrow_bor_l\"><i class=\"S_line3\"></i><em class=\"S_bg2_br\"></em></span>美女</a></span></li>        </ul></div></div></div></div>";
		
		String basicInfoScript = "";
		String basicInfoFragment="";
		String tag="";
		int tagNum=0;
		
		HashMap<String, String> infoMap=new HashMap<>();
		
//		String url="http://weibo.com/87983677/info";
		
		String url="http://weibo.com/1000183485/info";
		
		String webHTML=HttpClientWeibo.get(url);
		
		Document webHTMLDoc=Jsoup.parse(webHTML);
		
//		System.out.println(dd.getElementsByTag("script"));
		
		Elements scriptElements = webHTMLDoc.getElementsByTag("script");
		for(Element e: scriptElements){
			if(e.toString().contains("基本信息")){
				basicInfoScript=e.toString();
//				System.out.println(e);
			}
		}
		
	    Pattern basicInfoPattern=Pattern.compile("<div.*div>");  
	    Matcher basicInfoMatcher=basicInfoPattern.matcher(basicInfoScript);  
//	    while(basicInfoMatcher.find()){
	    if(basicInfoMatcher.find()){
	    	String temp=basicInfoMatcher.group();
	    	basicInfoFragment=temp.replace("\\r", "").replace("\\n", "").replace("\\t", "").replace("\\", "");
//	    	System.out.println(basicInfoFragment);
	    }  
	    
		Document docBody = Jsoup.parseBodyFragment(basicInfoFragment);
		
		Elements basicInfoElement = docBody.getElementsByTag("ul");
		System.out.println(basicInfoElement.size());
//		System.out.println(e_tag.text());
		
		for(Element e: basicInfoElement.get(0).getElementsByTag("li")){
//			System.out.println(e.text());
			String[] info=e.text().split("：", 2);
			if(info.length==2){
				infoMap.put(info[0], info[1]);
			}
		}

		for(Element e: basicInfoElement.get(1).getElementsByTag("a")){
//			System.out.println(e.text());
			if(tagNum==0){
				tag=e.text();
				tagNum++;
			}else{
				tag=tag+"_"+e.text();
			}
			infoMap.put("标签", tag);
		}
		
//		System.out.println(infoMap);
		
		JSONObject jsonObject=JSONObject.fromObject(infoMap);
		
//		System.out.println(jsonObject.toString());
		
//		for(Element eo: e_tag_b){
//			System.out.println(eo.text());
//		}
	    
		/*
		Document doc = Jsoup.parse(h);
		
		Elements e = doc.getElementsByClass("main_title W_fb W_f14");
//		System.out.println(e.text()+"\n");
		
		Elements biaoQian=doc.getElementsByClass("obj_name");
//		System.out.println(biaoQian.text());
		
		Elements e_tag = doc.getElementsByTag("ul");
//		System.out.println(e_tag.size());
//		System.out.println(e_tag.text());
		
//		for(Element eo: e_tag){
//			System.out.println(eo.text());
//		}
		*/

	}
	
}
