package weibo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.log4j.Logger;

public class WeiboGetUrls implements Runnable {

	public static Logger logger = Logger.getLogger(WeiboGetUrls.class);

//	private static String wb_url_prefix = "http://www.weibo.com/";
	private static String wb_url_prefix = "http://weibo.com/";
	private static String wb_url_sufix = "/info";

	private  ArrayBlockingQueue<String> urlsQueue = new ArrayBlockingQueue<String>(10000);
	private  String filePath = "";
	private  int readFromLine = 0;

	public static void main(String[] args) {
		String inputfp = "E:\\work\\tmp\\weibo_id_20161211_head_200";
		ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(10000);
		WeiboGetUrls wfu = new WeiboGetUrls(queue, inputfp, 0);

		wfu.getUrlQueue(inputfp);
		System.out.println(queue);
	}

	/**
	 * constructor
	 * 
	 * @param queue
	 *            存放URL的阻塞队列
	 * @param inputFile
	 *            读取微博ID的输入文件
	 * @param readFromLine
	 *            从输入文件的某一行开始读取
	 */
	public WeiboGetUrls(ArrayBlockingQueue<String> queue, String inputFile, int readFromLine) {
		this.urlsQueue = queue;
		this.filePath = inputFile;
		this.readFromLine = readFromLine;
	}

	/**
	 * 多线程
	 */
	public void run() {
		try {
			String isDone = "Done";// 线程结束标志
			getUrlQueue(filePath);
			urlsQueue.put(isDone);
		} catch (InterruptedException e) {
			logger.info(e);
		}
	}

	/**
	 * 从文件中读取微博帐号ID，构造URL，然后将数据插入到同步队列中
	 * 
	 * @param fp account id 文件
	 */
	public void getUrlQueue(String fp) {
		BufferedReader bfReader = null;
		int ln = 0;
		try {
			File file = new File(filePath);
			bfReader = new BufferedReader(new FileReader(file));
			String tmpString = null;
			while ((tmpString = bfReader.readLine()) != null) {
				ln++;
				if (ln >= readFromLine) {
					String wb_url = wb_url_prefix + tmpString + wb_url_sufix;
					urlsQueue.put(wb_url);
				}
			}
		} catch (Exception e) {
			logger.info(e);
		} finally {
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
