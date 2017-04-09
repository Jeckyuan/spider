package com.ai.autohome.spider;

//import com.asiainfo.mixdata.qqspider.bean.LoginedQQ;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/29.
 */
public class HttpClientUtils {
	private static final Log log = LogFactory.getLog(HttpClientUtils.class);
	public static ThreadSafeClientConnManager cm = null;

	public static void main(String[] args) throws Exception{
//    	String url =  "http://www.baidu.com";
    	String url =  "http://www.autohome.com.cn/car/";
    	System.out.println(get(url));
    }

	static {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

		cm = new ThreadSafeClientConnManager(schemeRegistry);
		try {
			int maxTotal = 1000;
			cm.setMaxTotal(maxTotal);
		} catch (NumberFormatException e) {
			log.error("Key[httpclient.max_total] Not Found in systemConfig.properties", e);
		}
		// 每条通道的并发连接数设置（连接池）
		try {
			int defaultMaxConnection = 50;
			cm.setDefaultMaxPerRoute(defaultMaxConnection);
		} catch (NumberFormatException e) {
			log.error("Key[httpclient.default_max_connection] Not Found in systemConfig.properties", e);
		}
	}

//	public static String get(String url) throws Exception {
//		return get(url, null, null);
//	}

	public static String get(String url) throws Exception {
		DefaultHttpClient client;
		HttpGet get = new HttpGet(url);
		// get.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64;
		// Trident/7.0; rv:11.0; SE 2.X MetaSr 1.0) like Gecko");
		// get.addHeader("Referer","http://user.qzone.qq.com/"+todoQQ);
		if (true) {// 是否启用连接池
			client = getHttpClient();
		} else {
			client = new DefaultHttpClient();
		}

		// setProxy(client);
		//
		// CookieStore cookieStore = getCookieStore(url,qq);
		// client.setCookieStore(cookieStore);

		HttpResponse response = client.execute(get);
		client.setCookieStore(null);// 清除cookie
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			InputStream inputStream = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
//			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "gb2312"));
			StringBuffer stringBuffer = new StringBuffer();
			String str = "";
			while ((str = br.readLine()) != null) {
				stringBuffer.append(str);
			}
			return stringBuffer.toString();
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	public static DefaultHttpClient getHttpClient() {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		//params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "gb2312");
		//params.setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET, "gb2312");
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000); // 连接超时时间
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 150000);// 读取超时时间
		return new DefaultHttpClient(cm, params);
	}

	public static void setProxy(DefaultHttpClient httpClient) throws Exception {
		String proxyHost = "117.121.97.25";
		int proxyPort = 21;
		String username = "chengxf";
		String pwd = "YAheMitv";
		proxyHost = "58.222.254.11";
		proxyPort = 3128;
		httpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort),
				new UsernamePasswordCredentials(username, pwd));
		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
		httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
	}

	public static void release() {
		if (cm != null) {
			cm.shutdown();
		}
	}

}
