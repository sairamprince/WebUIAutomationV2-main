package com.rgt.engine;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.rgt.base.Base;
import com.rgt.utils.CommonUtils;
import com.rgt.utils.ExcelUtils;



public class TestDriver 
{

	public WebDriver driver;
	public Properties prop;
	public ExcelUtils excel;
	public Base base;
	public CommonUtils commonUtils;
	public WebElement element;
	ExtentReports extentreport;
	ExtentSparkReporter spark;
	ExtentTest extentTest;
	Select selectDropDown;
	Actions act;
	//Actions act = new Actions(driver);
	public final String SCENARIO_SHEET_PATH = System.getProperty("user.dir")+"/resources/datafiles/TC_Master.xlsx";
	public final String ExtentReport_Path = System.getProperty("user.dir")+"/resources/htmlreports/WebAutomationReport.html";

	public void startExecution() throws IOException {
		commonUtils= new CommonUtils();
		extentreport = new ExtentReports();
		spark = new ExtentSparkReporter(ExtentReport_Path).viewConfigurer().viewOrder().as(new ViewName[] {ViewName.TEST,ViewName.DASHBOARD,ViewName.CATEGORY}).apply();
		spark.config().setTheme(Theme.DARK);
		spark.config().setDocumentTitle("Web UI Test");
		spark.config().setReportName("Web UI Test");
		extentreport.attachReporter(spark);
		excel = new ExcelUtils(SCENARIO_SHEET_PATH);
		int count =excel.getTestdata().size();
		int testCaseCount = excel.getTCMaster().size();
		Set<String> testCaseNames = new HashSet<String>();

		for(int j=0;j<testCaseCount;j++) {

			testCaseNames.add(excel.getTCMaster().get(j).getTestCase());
			extentTest=extentreport.createTest(excel.getTCMaster().get(j).getTestCase());

			for (int i = 0; i < count; i++) {

				if(excel.getTCMaster().get(j).getTestCase()==excel.getTestdata().get(i).getTestCase()) {
					Set<String> tc= new HashSet<>();
					tc.add(excel.getTestdata().get(i).getSteps());
					int stepsCount= tc.size();

					for (int k = 0; k < stepsCount; k++) {

						String locatorType = excel.getTestdata().get(i).getLocatorType().trim();
						String locatorValue = excel.getTestdata().get(i).getLocatorValue().trim();
						String action = excel.getTestdata().get(i).getAction().trim();
						String value = excel.getTestdata().get(i).getInputValue().trim();
						String steps = excel.getTestdata().get(i).getTestSteps().trim();

						try {
							switch (action) {
							case "OPEN_BROWSER":
								base = new Base();
								prop =base.init_properties();
								if (value.isEmpty() || value.equals("NA")) {
									driver = base.init_driver(prop.getProperty("browser"));
									extentTest.pass(steps+"--"+prop.getProperty("browser"));
									System.out.println(steps);

								} else {

									driver = base.init_driver(value);
									extentTest.pass(steps+"--"+value);
									System.out.println(steps +"--"+ value);
								}
								break;

							case "CLOSE_BROWSER":
								driver.quit();
								extentTest.pass(steps);
								System.out.println(steps +"--"+ value);
								break;

							case "ENTER_URL":
								if (value.isEmpty() || value.equals("NA")) {
									driver.get(prop.getProperty("url"));
									extentTest.pass(steps+"--"+prop.getProperty("browser"));
									System.out.println(steps +"--"+ value);
								} else {
									driver.get(value);
									extentTest.pass( steps +"--"+ value);
									System.out.println(steps +"--"+ value);
								}
								break;

							case "WAIT":
								Thread.sleep(Integer.parseInt(value)*1000);
								extentTest.pass(steps+"--"+value+"sec");
								System.out.println(steps +"--"+ value);
								break;

							case "IMPLICITLYWAIT":
								driver.manage().timeouts().implicitlyWait(Integer.parseInt(value), TimeUnit.SECONDS);
								extentTest.pass( steps+"--Implicit Wait");
								System.out.println(steps +"--"+ value);
								break;	

							case "ENTER":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								element.clear();
								element.sendKeys(value);
								extentTest.pass(steps+":"+value);
								System.out.println(steps +"--"+ value);
								break;

							case "ISDISPLAYED":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								element.isDisplayed();
								extentTest.pass(steps+" : Element displayed");
								System.out.println(steps +"--"+ value);
								break;

							case "CLICK":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								element.click();
								extentTest.pass(steps+" : Element clicked");
								System.out.println(steps +"--"+ value);
								break;

							case "EXPLICITWAIT":
								commonUtils.explicitWait(driver, locatorType, locatorValue, Integer.parseInt(value));
								extentTest.pass(steps+" : Wait for Element Visible");
								System.out.println(steps +"--"+ value);
								break;

							case "VERIFYTEXT":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								String actualText=element.getText();
								if(value.contentEquals(actualText)) {
									extentTest.pass(steps+"--"+"Actual:"+actualText+"--Expected:"+value);
								}else {
									extentTest.fail(steps+"--"+"Actual:"+actualText+"--Expected:"+value);
								}

								System.out.println(steps+"--"+ value);
								break;

							case "CLOSE":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								driver.close();
								extentTest.pass(steps);
								System.out.println(steps +"--"+ value);
								break;

							case "QUIT":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								driver.quit();
								extentTest.pass(steps);
								System.out.println(steps +"--"+ value);
								break;


							case "NAVIGATION_TO":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								driver.navigate().to(value);
								extentTest.pass(steps);
								System.out.println(steps +"--"+ value);
								break;

							case "NAVIGATE_BACK":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								driver.navigate().back();
								extentTest.pass(steps);
								System.out.println(steps +"--"+ value);
								break;

							case "NAVIGATE_FORWARD":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								driver.navigate().forward();
								extentTest.pass(steps);
								System.out.println(steps +"--"+ value);
								break;

							case "NAVIGATE_REFRESH":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								driver.navigate().refresh();
								extentTest.pass(steps);
								System.out.println(steps +"--"+ value);
								break;

							case "ALERT_WITH_OK":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								driver.switchTo().alert().accept();
								extentTest.pass(steps+" : Clicked OK button");
								System.out.println(steps +"--"+ value);
								break;

							case "ALERT_CONFIRMBOX_WITH_OK":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								driver.switchTo().alert().accept();
								extentTest.pass(steps+" : Clicked OK button on Alert confirm box");
								System.out.println(steps +"--"+ value);
								break;

							case "ALERT_CONFIRMBOX_WITH_CANCEL":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								driver.switchTo().alert().dismiss();
								extentTest.pass(steps+" : Clicked CANCEL button on Alert confirm box");
								System.out.println(steps +"--"+ value);
								break;

							case "RIGHT_CLICK":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);						
								act =new Actions(driver);
								act.contextClick(element).build().perform();
								extentTest.pass(steps+" : Right clicked on element");
								System.out.println(steps +"--"+ value);
								break;

							case"DOUBLE_CLICK":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								act = new Actions(driver);
								act.doubleClick(element).perform();
								extentTest.pass(steps+" : Double clicked on element");
								System.out.println(steps +"--"+ value);
								break;

							case"MOUSE_HOVER":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								act = new Actions(driver);
								act.moveToElement(element).build().perform();
								extentTest.pass(steps+" : Mouse hovered on element");
								System.out.println(steps +"--"+ value);
								break;

							case"MOUSE_HOVER_CLICK":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								act = new Actions(driver);
								act.moveToElement(element).click().build().perform();
								extentTest.pass(steps+" : Mouse hovered and element clicked");
								System.out.println(steps +"--"+ value);
								break;

							case"DRAG":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								act = new Actions(driver);
								act.clickAndHold(element).build().perform();
								extentTest.pass(steps+" : Draged element ");
								System.out.println(steps +"--"+ value);
								break;

							case"DROP":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								act = new Actions(driver);
								act.moveToElement(element).release().build().perform();
								extentTest.pass(steps+" : Dropped element ");
								System.out.println(steps +"--"+ value);
								break;

								//case"SLIDER":
								//element=commonUtils.getLocators(driver,locatorType, locatorValue);
								//act = new Actions(driver);
								//act.dragAndDropBy(element, Integer.parseInt(value), Integer.parseInt(value)).build().perform();
								//extentTest.pass(steps+" : Slided the  element ");
								//System.out.println(steps +"--"+ value);
								//break;


							case"FRAME":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								driver.switchTo().frame(element);
								extentTest.pass(steps+" : Switched to frame ");
								System.out.println(steps +"--"+ value);
								break;

							case"WINDOW_HANDLES_TO_CHILD":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								Set<String> windowIDs = driver.getWindowHandles();
								List<String> windowsIDlist = new ArrayList(windowIDs);

								for(String windowID : windowsIDlist)
								{
									String title = driver.switchTo().window(windowID).getTitle();
									System.out.println(title);
									extentTest.pass(steps+" : Switched to parent to child window ");
								}  
								System.out.println(steps +"--"+ value);
								break;

							case"SCREENSHOT":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								TakesScreenshot scrShot =((TakesScreenshot)driver);
								File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);
								FileUtils.copyFile(SrcFile, new File("C:\\repo1\\WebUIAutomationV2\\resources\\screenshots"+value+".jpg"));
								extentTest.pass(steps + " : Screenshot captured ");
								System.out.println(steps + "--" + value);
								break;

							case"ELEMENT_SCREENSHOT":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								File srcfile = element.getScreenshotAs(OutputType.FILE);
								FileUtils.copyFile(srcfile, new File("C:\\repo1\\WebUIAutomationV2\\resources\\screenshots"+value+".png"));
								extentTest.pass(steps + " : WebElement Screenshot captured ");
								System.out.println(steps + "--" + value);
								break;

							case "SELECTBYVISIBILETEXT":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								selectDropDown=new Select(element);
								selectDropDown.selectByVisibleText(value);
								extentTest.pass( steps+":Selected Value By "+action+" is "+value);
								System.out.println(steps+"--"+ value);
								break;

							case "SELECTBYVALUE":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								selectDropDown=new Select(element);
								selectDropDown.selectByVisibleText(value);
								extentTest.pass( steps+":Selected Value By "+action+" is "+value);
								System.out.println(steps+"--"+ value);
								break;

							case "SELECTBYINDEX":
								element=commonUtils.getLocators(driver,locatorType, locatorValue);
								selectDropDown=new Select(element);
								selectDropDown.selectByIndex(Integer.parseInt(value));
								extentTest.pass( steps+":Selected Value By "+action+" is "+value);
								System.out.println(steps+"--"+ value);
								break;


							default:;

							}



						}
						catch(Exception e) {
							extentTest.fail(steps+"--"+e);
						}
					}
				}
				extentreport.flush();
			}
		}
	}
}


