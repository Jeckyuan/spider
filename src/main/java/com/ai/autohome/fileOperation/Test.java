package com.ai.autohome.fileOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
//import java.sql.Date;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.ai.autohome.spider.HttpClientUtils;

import weixin.HttpClientSample;

public class Test {

	public static Logger logger=Logger.getLogger(Test.class);
	
	public static void main(String[] args){
		String in = "http://mp.weixin.qq.com/s?__biz=MzI3NDE1Mjk4NQ==&mid=2649893196&idx=1&sn=568b6e0bff6dec66d28bee8ff62fd8b1&";
		String in2 = "http://mp.weixin.qq.com/s?__biz=MzI5NDA1MzQwMg==&mid=2649997803&idx=1&sn=8afefe875196e2f16b2d1845b8537533& 30";
//		Pattern pt = Pattern.compile(".*http://mp.weixin.qq.com/\\s?__biz=.*==\\&mid=.*\\&idx=.\\&sn=.*\\&.*");
		Pattern pt = Pattern.compile("(http://mp.weixin.qq.com/s\\?__biz=.*==&mid=.*&idx=.*&sn=.*&).*");
		Matcher mt = pt.matcher(in2);
//		String url="http://mp.weixin.qq.com/s?__biz=MjM5MTQ2NzA4Mg==&mid=2649699894&idx=5&sn=11bc54ea0bd468f33526cea79b690cf7";
//		String url="http://mp.weixin.qq.com/s?__biz=MjM5NTM1ODIxNw%3D%3D&mid=2653706824&idx=6&sn=8cd85a4e7f79c2b7e5acf15eb59496bf&chksm=bd21492e8a56c03893593d32e60324f87e661434e25fbb3a660128a872b4b69d675ec778896b&scene=0&ascene=7&visitDstTime=1478692009250&";
		String[] urlArrary = {
				"http://mp.weixin.qq.com/s?__biz=MzI5NDA1MzQwMg==&mid=2649997803&idx=1&sn=8afefe875196e2f16b2d1845b8537533",
				"http://mp.weixin.qq.com/s?__biz=MjM5NTM1ODIxNw%3D%3D&mid=2653706824&idx=6&sn=8cd85a4e7f79c2b7e5acf15eb59496bf&chksm=bd21492e8a56c03893593d32e60324f87e661434e25fbb3a660128a872b4b69d675ec778896b&scene=0&ascene=7&visitDstTime=1478692009250&",
				"http://mp.weixin.qq.com/s?__biz=MjM5MDA0NDI2MA%3D%3D&mid=2653348696&idx=1&sn=5c624bf463db2802a54313fae6a89363&scene=26&ascene=0&"
		};
	
		Date dNow = new Date();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
//		System.out.println("Current Date: " + dateFormat.format(dNow));
		
		
		String[] urisToGet = {
                "http://mp.weixin.qq.com/s?__biz=MzI2NzMxOTY4NA==&idx=1&sn=8d3c4865fdac5c9487092c45c94648b0&chksm=ea81e4f5ddf66de3a90e08d0d2864cc0125478f49835307e8893b827b9044a840cc8696af7a6&mpshare=1&scene=1&srcid%",
                "http://mp.weixin.qq.com/s?__biz=MjM5Njc3NDQ0Mg==&mid=2667120999&sn=437783a5ba0df5a01af3fa21ae0f6495&chksm=bde9be228a9e373417cee08ef3c326da94239f030e54ffa5d23259468761a187218e7e3618a4&mpshare=1&scene=2&srcid=1113GHaI6kK39slJwAmpbYLZ&from=timeline&isappinstalled=0&key=cde9f53f8128acbd67acdd59e62a2cf07610cab760a2508b2ee8e86d4e377502aaee30cd014f1372818d0db90eceece9&ascene=2&uin=MTcxNzQ5MzUwMg%3D%3D&devicetype=android-22&version=2603163b&nettype=cmnet&pass_ticket=EJ5V9DwW2jmPpf14tUG6NDxN%a",
                "http://mp.weixin.qq.com/s?__biz=MzA3Njg2ODcxNw==&sn=a91b7f0b1f05773e50fae80277db2c2d&chksm=8591b824b2e631322361a7b5e56f7307df572bf36071c1452b933f382f52208eecffc77b050e&mpshare=1&scene=2&srcid=1024W5sG55YEURTGDX9fLmuQ&from=timeline&isappinstalled=0&key=c3acc508db720376b569ae8eef0acac045bfa0176823ca92962a14082207ac8024287f0b43e2335c2332cd781dd5358f&ascene=2&uin=MjU4MjY0MzExMA%3D%3D&devicetype=android-19&version=26031933&nettype=cmnet&pass_ticket=jET0vPkpG79xPO6CRylxn%2Fg%ab",
                "http://mp.weixin.qq.com/s?__biz=MzI4NDE3Nzc2OA==&chksm=f3fe4f71c489c6672369ae70b03ea3310df6cae2f0c76308cd3f7dff9b1297ab440c83610232&mpshare=1&scene=1&srcid=10291zHaTjkTyLQkyhqj7Mwu&from=groupmessage&isappinstalled=0&key=cde9f53f8128acbde0df0076da5ccbba394081838ceaee964530be2e994afcc063ab76e37621521f164f0989ff31312b&ascene=1&uin=MjIxMTcxMTMyMQ%3D%3D&devicetype=android-23&version=26031b31&nettype=3gnet&pass_ticket=IisGERHk%2BkG21%2Fci%abc", 
                "http://mp.weixin.qq.com/s?__biz=MzI0MTI4NTkyMQ==&amp;mid=402786954&amp;idx=1&amp;sn=6c937add0b779b96070926b98f1de5b7&amp;scene=25&scene=25&key=c3acc508db7203763bc5847648eae36ec80b17a9381dd90b4811e00c78d5cb39ddad3b1b20b1f57f36ae19e481171cd5&ascene=1&uin=MzgyNjk5NTEz&devicetype=android-22&version=26031b31&nettype=3gwap&pass_ticket=k5Woqqgy8Bfjbf55qyOuFJ5hfetwL4BUUS6Hvas50liUJdPk8DeM7dNVbb6s5eZ1&wx_header=1",
                "http://mp.weixin.qq.com/s?__biz=MjM5NDI5MTgyMA==&srcid=0112qAk4XqoaZabHosE5Z8cc&mid=407095254&from=singlemessage&sn=b0df457359b1e33bf5f537fce50e42d6&idx=2&isappinstalled=0&scene=1&sid="};
		
		for (String s:urisToGet){
//			s.replaceAll("%", "");
			String sr=s.replaceAll("%.?$", "");
//			System.out.println(sr);
			
//			System.out.println(s.split("s\\?")[0]+"<>"+s.split("s\\?")[1]);
			
			String parms = s.split("s\\?")[1];
			
			String biz = "";
			String mid = "";
			String idx = "";
			String sn = "";
			
			for(String p: parms.split("&")){
				if(p.split("=")[0].contains("__biz")){
					biz = p.split("=")[1];
				}else if(p.split("=")[0].contains("mid")){
					mid = p.split("=")[1];
				}else if(p.split("=")[0].contains("idx")){
					idx = p.split("=")[1];
				}else if(p.split("=")[0].contains("sn")){
					sn = p.split("=")[1];
				}
			}
			
			String arId = mid+"_"+idx+"_"+sn;
//			System.out.println(arId);
		}

		String url1 = "http://mp.weixin.qq.com/s?__biz=MjM5NTc5MzI0Mw==&mid=2651019575&idx=1&sn=15d068858a61571a038b0f2363%E9%94%9F%E7%B5%A2NUS%E9%94%9F%E6%96%A4%E6%8B%B7B%E9%94%9F%E7%B5%AA%E9%94%9F%E6%96%A4%E6%8B%B7%22";
		String url2="http://mp.weixin.qq.com/s?__biz=MzAwNDAyODk4NA%3D%3D&mid=205530081&idx=2&sn=5a1ef50bd77e5c40fa833c33aae0a1a0&scene=7&ascene=0&";
		String url3="http://mp.weixin.qq.com/s?__biz=MjM5NTc5MzI0Mw==&mid=2651019575&idx=1&sn=15d068858a61571a038b0f2363";
		String url4="http://mp.weixin.qq.com/s?__biz=MzI0ODQ3MzkyMg%3D%3D&mid=2247483653&idx=3&sn=d0c9a0ec2dd8ae00e86233f49685d1d1&chksm=e9a17128ded6f83ec77ddc1c4eaac6f67b8b9fddcb35bf95c2ba2b3d1ba7e19454d26e312bc1&scene=7&ascene=0&";
		String url5="http://mp.weixin.qq.com/s?__biz=MzIzMjA1MDQ5MQ%3D%3D&mid=2651602451&idx=2&sn=4226467f91a26f3c62e0aa16c4479e2d&chksm=f3624447c415cd51608b50918c920b19fd79545d7fac5dedb47754de36175974e1dba1e78460&mpshare=1&scene=1&srcid=1019R0C0s9ca60RvMosag89B&from=singlemessage&isappinstalled=0&ascene=1&";
		String url6="http://mp.weixin.qq.com/s?__biz=MTAyMDM4OTA4MQ==&idx=1&mid=2650348301&sn=3234475d47bb48cd90fa73cd17e9d652";
		String url7="http://mp.weixin.qq.com/s?__biz=MjM5MDA2MTI1MA==&mid=2649084269&idx=1&sn=a2ff163bfd00e0c8a6837f53ed0575b8&chksm=be5bf6c0892c7fd6e47e4e878ec81646728a3f32a248879596626c9055459d750aa6850775a6&mpshare=1&scene=23&srcid=1107gCiDmhQs1p4Wj3R95cim";
		/*
//		String html = HttpClientSample.get(url7);
//		Document doc = Jsoup.parse(html);
		//获取公众号信息:nickname,account,founction
		Elements profile_inners=doc.getElementsByAttributeValue("class", "profile_inner");
		if(profile_inners.size()>0 && 
				doc.getElementsByAttributeValue("class", "rich_media_title").size()>0 && 
				doc.getElementsByAttributeValue("class", "rich_media_content").size()>0){
//			System.out.println("all is greater than 0");
			}
		*/
//		System.out.println(doc.getElementsByAttributeValue("class", "profile_inner").size());
//		
//		System.out.println(doc.getElementsByAttributeValue("class", "rich_media_title").size());
//		
//		String s=doc.getElementsByAttributeValue("class", "rich_media_content").text().replace("\n", "    ");
		
//		System.out.println(s);
		
		System.out.println(System.getProperty("user.dir"));
		
		System.out.println(System.getProperty("user.home"));
		
		logger.error("test01");
		
//		System.out.println(profile_inners.get(0).getElementsByAttributeValue("class", "profile_nickname").text());
//		
//		System.out.println(doc.getElementsByAttributeValue("class", "rich_media_title").get(0).text());
		/*if(profile_inners.size()>0 &&
				doc.getElementsByAttributeValue("class", "rich_media_title").size() > 0 && 
					doc.getElementsByAttributeValue("class", "rich_media_content").size() > 0)
			
			
		if(profile_inners.size()>0 &&
				doc.getElementsByAttributeValue("class", "rich_media_title").size() > 0 && 
					doc.getElementsByAttributeValue("class", "rich_media_content").size() > 0)
				
		if(profile_inners.size()>0 &&
				doc.getElementsByAttributeValue("class", "rich_media_title").size() > 0 && 
					doc.getElementsByAttributeValue("class", "rich_media_content").size() > 0)*/
		
		/*HashSet<String> hSet = new HashSet<>();
		
		boolean r1=hSet.add("test1");
		System.out.println(r1);
		
		Boolean r2=hSet.add("test2");
		System.out.println(r2);
		
		Boolean  r11=hSet.add("test1");
		System.out.println(r11);
		
		boolean r3=hSet.add("test3");
		System.out.println(r3);
		
		Boolean r33=hSet.add("test3");
		System.out.println(r33);
		*/
//		System.out.println(url.split("__biz=")[1].split("\\&")[0]);
		
		
	}
}
