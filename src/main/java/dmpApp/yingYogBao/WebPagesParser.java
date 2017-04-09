package dmpApp.yingYogBao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


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
						logger.info("Duplicated url: "+crawlingUrl);
						write(recordWriter, crawlingUrl+"\t2");
						YingYongBaoMain.duplicatedUrlNum++;
					}else{
						try{
							String[] result = HttpClientSample.get(crawlingUrl);
							logger.info(result[0]);
							htmlParser(crawlingUrl, result[1]);
						}catch(IllegalArgumentException ie){
							logger.error(ie);
							write(recordWriter, crawlingUrl+"\t3");
							YingYongBaoMain.urlErrorUrlNum++;
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
	public String htmlParser(String url, String html){
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
		pkId = doc.select(".det-ins-btn").attr("apk");
		appName = doc.select(".det-name-int").text();

		versionName = doc.select("div.det-othinfo-data:nth-child(2)").text();
		marketName = "应用宝";

		categoryName = doc.select("#J_DetCate").text();
		downCount = doc.select(".det-ins-num").text();
		pkSize =  doc.select(".det-size").text();

		downUrl = doc.select(".det-down-btn").attr("data-apkurl");
		desc =  doc.select("div.det-app-data-info:nth-child(1)").text();

		updateDate = doc.select("#J_ApkPublishTime").text();
		developer = doc.select("div.det-othinfo-data:nth-child(6)").text();

		String outString = url + "\t"+
				pkId+"\t"+
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
		if(!pkId.equals("")){
			write(contentWriter, outString);

			logger.info("Good url: "+url);
			write(recordWriter, url+"\t0");
			YingYongBaoMain.comUrlNum++;
		}else{
			logger.error("URL is not available: "+url);
			write(recordWriter, url+"\t1");
			YingYongBaoMain.urlErrorUrlNum++;
		}


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
