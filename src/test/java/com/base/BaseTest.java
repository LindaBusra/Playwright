package com.base;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.SelectOption;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

public class BaseTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;

    // Setup once before all tests
    @BeforeAll
    static void launchBrowser() {
        // Start Playwright and browser (non-headless for visibility)
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
    }

    // Setup before each test
    @BeforeEach
    void createContextAndPage() {
        // Open new browser context and tab
        context = browser.newContext();
        page = context.newPage();
    }

    // Teardown after each test
    @AfterEach
    void closeContext() {
        page.close();    // close current page
        context.close(); // close current context
    }

    // Teardown once after all tests
    @AfterAll
    static void closeBrowser() {
        browser.close();    // close browser
        playwright.close(); // stop Playwright
    }

    // ===== Reusable helper methods =====

    // Go to a specific URL
    protected void goTo(String url) {
        page.navigate(url); // go to web page
    }

    // Click element by XPath or CSS
    protected void click(String locator) {
        page.locator(locator).click(); // click element
    }

    // Type text into input
    protected void fillInput(String locator, String text) {
        page.locator(locator).fill(text); // fill input
    }

    // Press keyboard Enter
    protected void pressEnter() {
        page.keyboard().press("Enter"); // simulate Enter key
    }

    // Wait for element to appear
    protected void waitForElement(String locator) {
        page.waitForSelector(locator); // wait for DOM presence
    }

    // Check if element is visible
    protected boolean isVisible(String locator) {
        return page.locator(locator).isVisible(); // true if visible
    }

    // Get text from element
    protected String getText(String locator) {
        return page.locator(locator).innerText(); // read text
    }

    // Screenshot
    protected void takeScreenshot(String fileName) {
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(fileName)));
    }

    // Wait for X milliseconds
    protected void waitFor(int ms) {
        page.waitForTimeout(ms); // delay execution
    }

    // Hover over an element
    protected void hover(String locator) {
        page.locator(locator).hover(); // move the mouse over the element
    }

    // Check if specific text exists anywhere on the page
    protected boolean isTextPresent(String text) {
        return page.locator("body").innerText().contains(text); // search for text inside the body
    }

    // Select an option from a dropdown by visible label
    protected void selectOptionByText(String locator, String visibleText) {
        page.locator(locator).selectOption(new SelectOption().setLabel(visibleText)); // select option by its label
    }

    // Select an option from a dropdown by its value attribute
    protected void selectOptionByValue(String locator, String value) {
        page.locator(locator).selectOption(value); // select option by its value attribute
    }

    // Check a checkbox (if not already checked)
    protected void check(String locator) {
        page.locator(locator).check(); // mark checkbox as checked
    }

    // Uncheck a checkbox (if it's currently checked)
    protected void uncheck(String locator) {
        page.locator(locator).uncheck(); // uncheck checkbox
    }

    // Check if element exists in the DOM (regardless of visibility)
    protected boolean isElementPresent(String locator) {
        return page.locator(locator).count() > 0; // element count > 0 means it exists
    }



}
