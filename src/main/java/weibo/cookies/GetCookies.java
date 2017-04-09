package weibo.cookies;

import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import weibo.webdriver.SmartWebDriver;
import weibo.webdriver.WebDriverFactory;


public class GetCookies {

	public static Logger logger=Logger.getLogger(GetCookies.class);
	
	public static void getHeaders(String ul) throws Exception{
		SmartWebDriver smartWebDriver = null;
		try{
	        logger.info(".............................");
	        smartWebDriver = WebDriverFactory.getInstance("chrome");

	        String url = "http://www.baidu.com/";
//	        String url = "http://weibo.com/1222398917/info";
//	        String url = "http://weibo.com/u/1222398917/";
	        smartWebDriver.driver.get(url);
	        
//	        System.out.println("Page title is: " + smartWebDriver.driver.getTitle());
	        HashMap<String , String> coookieMap = new HashMap<>();
	        
	        Set<Cookie> cookies = smartWebDriver.driver.manage().getCookies();
	        
//	        smartWebDriver.driver.manage().
	        
            for (Cookie cookie : cookies) {
            	/*
                if ("skey" .equals(cookie.getName())) {
                    qq.setCookie_skey(cookie.getValue());
                } else if ("p_uin" .equals(cookie.getName())) {
                    qq.setCookie_p_uin(cookie.getValue());
                } else if ("p_skey" .equals(cookie.getName())) {
                    qq.setCookie_p_skey(cookie.getValue());
                }
                */
            	coookieMap.put(cookie.getName(), cookie.getValue());
            }
            
            System.out.println(coookieMap);
		}finally{
//			WebDriverFactory.close(smartWebDriver);
		}
    }
	
	public static void main(String[] args) throws Exception{
		GetCookies.getHeaders("test");
	}
}
