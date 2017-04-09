package carBase.webParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Autohome {
	private static Logger logger = Logger.getLogger(Autohome.class);
	private static String[] alphabet = { "A", "B", "C", "D", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "W", "X", "Y", "Z" };

	private CloseableHttpClient closeableHttpClient = null;
	private ArrayList<String> carSeriesInfoList = new ArrayList<>();
	private ArrayList<String> carDetailInfoList = new ArrayList<>();

	/**
	 * @param closeableHttpClient
	 */
	public Autohome(CloseableHttpClient closeableHttpClient) {
		super();
		this.closeableHttpClient = closeableHttpClient;
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
	 * 获取所有的车系链接
	 *
	 * @return
	 */
	public ArrayList<String> getCarSeriesUrl() {
		String rootUrl = "http://www.autohome.com.cn/grade/carhtml/";
		ArrayList<String> csUrlList = new ArrayList<>();
		for (String al : alphabet) {
			String url = rootUrl + al + "_photo.html";
			try {
				String html = getHtml(url);
				Document doc = Jsoup.parse(html);
				Elements els = doc.getElementsByTag("h4");
				for (Element el : els) {
					Element e = el.getElementsByTag("a").first();
					csUrlList.add(e.attr("href"));
					// System.out.println(e.text() + ": " + e.attr("href"));
				}
			} catch (Exception e) {
				logger.error("获取车系链接失败：" + url);
				logger.error(e);
			}
		}
		logger.info("成功获取到" + csUrlList.size() + "个车系的链接");
		return csUrlList;
	}

	/**
	 * 获取url车系的综述信息和属于该车系的所有车型的信息
	 *
	 * @param csUrl
	 * @throws ParseException
	 * @throws IOException
	 */
	public void getCarSeriesInfo(String csUrl) throws ParseException, IOException {
		String html = getHtml(csUrl);
		Document doc = Jsoup.parse(html);
		Elements es = new Elements();

		String csId = "";
		String csName = "";
		String csLocation = "";
		String csPrice = "";
		String typeRank = "";
		String webSite = "汽车之家";
		String configuration = "";

		// 车系ID和名称
		es = doc.getElementsByAttributeValue("class", "subnav-title-name");
		csId = es.get(0).getElementsByTag("a").get(0).attr("href").replace("/", "").trim();
		// 车系名称
		csName = es.get(0).getElementsByTag("a").get(0).text().trim();
		// 车系URL
		String csUl = csUrl;
		// 车系排名
		typeRank = doc.select(".subnav-title-rank").first().text().trim();
		// 判断该车系是否在售
		es = doc.getElementsByAttributeValue("class", "other-car");
		int aTagNum = es.get(0).getElementsByTag("a").size();
		if (aTagNum > 1) {
			// 当前位置
			es = doc.getElementsByAttributeValue("class", "path fn-clear");
			csLocation = es.get(0).text().replace("当前位置：", "").trim();
			// 新车指导价
			es = doc.getElementsByAttributeValue("class", "autoseries-info");
			csPrice = es.get(0).getElementsByTag("dt").get(0).text().replaceAll("\\(.*\\)", "").replaceAll("新车指导价：", "")
					.trim();
			// 参数配置URL
			es = doc.getElementsMatchingOwnText("参数配置");
			configuration = es.get(0).attr("href");
			// 获取该车系所有车型的信息
			if (!configuration.equals("")) {
				ArrayList<String> carsInfo = getCarInfo(configuration);
				if (carsInfo.size() != 0) {
					for (String ci : carsInfo) {
						String cf = webSite + "\t" + csId + "\t" + ci;
						logger.info("--获取到一个车型的信息：" + cf);
						carDetailInfoList.add(cf);
					}
				} else {
					logger.info("无法获取该车系所有车型的信息" + csUrl);
				}
			} else {
				logger.info("无法获取该车系所有车型的信息的链接" + csUrl);
			}

		} else {
			csLocation = doc.select(".path").first().text().replace("当前位置：", "").trim();
			csPrice = "已停售";
		}

		String csInfo = webSite + "\t" + csId + "\t" + csName + "\t" + csLocation + "\t" + csUl + "\t" + csPrice + "\t"
				+ "参考价" + "\t" + typeRank;
		logger.info("获取到一个车系的信息：" + csInfo);
		carSeriesInfoList.add(csInfo);
	}

	/**
	 * 获取某系车所有车型的参数配置
	 *
	 * @param carSeriesSettinggUrl
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @throws Exception
	 */
	public ArrayList<String> getCarInfo(String carSeriesSettinggUrl) throws ParseException, IOException {
		ArrayList<String> carInfoList = new ArrayList<>();
		String varConfig = "";
		try {
			String html = getHtml(carSeriesSettinggUrl);
			Document doc = Jsoup.parse(html);
			// logger.info(doc.getElementsContainingOwnText("paramtypeitems"));
			Elements els = doc.getElementsByTag("script");
			for (Element el : els) {
				if (el.data().contains("paramtypeitems")) {
					/* 取得JS变量数组 */
					String[] data = el.data().toString().split("var ");
					/* 取得单个JS变量 */
					for (String variable : data) {
						// 过滤variable为空的数据
						if (variable.contains("=")) {
							// 取到满足条件的JS变量
							if (variable.contains("paramtypeitems")) {
								String[] kvp = variable.split("=");
								varConfig = kvp[1].trim();
								// 去除行尾分号";"
								varConfig = varConfig.substring(0, varConfig.length() - 1);
							}
						}
					}
				}
			}
			// logger.info(varConfig);
			JSONArray paramitems = JSONObject.fromObject(varConfig.trim()).getJSONObject("result")
					.getJSONArray("paramtypeitems").getJSONObject(0).getJSONArray("paramitems");

			JSONArray carNameJsonObject = paramitems.getJSONObject(0).getJSONArray("valueitems");
			JSONArray carPriceJsonObject = paramitems.getJSONObject(1).getJSONArray("valueitems");
			JSONArray carProducerJsonObject = paramitems.getJSONObject(2).getJSONArray("valueitems");
			JSONArray carTypeJsonObject = paramitems.getJSONObject(3).getJSONArray("valueitems");
			JSONArray carPowerJsonObject = paramitems.getJSONObject(4).getJSONArray("valueitems");
			JSONArray carTransmissionJsonObject = paramitems.getJSONObject(5).getJSONArray("valueitems");
			JSONArray carStructureJsonObject = paramitems.getJSONObject(7).getJSONArray("valueitems");
			int count = carNameJsonObject.size();
			for (int i = 0; i < count; i++) {
				String carId = carNameJsonObject.getJSONObject(i).getString("specid");
				String carName = carNameJsonObject.getJSONObject(i).getString("value");
				String carProducer = carProducerJsonObject.getJSONObject(i).getString("value");
				String carPrice = carPriceJsonObject.getJSONObject(i).getString("value");
				String carType = carTypeJsonObject.getJSONObject(i).getString("value");
				String carPower = carPowerJsonObject.getJSONObject(i).getString("value");
				String carTransmission = carTransmissionJsonObject.getJSONObject(i).getString("value");
				String carStructure = carStructureJsonObject.getJSONObject(i).getString("value");
				String carInfo = carId + "\t" + carName + "\t" + carProducer + "\t" + carPrice + "\t" + carType + "\t"
						+ carPower + "\t" + carTransmission + "\t" + carStructure;
				carInfoList.add(carInfo);
			}
		} catch (Exception e) {
			logger.error("输入URL：" + carSeriesSettinggUrl);
			logger.error(e);
			e.printStackTrace();
		}

		return carInfoList;
	}

	public static void main(String[] args) throws ParseException, IOException {
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		Autohome autohome = new Autohome(closeableHttpClient);
		ArrayList<String> csUrlList = autohome.getCarSeriesUrl();

		for (String csUrl : csUrlList) {
			autohome.getCarSeriesInfo(csUrl);
		}

		try (BufferedWriter bufferedWriter = new BufferedWriter(
				new FileWriter("E:\\work\\汽车之家\\tmp\\autohome_csinfo.txt"))) {
			for (String info : autohome.carSeriesInfoList) {
				bufferedWriter.write(info + "\n");
			}
		}

		try (BufferedWriter bufferedWriter = new BufferedWriter(
				new FileWriter("E:\\work\\汽车之家\\tmp\\autohome_carinfo.txt"))) {
			for (String info : autohome.carDetailInfoList) {
				bufferedWriter.write(info + "\n");
			}
		}
	}

}
