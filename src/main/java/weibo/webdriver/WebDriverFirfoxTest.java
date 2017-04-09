package weibo.webdriver;

import java.util.HashMap;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebDriverFirfoxTest {

	
	public static void main(String[] args) {
		String url="http://weibo.com/1222398917/info";
		
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
        WebDriver driver = new FirefoxDriver();

        // And now use this to visit Google
//        driver.get("http://www.baidu.com");
        driver.get(url);
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");
        
        HashMap<String , String> coookieMap = new HashMap<>();
        
        Set<Cookie> cookies = driver.manage().getCookies();
        
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
        
        /*
        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());
        
        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });
        */
        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        
        //Close the browser
//        driver.quit();
    }
}
