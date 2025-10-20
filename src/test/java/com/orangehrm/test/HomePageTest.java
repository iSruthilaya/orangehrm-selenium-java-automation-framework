package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class HomePageTest extends BaseClass {

    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
    }

    @Test(dataProvider="validLoginData", dataProviderClass = DataProviders.class)
	public void verifyOrangeHRMLogo(String username, String password) {
        ExtentManager.startTest("Home Page Verify Logo Test"); 
        
        ExtentManager.logStep("Navigating to Login Page");
        ExtentManager.logStepWithScreenshot(getDriver(), "Login page loaded", "Login Page");
        
        ExtentManager.logStep("Entering username and password");
        loginPage.login(username, password);
        ExtentManager.logStepWithScreenshot(getDriver(), "After successful login", "Dashboard");
        
        ExtentManager.logStep("Verifying Logo is visible or not");
        boolean isLogoVisible = homePage.verifyOrangeHRMlogo();
        
        if (isLogoVisible) {
            ExtentManager.logStepWithScreenshot(getDriver(), "Logo is visible on homepage", "Logo Verification");
            ExtentManager.logStep("Validation Successful - Logo is visible");
        } else {
            ExtentManager.logFailure(getDriver(), "Logo is NOT visible", "Logo Missing");
        }
        
        Assert.assertTrue(isLogoVisible, "Logo is not visible");
        
        homePage.logout();
        ExtentManager.logStepWithScreenshot(getDriver(), "After logout", "Logout Page");
        ExtentManager.logStep("Logged out Successfully!");
    }
}