package dmpApp.yingYogBao;

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

public class YingYongBaoMain {
	public static Logger logger =  Logger.getLogger(YingYongBaoMain.class);
	
	private static int threadNum;
	private static int lineNum;
	private final static int cacheURLS=10000;
	
	private static BufferedWriter contentOutputWriter = null;
	private static BufferedWriter recordOutputWriter = null;
	private static ArrayBlockingQueue<String> conQueue = new ArrayBlockingQueue<String>(cacheURLS);
	private static ConcurrentSkipListSet<String> conSet=new ConcurrentSkipListSet<>();
	
	public static int comUrlNum = 0;
	public static int urlErrorUrlNum = 0;
	public static int duplicatedUrlNum = 0;

	
	public static void main(String[] args){
		String start_time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		System.out.println("Start time: "+start_time);
		logger.info("Start time: "+start_time);
		
		String inputFilePath = args[0];
		String contentOutputFilePath = args[1] + "_" + start_time;
		String recordOutputFilePath = args[2];
		//线程数量
		threadNum = Integer.parseInt(args[3]);
		//从输入文件第x行开始读取
		lineNum = Integer.parseInt(args[4]);
		
		try{
			FileWriter acfw = new FileWriter(contentOutputFilePath);
			contentOutputWriter = new BufferedWriter(acfw);
			
			FileWriter refw = new FileWriter(recordOutputFilePath);
			recordOutputWriter = new BufferedWriter(refw);
			
			recordOutputWriter.write("Start time: "+start_time+"\n");
			
			//启动URL读取线程
			PackageIdsReader wf=new PackageIdsReader(conQueue, inputFilePath, lineNum);
			new Thread(wf).start();
			
			//启动爬取线程
			ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			CountDownLatch cdl = new CountDownLatch(threadNum);
			for(int i=0; i<threadNum; i++){
				WebPagesParser wht = new WebPagesParser(conQueue, contentOutputWriter, 
						 recordOutputWriter, cdl, conSet);
				executor.execute(wht);
			}
			cdl.await();
			executor.shutdown();
			
			recordOutputWriter.write("Crawled urls: "+comUrlNum+"\n");
			recordOutputWriter.write("Url is not available: "+urlErrorUrlNum+"\n");
			recordOutputWriter.write("Dumplicated urls: "+duplicatedUrlNum+"\n");
			int allUrl = comUrlNum+urlErrorUrlNum+duplicatedUrlNum;
			recordOutputWriter.write("Total urls: "+allUrl+"\n");
			
			String finish_time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			recordOutputWriter.write("Finished time: "+finish_time+"\n");
			logger.info("Finished time: "+finish_time);
		}catch(Exception e){
			logger.info(e);
		}finally {//关闭输出文件流
			if(contentOutputWriter!= null){
				try{
					contentOutputWriter.flush();
					contentOutputWriter.close();

					recordOutputWriter.flush();
					recordOutputWriter.close();
				}catch(Exception ec){
					logger.info(ec);
				}
			}
		}
	}
	
}
