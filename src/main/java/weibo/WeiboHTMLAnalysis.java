package weibo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.sf.json.JSONObject;

public class WeiboHTMLAnalysis implements Runnable {

	public static Logger logger = Logger.getLogger(WeiboHTMLAnalysis.class);
	
	ArrayBlockingQueue<String> urlQueue = new ArrayBlockingQueue<String>(10000);
	HashSet<String> urlSet = new HashSet<String>();
	BufferedWriter accountWriter = null;
	BufferedWriter recordWriter = null;
	private boolean flag = true;
	private CountDownLatch countDownLatch;
	private ConcurrentSkipListSet<String> cSet;
	
//	private int sum = 0;
//	private Boolean updateCookieFlag = false;
	
	/**
	 * for debug
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
//		String url1="http://weibo.com/1000399913/info";
//		String url2="http://weibo.com/1002604113/info";
//		String url3="http://weibo.com/1111373675/info";
//		String url4="http://weibo.com/1412773094/info";
//		String url5="http://weibo.com/1570649847/info";
//		String url6="http://weibo.com/1645280112/info";
		String url7="http://weibo.com/1225781114/info";
//		String url8="http://mp.weixin.qq.com/s?__biz=MzA3NDYwNjUxMw==&mid=2657546065&idx=1&sn=799648e9e22c9e4aeeea25f064539a34&chksm=84ef7d8fb398f499eb717f59e197f22c2bb3cace483971ec0905cf4c396bb6f24ae13b75cf4f&mpshare=1&scene=23&srcid=1206qN6g7vSyWh1bZb2Aiss9";
//		String url9="http://mp.weixin.qq.com/s?__biz=MzA5OTk2NjYwMg==&mid=2657720350&idx=1&sn=376c2cfd4a3d9295a1726d998b4de6df";
//		String url10="http://mp.weixin.qq.com/s?__biz=MjM5MTY5MjYwMA==&mid=2651113386&idx=7&sn=d512a5f46f23986868686b4166468e2d&chksm=bd41c5fd8a364ceb744425a947671c3cc99bcbd2be74fb3adc815b8cda046294a458c9d1f348&f=json";
		
		ArrayBlockingQueue<String> q = new ArrayBlockingQueue<String>(10000);
//		q.add(url1);
//		q.add(url2);
//		q.add(url3);
//		q.add(url4);
//		q.add(url5);
//		q.add(url6);
		q.add(url7);
//		q.add(url8);
//		q.add(url9);
//		q.add(url10);
		q.add("Done");
		CountDownLatch cd = new CountDownLatch(1);
		ConcurrentSkipListSet<String> cs=new ConcurrentSkipListSet<>();
		
		String acf="E:\\work\\新浪微博\\tmp\\account_test";
		String rcf="E:\\work\\新浪微博\\tmp\\record_test";
		BufferedWriter acw=null;
		BufferedWriter rcw=null;
		try{
			acw = new BufferedWriter(new FileWriter(acf));
			rcw = new BufferedWriter(new FileWriter(rcf));
			
			WeiboHTMLAnalysis wa = new WeiboHTMLAnalysis(q, acw, rcw, cd, cs);
			wa.run();
		}finally{
			acw.close();
			rcw.close();
		}
	}
	

	/**
	 * 微博Constructor
	 * @param queue
	 * @param acw
	 * @param arw
	 * @param erw
	 * @param cd
	 * @param st
	 */
	public WeiboHTMLAnalysis(ArrayBlockingQueue<String> queue, BufferedWriter acw, 
			BufferedWriter erw,  CountDownLatch cd, ConcurrentSkipListSet<String> st) {
		this.urlQueue=queue;
		this.accountWriter=acw;
		this.recordWriter=erw;
		this.countDownLatch=cd;
		this.cSet=st;
	}
	
