package com.orangehrm.actiondriver;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;
	public static final Logger logger=BaseClass.logger;
	
	public ActionDriver(WebDriver driver)
	{
		this.driver=driver;
		int explicitWait=Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		this.wait=new WebDriverWait(driver,Duration.ofSeconds(explicitWait));
		logger.info("WebDriver instance is created");
	}
	
	//click method
	public void click(By by)
	{
		String elementDescription=getElementDescription(by);
		
		try {
			applyBorder(by,"green");
			waitForElementToBeClickable(by);
			driver.findElement(by).click();
			ExtentManager.logStep("Clicked on element: " + elementDescription);
			logger.info("Clicked an element---->"+elementDescription);
			} catch (Exception e) {
				applyBorder(by,"red");
			System.out.println("unable to click on element:"+e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to click element:", elementDescription+"_unable to click");
			logger.error("Unable to click on element:");
		}
	}
	
	//method to enter text into an input filed
	public void enterText(By by,String value)
	{
		try {
			waitForElementToBeVisible(by);
			applyBorder(by,"green");
			WebElement element=driver.findElement(by);
			element.clear();
			element.sendKeys(value);
			logger.info("Entered text on"+getElementDescription(by)+"----> "+value);
		} catch (Exception e) {
			applyBorder(by,"red");
			logger.error("Unable to enter value:"+e.getMessage());
		}
	}
	
	//method to check if an element is displayed
	public boolean isDisplayed(By by) {
	    try {
	        waitForElementToBeVisible(by);
	        applyBorder(by,"green");
	        logger.info("Element is displayed " + getElementDescription(by));
	        ExtentManager.logStep("Element is displayed: "+getElementDescription(by));
			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Element is displayed: ", "Element is displayed: "+getElementDescription(by));
	        return driver.findElement(by).isDisplayed();
	        
	    } catch (Exception e) {
	    	applyBorder(by,"red");
	    	logger.error("Unable to check if element is displayed:"+e.getMessage());
	    	ExtentManager.logFailure(BaseClass.getDriver(),"Element is not displayed: ","Elemenet is not displayed: "+getElementDescription(by));
			return false;
	    }
	}
	
	public String getText(By by)
	{
		try {
			waitForElementToBeVisible(by);
			applyBorder(by,"green");
			return driver.findElement(by).getText();
		} catch (Exception e) {
			applyBorder(by,"red");
			logger.error("Unable to get text from element:"+e.getMessage());
			return "";
		}
	}
	
	// Method to compare Two text -- changed the return type
		public boolean compareText(By by, String expectedText)
		{
			try {
				waitForElementToBeVisible(by);
				
				String actualText=driver.findElement(by).getText();
				if(actualText.equals(expectedText))
				{
					applyBorder(by,"green");
					logger.info("Text matches");
		ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Compare Text", "Text Verified Successfully! "+actualText+ " equals "+expectedText);
					return true;
				}
				else
				{
					applyBorder(by,"red");
					logger.error("text does not match");
		ExtentManager.logFailure(BaseClass.getDriver(), "Text Comparison Failed!", "Text Comparison Failed! "+actualText+ " not equals "+expectedText);
					return false;
				}
			} catch (Exception e) {

				logger.error("unable to compare text"+e.getMessage());
			}
			return false;
		}
		
		
		//Wait for page to Load
		public void waitForPageLoad(int timeOutInSec) {
		    try {
		    	
		 wait.withTimeout(Duration.ofSeconds(timeOutInSec)).until(webDriver ->
		            ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
		        );
		        logger.info("Page loaded successfully.");
		    } catch (Exception e) {
		    	logger.error("Page did not load within " + timeOutInSec + " seconds.");
		    }
		}
		
//Scroll to an element
		public void scrollToElement(By by)
		{
			try {
				applyBorder(by,"green");
				JavascriptExecutor js=(JavascriptExecutor) driver;
				WebElement element=driver.findElement(by);
				js.executeScript("arguments[0].scrollIntoView(true);",element);
			} catch (Exception e) {
				applyBorder(by,"red");
				logger.error("unable to scroll to element:"+e.getMessage());
			}
		}
		
	//wait for element to be clickable
	private void waitForElementToBeClickable(By by)
	{
		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error("element is not clickable:"+e.getMessage());
		}
	}
	////wait for element to be Visible
	private void waitForElementToBeVisible(By by)
	{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			logger.error("element is not visible:"+e.getMessage());
		}
	}
	
	// Method to get the description of an element using By locator
		public String getElementDescription(By locator) {
			// Check for null driver or locator to avoid NullPointerException
			if (driver == null) {
				return "Driver is not initialized.";
			}
			if (locator == null) {
				return "Locator is null.";
			}

			try {
				// Find the element using the locator
				WebElement element = driver.findElement(locator);

				// Get element attributes
				String name = element.getDomProperty("name");
				String id = element.getDomProperty("id");
				String text = element.getText();
				String className = element.getDomProperty("class");
				String placeholder = element.getDomProperty("placeholder");

				// Return a description based on available attributes
				if (isNotEmpty(name)) {
					return "Element with name: " + name;
				} else if (isNotEmpty(id)) {
					return "Element with ID: " + id;
				} else if (isNotEmpty(text)) {
					return "Element with text: " + truncate(text, 50);
				} else if (isNotEmpty(className)) {
					return "Element with class: " + className;
				} else if (isNotEmpty(placeholder)) {
					return "Element with placeholder: " + placeholder;
				} else {
					return "Element located using: " + locator.toString();
				}
			} catch (Exception e) {
				// Log exception for debugging
				e.printStackTrace(); // Replace with a logger in a real-world scenario
				return "Unable to describe element due to error: " + e.getMessage();
			}
		}
		// Utility method to check if a string is not null or empty
		private boolean isNotEmpty(String value) {
			return value != null && !value.isEmpty();
		}
		// Utility method to truncate long strings
		private String truncate(String value, int maxLength) {
			if (value == null || value.length() <= maxLength) {
				return value;
			}
			return value.substring(0, maxLength) + "...";
		}
		
		// Utility method to truncate long strings
		public void applyBorder(By by,String colour)
		{
			try {
				WebElement element=driver.findElement(by);
				String script="arguments[0].style.border='5px solid "+colour+"'";
				JavascriptExecutor js=(JavascriptExecutor) driver;
				js.executeScript(script,element);
				logger.info("Applied the border with colour "+colour+ " to element: "+getElementDescription(by));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.warn("Failed to apply the border to an element: "+getElementDescription(by),e);
			}
		}
		
}
