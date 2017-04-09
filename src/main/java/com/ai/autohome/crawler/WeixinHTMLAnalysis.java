package com.ai.autohome.crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WeixinHTMLAnalysis implements Runnable {

	public static Logger logger = Logger.getLogger(WeixinHTMLAnalysis.class);
	
	ArrayBlockingQueue<String> urlQueue = new ArrayBlockingQueue<String>(10000);
	HashSet<String> urlSet = new HashSet<String>();
	BufferedWriter accountWriter = null;
	BufferedWriter articleWriter = null;
	private boolean flag = true;
	private CountDownLatch countDownLatch;
	
	
	/**
	 * for debug
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		String url="http://mp.weixin.qq.com/s?__biz=MjM5MTQ2NzA4Mg==&mid=2649699894&idx=5&sn=11bc54ea0bd468f33526cea79b690cf7";
		String url2="http://mp.weixin.qq.com/s?__biz=MzAwNDAyODk4NA%3D%3D&mid=205530081&idx=2&sn=5a1ef50bd77e5c40fa833c33aae0a1a0&scene=7&ascene=0&";
		
		ArrayBlockingQueue<String> q = new ArrayBlockingQueue<String>(10000);
		q.add(url);
		q.add(url2);
		q.add("Done");
		CountDownLatch cd = new CountDownLatch(1);
		
		String acf="E:\\Documents\\微信公众号\\tmp\\account_test";
		String arf="E:\\Documents\\微信公众号\\tmp\\article_test";
		BufferedWriter acw=null;
		BufferedWriter arw=null;
		try{
			acw = new BufferedWriter(new FileWriter(acf));
			arw = new BufferedWriter(new FileWriter(arf));
			
			WeixinHTMLAnalysis wa = new WeixinHTMLAnalysis(q, acw, arw, cd);
			wa.run();
		}finally{
			acw.close();
			arw.close();
		}
	}
	
	/**
	 * conatructor 
	 * @param queue 阻塞队列
	 * @param acw 账号writer
	 * @param arw 文章writer
	 * @param cd 线程结束判断
	 */
	public WeixinHTMLAnalysis(ArrayBlockingQueue<String> queue, BufferedWriter acw, BufferedWriter arw, CountDownLatch cd) {
		this.urlQueue=queue;
		this.accountWriter=acw;
		this.articleWriter=arw;
		this.countDownLatch=cd;
	}
	
	/**
	 * implement runable interface
	 */
	public void run(){
		try{
			while(flag){//循环处理urlQueue直到遇到结束标志
				try{
					String ul=urlQueue.take();
					
					String rp_ul = ul.replaceAll("%.?$", "");
					
					logger.info("Start request from --> "+ul);
					//线程结束判断
					if(ul.equals("Done")){
						flag=false;
						urlQueue.put("Done");
					}else{
						outputWriter(getValues(rp_ul));
					}
					logger.info("Complete request from --> "+ul);
				}catch(Exception e){
					logger.info(e);
				}
			}
		}finally{
			countDownLatch.countDown();
		}
	}
	
	/**
	 * 从网页获取输出结果中的值
	 * @param url 待爬取网页地址
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getValues(String url) {
		ArrayList<String>  vals = new ArrayList<String>();
		
		String webUrl = url;
		String accountID_val = url.split("__biz=")[1].split("&")[0];
		String nickname_val="";
		String name_val=" ";
		String function_val=" ";
		String article_title="";
		String article_content=" ";
		String article_id=url.split("&sn=")[1].split("&")[0];
		
//		String html = HttpClientUtils.get(url);
		String html = HttpClientSample.get(url);
		Document doc = Jsoup.parse(html);
		//获取公众号信息:nickname,account,founction
		Elements profile_inners=doc.getElementsByAttributeValue("class", "profile_inner");
		if(profile_inners.size()>0 &&
				doc.getElementsByAttributeValue("class", "rich_media_title").size() > 0 && 
					doc.getElementsByAttributeValue("class", "rich_media_content").size() > 0){
			article_title=doc.getElementsByAttributeValue("class", "rich_media_title").get(0).text();
			article_content=doc.getElementsByAttributeValue("class", "rich_media_content").get(0).text();
			nickname_val=profile_inners.get(0).getElementsByAttributeValue("class", "profile_nickname").text();
			
			Elements profile_metas=profile_inners.get(0).getElementsByAttributeValue("class", "profile_meta");
			if(profile_metas.size()==2){
				name_val=profile_metas.get(0).getElementsByAttributeValue("class", "profile_meta_value").text();
				function_val=profile_metas.get(1).getElementsByAttributeValue("class", "profile_meta_value").text();
				logger.info("Good URL account info is available: "+url);
			}else{
				logger.error("Bad URL account info is not available: "+url);
			}
		}else{
			logger.error("Bad URL account info is not available: "+url);
		}
		
		//获取文章标题和文字内容
		/*int t;
		int c;
		if(doc.getElementsByAttributeValue("class", "rich_media_title").size() > 0 && 
				doc.getElementsByAttributeValue("class", "rich_media_content").size() > 0){
			article_title=doc.getElementsByAttributeValue("class", "rich_media_title").get(0).text();
			article_content=doc.getElementsByAttributeValue("class", "rich_media_content").get(0).text();
		}else{
			logger.error("Bad URL article is not available: "+url);
		}*/
		
		
		
		vals.add(webUrl);//index 0
		vals.add(accountID_val);//index 1
		vals.add(nickname_val);//index 2
		vals.add(name_val);//index 3
		vals.add(function_val);//index 4
		vals.add(article_title);//index 5
		vals.add(article_content);//index 6
		vals.add(article_id);//index 7
		System.out.println(Thread.currentThread()+" "+vals);
		return vals;
	}
	
	/**
	 * 帐号和文章内容分别使用文件输出
	 * @param contenttWriter
	 * @param acticleWriter
	 * @param vals
	 * @throws IOException
	 */
	public void outputWriter(ArrayList<String> vals) throws IOException{
		
		for(String s:vals){
			s.replace("\t", "  ");
			s.replace("\n", "    ");
		}
		
		if(!vals.get(2).equals("") && !vals.get(5).equals("")){
			String account = vals.get(1)+"\t"+vals.get(2)+"\t"+vals.get(3)+"\t"+vals.get(4);
			System.out.println(account);
			String article = vals.get(7)+"\t"+vals.get(5)+"\t"+vals.get(6)+"\t"+vals.get(0);
			System.out.println(article);
			
			accountWriter.write(account+"\n");
			articleWriter.write(article+"\n");
		}
	}
	
}
