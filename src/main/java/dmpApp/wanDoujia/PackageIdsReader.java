package dmpApp.wanDoujia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;


public class PackageIdsReader implements Runnable{

	private static Logger logger = Logger.getLogger(PackageIdsReader.class);
	private static final String url = "http://www.wandoujia.com/apps/";
	
	ArrayBlockingQueue<String> urlsQueue = new ArrayBlockingQueue<String>(10000);
	String filePath = "";
	int readFromLine = 0;
	
	public PackageIdsReader(ArrayBlockingQueue<String> queue, String inputFile, int readFromLine){
		this.urlsQueue=queue;
		this.filePath=inputFile;
		this.readFromLine=readFromLine;
	}
	
	@Override
	public void run(){
		try{
			String isDone = "Done";//线程结束标志
			getPackageIdsQueue();
			urlsQueue.put(isDone);
		}catch(InterruptedException e){
			logger.info(e);
		}
	}
	
	public void getPackageIdsQueue()  {
		BufferedReader bfReader=null;
		int ln=0;
		try{
			File file = new File(filePath);
			bfReader = new BufferedReader(new FileReader(file));
			String tmpString = null;
			while((tmpString=bfReader.readLine())!=null){
				ln++;
				if(ln>=readFromLine){
					String[] columns= tmpString.split("\t");
					urlsQueue.put(url + columns[0]);
				}
			}
		}catch(Exception e){
			logger.info(e);
		}finally {
            if (bfReader != null) {
                try {
                	bfReader.close();
                } catch (IOException ei) {
                	logger.info(ei);
                }
            }
        }
	}
	
}
