package com.ai.autohome.crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

import com.ai.autohome.fileOperation.WeixinFileUtil;

public class WexinMain {
	public static Logger logger =  Logger.getLogger(WexinMain.class);
	
	private static int threadNum;
	private static int lineNum;
	private final static int cacheURLS=10000;
	
	private static BufferedWriter accountOutputWriter = null;
	private static BufferedWriter articleOutputWriter = null;
	private static ArrayBlockingQueue<String> conQueue = new ArrayBlockingQueue<String>(cacheURLS);
	
	public static void main(String[] args){
		long start = System.currentTimeMillis();
		System.out.println("Start time: "+start);
		
//		String inputFilePath ="E:\\Documents\\微信公众号\\tmp\\sample_urls_100";
//		String outputFilePath ="E:\\Documents\\微信公众号\\tmp\\output_test";
		
		String inputFilePath = args[0];
		String accountOutputFilePath = args[1];
		String articleOutputFilePath = args[2];
		//线程数量
		threadNum = Integer.parseInt(args[3]);
		//从输入文件第x行开始读取
		lineNum = Integer.parseInt(args[4]);
		
		try{
			FileWriter acfw = new FileWriter(accountOutputFilePath);
			accountOutputWriter = new BufferedWriter(acfw);
			
			FileWriter arfw = new FileWriter(articleOutputFilePath);
			articleOutputWriter = new BufferedWriter(arfw);
			
			//启动URL读取线程
			WeixinFileUtil wf=new WeixinFileUtil(conQueue, inputFilePath, lineNum);
			new Thread(wf).start();
			
			//启动解析线程
			ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			CountDownLatch cdl = new CountDownLatch(threadNum);
			for(int i=0; i<threadNum; i++){
				WeixinHTMLAnalysis wht = new WeixinHTMLAnalysis(conQueue, accountOutputWriter, articleOutputWriter, cdl);
				executor.execute(wht);
			}
			cdl.await();
			executor.shutdown();
		}catch(Exception e){
			logger.info(e);
		}finally {//关闭输出文件流
			if(accountOutputWriter!= null){
				try{
					accountOutputWriter.flush();
					accountOutputWriter.close();
					
					articleOutputWriter.flush();
					articleOutputWriter.close();
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
