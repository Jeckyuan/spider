package com.ai.autohome.spider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.omg.CORBA.PRIVATE_MEMBER;

import com.ai.autohome.fileOperation.WeixinFileUtil;

import net.sf.ezmorph.test.ArrayAssertions;

public class WexinMain {
	public static Logger logger =  Logger.getLogger(WexinMain.class);
	
//	private final static int threadNum=20;
	private final static int cacheURLS=10000;
	
	public static List<Runnable> threadList = new ArrayList<Runnable>();
	private static BufferedWriter outputWriter = null;
	private static HashSet<String> idSet = new HashSet<String>();
	private static ArrayBlockingQueue<String> conQueue = new ArrayBlockingQueue<String>(cacheURLS);
	
	public static void main(String[] args){
		long start = System.currentTimeMillis();
		System.out.println("Start time: "+start);
		
//		String inputFilePath ="E:\\Documents\\微信公众号\\tmp\\sample_urls_100";
//		String outputFilePath ="E:\\Documents\\微信公众号\\tmp\\output_test";
		
		String inputFilePath = args[0];
		String outputFilePath = args[1];
		//线程数量
		int threadNum = Integer.parseInt(args[2]);
		//从输入文件第x行开始读取
		int lineNum = Integer.parseInt(args[3]);
		
		try{
			FileWriter fw = new FileWriter(outputFilePath);
			outputWriter = new BufferedWriter(fw);
			
			//启动URL读取线程
			WeixinFileUtil wf=new WeixinFileUtil(conQueue, inputFilePath, lineNum);
			new Thread(wf).start();
			
			//启动解析线程
			ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			CountDownLatch cdl = new CountDownLatch(threadNum);
			for(int i=0; i<threadNum; i++){
				WeixinHTMLAnalysis wht = new WeixinHTMLAnalysis(conQueue, idSet, outputWriter, cdl);
				executor.execute(wht);
			}
			cdl.await();
			executor.shutdown();
			
			/*for(int i=0; i<threadNum; i++){
				WeixinHTMLAnalysis wht= new WeixinHTMLAnalysis(conQueue, idSet, outputWriter);
				Thread t=new Thread(wht);
				t.setName("_thread_"+i);
				threadList.add(wht);
				t.start();
			}*/
			
			/*
			//监控解析线程是否全部结束，若全部结束后关闭输出流
			while (true) {
				int finishedThreadNum = 0;
				for (int i=0; i < threadList.size(); i++) {
					WeixinHTMLAnalysis t = (WeixinHTMLAnalysis) threadList.get(i);
					if (t.isFinish) {
						finishedThreadNum++;
					}
				}
				
				if (finishedThreadNum == threadList.size()) {// 所有线程结束
					break;
				}
				
				try {
					Thread.sleep(1000);
					System.out.println("Please wait 1000 ms!");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			*/
		}catch(Exception e){
//			e.printStackTrace();
			logger.info(e);
		}finally {//关闭输出文件流
			if(outputWriter!= null){
				try{
					outputWriter.flush();
					outputWriter.close();
				}catch(Exception ec){
//					ec.printStackTrace();
					logger.info(ec);
				}
			}
		}
		
		long end = System.currentTimeMillis();
		System.out.print("Used time: ");
		System.out.println(end-start);
	}
	
	
}
