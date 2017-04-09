package dmpApp.wanDoujia;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import weixin.WeixinMain;

public class WebPagesParser implements Runnable {

	private static final Logger logger = Logger.getLogger(WebPagesParser.class); 
	
	private ArrayBlockingQueue<String> urlQueue = new ArrayBlockingQueue<String>(10000);
	private BufferedWriter contentWriter = null;
	private BufferedWriter recordWriter = null;
	private boolean flag = true;
	private CountDownLatch countDownLatch = null;
	private ConcurrentSkipListSet<String> cSet = null;
	
	public WebPagesParser(ArrayBlockingQueue<String> urlQueue, BufferedWriter contentWriter, 
			BufferedWriter recordWriter, CountDownLatch countDownLatch, ConcurrentSkipListSet<String> cSet) {
		this.urlQueue = urlQueue;
		this.contentWriter = contentWriter;
		this.recordWriter = recordWriter;
		this.countDownLatch = countDownLatch;
		this.cSet = cSet;
	}
	
	@Override
	public void run(){
		try{
			while(flag){//循环处理urlQueue直到遇到结束标志
				String crawlingUrl=urlQueue.take();
				logger.info("Start request from --> "+crawlingUrl);
				//线程结束判断
				if(crawlingUrl.equals("Done")){
					flag=false;
					urlQueue.put("Done");
				}else{
					if(cSet.contains(crawlingUrl)){
						WeixinMain.duplicatedUrlNum++;
						logger.info("Duplicated url: "+crawlingUrl);
						write(recordWriter, crawlingUrl+"\t2");
						WandoujiaMain.duplicatedUrlNum++;
					}else{
						try{
							String[] result = HttpClientSample.get(crawlingUrl);
							logger.info(result[0]);
							if(result[0].equals("200")){
								htmlParser(result[1]);
								logger.info("Good url: "+crawlingUrl);
								write(recordWriter, crawlingUrl+"\t0");
								WandoujiaMain.comUrlNum++;
							}else{
								logger.error("URL is not available: "+crawlingUrl);
								write(recordWriter, crawlingUrl+"\t1");
								WandoujiaMain.urlErrorUrlNum++;
							}
						}catch(IllegalArgumentException ie){
							logger.error(ie);
							write(recordWriter, crawlingUrl+"\t3");
							WandoujiaMain.urlErrorUrlNum++;
						}
						cSet.add(crawlingUrl);
					}
				}
				logger.info("Complete request from --> "+crawlingUrl);
			}
		}catch (InterruptedException e){
			logger.error(e);
		}finally{
			countDownLatch.countDown();
		}
	}
	
	/**
	 * 解析网页
	 * @param url 待爬URL
	 * @return
	 */
	public String htmlParser(String html){
		//定义需要爬取内容的变量
		String pkId = "";
		String appName = "";
		String versionCode = "";
		
		String versionName = "";
		String rating = "";
		String marketName = "";
		
		String categoryName = "";
		String downCount = "";
		String pkSize = "";
		
		String pkMd5 = "";
		String downUrl = "";
		String desc = "";
		
		String updateDate = "";
		String osVersion = "";
		String developer = "";

		//获取网页内容
		Document doc = Jsoup.parse(html);
		pkId = doc.select(".install-btn").attr("data-pn");
		appName = doc.select("body > div.container > div.detail-wrap > div.detail-top.clearfix > div.app-info > p.app-name > span").text();
		
		versionName = doc.select(".infos-list > dd:nth-child(10)").text();
		marketName = "豌豆荚";
		
		categoryName = doc.select("dd.tag-box").text();
		downCount = doc.select("span.item:nth-child(1) > i:nth-child(1)").text();
		pkSize =  doc.select(".infos-list > dd:nth-child(2)").text();
		
		downUrl = doc.select(".qr-info > a:nth-child(2)").attr("href");
		desc =  doc.select(".desc-info > div:nth-child(2)").text();
		
		updateDate = doc.select("#baidu_time").text();
		osVersion = doc.select("dd.perms").text(); 
		if(osVersion.indexOf("查看权限要求") != -1){
			osVersion = osVersion.substring(0, osVersion.indexOf("查看权限要求"));
		}
		developer = doc.select(".dev-sites > span:nth-child(1)").text();
		
		String outString = pkId+"\t"+
						appName+"\t"+
						versionCode+"\t"+
						
						versionName+"\t"+
						rating+"\t"+
						marketName+"\t"+
						
						categoryName+"\t"+
						downCount+"\t"+
						pkSize+"\t"+
						
						pkMd5+"\t"+
						downUrl+"\t"+
						desc+"\t"+
						
						updateDate+"\t"+
						osVersion+"\t"+
						developer;
		
		write(contentWriter, outString);
		
		return  "pkId:\n"+pkId+"\n"+
				"appName:\n"+appName+"\n"+
				"versionName:\n"+versionName+"\n"+
				"marketName:\n"+marketName+"\n"+
				"categoryName:\n"+categoryName+"\n"+
				"downCount:\n"+downCount+"\n"+
				"pkSize:\n"+pkSize+"\n"+
				"downUrl:\n"+downUrl+"\n"+
				"desc:\n"+desc+"\n"+
				"updateDate:\n"+updateDate+"\n"+
				"osVersion:\n"+osVersion+"\n"+
				"developer:\n"+developer;
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
	
	
	public static void main(String[] args){
//		String url = "http://www.wandoujia.com/apps/com.ss.android.article.news";
//		WebPagesParser wp = new WebPagesParser();
//		String[] rs = HttpClientSample.get(url);
//		System.out.println(wp.htmlParser(rs[1]));
	}
	
}
