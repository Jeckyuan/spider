package weibo;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


/**
 * HttpClient工具类
 * 
 * @return
 * @author SHANHY
 * @create 2015年12月18日
 */
public class HttpClientWeibo {

	public static Logger logger = Logger.getLogger(HttpClientWeibo.class);
	
    private static final int timeOut = 15 * 1000;
    
    private static CloseableHttpClient httpClient = null;

    private final static Object syncLock = new Object();
    
    private static String[] ckKeys={//"UOR",
//    		"SINAGLOBAL",
//    		"ULV",
//    		"__utma",
//    		"__utmz",
//    		"SUHB",
//    		"SCF",
//    		"un",
    		"SUB",
    		"SUBP",
    		"TC-Page-G0",
//    		"_s_tentry",
//    		"Apache",
//    		"TC-V5-G0",
//    		"YF-Page-G0",
//    		"YF-V5-G0"
    		};
    
    private static String[] ckVals={//"news.china.com.cn,widget.weibo.com,www.baidu.com",
//    		"3969300748396.546.1478913637693",
//    		"1481697451753:17:8:6:9531146149729.549.1481697451746:1481682313333",
//    		"15428400.387903278.1481525323.1481596684.1481609696.3",
//    		"15428400.1481525323.1.1.utmcsr=weibo.com|utmccn=(referral)|utmcmd=referral|utmcct=/login",
//    		"0auRNzsJMs2SNp",
//    		"Aovul-ga5_a0ztLYlLyWJL0D8FfOdrfPxBmvtP1m3OMjPLgTWIf-b2oXEDwychMucug_UkEERN3uSl87KM0Jpbo.",
//    		"yuanjiankuan@sina.com",
    		"_2AkMvBYOkf8NhqwJRmP4WzGPlZY11yQDEieLBAH7sJRMxHRl-yT9jqlw6tRCpaJ6w6JvFBqBAiY1iU68JQIq0xA..",
    		"0033WrSXqPxfM72-Ws9jqgMF55529P9D9WW2ZM_p2oCnMEkTDClV09He",
    		"1bbd8b9d418fd852a6ba73de929b3d0c",
//    		"",
//    		"-",
//    		"9531146149729.549.1481697451746",
//    		"784f6a787212ec9cddcc6f4608a78097",
//    		"ed0857c4c190a2e149fc966e43aaf725",
//    		"b2423472d8aef313d052f5591c93cb75"
    		};

