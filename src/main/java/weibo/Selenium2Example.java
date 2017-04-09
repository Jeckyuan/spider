package weibo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//import org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore.Processor.list_privileges;
//import org.apache.hadoop.hive.ql.optimizer.listbucketingpruner.ListBucketingPruner;
//import org.apache.http.cookie.Cookie;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

//import com.sun.tools.javac.util.List;

public class Selenium2Example  {
	
	private static String[] ckKeys={"UOR",
    		"SINAGLOBAL",
    		"ULV",
    		"__utma",
    		"__utmz",
    		"SUHB",
    		"SCF",
    		"un",
    		"SUB",
    		"SUBP",
    		"TC-Page-G0",
    		"_s_tentry",
    		"Apache",
    		"TC-V5-G0",
    		"YF-Page-G0",
    		"YF-V5-G0"};
    
    private static String[] ckVals={"news.china.com.cn,widget.weibo.com,www.baidu.com",
    		"3969300748396.546.1478913637693",
    		"1481697451753:17:8:6:9531146149729.549.1481697451746:1481682313333",
    		"15428400.387903278.1481525323.1481596684.1481609696.3",
    		"15428400.1481525323.1.1.utmcsr=weibo.com|utmccn=(referral)|utmcmd=referral|utmcct=/login",
    		"0auRNzsJMs2SNp",
    		"Aovul-ga5_a0ztLYlLyWJL0D8FfOdrfPxBmvtP1m3OMjPLgTWIf-b2oXEDwychMucug_UkEERN3uSl87KM0Jpbo.",
    		"yuanjiankuan@sina.com",
    		"_2AkMvDD43f8NhqwJRmP0VyGngao53zw_EieLBAH7sJRMxHRl-yT9jql4atRDG86l29H-KWWY8wE1cxVyP_G-stw..",
    		"0033WrSXqPxfM72-Ws9jqgMF55529P9D9WhwabOlcKeGc.5szFf-ajgS",
    		"2b304d86df6cbca200a4b69b18c732c4",
    		"-",
    		"9531146149729.549.1481697451746",
    		"784f6a787212ec9cddcc6f4608a78097",
    		"ed0857c4c190a2e149fc966e43aaf725",
    		"b2423472d8aef313d052f5591c93cb75"};
	
    public static void main(String[] args) {
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
    	System.setProperty("webdriver.gecko.driver","C:\\Program Files (x86)\\Mozilla Firefox\\geckodriver-v0.11.1-win32\\geckodriver.exe");
//    	System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\chromedriver_win32\\chromedriver.exe");
        WebDriver driver = new FirefoxDriver();
//        WebDriver driver = new ChromeDriver();

        
        // And now use this to visit Google
//        driver.get("http://www.google.com");
        /*
        driver.get("http://www.autohome.com.cn/beijing/");
        List<WebElement> wes = driver.findElements(By.className("title-subcnt-tab"));
        
        for(WebElement we: wes){
        	System.out.println(we.getText());
        }
        */
        String url = "http://weibo.com/";
//        driver.get("http://weibo.com/u/1768305123?is_hot=1");
//        driver.get("http://weibo.com/p/1005051056695363/info?mod=pedit_more");
        driver.get(url);
        List<WebElement> wes=driver.findElements(By.className("W_login_form"));
        for(WebElement we: wes){
        	System.out.println(we.getText());
        }
        /*
		// Now set the cookie. This one's valid for the entire domain
        for(int i=0; i<ckKeys.length; i++){
    		Cookie cookie = new Cookie(ckKeys[i], ckVals[0]);
    		driver.manage().addCookie(cookie);
        }
        */
		// And now output all the available cookies for the current URL
		Set<Cookie> allCookies = driver.manage().getCookies();
		for (Cookie loadedCookie : allCookies) {
		    System.out.println(String.format("%s -> %s", loadedCookie.getName(), loadedCookie.getValue()));
		}
	    /*    
		List<WebElement> wes  = driver.findElements(By.className("WB_frame_c"));

        */
		/*		
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");
        
        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());
        */
        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
//        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
//            public Boolean apply(WebDriver d) {
//                return d.getTitle().toLowerCase().startsWith("cheese!");
//            }
//        });
        
        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        
        //Close the browser
        driver.quit();
    }
}
