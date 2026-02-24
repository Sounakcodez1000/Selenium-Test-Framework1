package com.orangehrm.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class LoginPage {

	private ActionDriver actionDriver;
	
	private By userNameField=By.name("username");
	private By passwordField=By.cssSelector("input[type='password']");
	private By loginButton=By.xpath("//button[text()= ' Login ']");
	private By errorMessage=By.xpath("//p[text()= 'Invalid credentials']");
	
	//initialize ActionDriver Object by passing WebDriver instance
	public LoginPage(WebDriver driver)
	{
		this.actionDriver=BaseClass.getActionDriver();
	}
	
	//Method to perform login
	public void login(String username,String password)
	{
		actionDriver.enterText(userNameField, username);
		actionDriver.enterText(passwordField, password);
		actionDriver.click(loginButton);
	}
	
	//Method to check if error message is displayed
	public boolean isErrorMessageDisplayed()
	{
		return actionDriver.isDisplayed(errorMessage);
	}
	////Method to get the error message text
	public String getErrorMessageText()
	{
		return actionDriver.getText(errorMessage);
	}
	//Verify displayed error is correct or not
	public boolean verifyErrorMessage(String expectedError)
	{
		return actionDriver.compareText(errorMessage, expectedError);
		
	}
}
