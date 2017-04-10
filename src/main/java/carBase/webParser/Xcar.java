package carBase.webParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Xcar {
	private static Logger logger = Logger.getLogger(Xcar.class);

	private CloseableHttpClient closeableHttpClient = null;

	// private ArrayList<String> carSeriesId = new ArrayList<>();
	private HashMap<String, String> carSeriesId = new HashMap<>();
	private ArrayList<String> csInfoList = new ArrayList<>();
	private ArrayList<String> carInfoList = new ArrayList<>();
	private static String rootUrl = "http://newcar.xcar.com.cn/price/";
	private static String carSeriesUrlPrefix = "http://newcar.xcar.com.cn";

	/**
	 * @param closeableHttpClient
	 */
	public Xcar(CloseableHttpClient closeableHttpClient) {
		super();
		this.closeableHttpClient = closeableHttpClient;
	}

	/**
	 * 获取车系ID
	 *
	 * @param rootUrl
	 *            http://newcar.xcar.com.cn/price/
	 */
	public void getCarSeriesId(String rootUrl) {
		String html = null;
		try {
			html = getHtml(rootUrl);
		} catch (ParseException e) {
			logger.error(rootUrl);
			logger.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(rootUrl);
			logger.error(e);
			e.printStackTrace();
		}
		if (html != null) {
			Document doc = Jsoup.parse(html);
			Elements els = doc.getElementById("img_load_box").getElementsByTag("li");
			int csCount = 0;
			for (Element el : els) {
				Elements es = el.getElementsByTag("a");
				String csId = "";
				String csName = "";
				if (es.size() == 1) {
					csId = es.first().attr("href");
					csName = es.first().text();
					csCount++;
					// System.out.println(csId + ":" + csName);
					carSeriesId.put(csId, csName);
				} else {
					logger.error(es.toString());
				}
			}
			logger.info("共找到" + csCount + "个车系");
		} else {
			logger.error("获取车系ID信息失败");
		}

	}

	/**
	 * 获取车系信息
	 *
	 * @param carSeriesId
	 */
	public void getCarInfo(HashMap<String, String> carSeriesId) {
		int count = 0;
		for (String csId : carSeriesId.keySet()) {
			logger.debug("---" + carSeriesUrlPrefix + csId + "---");
			String html = null;
			try {
				html = getHtml(carSeriesUrlPrefix + csId);
			} catch (ParseException e) {
				logger.error(carSeriesUrlPrefix + csId);
				e.printStackTrace();
			} catch (IOException e) {
				logger.error(carSeriesUrlPrefix + csId);
				e.printStackTrace();
			}
			if (html != null) {
				String id = csId.trim().replace("/", "");
				String csName = "";
				String currentLocation = "";
				String url = carSeriesUrlPrefix + csId;
				String indicativePrice = "";
				String referencePrice = "";
				String typeRank = "";
				String configuration = "";
				String webSite = "爱卡汽车";

				Document doc = Jsoup.parse(html);
				csName = doc.select(".lt_f1").text() + doc.select(".tt_h1 > h1:nth-child(3)").text().trim();
				currentLocation = doc.select(".place").text().replace("当前位置:", "").trim();
				referencePrice = doc.select(".ref_gd").text().replace("厂商指导价：", "").trim();
				if (referencePrice.equals("")) {
					referencePrice = doc.select(".ref_pc").first().text().replace("厂商指导价：", "");
				}
				typeRank = doc.select(".ranking").text().trim();
				// 获取该车系所有车型的信息
				Elements els = doc.select(".tt_nav > li:nth-child(2) > a:nth-child(1)");
				if (els.size() != 0) {
					configuration = els.first().attr("href");
					if (!configuration.contains("javascript:void(0);")) {
						try {
							getCarDetailInfo(configuration);
						} catch (Exception e) {
							logger.error("无法获取该车系所属车型的详细信息：" + carSeriesUrlPrefix + csId);
							logger.info("无法获取车型信息：" + carSeriesUrlPrefix + configuration);
							e.printStackTrace();
						}
					} else {
						logger.error("无法获取该车系所属车型的详细信息：" + carSeriesUrlPrefix + csId);
						logger.info("无法获取车型信息：" + carSeriesUrlPrefix + configuration);
					}
				} else {
					logger.error("无法获取该车系所属车型的详细信息：" + carSeriesUrlPrefix + csId);
					logger.info("无法获取车型信息：" + els.toString());
				}

				String csInfo = webSite + "\t" + id + "\t" + csName + "\t" + currentLocation + "\t" + url + "\t"
						+ referencePrice + "\t" + indicativePrice + "\t" + typeRank;
				if (!csName.equals("")) {
					csInfoList.add(csInfo);
					count++;
					logger.info(csInfo);
				} else {
					logger.error("无法获取车系完整信息：" + carSeriesUrlPrefix + csId);
					logger.error("可以获取的车系信息有：" + csInfo);
				}
			} else {
				logger.error("获取车系详细信息失败：" + carSeriesUrlPrefix + csId);
			}
		}
		logger.info("成功获取到" + count + "个车系的详细信息");
	}

	public void getCarDetailInfo(String urlSuffix) throws ParseException, IOException {
		String html = getHtml(carSeriesUrlPrefix + urlSuffix);
		// 品牌，厂商指导价，级别，发动机，变速箱， 车身结构
		String[] idPrefix = { "mod_", "bname_", "price_", "type_name_", "m_disl_working_mpower_", "m_speed_transtype_",
				"m_door_seat_frame_" };
		ArrayList<String> carIdList = new ArrayList<>();
		if (html != null) {
			Document doc = Jsoup.parse(html);
			String csId = urlSuffix.trim().replace("config.htm", "").replace("/", "");
			// 获取该 车系下各个车型的id
			Element el = doc.getElementById("base_title");
			if (el != null) {
				Elements els = el.getElementsByTag("td");
				for (int i = 1; i < els.size(); i++) {
					Element e = els.get(i).getElementsByTag("a").first();
					String id = e.attr("href").trim().replace("/", "").replace("m", "");
					carIdList.add(id);
				}
				// 获取各个车型的详细信息
				for (String id : carIdList) {
					String carId = id;
					String name = "";
					String company = "";
					String indicativePrice = "";
					String carType = "";
					String machine = "";
					String transmissionType = "";
					String carStructure = "";
					String webSite = "爱卡汽车";

					name = doc.getElementById(idPrefix[0] + id).getElementsByTag("a").text();
					company = doc.getElementById(idPrefix[1] + id).getElementsByTag("a").text();
					indicativePrice = doc.getElementById(idPrefix[2] + id).text();
					carType = doc.getElementById(idPrefix[3] + id).getElementsByTag("a").text();
					machine = doc.getElementById(idPrefix[4] + id).text();
					transmissionType = doc.getElementById(idPrefix[5] + id).text();
					carStructure = doc.getElementById(idPrefix[6] + id).text();

					String carInfo = webSite + "\t" + csId + "\t" + carId + "\t" + name + "\t" + company + "\t"
							+ indicativePrice + "\t" + carType + "\t" + machine + "\t" + transmissionType + "\t"
							+ carStructure;
					if (!name.equals("")) {
						carInfoList.add(carInfo);
						logger.info(carInfo);
					} else {
						logger.error("无法获取车型完整信息：" + carSeriesUrlPrefix + carId);
						logger.error("可以获取到的车型信息：" + carInfo);
					}
				}
			} else {
				logger.error("未发现属于该车系的车型" + carSeriesUrlPrefix + urlSuffix);
			}
		} else {
			logger.error("获取车型详细信息失败：" + carSeriesUrlPrefix + urlSuffix);
		}
	}

	/**
	 * 获取网页数据
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public String getHtml(String url) throws ParseException, IOException {
		logger.debug("开始爬取：" + url);
		HttpGet httpGet = new HttpGet(url);
		// 设置user agent
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
		// 设置代理
		HttpHost proxy = new HttpHost("10.1.3.110", 21);
		RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
		httpGet.setConfig(requestConfig);

		CloseableHttpResponse response = null;
		String html = null;
		try {
			response = closeableHttpClient.execute(httpGet);
			// int statusCode = response.getStatusLine().getStatusCode();
			// logger.info("response status code: " + statusCode);
			HttpEntity entity = response.getEntity();
			html = EntityUtils.toString(entity, "gb2312");
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error(e);
					e.printStackTrace();
				}
			}
		}
		return html;
	}

	/**
	 * 爬取易车的车系和车型信息，存放到指定的文件中
	 *
	 * @param filePath
	 *            信息存放路径
	 * @throws IOException
	 */
	public void crawlingCarInfo(String filePath) throws IOException {
		getCarSeriesId(rootUrl);
		getCarInfo(carSeriesId);

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath + "Xcar_csinfo.txt"))) {
			for (String info : csInfoList) {
				bufferedWriter.write(info + "\n");
			}
		}

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath + "Xcar_carinfo.txt"))) {
			for (String info : carInfoList) {
				bufferedWriter.write(info + "\n");
			}
		}
	}

	public static void main(String[] args) throws IOException {
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		Xcar xcar = new Xcar(closeableHttpClient);
		xcar.getCarSeriesId(rootUrl);
		// System.out.println(xcar.carSeriesId.keySet());
		xcar.getCarInfo(xcar.carSeriesId);

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("E:\\work\\汽车之家\\tmp\\csinfo.txt"))) {
			for (String info : xcar.csInfoList) {
				bufferedWriter.write(info + "\n");
			}
		}

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("E:\\work\\汽车之家\\tmp\\carinfo.txt"))) {
			for (String info : xcar.carInfoList) {
				bufferedWriter.write(info + "\n");
			}
		}
	}

}
