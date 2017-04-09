package com.ai.autohome.spider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import net.sf.json.util.JSONStringer;
import weibo.HttpClientWeibo;

public class WeixinHTMLAnalysis implements Runnable {

	public static Logger logger = Logger.getLogger(WeixinHTMLAnalysis.class);
	
	public static void main(String[] args) throws Exception{
		String url="http://mp.weixin.qq.com/s?__biz=MjM5MTQ2NzA4Mg==&mid=2649699894&idx=5&sn=11bc54ea0bd468f33526cea79b690cf7";
		String url2="http://mp.weixin.qq.com/s?__biz=MzA4NDMwNDkxNA%3D%3D&mid=2656775145&idx=1&sn=fb478178f0eed2aadd457a47a8f3c41a&chksm=8446be94b33137820caca32df745e2eceb7cc4b2bc91cfd39df76c167f86087afb4542ee0a22&scene=0&ascene=2&from=timeline&isappinstalled=0&";
		
		ArrayBlockingQueue<String> q = new ArrayBlockingQueue<String>(10000);
		q.add(url);
		q.add(url2);
		q.add("Done");
		HashSet<String> s= new HashSet<String>();
		CountDownLatch cd = new CountDownLatch(1);
		
		String f="E:\\Documents\\微信公众号\\tmp\\test";
		BufferedWriter w=null;
		try{
			w = new BufferedWriter(new FileWriter(f));
			
			WeixinHTMLAnalysis wa = new WeixinHTMLAnalysis(q, s, w, cd);
			wa.run();
		}finally{
			w.close();
		}

//		System.out.println();
	}
	
	ArrayBlockingQueue<String> urlQueue = new ArrayBlockingQueue<String>(10000);
	HashSet<String> urlSet = new HashSet<String>();
	BufferedWriter writer = null;
//	PrintStream writer = new PrintStream(new File("E:\\Documents\\微信公众号\\tmp\\multi_files"));
	private boolean flag = true;
	boolean isFinish  = false;
	private CountDownLatch countDownLatch;
	
	public WeixinHTMLAnalysis(ArrayBlockingQueue<String> queue, HashSet<String> hst, BufferedWriter wt, CountDownLatch cd){
		this.urlQueue=queue;
		this.urlSet=hst;
		this.writer=wt;
		this.countDownLatch=cd;
	}
	
	public void run(){
		try{
			while(flag){//循环处理urlQueue直到遇到结束标志
				try{
					String ul=urlQueue.take();
					logger.info("Start request from --> "+ul);
					//线程结束判断
					if(ul.equals("Done")){
						flag=false;
						urlQueue.put("Done");
					}else{
						/*//判断是否已爬取
						String id = ul.split("__biz=")[1].split("\\&")[0];
						if(!urlSet.contains(id)){
							try{
								writer.println(outputJson(getValues(ul)));
								urlSet.add(id);//将爬取之后的ID添加到hashset
							}catch(Exception ei){
								ei.printStackTrace();
							}
						}*/
//						urlSet.add(ul);
						writer.write(outputJson(getValues(ul))+"\n");
					}
					logger.info("Complete request from --> "+ul);
				}catch(Exception e){
//					e.printStackTrace();
					logger.info(e);
				}
			}
		}finally{
			countDownLatch.countDown();
		}

	}
	
