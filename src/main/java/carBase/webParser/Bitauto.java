package carBase.webParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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

public class Bitauto {
	private static Logger logger = Logger.getLogger(Bitauto.class);

	private CloseableHttpClient closeableHttpClient = null;
	// private ArrayList<String> brandInfoList = new ArrayList<>();
	private ArrayList<String> carSeriesInfoList = new ArrayList<>();
	private ArrayList<String> carDetailInfoList = new ArrayList<>();

	private static String ROOTURL = "http://car.bitauto.com";

	/**
	 * @param closeableHttpClient
	 */
	public Bitauto(CloseableHttpClient closeableHttpClient) {
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
	 * 从文件中读取各个品牌的链接
	 */
	public ArrayList<String> readBrands() {
		ArrayList<String> brandInfoList = new ArrayList<>();
		try (BufferedReader bufferedReader = new BufferedReader(
				new FileReader("src/main/java/carBase/webParser/brandTree"))) {
			String tmpLine = null;
			while ((tmpLine = bufferedReader.readLine()) != null) {
				JSONObject brandJB = JSONObject.fromObject(tmpLine);
				@SuppressWarnings("unchecked")
				Iterator<String> keys = brandJB.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					JSONArray brandJA = brandJB.getJSONArray(key);
					int count = brandJA.size();
					for (int i = 0; i < count; i++) {
						JSONObject brand = brandJA.getJSONObject(i);
						String url = brand.getString("url");
						String name = brand.getString("name");
						brandInfoList.add(brand.getString("url"));
						logger.info(name + ": " + url);
					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("读取车系链接失败");
			logger.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("读取车系链接失败");
			logger.error(e);
			e.printStackTrace();
		}
		return brandInfoList;
	}

	/**
	 * 获取车系综述链接
	 *
	 * @param brandInfoList
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public ArrayList<String> getCarSeriesUrl(ArrayList<String> brandInfoList) throws ParseException, IOException {
		ArrayList<String> csUrlSuffixList = new ArrayList<>();
		int csCount = 0;
		for (String brandUrlSuffix : brandInfoList) {
			String url = ROOTURL + brandUrlSuffix;
			String html = getHtml(url);
			Document doc = Jsoup.parse(html);
			Elements els = doc.getElementsByAttributeValue("class", "p-list");
			for (Element el : els) {
				Elements es = el.getElementsByAttributeValue("class", "name");
				if (es.size() == 1) {
					String csName = es.first().getElementsByTag("a").first().text();
					String urlSuffix = es.first().getElementsByTag("a").first().attr("href");
					String id = es.first().getElementsByTag("a").first().attr("id");
					id = id.trim().replace("m", "");
					csCount++;
					csUrlSuffixList.add(id + "_" + urlSuffix);
					logger.info(csName + ": " + id + "_" + urlSuffix);
				}
			}
		}
		logger.info("易车网找到" + csCount + "个车系");
		return csUrlSuffixList;
	}

	/**
	 * 获取车系和属于该车系的车型的信息
	 *
	 * @param csUrlSuffix
	 *            车系id_车系URL后缀
	 * @throws ParseException
	 * @throws IOException
	 */
	public void getCarSeriesInfo(String csUrlSuffix) throws ParseException, IOException {
		String[] idUrl = csUrlSuffix.split("_");
		if (idUrl.length == 2) {
			String csUrl = ROOTURL + idUrl[1];
			String html = getHtml(csUrl);
			Document doc = Jsoup.parse(html);

			String csId = idUrl[0];
			String csName = "";
			String csLocation = "";
			String csPrice = "";
			String typeRank = "";
			String webSite = "易车";
			String configuration = "";

			csName = doc.select(".brand-info > h1:nth-child(1) > a:nth-child(2)").text().trim();
			csLocation = doc.select(".crumbs-txt").text().replace("当前位置：", "").trim();
			csPrice = doc.select("span.price").text().trim();
			typeRank = doc.select(".h6").text().trim();

			Elements els = doc.select(".list-justified > li:nth-child(2) > a:nth-child(1)");
			if (els.size() > 0) {
				configuration = els.first().attr("href").trim();
				try {
					getCarInfo(csId, configuration);
				} catch (Exception e) {
					logger.error("获取车型参数配置信息失败：" + ROOTURL + configuration);
					logger.error(csUrl);
					logger.error(e);
				}
			} else {
				logger.error("无法获取车系的参数配置信息：" + csUrl);
			}

			String csInfo = webSite + "\t" + csId + "\t" + csName + "\t" + csLocation + "\t" + ROOTURL + idUrl[1] + "\t"
					+ csPrice + "\t" + "参考价" + "\t" + typeRank;
			carSeriesInfoList.add(csInfo);
			logger.info("获取到一个车系的信息：" + csInfo);
		} else {
			logger.error("获取车系信息输入错误：" + csUrlSuffix);
		}
	}

	/**
	 * 获取该车系所有车型的参数配置
	 *
	 * @param urlSuffix
	 *            车型参数配置URL的后缀
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public String getCarInfo(String csId, String urlSuffix) throws ParseException, IOException {
		String rs = "";

		String html = getHtml(ROOTURL + urlSuffix);
		Document doc = Jsoup.parse(html);
		Elements trForPicEls = doc.getElementsByTag("script");
		String carJsonStr = "";
		for (Element el : trForPicEls) {
			if (el.toString().contains("carCompareJson")) {
				String tmp = el.data().split("=")[1].trim();
				carJsonStr = tmp.substring(0, tmp.length() - 1);
				// logger.info(carJsonStr);
			}
		}
		JSONArray carInfoJsonArray = JSONArray.fromObject(carJsonStr);
		int count = carInfoJsonArray.size();
		for (int i = 0; i < count; i++) {
			JSONArray headCarInfoJa = carInfoJsonArray.getJSONArray(i).getJSONArray(0);
			JSONArray basicCarInfoJa = carInfoJsonArray.getJSONArray(i).getJSONArray(1);

			String id = headCarInfoJa.getString(0);
			String name = headCarInfoJa.getString(1);
			String year = headCarInfoJa.getString(7) + "款 ";
			String type = headCarInfoJa.getString(12);

			String price = basicCarInfoJa.getString(0);
			String power = "排量(L) " + basicCarInfoJa.getString(5);
			String transmission = basicCarInfoJa.getString(7);
			String powerLevel = basicCarInfoJa.getString(6) + "挡 ";
			String structure = "乘员人数 " + basicCarInfoJa.getString(17);

			String webSite = "易车";

			rs = webSite + "\t" + csId + "\t" + id + "\t" + year + name + "\t" + "厂商" + "\t" + price + "\t" + type
					+ "\t" + power + "\t" + powerLevel + transmission + "\t" + structure;
			carDetailInfoList.add(rs);
			logger.info("获取一条车型信息：" + rs);
		}
		return rs;
	}

	/**
	 * 爬取易车的车系和车型信息，存放到指定的文件中
	 *
	 * @param filePath
	 *            信息存放路径
	 * @throws ParseException
	 * @throws IOException
	 */
	public void crawlingCarInfo(String filePath) throws ParseException, IOException {
		ArrayList<String> brandInfoList = readBrands();
		ArrayList<String> csUrlList = getCarSeriesUrl(brandInfoList);
		for (String url : csUrlList) {
			try {
				getCarSeriesInfo(url);
			} catch (Exception e) {
				logger.error("获取车系信息失败：" + url);
			}
		}

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath + "bitauto_csinfo.txt"))) {
			for (String info : carSeriesInfoList) {
				bufferedWriter.write(info + "\n");
			}
		}

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath + "bitauto_carinfo.txt"))) {
			for (String info : carDetailInfoList) {
				bufferedWriter.write(info + "\n");
			}
		}
	}

	public static void main(String[] args) throws ParseException, IOException {
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		Bitauto bitauto = new Bitauto(closeableHttpClient);
		ArrayList<String> brandInfoList = bitauto.readBrands();
		ArrayList<String> csUrlList = bitauto.getCarSeriesUrl(brandInfoList);
		for (String url : csUrlList) {
			try {
				bitauto.getCarSeriesInfo(url);
			} catch (Exception e) {
				logger.error("获取车系信息失败：" + url);
			}
		}

		try (BufferedWriter bufferedWriter = new BufferedWriter(
				new FileWriter("E:\\work\\汽车之家\\tmp\\bitauto_csinfo.txt"))) {
			for (String info : bitauto.carSeriesInfoList) {
				bufferedWriter.write(info + "\n");
			}
		}

		try (BufferedWriter bufferedWriter = new BufferedWriter(
				new FileWriter("E:\\work\\汽车之家\\tmp\\bitauto_carinfo.txt"))) {
			for (String info : bitauto.carDetailInfoList) {
				bufferedWriter.write(info + "\n");
			}
		}
	}

}
