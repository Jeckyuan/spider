package weibo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class WeiboMain {
	public static Logger logger =  Logger.getLogger(WeiboMain.class);
	
	private static int threadNum;
	private static int lineNum;
	private final static int cacheURLS=10000;
	
	private static BufferedWriter accountOutputWriter = null;
	private static BufferedWriter recordOutputWriter = null;
	private static ArrayBlockingQueue<String> conQueue = new ArrayBlockingQueue<String>(cacheURLS);
	private static ConcurrentSkipListSet<String> conSet=new ConcurrentSkipListSet<>();
	
	public static int goodUrlNum = 0;
	public static int badUrlNum = 0;
	public static int duplicatedUrlNum = 0;
	public static int urlErrorUrlNum =0;
	
	/**
	 *微博用户信息爬取主线程
	 * @param args eg:inputFilePath, accountOutputFilePath, recordOutputFilePath, threadNum, lineNum
	 */
	public static void main(String[] args){
		long start = System.currentTimeMillis();
		System.out.println("Start time: "+start);
		
		String start_time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		
		String inputFilePath = args[0];
		String accountOutputFilePath = args[1];
		String recordOutputFilePath = args[2];
		//线程数量
		threadNum = Integer.parseInt(args[3]);
		//从输入文件第x行开始读取
		lineNum = Integer.parseInt(args[4]);
		//初始化已爬取ID set
		initCrawledIDSet(recordOutputFilePath);
		
		try{
			FileWriter acfw = new FileWriter(accountOutputFilePath);
			accountOutputWriter = new BufferedWriter(acfw);
			
			FileWriter refw = new FileWriter(recordOutputFilePath, true);
			recordOutputWriter = new BufferedWriter(refw);
			
			recordOutputWriter.write("Start time: "+start_time+"\n");
			
			//启动URL读取线程
			WeiboGetUrls wf=new WeiboGetUrls(conQueue, inputFilePath, lineNum);
			new Thread(wf).start();
			
			//启动解析线程
			ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			CountDownLatch cdl = new CountDownLatch(threadNum);
			for(int i=0; i<threadNum; i++){
				WeiboHTMLAnalysis wht = new WeiboHTMLAnalysis(conQueue, accountOutputWriter, 
						recordOutputWriter, cdl, conSet);
				executor.execute(wht);
			}
			cdl.await();
			executor.shutdown();
			
			//输出url统计信息
			recordOutputWriter.write("Crawled urls: "+goodUrlNum+"\n");
			recordOutputWriter.write("Bad urls: "+badUrlNum+"\n");
			recordOutputWriter.write("Duplicated urls: "+duplicatedUrlNum+"\n");
			recordOutputWriter.write("Parameter error urls: "+urlErrorUrlNum+"\n");
			int allUrl = goodUrlNum+badUrlNum+duplicatedUrlNum+urlErrorUrlNum;
			recordOutputWriter.write("Total urls: "+allUrl+"\n");
			//记录结束时间
			String finish_time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			recordOutputWriter.write("Finished time: "+finish_time+"\n");
		}catch(Exception e){
			logger.info(e);
		}finally {//关闭输出文件流
			if(accountOutputWriter!= null){
				try{
					accountOutputWriter.flush();
					accountOutputWriter.close();
					
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
	
	public static void initCrawledIDSet(String crawledIDFile){
//		HashSet<String> crawledIdSet = new HashSet<>();
		FileReader crawledIdFileWriter = null;
		BufferedReader crawledIdBufferReader = null;
		try{
			File f = new File(crawledIDFile);
			if(! f.exists()){
				File pf = f.getParentFile();
				pf.mkdirs();
				f.createNewFile();
			}
			crawledIdFileWriter = new FileReader(crawledIDFile);
			crawledIdBufferReader = new BufferedReader(crawledIdFileWriter);
			String tmpString;
			while((tmpString = crawledIdBufferReader.readLine())  != null){
				String[] ts = tmpString.split("\t", 2);
				if(ts.length == 2){
					conSet.add(ts[1]);
				}
			}
		}catch(IOException e){
			logger.error("读取已爬取ID文件失："+crawledIDFile);
		}finally {
			try{
				crawledIdBufferReader.close();
			}catch(IOException e){
				logger.error("Record file close error!");
			}
		}
	}
	
}