	/**
	 * 获取解析后的JSON格式的内容
	 * @param url :top josn key
	 * @param html 
	 * @return
	 * @throws Exception 
	 */
	/*public String getContent(String url, String html){
		
		String accountID_key = "公众号ID";
//		String accountID_val = url.split("=")[1];
		String accountID_val = url.split("__biz=")[1].split("&")[0];
//		String topKey = url.split("=")[1];
		String nickname_key="公众号名称";
		String nickname_val="";
		String name_key="微信号";
		String name_val="";
		String function_key="功能介绍";
		String function_val="";
		
		Document doc = Jsoup.parse(html);
		Elements profile_inners=doc.getElementsByAttributeValue("class", "profile_inner");
		if(profile_inners.size()>0){
			nickname_val=profile_inners.get(0).getElementsByAttributeValue("class", "profile_nickname").text();

			Elements profile_metas=profile_inners.get(0).getElementsByAttributeValue("class", "profile_meta");
			if(profile_metas.size()==2){
				name_val=profile_metas.get(0).getElementsByAttributeValue("class", "profile_meta_value").text();
				name_val.replace("\t", "?t");//hive use
				function_val=profile_metas.get(1).getElementsByAttributeValue("class", "profile_meta_value").text();
			}
		}
		
		JSONStringer jtest = new JSONStringer();
		
		jtest.object().key(topKey).array();
				jtest.value(nickname);
				jtest.object().key(name_key).value(name_val).endObject();
				jtest.object().key(function_key).value(function_val).endObject();
			jtest.endArray();
		jtest.endObject();
		
			
		jtest.object().key(topKey).array()
			 .value(nickname)
			 .object().key(name_key).value(name_val).endObject()
			 .object().key(function_key).value(function_val).endObject()
			 .endArray()
			 .endObject();
		
		jtest.object()
			.key(accountID_key).value(accountID_val)
			.key(nickname_key).value(nickname_val)
			.key(name_key).value(name_val)
			.key(function_key).value(function_val)
			.endObject();
		
		return jtest.toString();
	}*/
	
	/**
	 * 从网页获取输出结果中的值
	 * @param url 待爬取网页地址
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getValues(String url) throws Exception{
		ArrayList<String>  vals = new ArrayList<String>();
		
		String webUrl = url;
		String accountID_val = url.split("__biz=")[1].split("&")[0];
		String nickname_val="";
		String name_val="";
		String function_val="";
		String article_title="";
		String article_content="";
		
//		String html = HttpClientUtils.get(url);
		String html = HttpClientWeibo.get(url);
		Document doc = Jsoup.parse(html);
		//获取公众号信息:nickname,account,founction
		Elements profile_inners=doc.getElementsByAttributeValue("class", "profile_inner");
		if(profile_inners.size()>0){
			nickname_val=profile_inners.get(0).getElementsByAttributeValue("class", "profile_nickname").text();
			Elements profile_metas=profile_inners.get(0).getElementsByAttributeValue("class", "profile_meta");
			if(profile_metas.size()==2){
				name_val=profile_metas.get(0).getElementsByAttributeValue("class", "profile_meta_value").text();
//				name_val.replace("\t", " ");//hive use
				function_val=profile_metas.get(1).getElementsByAttributeValue("class", "profile_meta_value").text();
//				function_val.replace("\t", " ");
			}
		}
		
		//获取文章标题和文字内容
		article_title=doc.getElementsByAttributeValue("class", "rich_media_title").get(0).text();
		article_content=doc.getElementsByAttributeValue("class", "rich_media_content").get(0).text();
		
		vals.add(webUrl);
		vals.add(accountID_val);
		vals.add(nickname_val);
		vals.add(name_val);
		vals.add(function_val);
		vals.add(article_title);
		vals.add(article_content);
		System.out.println(Thread.currentThread()+" "+vals);
		return vals;
	}
	
	/**
	 * 将爬取结果组合为JSON格式输出
	 * @param valuesArrary
	 * @return
	 */
	public String outputJson(ArrayList<String> valuesArrary){
		
		ArrayList<String> vals=valuesArrary;
		
		//制表符替换，"\t"-->"  "
		for(String s: valuesArrary){
			s.replace("\t", "  ");
		}
		
		String url_key = "网址URL";
		String accountID_key = "公众号ID";
		String nickname_key="公众号名称";
		String name_key="微信号";
		String function_key="功能介绍";
		String ac_title="文章标题";
		String ac_content="文章内容";
		
		JSONStringer jtest = new JSONStringer();
		jtest.object()
			.key(url_key).value(vals.get(0))
			.key(accountID_key).value(vals.get(1))
			.key(nickname_key).value(vals.get(2))
			.key(name_key).value(vals.get(3))
			.key(function_key).value(vals.get(4))
			.key(ac_title).value(vals.get(5))
			.key(ac_content).value(vals.get(6))
			.endObject();
		
		return jtest.toString();
	}
}
