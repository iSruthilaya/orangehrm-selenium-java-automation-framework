package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class LoginPageTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}

	@Test(dataProvider="validLoginData", dataProviderClass = DataProviders.class)
	public void verifyValidLoginTest(String username, String password) {
	    //ExtentManager.startTest("Valid Login Test");
	    System.out.println("Running testMethod1 on thread: " + Thread.currentThread().getId());
	    
	    ExtentManager.logStep("Navigating to Login Page");
	    ExtentManager.logStepWithScreenshot(getDriver(), "Login page loaded", "Login Page");
	    
	    ExtentManager.logStep("Entering username and password");
	    loginPage.login(username, password);
	    ExtentManager.logStepWithScreenshot(getDriver(), "Credentials entered and login clicked", "After Login Click");
	    
	    ExtentManager.logStep("Verifying Admin tab is visible or not");
	    boolean isAdminVisible = homePage.isAdminTabVisible();
	    ExtentManager.logStepWithScreenshot(getDriver(), "Dashboard after login", "Dashboard");
	    
	    Assert.assertTrue(isAdminVisible, "Admin tab should be visible after successful login");
	    ExtentManager.logStep("Validation Successful");
	    
	    homePage.logout();
	    ExtentManager.logStepWithScreenshot(getDriver(), "After logout", "Logout Page");
	    ExtentManager.logStep("Logged out Successfully!");
	    staticWait(2);
	}

	@Test(dataProvider="inValidLoginData", dataProviderClass = DataProviders.class)
	public void inValidLoginTest(String username, String password) {
	    //ExtentManager.startTest("In-valid Login Test!");
	    System.out.println("Running testMethod2 on thread: " + Thread.currentThread().getId());
	    
	    ExtentManager.logStep("Navigating to Login Page");
	    ExtentManager.logStepWithScreenshot(getDriver(), "Login page before invalid login", "Login Page Before");
	    
	    ExtentManager.logStep("Entering invalid username and password");
	    loginPage.login(username,password);
	    ExtentManager.logStepWithScreenshot(getDriver(), "After entering invalid credentials", "Invalid Login Attempt");
	    
	    String expectedErrorMessage = "Invalid credentials";
	    boolean isErrorDisplayed = loginPage.verifyErrorMessage(expectedErrorMessage);
	    
	    if (isErrorDisplayed) {
	        ExtentManager.logStepWithScreenshot(getDriver(), "Error message displayed correctly", "Error Message");
	        ExtentManager.logStep("Validation Successful - Error message verified");
	    } else {
	        ExtentManager.logFailure(getDriver(), "Error message NOT displayed", "Missing Error Message");
	    }
	    
	    Assert.assertTrue(isErrorDisplayed, "Test Failed: Invalid error message");
	    ExtentManager.logStep("Test completed");
	}
	
	
}




