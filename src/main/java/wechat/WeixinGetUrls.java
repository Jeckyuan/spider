package wechat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;


public class WeixinGetUrls implements Runnable{

	public static Logger logger = Logger.getLogger(WeixinGetUrls.class);
	
	public static void main(String[] args){
		String inputfp = "E:\\Documents\\微信公众号\\sample_urls";
		ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(10000);
		WeixinGetUrls wfu=new WeixinGetUrls(queue, inputfp, 97);
		wfu.getUrlQueue(inputfp);
//		System.out.println(wfu.getUrls(inputfp));
		System.out.println(queue);
	}
	
	private ArrayBlockingQueue<String> urlsQueue = new ArrayBlockingQueue<String>(10000);
	private String filePath = "";
	private int readFromLine = 0;
	
	public WeixinGetUrls(ArrayBlockingQueue<String> queue, String inputFile, int readFromLine){
		this.urlsQueue=queue;
		this.filePath=inputFile;
		this.readFromLine=readFromLine;
	}
	
	public void run(){
		try{
			String isDone = "Done";//线程结束标志
			getUrlQueue(filePath);
			urlsQueue.put(isDone);
		}catch(InterruptedException e){
			logger.info(e);
		}
		
	}
	
	/**
	 * 从文件中读取URL，然后将数据插入到同步队列中
	 * @param fp 读取URL文件的路径
	 */
	public void getUrlQueue(String fp)  {
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
					urlsQueue.put(columns[1]);
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
	
	/**
	 * 获取有效URLs，相同ID的URL取最后读取的url
	 * @param fp
	 * @return
	 */
	public HashMap<String, String> getUrlsMap(String fp){
		String filePath = fp;
		BufferedReader bfReader=null;
		HashMap<String, String> urls=new HashMap<String, String>();
		try{
			File file = new File(filePath);
			bfReader = new BufferedReader(new FileReader(file));
			String tmpString = null;
//			Pattern pt = Pattern.compile("http://mp.weixin.qq.com/s\\?__biz=.*==&mid=.*&idx=.*&sn=.*");
			Pattern pt = Pattern.compile("http://mp.weixin.qq.com/s\\?__biz=.*");
			while((tmpString=bfReader.readLine())!=null){
				String[] columns= tmpString.split("\t");
				if(columns.length>5){
					if(pt.matcher(columns[5]).matches()){
						urls.put(columns[5].split("=")[1], columns[5]);
					}
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (bfReader != null) {
                try {
                	bfReader.close();
                } catch (IOException e1) {
                }
            }
        }
		return urls;
	}
}