    /**
     * 设置request参数
     */
    private static void config(HttpRequestBase httpRequestBase) {
        // 设置Header等
//    	 httpRequestBase.setHeader("Host", "weibo.com");
    	 httpRequestBase.setHeader("Connection", "keep-alive");
    	 httpRequestBase.setHeader("Upgrade-Insecure-Requests", "1");
         httpRequestBase.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
         httpRequestBase.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
         httpRequestBase.setHeader("Accept-Encoding", "gzip, deflate, sdch");
         httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
//         httpRequestBase.setHeader("Accept-Charset", "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");

        // 配置请求的超时设置
        /*
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut)
                .setSocketTimeout(timeOut)
                .build();
        */
//        HttpHost proxy = new HttpHost("10.1.3.110", 21);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut)
                .setSocketTimeout(timeOut)
//                .setProxy(proxy)
                .build();
        
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 获取HttpClient对象
     * 
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(200, 40, 100, hostname, port);
                }
            }
        }
        return httpClient;
    }

    /**
     * 创建HttpClient对象
     * 
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute, 
    		int maxRoute, String hostname, int port) {
    	
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create()
                .register("http", plainsf)
                .register("https", sslsf)
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                    int executionCount, HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
        
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler)
//                .setDefaultCookieStore(getCookieStore(ckKeys, ckVals))
                .setRedirectStrategy(new RedirectStrategy() {
					@Override
					public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
							throws ProtocolException {
						// TODO Auto-generated method stub
						return false;
					}
					
					@Override
					public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
							throws ProtocolException {
						// TODO Auto-generated method stub
						return null;
					}
				})
                .build();
        
        return httpClient;
    }

    /**
     * 设置post entity
     * @param httpost
     * @param params
     */
    private static void setPostParams(HttpPost httpost, Map<String, Object> params) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * POST请求URL获取内容
     * 
     * @param url
     * @return
     * @author SHANHY
     * @throws Exception 
     * @create 2015年12月18日
     */
    public static String post(String url, Map<String, Object> params) throws Exception {
        HttpPost httppost = new HttpPost(url);
        config(httppost);
        setPostParams(httppost, params);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httppost, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
//          e.printStackTrace();
            throw e;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * GET请求URL获取内容
     * 
     * @param url
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static String get(String url) {
        HttpGet httpget = new HttpGet(url);
//        httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
        config(httpget);
        CloseableHttpResponse response = null;
        try {
            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(getCookieStore(ckKeys, ckVals));
            response = getHttpClient(url).execute(httpget, context);
            HttpEntity entity = response.getEntity();
//            String result = EntityUtils.toString(entity, "utf-8");
            String result = EntityUtils.toString(entity, "gb2312");
            EntityUtils.consume(entity);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取网页内容
     * @param url
     * @param flag：是否更新cookie
     * @return
     */
    public static String[] get(String url, Boolean flag) {
        HttpGet httpget = new HttpGet(url);
        config(httpget);
        CloseableHttpResponse response = null;
        try {
            if(flag){
            	String[] ch=getRespondCookieHeader(url);
            	if(ch[1] !=  null && ch[2] !=  null){
            		ckKeys[2] = ch[1];
            		ckVals[2] = ch[2];
            	}
            }
//            System.out.println(ckKeys[0]+ckKeys[1]+ckKeys[2]);
//            System.out.println(ckVals[0]+ckVals[1]+ckVals[2]);
            HttpClientContext context = HttpClientContext.create();
            CookieStore cks = getCookieStore(ckKeys, ckVals);
            context.setCookieStore(cks);
            response = getHttpClient(url).execute(httpget, context);
            
            System.out.println("----------------get(String url, Boolean flag)------------------------");
//            System.out.println(response.getStatusLine());
			List<Cookie> cookies = cks.getCookies();
            for (int i = 0; i < cookies.size(); i++) {
                System.out.println("Local cookie: " + cookies.get(i));
            }
            
            HttpEntity entity = response.getEntity();
//            String result = EntityUtils.toString(entity, "utf-8");
            String result = EntityUtils.toString(entity, "gb2312");
            EntityUtils.consume(entity);
            //获取状态行
            String status=response.getStatusLine().toString();
            String[]  rs={result, status, (ckKeys[0]+"_"+ckKeys[1]+"_"+ckKeys[2]), (ckVals[0]+"_"+ckVals[1]+"_"+ckVals[2])};
            return rs;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * GET请求URL获取内容
     * 
     * @param url
     * @return 状态行，cookie name，cookie value
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static String[] getRespondCookieHeader(String url) {
        HttpGet httpget = new HttpGet(url);
        config(httpget);
        CloseableHttpResponse response = null;
        try {
        	HttpClientContext context = HttpClientContext.create();
        	CookieStore cookieStore = new BasicCookieStore();
        	context.setCookieStore(cookieStore);
        	
            response = getHttpClient(url).execute(httpget, context);
//            response = getHttpClient(url).execute(httpget);
            System.out.println("-----------------getRespondCookieHeader(String url)-----------------------");
            System.out.println(response.getStatusLine());
            List<Cookie> cookies = cookieStore.getCookies();
            for (int i = 0; i < cookies.size(); i++) {
                System.out.println("Local cookie: " + cookies.get(i));
            }
            
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "gb2312");
            EntityUtils.consume(entity);
            String[]  rs = new String[3];
            //获取状态行
            String status=response.getStatusLine().toString();
            rs[0] = status;
            logger.info("Get responsed headers, status line: "+status);
            //获取response的Set-Cookie header
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Set-Cookie"));
            while (it.hasNext()) {
			    HeaderElement elem = it.nextElement(); 
			    rs[1]=elem.getName();
			    rs[2] = elem.getValue();
			    logger.info("Set-Cookie: "+elem.getName() + " = " + elem.getValue());
            }
            return rs;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

/**
 * make cookie store from key and value arrary
 * @param ks
 * @param vs
 * @return
 */
    public static CookieStore getCookieStore(String[] ks, String[]  vs){
    	CookieStore cookieStore = new BasicCookieStore();
    	
    	for(int i=0; i<ks.length; i++ ){
    		BasicClientCookie ck = new BasicClientCookie(ks[i] ,vs[i]);
    		ck.setDomain(".weibo.com");
    		ck.setPath("/");
    		cookieStore.addCookie(ck);
    	}
    	
    	return cookieStore;
    }
    
    
    public static void main(String[] args) {
        // URL列表数组
        String[] urisToGet = {
//                "http://dictyoudao.com/",
                "http://weibo.com/1222398917/info",
                "http://weibo.com/1222420682/info"
                };

        long start = System.currentTimeMillis();
        try {
            int pagecount = urisToGet.length;
            ExecutorService executors = Executors.newFixedThreadPool(pagecount);
            CountDownLatch countDownLatch = new CountDownLatch(pagecount);
            for (int i = 0; i < pagecount; i++) {
                HttpGet httpget = new HttpGet(urisToGet[i]);
                config(httpget);
                // 启动线程抓取
                executors.execute(new GetRunnable(urisToGet[i], countDownLatch));
            }
            countDownLatch.await();
            executors.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("线程" + Thread.currentThread().getName() + ","
                    + System.currentTimeMillis() + ", 所有线程已完成，开始进入下一步！");
        }

        long end = System.currentTimeMillis();
        System.out.println("consume -> " + (end - start));
    }

    static class GetRunnable implements Runnable {
        private CountDownLatch countDownLatch;
        private String url;

        public GetRunnable(String url, CountDownLatch countDownLatch) {
            this.url = url;
            this.countDownLatch = countDownLatch;
        }

        public void run() {
            try {
//            	String[] st=HttpClientWeibo.get(url, true);
////            	System.out.println(st[0]);
//            	System.out.println(st[1]);
//            	System.out.println(st[2]);
//            	System.out.println(st[3]);
            	
            	String[] cookieArrary = HttpClientWeibo.getRespondCookieHeader(url);
            	System.out.println(cookieArrary[0]);
            	System.out.println(cookieArrary[1]);
            	System.out.println(cookieArrary[2]);
            } finally {
                countDownLatch.countDown();
            }
        }
    }
}