package com.orangehrm.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

	private static ExtentReports extent;
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	private static Map<Long, WebDriver> driverMap = new HashMap<>();

	// Initialize the Extent Report
	// Initialize the Extent Report - FIXED VERSION
	public synchronized static ExtentReports getReporter() {
		if (extent == null) {
			String reportPath = System.getProperty("user.dir") + "/src/test/resources/ExtentReport/ExtentReport.html";

			// CREATE PARENT DIRECTORIES IF THEY DON'T EXIST
			File reportFile = new File(reportPath);
			File parentDir = reportFile.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}

			ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
			spark.config().setReportName("Automation Test Report");
			spark.config().setDocumentTitle("OrangeHRM Report");
			spark.config().setTheme(Theme.DARK);

			extent = new ExtentReports();
			extent.attachReporter(spark);
			// Adding system information
			extent.setSystemInfo("Operating System", System.getProperty("os.name"));
			extent.setSystemInfo("Java Version", System.getProperty("java.version"));
			extent.setSystemInfo("User Name", System.getProperty("user.name"));
		}
		return extent;
	}

	// Start the Test
	public synchronized static ExtentTest startTest(String testName) {
		ExtentTest extentTest = getReporter().createTest(testName);
		test.set(extentTest);
		return extentTest;
	}

	// End a Test - FIXED
	public synchronized static void endTest() {
		test.remove();
	}

	// ADD THIS NEW METHOD to flush the report at the very end
	public synchronized static void flushReport() {
		if (extent != null) {
			extent.flush();
			System.out.println("Extent Report flushed and HTML file created!");
		}
	}

	// Get Current Thread's test
	public synchronized static ExtentTest getTest() {
		return test.get();
	}

	// Method to get the name of the current test
	public static String getTestName() {
		ExtentTest currentTest = getTest();
		if (currentTest != null) {
			return currentTest.getModel().getName();
		} else {
			return "No test is currently active for this thread";
		}
	}

	// Log a step
	public static void logStep(String logMessage) {
		getTest().info(logMessage);
	}

	// Log a step validation with screenshot
	public static void logStepWithScreenshot(WebDriver driver, String logMessage, String screenShotMessage) {
		getTest().pass(logMessage);
		// Screenshot method
		attachScreenshot(driver, screenShotMessage);

	}

	// Log a step validation for API
	public static void logStepValidationForAPI(String logMessage) {
		getTest().pass(logMessage);
	}

	// Log a Failure
	public static void logFailure(WebDriver driver, String logMessage, String screenShotMessage) {
		String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);
		getTest().fail(colorMessage);
		// Screenshot method
		attachScreenshot(driver, screenShotMessage);
	}

	// Log a Failure for API
	public static void logFailureAPI(String logMessage) {
		String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);
		getTest().fail(colorMessage);
	}

	// Log a skip
	public static void logSkip(String logMessage) {
		String colorMessage = String.format("<span style='color:orange;'>%s</span>", logMessage);
		getTest().skip(colorMessage);
	}

	// Take a screenshot with date and time in the file

	public synchronized static String takeScreenshot(WebDriver driver, String screenshotName) {
	    try {
	        TakesScreenshot ts = (TakesScreenshot) driver;
	        File src = ts.getScreenshotAs(OutputType.FILE);
	        
	        // Create screenshots directory if it doesn't exist
	        String screenshotsDir = System.getProperty("user.dir") + "/src/test/resources/screenshots/";
	        File dir = new File(screenshotsDir);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }
	        
	        // Format date and Time for file name
	        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

	        // Saving the screenshot to a file
	        String destPath = screenshotsDir + screenshotName + "_" + timeStamp + ".png";
	        File finalPath = new File(destPath);
	        
	        FileUtils.copyFile(src, finalPath);
	        System.out.println("Screenshot saved: " + destPath);
	        
	        // Convert screenshot to Base64 for embedding in the Report
	        return convertToBase64(src);
	        
	    } catch (Exception e) {
	        System.out.println("Failed to take screenshot: " + e.getMessage());
	        e.printStackTrace();
	        return "";
	    }
	}

	// Fixed attach screenshot method
	public synchronized static void attachScreenshot(WebDriver driver, String message) {
	    try {
	        String screenShotBase64 = takeScreenshot(driver, getTestName());
	        if (!screenShotBase64.isEmpty()) {
	            getTest().info(message, 
	                com.aventstack.extentreports.MediaEntityBuilder
	                    .createScreenCaptureFromBase64String(screenShotBase64)
	                    .build()
	            );
	            System.out.println("Screenshot attached to report: " + message);
	        } else {
	            getTest().info(message + " - [Screenshot failed]");
	        }
	    } catch (Exception e) {
	        getTest().info("Failed to attach screenshot: " + message);
	        System.out.println("Error attaching screenshot: " + e.getMessage());
	    }
	}

	// Convert screenshot to Base64 format
	public static String convertToBase64(File screenShotFile) {
		String base64Format = "";
		// Read the file content into a byte array
		try {
			byte[] fileContent = FileUtils.readFileToByteArray(screenShotFile);
			base64Format = Base64.getEncoder().encodeToString(fileContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return base64Format;
	}


	// Register WebDriver for current Thread
	public static void registerDriver(WebDriver driver) {
		driverMap.put(Thread.currentThread().getId(), driver);
	}

}