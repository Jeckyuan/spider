package dmpApp.appBasicInfo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class AppInfoWebPagesParser implements Runnable {

	private static final Logger logger = Logger.getLogger(AppInfoWebPagesParser.class);
	private static final String wdjUrl = "http://www.wandoujia.com/apps/";
	private static final String yybUrl = "http://sj.qq.com/myapp/detail.htm?apkName=";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private ArrayBlockingQueue<String> packageIdQueue = new ArrayBlockingQueue<String>(10000);
	private BufferedWriter contentWriter = null;
	private BufferedWriter recordWriter = null;
	private boolean flag = true;
	private CountDownLatch countDownLatch = null;
	private ConcurrentSkipListSet<String> cSet = null;
	private AppInfoMySQLInsert mySQLAppInfoImport = null;

	public AppInfoWebPagesParser(ArrayBlockingQueue<String> packageIdQueue, BufferedWriter contentWriter,
			BufferedWriter recordWriter, CountDownLatch countDownLatch, ConcurrentSkipListSet<String> cSet) {
		this.packageIdQueue = packageIdQueue;
		this.contentWriter = contentWriter;
		this.recordWriter = recordWriter;
		this.countDownLatch = countDownLatch;
		this.cSet = cSet;
		this.mySQLAppInfoImport = new AppInfoMySQLInsert();
	}

	@Override
	public void run(){
		try{
			//启动数据库连接
			mySQLAppInfoImport.getConn();
			//循环处理urlQueue直到遇到结束标志
			while(flag){
				String crawlingPackageId=packageIdQueue.take();
				String appInfo = "";
				logger.info("Start request from --> "+crawlingPackageId);
				//线程结束判断
				if(crawlingPackageId.equals("Done")){
					flag=false;
					packageIdQueue.put("Done");
				}else{
					if(cSet.contains(crawlingPackageId)){
						logger.info("Duplicated url: "+crawlingPackageId);
						write(recordWriter, crawlingPackageId+"\t2");
						AppInfoMain.duplicatedUrlNum++;
					}else{
						try{
							String url = yybUrl+crawlingPackageId;
							String[] result = AppInfoHttpClient.get(url);
							Document doc = Jsoup.parse(result[1]);
							//应用宝爬取是否成功
							boolean fg = doc.select(".search-none-text > a:nth-child(1).search-none-text > a:nth-child(1)").text().contains("没有找到相关结果");
							if(!fg){
								appInfo = yybHtmlParser(url, result[1]);
								mySQLAppInfoImport.insertAppInfo(appInfo);
								AppInfoMain.crawledUrlNum++;
							}else{
								url = wdjUrl + crawlingPackageId;
								result = AppInfoHttpClient.get(url);
								//判断豌豆荚爬取是否成功
								if(result[0].equals("200")){
									appInfo = wdjHtmlParser(url, result[1]);
									mySQLAppInfoImport.insertAppInfo(appInfo);
									AppInfoMain.crawledUrlNum++;
								}else{
									logger.error("URL is not available: "+url);
									write(recordWriter, crawlingPackageId+"\t"+"\t"+1);
									AppInfoMain.errorUrlNum++;
								}
							}
						}catch(IllegalArgumentException ie){
							logger.error(ie);
							write(recordWriter, crawlingPackageId+"\t"+"\t"+2);
							AppInfoMain.errorUrlNum++;
						}
						cSet.add(crawlingPackageId);
					}
				}
				logger.info("Complete request from --> "+crawlingPackageId);
			}
		}catch (InterruptedException e){
			logger.error(e);
		}finally{
			//关闭数据库连接
			mySQLAppInfoImport.closeConn();
			countDownLatch.countDown();
		}
	}

	/**
	 * 解析网页
	 * @param url 待爬URL
	 * @return
	 */
	public String wdjHtmlParser(String url, String html){
		//定义需要爬取内容的变量
		String pkId = "";
		String appName = "";
		String versionName = "";

		String rating = "";
		String marketName = "";
		String categoryName = "";

		String downCount = "";
		downCount = unitTrans(downCount);
		String pkSize = "";
		String pkMd5 = "";

		String downUrl = "";
		String updateDate = "";
		//系统类型，0：Android
		int os = 0;

		String osVersion = "";
		String developer = "";
		String crawlingTime = simpleDateFormat.format(new Date());
		String desc = "";

		//获取网页内容
		Document doc = Jsoup.parse(html);
		pkId = doc.select(".install-btn").attr("data-pn");
		appName = doc.select("body > div.container > div.detail-wrap > div.detail-top.clearfix > div.app-info > p.app-name > span").text();
		marketName = "豌豆荚";

		int infoList = doc.select(".infos-list").get(0).getElementsByTag("dt").size();
		if(infoList == 7){
			versionName = doc.select(".infos-list > dd:nth-child(10)").text();
		}else if(infoList == 6){
			versionName = doc.select(".infos-list > dd:nth-child(8)").text();
		}
		if(osVersion.indexOf("以上") != -1){
			osVersion = osVersion.substring(0, osVersion.indexOf("以上"));
		}

		categoryName = doc.select("dd.tag-box").text();
		downCount = doc.select("span.item:nth-child(1) > i:nth-child(1)").text();
		downCount = unitTrans(downCount);

		pkSize =  doc.select(".infos-list > dd:nth-child(2)").text();
		downUrl = doc.select(".qr-info > a:nth-child(2)").attr("href").replaceAll("\t", " ");
		desc =  doc.select(".desc-info > div:nth-child(2)").text().replaceAll("\t", " ");
		if(desc.length() > 1000){
			desc = desc.substring(0, 1000);
		}
		updateDate = doc.select("#baidu_time").text();
		osVersion = doc.select("dd.perms").text();
		developer = doc.select(".dev-sites > span:nth-child(1)").text().replaceAll("\t", " ");

		String outString = pkId+"\t"+
				appName+"\t"+
				versionName+"\t"+

				rating+"\t"+
				marketName+"\t"+
				categoryName+"\t"+

				downCount+"\t"+
				pkSize+"\t"+
				pkMd5+"\t"+

				downUrl+"\t"+
				updateDate+"\t"+
				os+"\t"+

				osVersion+"\t"+
				developer+"\t"+
				crawlingTime+"\t"+
				desc;
		//输出爬取信息，用于备份
		write(contentWriter, url+"\t"+outString);
		logger.info("豌豆荚爬取: "+url);
		//输出已爬取过的package id
		write(recordWriter, pkId +"\t"+ appName +"\t"+ 0);
		return  outString;
	}

	/**
	 * 解析网页
	 * @param url 待爬URL
	 * @return
	 */
	public String yybHtmlParser(String url, String html){
		//定义需要爬取内容的变量
		String pkId = "";
		String appName = "";
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
		//系统类型，0：Android
		int os = 0;

		String crawlingTime = simpleDateFormat.format(new Date());

		//获取网页内容
		Document doc = Jsoup.parse(html);
		pkId = doc.select(".det-ins-btn").attr("apk");
		appName = doc.select(".det-name-int").text();

		versionName = doc.select("div.det-othinfo-data:nth-child(2)").text();
		versionName = versionName.replaceFirst("^V", "");
		marketName = "应用宝";

		categoryName = doc.select("#J_DetCate").text();
		downCount = doc.select(".det-ins-num").text();
		//统一单位
		downCount = unitTrans(downCount.replace("下载", ""));
		pkSize =  doc.select(".det-size").text();

		downUrl = doc.select(".det-down-btn").attr("data-apkurl").replaceAll("\t", " ");
		desc =  doc.select("div.det-app-data-info:nth-child(1)").text().replaceAll("\t", " ");
		if(desc.length() > 1000){
			desc = desc.substring(0, 1000);
		}

		updateDate = doc.select("#J_ApkPublishTime").text();
		developer = doc.select("div.det-othinfo-data:nth-child(6)").text().replaceAll("\t", " ");

		String outString = //url + "\t"+
				pkId+"\t"+
				appName+"\t"+
				versionName+"\t"+

				rating+"\t"+
				marketName+"\t"+
				categoryName+"\t"+

				downCount+"\t"+
				pkSize+"\t"+
				pkMd5+"\t"+

				downUrl+"\t"+
				updateDate+"\t"+
				os+"\t"+

				osVersion+"\t"+
				developer+"\t"+
				crawlingTime+"\t"+
				desc;
		//输出爬取信息，用于备份
		write(contentWriter, url+"\t"+outString);
		logger.info("应用宝爬取: "+url);
		//输出已爬取过的package id
		write(recordWriter, pkId +"\t"+ appName +"\t"+ 0);
		return outString;
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
	 *
	 * @param str
	 * @return
	 */
	public static String unitTrans(String str){
		String rs = " ";
		// 正则表达式规则
		String regEx = "[0-9]+[0-9.]*";
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regEx);
		// 忽略大小写的写法
		// Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str.trim());

		DecimalFormat df = new DecimalFormat("####");
		//		nf.setMaximumFractionDigits(0);

		if(matcher.find()){
			String tmp =matcher.group();
			double val =  Double.parseDouble(tmp);
			if(str.contains("万")){
				//				rs = "" + val*10000;
				rs = df.format(val*10000);
			}else if(str.contains("亿")){
				//				rs = "" + val*10000*10000;
				rs = df.format(val*10000*10000);
			}else{
				//				rs =  "" + val;
				rs = df.format(val);
			}
		}
		return rs;
	}


	public static void main(String[] args){
		//		String url = "http://www.wandoujia.com/apps/com.ss.android.article.news";
		//		WebPagesParser wp = new WebPagesParser();
		//		String[] rs = HttpClientSample.get(url);
		//		System.out.println(wp.htmlParser(rs[1]));
	}

}
