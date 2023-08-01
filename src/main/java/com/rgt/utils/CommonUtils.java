
package com.rgt.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.logging.FileHandler;

import org.apache.tools.ant.util.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;

import com.aventstack.extentreports.ExtentTest;


public class CommonUtils 
{

	public WebElement getLocators(WebDriver driver,String locatorType, String locatorValue)
	{
		WebElement element = null;
		switch(locatorType) 
		{

		case "id": element = driver.findElement(By.id(locatorValue));
		break;

		case "xpath": element = driver.findElement(By.xpath(locatorValue));
		break;

		case "linkText": element = driver.findElement(By.linkText(locatorValue));
		break;

		case "partialLinkText": element = driver.findElement(By.partialLinkText(locatorValue));
		break;

		case "name": element = driver.findElement(By.name(locatorValue));
		break;

		case "ClassName": element = driver.findElement(By.className(locatorValue));
		break;

		case "tagName": element = driver.findElement(By.tagName(locatorValue));
		break;

		case "cssSelector": element = driver.findElement(By.cssSelector(locatorValue));
		break;

		}

		return element;
	}

	public void explicitWait(WebDriver driver,String locatorType, String locatorValue, int inuputValue) 
	{
		WebDriverWait  wait = new WebDriverWait(driver,Duration.ofSeconds(inuputValue));
		switch(locatorType) 
		{
		case "id":
			wait.until((WebDriver d) -> ExpectedConditions.visibilityOfElementLocated(By.id(locatorValue)).apply(d));
			break;
		case "xpath":
			wait.until((WebDriver d) -> ExpectedConditions.visibilityOfElementLocated(By.xpath(locatorValue)).apply(d));
			break;
		case "linkText":
			wait.until((WebDriver d) -> ExpectedConditions.visibilityOfElementLocated(By.linkText(locatorValue)).apply(d));
			break;
		case "partialLinkText":
			wait.until((WebDriver d) -> ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(locatorValue)).apply(d));
			break;
		case "name":
			wait.until((WebDriver d) -> ExpectedConditions.visibilityOfElementLocated(By.name(locatorValue)).apply(d));
			break;
		case "className":
			wait.until((WebDriver d) -> ExpectedConditions.visibilityOfElementLocated(By.className(locatorValue)).apply(d));
			break;
		case "cssSelector":
			wait.until((WebDriver d) -> ExpectedConditions.visibilityOfElementLocated(By.cssSelector(locatorValue)).apply(d));
			break;
		case "tagName":
			wait.until((WebDriver d) -> ExpectedConditions.visibilityOfElementLocated(By.tagName(locatorValue)).apply(d));
			break;
		}

	}


}
