package carBase;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import carBase.webParser.Autohome;
import carBase.webParser.Bitauto;
import carBase.webParser.Xcar;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		String filePath = "E:\\work\\汽车之家\\car_info_base\\";

		try {
			Autohome autohome = new Autohome(closeableHttpClient);
			autohome.crawlingCarInfo(filePath);
		} catch (Exception e) {
			logger.error("汽车之家爬取失败");
			logger.info(e);
		}

		try {
			Bitauto bitauto = new Bitauto(closeableHttpClient);
			bitauto.crawlingCarInfo(filePath);
		} catch (Exception e) {
			logger.error("易车爬取失败");
			logger.info(e);
		}

		try {
			Xcar xcar = new Xcar(closeableHttpClient);
			xcar.crawlingCarInfo(filePath);
		} catch (Exception e) {
			logger.error("爱卡汽车爬取失败");
			logger.info(e);
		}

	}
}
