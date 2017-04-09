package wechat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

public class WeixinMain {
	public static Logger logger =  Logger.getLogger(WeixinMain.class);
	
	private static int threadNum;
	private static int lineNum;
	private final static int cacheURLS=10000;
	
	private static BufferedWriter accountOutputWriter = null;
	private static BufferedWriter articleOutputWriter = null;
	private static BufferedWriter recordOutputWriter = null;
	private static ArrayBlockingQueue<String> conQueue = new ArrayBlockingQueue<String>(cacheURLS);
	private static ConcurrentSkipListSet<String> conSet=new ConcurrentSkipListSet<>();
	
	public static int comUrlNum = 0;
	public static int parmErrorUrlNum = 0;
	public static int forbidErrorUrlNum = 0;
	public static int otherErrorUrlNum = 0;
	public static int deleteErrorUrlNum = 0;
	public static int urlErrorUrlNum = 0;
	public static int duplicatedUrlNum = 0;

	
	public static void main(String[] args){
		long start = System.currentTimeMillis();
		System.out.println("Start time: "+start);
		
		String start_time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		
//		String inputFilePath ="E:\\Documents\\微信公众号\\tmp\\sample_urls_100";
//		String outputFilePath ="E:\\Documents\\微信公众号\\tmp\\output_test";
		
		String inputFilePath = args[0];
		String accountOutputFilePath = args[1];
		String articleOutputFilePath = args[2];
		String recordOutputFilePath = args[3];
		//线程数量
		threadNum = Integer.parseInt(args[4]);
		//从输入文件第x行开始读取
		lineNum = Integer.parseInt(args[5]);
		
		try{
			FileWriter acfw = new FileWriter(accountOutputFilePath);
			accountOutputWriter = new BufferedWriter(acfw);
			
			FileWriter arfw = new FileWriter(articleOutputFilePath);
			articleOutputWriter = new BufferedWriter(arfw);
			
			FileWriter refw = new FileWriter(recordOutputFilePath);
			recordOutputWriter = new BufferedWriter(refw);
			
			recordOutputWriter.write("Start time: "+start_time+"\n");
			
			//启动URL读取线程
			WeixinGetUrls wf=new WeixinGetUrls(conQueue, inputFilePath, lineNum);
			new Thread(wf).start();
			
			//启动爬取线程
			ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			CountDownLatch cdl = new CountDownLatch(threadNum);
			for(int i=0; i<threadNum; i++){
				WeixinHTMLAnalysis wht = new WeixinHTMLAnalysis(conQueue, accountOutputWriter, 
						articleOutputWriter, recordOutputWriter, cdl, conSet);
				executor.execute(wht);
			}
			cdl.await();
			executor.shutdown();
			
			recordOutputWriter.write("Crawled urls: "+comUrlNum+"\n");
			recordOutputWriter.write("Url is not available: "+urlErrorUrlNum+"\n");
			recordOutputWriter.write("Parameter error urls: "+parmErrorUrlNum+"\n");
			recordOutputWriter.write("Forbid error urls: "+forbidErrorUrlNum+"\n");
			recordOutputWriter.write("Deleted error urls: "+deleteErrorUrlNum+"\n");
			recordOutputWriter.write("Other error urls: "+otherErrorUrlNum+"\n");
			
			int allUrl = comUrlNum+urlErrorUrlNum+parmErrorUrlNum+forbidErrorUrlNum+deleteErrorUrlNum+otherErrorUrlNum+duplicatedUrlNum;
			recordOutputWriter.write("Total urls: "+allUrl+"\n");
			
			String finish_time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			recordOutputWriter.write("Finished time: "+finish_time+"\n");
			
		}catch(Exception e){
			logger.info(e);
		}finally {//关闭输出文件流
			if(accountOutputWriter!= null){
				try{
					accountOutputWriter.flush();
					accountOutputWriter.close();
					
					articleOutputWriter.flush();
					articleOutputWriter.close();
					
					recordOutputWriter.flush();
					recordOutputWriter.close();
				}catch(Exception ec){
					logger.info(ec);
				}
			}
		}
		
		long end = System.currentTimeMillis();
		System.out.print("Used time: ");
		System.out.println(end-start);
	}
	
	
}
