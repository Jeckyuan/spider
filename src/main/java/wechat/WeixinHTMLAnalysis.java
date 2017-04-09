package wechat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
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
	BufferedWriter recordWriter = null;
	private boolean flag = true;
	private CountDownLatch countDownLatch;
	private ConcurrentSkipListSet<String> cSet;
	
	/**
	 * for debug
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		String url="http://mp.weixin.qq.com/s?__biz=MjM5MTQ2NzA4Mg==&mid=2649699894&idx=5&sn=11bc54ea0bd468f33526cea79b690cf7";
		String url2="http://mp.weixin.qq.com/s?__biz=MzAwNDAyODk4NA%3D%3D&mid=205530081&idx=2&sn=5a1ef50bd77e5c40fa833c33aae0a1a0&scene=7&ascene=0&";
		String url3="http://mp.weixin.qq.com/s?__biz=MjM5NTc5MzI0Mw==&mid=2651019575&idx=1&sn=15d068858a61571a038b0f2363";
		String url4="http://mp.weixin.qq.com/s?__biz=MzI0ODQ3MzkyMg%3D%3D&mid=2247483653&idx=3&sn=d0c9a0ec2dd8ae00e86233f49685d1d1&chksm=e9a17128ded6f83ec77ddc1c4eaac6f67b8b9fddcb35bf95c2ba2b3d1ba7e19454d26e312bc1&scene=7&ascene=0&";
		String url5="http://mp.weixin.qq.com/s?__biz=MzIzMjA1MDQ5MQ%3D%3D&mid=2651602451&idx=2&sn=4226467f91a26f3c62e0aa16c4479e2d&chksm=f3624447c415cd51608b50918c920b19fd79545d7fac5dedb47754de36175974e1dba1e78460&mpshare=1&scene=1&srcid=1019R0C0s9ca60RvMosag89B&from=singlemessage&isappinstalled=0&ascene=1&";
		String url6="http://mp.weixin.qq.com/s?__biz=MTAyMDM4OTA4MQ==&idx=1&mid=2650348301&sn=3234475d47bb48cd90fa73cd17e9d652";
		String url7="http://mp.weixin.qq.com/s?__biz=MjM5MDA2MTI1MA==&mid=2649084269&idx=1&sn=a2ff163bfd00e0c8a6837f53ed0575b8&chksm=be5bf6c0892c7fd6e47e4e878ec81646728a3f32a248879596626c9055459d750aa6850775a6&mpshare=1&scene=23&srcid=1107gCiDmhQs1p4Wj3R95cim";
		String url8="http://mp.weixin.qq.com/s?__biz=MzA3NDYwNjUxMw==&mid=2657546065&idx=1&sn=799648e9e22c9e4aeeea25f064539a34&chksm=84ef7d8fb398f499eb717f59e197f22c2bb3cace483971ec0905cf4c396bb6f24ae13b75cf4f&mpshare=1&scene=23&srcid=1206qN6g7vSyWh1bZb2Aiss9";
		String url9="http://mp.weixin.qq.com/s?__biz=MzA5OTk2NjYwMg==&mid=2657720350&idx=1&sn=376c2cfd4a3d9295a1726d998b4de6df";
		String url10="http://mp.weixin.qq.com/s?__biz=MjM5MTY5MjYwMA==&mid=2651113386&idx=7&sn=d512a5f46f23986868686b4166468e2d&chksm=bd41c5fd8a364ceb744425a947671c3cc99bcbd2be74fb3adc815b8cda046294a458c9d1f348&f=json";
		
		ArrayBlockingQueue<String> q = new ArrayBlockingQueue<String>(10000);
//		q.add(url);
//		q.add(url2);
//		q.add(url3);
//		q.add(url4);
//		q.add(url5);
//		q.add(url6);
//		q.add(url7);
		q.add(url8);
		q.add(url9);
		q.add(url10);
		q.add("Done");
		CountDownLatch cd = new CountDownLatch(1);
		ConcurrentSkipListSet<String> cs=new ConcurrentSkipListSet<>();
		
		String acf="E:\\work\\微信公众号\\tmp\\account_test";
		String arf="E:\\work\\微信公众号\\tmp\\article_test";
		String erf="E:\\work\\微信公众号\\tmp\\error_test";
		BufferedWriter acw=null;
		BufferedWriter arw=null;
		BufferedWriter erw=null;
		try{
			acw = new BufferedWriter(new FileWriter(acf));
			arw = new BufferedWriter(new FileWriter(arf));
			erw = new BufferedWriter(new FileWriter(erf));
			
			WeixinHTMLAnalysis wa = new WeixinHTMLAnalysis(q, acw, arw, erw, cd, cs);
			wa.run();
		}finally{
			acw.close();
			arw.close();
			erw.close();
		}
	}
	
	/**
	 * conatructor 
	 * @param queue 阻塞队列
	 * @param acw 账号writer
	 * @param arw 文章writer
	 * @param cd 线程结束判断
	 */
	public WeixinHTMLAnalysis(ArrayBlockingQueue<String> queue, BufferedWriter acw, BufferedWriter arw,  CountDownLatch cd) {
		this.urlQueue=queue;
		this.accountWriter=acw;
		this.articleWriter=arw;
		this.countDownLatch=cd;
	}
	
	public WeixinHTMLAnalysis(ArrayBlockingQueue<String> queue, BufferedWriter acw, BufferedWriter arw, BufferedWriter erw,  CountDownLatch cd) {
		this.urlQueue=queue;
		this.accountWriter=acw;
		this.articleWriter=arw;
		this.recordWriter=erw;
		this.countDownLatch=cd;
	}
	
	public WeixinHTMLAnalysis(ArrayBlockingQueue<String> queue, BufferedWriter acw, BufferedWriter arw, BufferedWriter erw,  CountDownLatch cd, ConcurrentSkipListSet<String> st) {
		this.urlQueue=queue;
		this.accountWriter=acw;
		this.articleWriter=arw;
		this.recordWriter=erw;
		this.countDownLatch=cd;
		this.cSet=st;
	}
	
	/**
	 * implement runable interface
	 */
	public void run(){
		try{
			while(flag){//循环处理urlQueue直到遇到结束标志
				try{
					String ul=urlQueue.take();
					//去除尾部的%.或者%
					String rp_ul = ul.replaceAll("%.?$", "");
					
					logger.info("Start request from --> "+ul);
					//线程结束判断
					if(ul.equals("Done")){
						flag=false;
						urlQueue.put("Done");
					}else{
						synchronized(cSet){
							if(cSet.contains(getArticleIDFromUrl(rp_ul))){
								WeixinMain.duplicatedUrlNum++;
								logger.info("Duplicated urls: "+rp_ul);
								recordWriter.write("Duplicated urls: "+rp_ul+"\n");
							}else{
								outputWriter(getValues(rp_ul));
								cSet.add(getArticleIDFromUrl(rp_ul));
							}
						}
					}
					logger.info("Complete request from --> "+ul);
				}catch(IllegalArgumentException ae){
					logger.info(ae);
					//URL不符合标准
					try{
						WeixinMain.urlErrorUrlNum++;
						recordWriter.write(ae.toString()+"\n");
					}catch(IOException iie){
						logger.error(iie);
					}
				}catch(IOException ie){
					logger.error(ie);
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
	public synchronized ArrayList<String> getValues(String url) throws IOException {
		ArrayList<String>  vals = new ArrayList<String>();
		
		String webUrl = url;
		String nickname_val="";
		String wx_code=" ";
		String function_val=" ";
		String article_title="";
		String article_content=" ";
		
//		String html = HttpClientUtils.get(url);
		String html = HttpClientSample.get(url);
		Document doc = Jsoup.parse(html);
		
		//获取公众号信息:nickname,account,founction
		Elements profile_inners=doc.getElementsByAttributeValue("class", "profile_inner");
		int pi=profile_inners.size();
		int rt=doc.getElementsByAttributeValue("class", "rich_media_title").size();
		int rc=doc.getElementsByAttributeValue("class", "rich_media_content").size();
		String nm="pi="+pi+":rt="+rt+":rc="+rc;
		/*
		if(profile_inners.size()>0 && 
				doc.getElementsByAttributeValue("class", "rich_media_title").size()>0 && 
					doc.getElementsByAttributeValue("class", "rich_media_content").size()>0){
		*/
		if(pi>0 && rt>0 && rc>0){
			
			article_title=doc.getElementsByAttributeValue("class", "rich_media_title").get(0).text();
			article_content=doc.getElementsByAttributeValue("class", "rich_media_content").get(0).text();
			nickname_val=profile_inners.get(0).getElementsByAttributeValue("class", "profile_nickname").text();
			
			Elements profile_metas=profile_inners.get(0).getElementsByAttributeValue("class", "profile_meta");
			if(profile_metas.size()==2){
				wx_code=profile_metas.get(0).getElementsByAttributeValue("class", "profile_meta_value").text();
				function_val=profile_metas.get(1).getElementsByAttributeValue("class", "profile_meta_value").text();
				recordWriter.write("Good URL all info is available: "+url+"\n");
				WeixinMain.comUrlNum++;
				logger.info("Good URL all info is available: "+url);
			}else{
				recordWriter.write("Bad URL account info is not integrity: "+url+"\n");
				logger.error("Bad URL account info is not integrity: "+url);
			}
		}else{//错误分类记录和统计
			String er  = doc.getElementsByAttributeValue("class", "text_area").text();
			if(er.contains("参数错误")){
				recordWriter.write(er+": "+url+"\n");
				WeixinMain.parmErrorUrlNum++;
			}else if(er.contains("此帐号已被封,内容无法查看")){
				recordWriter.write(er+": "+url+"\n");
				WeixinMain.forbidErrorUrlNum++;
			}else if(er.contains("该内容已被发布者删除")){
				recordWriter.write(er+": "+url+"\n");
				WeixinMain.deleteErrorUrlNum++;
			}else{
				recordWriter.write(er+nm+": "+url+"\n");
				WeixinMain.otherErrorUrlNum++;
			}
			logger.error("Bad URL account info is not integraty: "+url);
		}
		
		vals.add(webUrl);//index 0
		vals.add(nickname_val);//index 1
		vals.add(wx_code);//index 2
		vals.add(function_val);//index 3
		vals.add(article_title);//index 4
		vals.add(article_content);//index 5
		return vals;
	}
	
	/**
	 * 帐号和文章内容分别使用文件输出
	 * @param contenttWriter
	 * @param acticleWriter
	 * @param valArr
	 * @throws IOException
	 */
	public void outputWriter(ArrayList<String> vals) throws IOException{
		
		String crawler_time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String day_id = new SimpleDateFormat("yyyyMMdd").format(new Date());
		
		ArrayList<String> valArr = new ArrayList<>();
		
		for(int i=0;i<vals.size(); i++){
			String s=vals.get(i).replace("\t", "  ").replace("\n", "    ");
			valArr.add(s);
		}
		
		if(!valArr.get(1).equals("") && !valArr.get(4).equals("")){
			
			//解析公众号id（__biz）和文章id
			String parms = valArr.get(0).split("s\\?")[1];
			
			String biz = "";
			String mid = "";
			String idx = "";
			String sn = "";
			String arId = "";
			for(String p: parms.split("&")){
				if(p.split("=")[0].contains("__biz")){
					biz = p.split("=", 2)[1];
				}else if(p.split("=")[0].contains("mid")){
					mid = p.split("=")[1];
				}else if(p.split("=")[0].contains("idx")){
					idx = p.split("=")[1];
				}else if(p.split("=")[0].contains("sn")){
					sn = p.split("=")[1];
				}
			}
			arId = mid+"_"+idx+"_"+sn;
			
			//id,name,wx_code,desc,crawler_time,day_id
			String account = biz+"\t"+valArr.get(1)+"\t"+valArr.get(2)+"\t"+valArr.get(3)+"\t"+crawler_time+"\t"+day_id;
			
			//id,pubnum_id,title,url,state,time,content,day_id
			String article = arId+"\t"+biz+"\t"+valArr.get(4)+"\t"+valArr.get(0)+"\t"+"1"+"\t"+crawler_time+"\t"+valArr.get(5)+"\t"+day_id;

			write(accountWriter, account);
			write(articleWriter, article);
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
	 * 从URL中解析文章id
	 * @param url
	 * @return
	 */
	public String getArticleIDFromUrl(String url){
		String parms = url.split("s\\?")[1];
		
		String mid = "";
		String idx = "";
		String sn = "";
		String arId = "";
		for(String p: parms.split("&")){
			if(p.split("=")[0].contains("mid")){
				mid = p.split("=")[1];
			}else if(p.split("=")[0].contains("idx")){
				idx = p.split("=")[1];
			}else if(p.split("=")[0].contains("sn")){
				sn = p.split("=")[1];
			}
		}
		arId = mid+"_"+idx+"_"+sn;
		return arId;
	}
	
}
