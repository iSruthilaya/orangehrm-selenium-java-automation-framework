package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class DummyClass extends BaseClass {
    
    @Test
    public void dummyTest() {
        //ExtentManager.startTest("DummyTest1 Test");
        
        String title = getDriver().getTitle();
        ExtentManager.logStep("Page title: " + title);
        
        // Use TestNG Assert instead of Java assert
        Assert.assertEquals(title, "OrangeHRM", "Test Failed - Title is Not Matching");
        
        System.out.println("Test Passed Title is Matching");
        ExtentManager.logStep("Validation Successful");
    }
}