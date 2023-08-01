package com.rgt.engine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.aventstack.*;
import com.aventstack.extentreports.io.BufferedWriterWriter;


import javax.imageio.ImageIO;
import javax.lang.model.util.Elements;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Duration;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
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
import org.testng.ITestListener;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.lowagie.text.DocumentException;
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

	public final String SCENARIO_SHEET_PATH = System.getProperty("user.dir")+"/resources/datafiles/TC_Master.xlsx";
	public final String ExtentReport_Path = System.getProperty("user.dir")+"/resources/reports/WebReport.html";
	public final String ExcelReport_Path = System.getProperty("user.dir")+"/resources/reports/ExcelReport.xlsx";

	public void startExecution() throws IOException, DocumentException {
	//public void startExecution(String tc_master) throws IOException, DocumentException {
		commonUtils= new CommonUtils();
		extentreport = new ExtentReports();
		spark = new ExtentSparkReporter(ExtentReport_Path).viewConfigurer().viewOrder().as(new ViewName[] {ViewName.TEST,ViewName.DASHBOARD,ViewName.CATEGORY,ViewName.DEVICE,ViewName.EXCEPTION }).apply();
		spark.config().setTheme(Theme.DARK);
		spark.config().setDocumentTitle("Web UI Test");
		spark.config().setReportName("Web UI Test Result");
		spark.config().setProtocol(Protocol.HTTPS);
		extentreport.setSystemInfo("username", "Sai Ram");
		extentreport.attachReporter(spark);
		extentreport.setSystemInfo("Environment", "QA");
		extentreport.setSystemInfo("User", "sairamprince33@gmail.com");
		extentreport.setSystemInfo("OS", System.getProperty("os.name"));
		extentreport.setSystemInfo("Java Version", System.getProperty("java.version"));

		//excel = new ExcelUtils(tc_master);
		excel = new ExcelUtils(SCENARIO_SHEET_PATH);
		int testCaseCount = excel.getTCMaster().size();
		System.out.println("Number of TestCases to be Executing = "+testCaseCount);


		try {
			FileInputStream fileInputStream = new FileInputStream(new File(ExtentReport_Path));
			Document document = Jsoup.parse(fileInputStream, "UTF-8", "");

			org.jsoup.select.Elements tableRows = document.select("table tr");

			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Report");

			Row headerRow = sheet.createRow(0);
			Cell testCaseNameHeader = headerRow.createCell(0);
			testCaseNameHeader.setCellValue("OPEN BOWSER");
			org.jsoup.select.Elements headerCells = tableRows.first().select("th");
			int columnNumber = 0;
			for (Element headerCellElement : headerCells) {
				Cell headerCell = headerRow.createCell(columnNumber++);
				headerCell.setCellValue(headerCellElement.text());

				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				headerCell.setCellStyle(headerCellStyle);
			}
			int rowNumber = 1;
			for (Element row : tableRows) {
				Row excelRow = sheet.createRow(rowNumber++);
				org.jsoup.select.Elements tableCells = row.select("td");

				int cellNumber = 0;
				for (Element cell : tableCells) {
					Cell excelCell = excelRow.createCell(cellNumber++);
					excelCell.setCellValue(cell.text());
				}
			}
			FileOutputStream fileOutputStream = new FileOutputStream(new File(ExcelReport_Path));
			workbook.write(fileOutputStream);
			fileInputStream.close();
			fileOutputStream.close();
			workbook.close();
			System.out.println("HTML report converted to Excel successfully!");
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int j=0;j<testCaseCount;j++) {
			extentTest=extentreport.createTest(excel.getTCMaster().get(j).getTestCase());
			int count = excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).size();
			for (int i = 0; i < count; i++) {

				try {
					switch (excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getAction().trim()) 
					{
					case "OPEN_BROWSER":
						base = new Base();
						prop =base.init_properties();
						if (excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim().isEmpty() || excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim().equals("NA")) 
						{
							driver = base.init_driver(prop.getProperty("browser"));
							extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+"--"+prop.getProperty("browser"));
							System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim());
						} else {
							driver = base.init_driver(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
							extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+"--"+excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
							System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						}
						break;

					case "CLOSE_BROWSER":
						driver.close();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim());
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "ENTER_URL":
						if (excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim().isEmpty() || excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim().equals("NA")) 
						{
							driver.get(prop.getProperty("url"));
							extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+"--"+prop.getProperty("browser"));
							System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						} else {
							driver.get(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
							extentTest.pass( excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
							System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						}
						break;

					case "WAIT":
						Thread.sleep(Integer.parseInt(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim())*1000);
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+"--"+excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim()+"sec");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "IMPLICITLYWAIT":
						driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim())*1000));
						extentTest.pass( excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+"--Implicit Wait");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "ENTER":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						element.clear();
						element.sendKeys(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+":"+excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "GET_TEXT":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						String getText = element.getText();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+":"+getText);
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ getText);
						break;

					case "ISDISPLAYED":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						element.isDisplayed();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Element is Present");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "CLICK":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						element.click();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Element clicked");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "EXPLICITWAIT":
						commonUtils.explicitWait(driver, excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim(), Integer.parseInt(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim()));
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Wait for Element Visible");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "VERIFYTEXT":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						String actualText=element.getText();
						if(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim().contentEquals(actualText)) 
						{
							extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+"--"+"Actual:"+actualText+"--Expected:"+excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						}else {
							extentTest.fail(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+"--"+"Actual:"+actualText+"--Expected:"+excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						}

						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "CLOSE":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						driver.close();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim());
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "QUIT":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						driver.quit();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim());
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "NAVIGATION_TO":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						driver.navigate().to(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim());
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "NAVIGATE_BACK":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						driver.navigate().back();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim());
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "NAVIGATE_FORWARD":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						driver.navigate().forward();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim());
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "NAVIGATE_REFRESH":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						driver.navigate().refresh();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim());
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "ALERT_WITH_OK":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						driver.switchTo().alert().accept();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Clicked OK button");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "ALERT_CONFIRMBOX_WITH_OK":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						driver.switchTo().alert().accept();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Clicked OK button on Alert confirm box");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "ALERT_CONFIRMBOX_WITH_CANCEL":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						driver.switchTo().alert().dismiss();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Clicked CANCEL button on Alert confirm box");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "RIGHT_CLICK":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());						
						act =new Actions(driver);
						act.contextClick(element).build().perform();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Right clicked on element");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case"DOUBLE_CLICK":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						act = new Actions(driver);
						act.doubleClick(element).perform();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Double clicked on element");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case"MOUSE_HOVER":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						act = new Actions(driver);
						act.moveToElement(element).build().perform();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Mouse hovered on element");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case"MOUSE_HOVER_CLICK":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						act = new Actions(driver);
						act.moveToElement(element).click().build().perform();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Mouse hovered and element clicked");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case"DRAG":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						act = new Actions(driver);
						act.clickAndHold(element).build().perform();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Draged element ");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case"DROP":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						act = new Actions(driver);
						act.moveToElement(element).release().build().perform();
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Dropped element ");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

						//case"SLIDER":
						//element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						//act = new Actions(driver);
						//act.dragAndDropBy(element, Integer.parseInt(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim()), Integer.parseInt(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim())).build().perform();
						//extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Slided the  element ");
						//System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						//break;


					case"FRAME":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						driver.switchTo().frame(element);
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Switched to frame ");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case"WINDOW_HANDLES_TO_CHILD":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						Set<String> windowIDs = driver.getWindowHandles();
						List<String> windowsIDlist = new ArrayList(windowIDs);

						for(String windowID : windowsIDlist)
						{
							String title = driver.switchTo().window(windowID).getTitle();
							System.out.println(title);
							extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+" : Switched to parent to child window ");
						}  
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() +"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "SCREENSHOT":
						element = commonUtils.getLocators(driver, excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						String screenshotFolderPath = "Screenshots/";
						File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
						String timeandDate = dateFormat.format(new Date());
						String screenshotFileName = "screenshot_" + timeandDate + ".png";
						String destinationFilePath = screenshotFolderPath + screenshotFileName;
						String base64Screenshot = null;
						try {
							byte[] screenshotBytes = FileUtils.readFileToByteArray(screenshotFile);
							base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							FileUtils.copyFile(screenshotFile, new File(destinationFilePath));
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (base64Screenshot != null) {
							String base64ScreenshotPath = "<a href='data:image/png;base64," + base64Screenshot + "' data-featherlight='image'><img src='data:image/png;base64," + base64Screenshot + "'/></a>";
							extentTest.log(Status.INFO, MarkupHelper.createLabel(base64ScreenshotPath, ExtentColor.GREEN));
						}
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() + " : Screenshot captured");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() + "--" + excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case"ELEMENT_SCREENSHOT":
						element = commonUtils.getLocators(driver, excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						String screenshotFolderPath1 = "Element-Screenshots/";
						File srcfile = element.getScreenshotAs(OutputType.FILE);
						SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd_HHmmss");
						String timeandDate1 = dateFormat1.format(new Date());
						String screenshotFileName1 = "Element_Screenshot_" + timeandDate1 + ".png";
						String destinationFilePath1 = screenshotFolderPath1 + screenshotFileName1;
						String base64Screenshot1 = null;
						try {
							byte[] screenshotBytes = FileUtils.readFileToByteArray(srcfile);
							base64Screenshot1 = Base64.getEncoder().encodeToString(screenshotBytes);
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							FileUtils.copyFile(srcfile, new File(destinationFilePath1));
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (base64Screenshot1 != null) {
							String base64ScreenshotPath = "<a href='data:image/png;base64," + base64Screenshot1 + "' data-featherlight='image'><img src='data:image/png;base64," + base64Screenshot1 + "'/></a>";
							extentTest.log(Status.INFO, MarkupHelper.createLabel(base64ScreenshotPath, ExtentColor.GREEN));
						}
						extentTest.pass(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() + " :Element Screenshot captured");
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() + "--" + excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "SELECTBYVISIBILETEXT":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						selectDropDown=new Select(element);
						selectDropDown.selectByVisibleText(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						extentTest.pass( excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+":Selected Value By "+excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getAction().trim()+" is "+excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "SELECTBYVALUE":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						selectDropDown=new Select(element);
						selectDropDown.selectByVisibleText(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						extentTest.pass( excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+":Selected Value By "+excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getAction().trim()+" is "+excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					case "SELECTBYINDEX":
						element=commonUtils.getLocators(driver,excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorType().trim(), excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getLocatorValue().trim());
						selectDropDown=new Select(element);
						selectDropDown.selectByIndex(Integer.parseInt(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim()));
						extentTest.pass( excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+":Selected Value By "+excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getAction().trim()+" is "+excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						System.out.println(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim()+"--"+ excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getInputValue().trim());
						break;

					default:;
					}
				}
				catch(Exception e)
				{
					extentTest.fail(MarkupHelper.createLabel(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() + "  is Failed", ExtentColor.RED));
					extentTest.fail(MarkupHelper.createCodeBlock(e.toString()));
					SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd_HHmmss");
					String timeandDate1 = dateFormat1.format(new Date());
					String screenshotPath = "Failed-Screenshots/Failed_Screenshot_"+timeandDate1+".png";
					File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
					String base64String = "";
					try {
						byte[] screenshotBytes = Files.readAllBytes(screenshotFile.toPath());
						base64String = Base64.getEncoder().encodeToString(screenshotBytes);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						FileUtils.copyFile(screenshotFile, new File(screenshotPath));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					if (base64String != null) {
						String base64ScreenshotPath = "<a href='data:image/png;base64," + base64String + "' data-featherlight='image'><img src='data:image/png;base64," + base64String + "'/></a>";
						extentTest.fail(MarkupHelper.createLabel(base64ScreenshotPath, ExtentColor.RED));
					}
					extentTest.fail(excel.getTestSteps(excel.getTCMaster().get(j).getTC_ID()).get(i).getTestSteps().trim() + " - Test case failed", MediaEntityBuilder.createScreenCaptureFromBase64String(base64String).build());
				}
				extentreport.flush();
			}
		}
	}

}



