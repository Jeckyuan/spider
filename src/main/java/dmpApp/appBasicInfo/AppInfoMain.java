package dmpApp.appBasicInfo;

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

public class AppInfoMain {
	public static Logger logger = Logger.getLogger(AppInfoMain.class);
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private static int threadNum;
	private static int lineNum;
	private final static int cacheURLS=10000;

	private static BufferedWriter contentOutputWriter = null;
	private static BufferedWriter recordOutputWriter = null;
	private static ArrayBlockingQueue<String> conQueue = new ArrayBlockingQueue<String>(cacheURLS);
	private static ConcurrentSkipListSet<String> conSet=new ConcurrentSkipListSet<>();

	//爬取成功数量
	public static int crawledUrlNum = 0;
	//爬取失败数量
	public static int errorUrlNum = 0;
	//重复package id数量
	public static int duplicatedUrlNum = 0;


	public static void main(String[] args){
		String start_time = simpleDateFormat.format(new Date());
		logger.info("Start time: "+start_time);

		String inputFilePath = args[0];
		String contentOutputFilePath = args[1] + start_time;
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
			//			recordOutputWriter.write("Start time: "+start_time+"\n");

			//启动package id读取线程
			AppInfoPackageIdReader wf=new AppInfoPackageIdReader(conQueue, inputFilePath, lineNum);
			new Thread(wf).start();

			//启动爬取线程
			ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			CountDownLatch cdl = new CountDownLatch(threadNum);
			for(int i=0; i<threadNum; i++){
				AppInfoWebPagesParser wht = new AppInfoWebPagesParser(conQueue, contentOutputWriter,
						recordOutputWriter, cdl, conSet);
				executor.execute(wht);
			}
			cdl.await();
			executor.shutdown();

			logger.info("已爬取package id数量: "+crawledUrlNum);
			logger.info("爬取失败package id数量: "+errorUrlNum);
			logger.info("重复package id数量: "+duplicatedUrlNum);
			int allUrl = crawledUrlNum+errorUrlNum+duplicatedUrlNum;
			logger.info("本次总的package id数量: "+allUrl+"\n");
			String finish_time = simpleDateFormat.format(new Date());
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