	/**
	 * implement runable interface
	 */
	public void run(){
		int sum=0;
		Boolean updateCookieFlag = false;
		try{
			while(flag){//循环处理urlQueue直到遇到结束标志
				String ul="test";
				try{
					ul=urlQueue.take();
					logger.info("Start request from --> "+ul);
					//线程结束判断
					if(ul.equals("Done")){
						flag=false;
						urlQueue.put("Done");
					}else{
						synchronized(cSet){
							if(cSet.contains(ul)){
								WeiboMain.duplicatedUrlNum++;
								logger.info("Duplicated urls: "+ul);
							}else{
								if(sum%100 == 0){
									updateCookieFlag = true;
								}else{
									updateCookieFlag = false;
								}
								logger.info(updateCookieFlag);
								getValues(ul, updateCookieFlag);
								cSet.add(ul);
								sum++;
							}
//							Thread.sleep(100);
						}
					}
					logger.info("Complete request from --> "+ul);
				}catch(IllegalArgumentException ae){
					logger.error(ae.getStackTrace());
					ae.printStackTrace();
					logger.error("URL不符合标准: "+ul);
					//URL不符合标准
					WeiboMain.urlErrorUrlNum++;
					write(recordWriter, "This url is not available\t"+ae);
					logger.info("This url is not available: "+ul);
				}catch (InterruptedException e) {
					logger.error(e);
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
	 * @throws IOException 
	 * @throws Exception
	 */
	public synchronized void getValues(String url, Boolean updateFlag) {
		String basicInfoScript = "";
		String basicInfoFragment="";
		String tag="";
		int tagNum=0;
		HashMap<String, String> infoMap=new HashMap<>();
		
		infoMap.put("ID", url.split("/")[3]);
		
		String[] webHTML=HttpClientWeibo.get(url, updateFlag);
		logger.info("Used Cookies: "+webHTML[3]);
		Document webHTMLDoc=Jsoup.parse(webHTML[0]);
		Elements scriptElements = webHTMLDoc.getElementsByTag("script");
		for(Element e: scriptElements){
			if(e.toString().contains("基本信息")){
				basicInfoScript=e.toString();
			}
		}
		
	    Pattern basicInfoPattern=Pattern.compile("<div.*div>");  
	    Matcher basicInfoMatcher=basicInfoPattern.matcher(basicInfoScript);
	    //获取包含基本信息的script标签
//	    while(basicInfoMatcher.find()){
	    if(basicInfoMatcher.find()){
	    	String temp=basicInfoMatcher.group();
	    	basicInfoFragment=temp.replace("\\r", "").replace("\\n", "").replace("\\t", "").replace("\\", "");
	    }  
	    //生成今本信息html
		Document docBody = Jsoup.parseBodyFragment(basicInfoFragment);
		Elements basicInfoElement = docBody.getElementsByTag("ul");
		int uls=basicInfoElement.size();
		if(uls != 0){
			//解析用户基本信息
			for(Element e: basicInfoElement.get(0).getElementsByTag("li")){
				String[] info=e.text().split("：", 2);
				if(info.length==2){
					infoMap.put(info[0], info[1]);
				}
			}
			if(uls >1){
				//解析用户标签
				for(Element e: basicInfoElement.get(uls-1).getElementsByTag("a")){
					if(tagNum==0){
						tag=e.text();
						tagNum++;
					}else{
						tag=tag+"_"+e.text();
					}
					infoMap.put("标签", tag);
				}
			}
			JSONObject jsonObject=JSONObject.fromObject(infoMap);
			write(accountWriter, jsonObject.toString());
			write(recordWriter, "Crawling is successful\t"+url);
			logger.info("Crawling is successful: "+url);
			WeiboMain.goodUrlNum++;
		}else{
			logger.info("Cannot get account infomation from this url: "+url+"\n"+"The respond status: "+webHTML[1]);
			write(recordWriter, "Cannot get account infomation from this url\t"+url+"\n"+"The respond status: "+webHTML[1]);
			WeiboMain.badUrlNum++;
		}
	}
	
	/**
	 * 写同步
	 * @param writer
	 * @param value
	 */
	public void write(BufferedWriter writer,String value){
		synchronized (writer) {
			try{
				writer.write(value+"\n");
			}catch(IOException e){
				logger.error(e);
			}
		}
	}
	
	/**
	 * 从URL中解析微博ID
	 * @param url
	 * @return
	 */
	public String getAccountIDFromUrl(String url){
		return url.split("/")[3];
	}
	
}
