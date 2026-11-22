package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class HomePage2 {
	private ActionDriver actionDriver;
	
	private By adminTab = By.xpath("//span[text()='Admin']");
	private By userIDButton = By.className("oxd-userdropdown-name");
	private By logoutButton = By.xpath("//a[text()='Logout']");
	private By oranageHRMlogo = By.xpath("//div[@class='oxd-brand-banner']//img");
	
	//initialize ActionDriver Object by passing WebDriver instance
		public HomePage2(WebDriver driver)
		{
			this.actionDriver=BaseClass.getActionDriver();
		}
		
		//method to verify if Admin tab is visible
		public boolean isAdminTabVisible()
		{
			return actionDriver.isDisplayed(adminTab);
		}
		public boolean verifyOrangeHRMLogo()
		{
			return actionDriver.isDisplayed(oranageHRMlogo);
		}
		
		// Method to perform logout operation
		public void logout() {
			actionDriver.click(userIDButton);
			actionDriver.click(logoutButton);
		}
}
