package weibo.webdriver;

//import com.asiainfo.mixdata.qqspider.conf.Config;
//import com.asiainfo.mixdata.qqspider.util.Const;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;

import java.io.File;

/**
 * Created by Administrator on 2016/8/18.
 */
public class WebDriverFactory {
    public static SmartWebDriver getInstance(String name) throws Exception{
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Administrator\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe");
        // 创建一个 ChromeDriver 的接口，用于连接 Chrome
        @SuppressWarnings("deprecation")
        DriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File( "E:\\workspace\\chromedriver_win32\\chromedriver.exe"))
                .usingAnyFreePort().build();
        service.start();
        // 创建一个 Chrome 的浏览器实例
        WebDriver driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());

        return new SmartWebDriver(driver,service);
    }

    public static void close(SmartWebDriver smartWebDriver){
        if(smartWebDriver == null){
            return;
        }
        if(smartWebDriver.driver != null){
            smartWebDriver.driver.quit();
        }
        if(smartWebDriver.service != null){
            smartWebDriver.service.stop();
        }
    }
}
