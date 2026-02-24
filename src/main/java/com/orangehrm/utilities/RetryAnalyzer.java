package com.orangehrm.utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer{

	private int retryCount=0;
	private static final int maxretryCount=1;
	@Override
	public boolean retry(ITestResult result) {
		if(retryCount<maxretryCount)
		{
			retryCount++;
			return true;
		}
		return false;
	}

}
