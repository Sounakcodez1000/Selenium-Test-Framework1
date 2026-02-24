package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {
	
	protected static Properties prop;
//	protected static WebDriver driver;
//	private static ActionDriver actionDriver;
	
	private static ThreadLocal<WebDriver> driver=new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver=new ThreadLocal<>();
	
	
	public SoftAssert getSoftAssert() {
		return softAssert.get();
	}
	

	protected ThreadLocal<SoftAssert> softAssert=ThreadLocal.withInitial(SoftAssert::new);
	
	public static final Logger logger=LoggerManager.getLogger(BaseClass.class);
	
	
	//Getter & Setters for Prop
	public static Properties getProp() {
		return prop;
	}
	
	@BeforeSuite
	public void loadConfig() throws IOException
	{
		//Load the configuration file
				prop = new Properties();
				// CORRECT PATH ✅
				FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\src\\main\\resources\\config.properties");
				prop.load(fis);
				logger.info("config.properties file loaded");
				
				//start the extent report
			//ExtentManager.getReporter();---->implemented in TestListener class
	}
	
	//Initialize the WebDriver based on the browser specified in the configuration file
	private synchronized void launchBrowser()
	{
				String browser=prop.getProperty("browser");
				if(browser.equalsIgnoreCase("chrome"))
				{
					boolean headless = Boolean.parseBoolean(prop.getProperty("headless"));
					//driver=new ChromeDriver();
					ChromeOptions options=new ChromeOptions();
					if(headless) {
					options.addArguments("--headless=new");
					options.addArguments("--window-size=1920,1080");
					options.addArguments("--disable-gpu");
					options.addArguments("--disable-notifications","--no-sandbox","--disable-dev-shm-usage");
					options.addArguments("--disable-extensions");
					options.addArguments("--page-load-strategy=normal");
					options.addArguments("--remote-allow-origins=*");
					}
					
					driver.set(new ChromeDriver(options));
					ExtentManager.registerDriver(getDriver());
					logger.info("ChromeDriver Instance is created.");
				}
				else if(browser.equalsIgnoreCase("firefox"))
				{
					//driver=new FirefoxDriver();
					FirefoxOptions options = new FirefoxOptions();
		            options.addArguments("-headless");
					driver.set(new FirefoxDriver(options));
					ExtentManager.registerDriver(getDriver());
					logger.info("FirefoxDriver Instance is created.");
				}
				else if(browser.equalsIgnoreCase("edge"))
				{
					//driver=new EdgeDriver();
					EdgeOptions options = new EdgeOptions();
		            options.addArguments("--headless=new", "--disable-gpu","--no-sandbox","--disable-dev-shm-usage");
					driver.set(new EdgeDriver(options));
					ExtentManager.registerDriver(getDriver());
					logger.info("EdgeDriver Instance is created.");
				}
				else
				{
					throw new IllegalArgumentException("Browser not supported:"+browser);
				}
	}
	
	private synchronized void configureBrowser()
	{
		//Implicit wait
				int implicitWait=Integer.parseInt(prop.getProperty("implicitWait"));
				getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
				
				//maximize the browser
				 if (!Boolean.parseBoolean(prop.getProperty("headless"))) {
				        getDriver().manage().window().maximize();
				 }
				//Navigate to url
				try {
					getDriver().get(prop.getProperty("url"));
				} catch (Exception e) {
					System.out.println("Failed to navigate to URL: " + e.getMessage());
				}
	}
	@BeforeMethod
	public synchronized void setup() throws IOException
	{
		launchBrowser();
		configureBrowser();
		staticWait(5);
		// Sample logger message
				logger.info("WebDriver Initialized and Browser Maximized");
				logger.trace("This is a Trace message");
				logger.error("This is a error message");
				logger.debug("This is a debug message");
				logger.fatal("This is a fatal message");
				logger.warn("This is a warm message");

	/*    //Initialize the ActionDriver only once
		if(actionDriver==null)
		{
			actionDriver=new ActionDriver(driver);
			logger.info("ActionDriver instance is created");
		}
	  */
				
				//Initialize the ActionDriver for the current Thread
		actionDriver.set(new ActionDriver(getDriver()));
		logger.info("ActionDriver initialized for thread:"+Thread.currentThread().getId());
	}			

	// Getter Method for WebDriver
		public static WebDriver getDriver() {

			if (driver.get() == null) {
				System.out.println("WebDriver is not initialized");
				throw new IllegalStateException("WebDriver is not initialized");
			}
			return driver.get();
		}
		// Getter Method for ActionDriver
		public static ActionDriver getActionDriver() {

			if (actionDriver.get() == null) {
				System.out.println("ActionDriver is not initialized");
				throw new IllegalStateException("ActionDriver is not initialized");
			}
			return actionDriver.get();
		}

	
	@AfterMethod
	public synchronized void tearDown()
	{
		try {
			if(getDriver()!=null)
			{
				getDriver().quit();
			}
		} catch (Exception e) {
			System.out.println("Unable to quit the driver: "+e.getMessage());
		}
		logger.info("WebDriver instance is closed");
		driver.remove();
		actionDriver.remove();
		//ExtentManager.endTest();----->This is implemented in TestListener class
	}
	
	public void staticWait(int seconds)
	{
		LockSupport.parkNanos(Duration.ofSeconds(seconds).toNanos());
	}
}
