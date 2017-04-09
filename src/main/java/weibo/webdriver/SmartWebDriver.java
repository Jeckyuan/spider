package weibo.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.service.DriverService;

/**
 * Created by Administrator on 2016/8/18.
 */
public class SmartWebDriver {
    public WebDriver driver;
    public DriverService service;

    public SmartWebDriver(WebDriver driver,DriverService service){
        this.driver = driver;
        this.service = service;
    }
}
