package com.base;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;
import java.util.Arrays;

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

    protected void checkIfNotChecked(String locator) {
        Locator element = page.locator(locator);
        if (!element.isChecked()) {
            element.check(); // only check if not already checked
        }
    }

    // Uncheck a checkbox (if it's currently checked)
    protected void uncheck(String locator) {
        page.locator(locator).uncheck(); // uncheck checkbox
    }

    // Uncheck a checkbox only if it is currently checked
    protected void uncheckIfChecked(String locator) {
        Locator element = page.locator(locator);
        if (element.isChecked()) {
            element.uncheck(); // if checked, uncheck it
        }
    }

    // Check if element exists in the DOM (regardless of visibility)
    protected boolean isElementPresent(String locator) {
        return page.locator(locator).count() > 0; // element count > 0 means it exists
    }

    // Click on element using JavaScript (for tricky elements)
    protected void jsClick(String locator) {
        page.evaluate("element => element.click()", page.locator(locator)); // use JS to click the element
    }

    // Wait until the page has fully loaded
    protected void waitForPageLoad() {
        page.waitForLoadState(LoadState.LOAD); // wait for the 'load' state of the page
    }

    // Listen for and print all console logs from the page (useful for debugging)
    protected void logConsoleMessages() {
        page.onConsoleMessage(msg -> System.out.println("Console log: " + msg.text())); // print all console messages
    }

    // Upload a file to an input[type='file']
    protected void uploadFile(String locator, String filePath) {
        page.locator(locator).setInputFiles(Paths.get(filePath)); // upload file from local system
    }

    // Upload multiple files at once
    protected void uploadMultipleFiles(String fileInputLocator, String... filePaths) {
        page.locator(fileInputLocator).setInputFiles(
                Arrays.stream(filePaths).map(Paths::get).toArray(java.nio.file.Path[]::new)
        );
    }

    // Scroll to a specific element
    protected void scrollToElement(String locator) {
        page.locator(locator).scrollIntoViewIfNeeded(); // scroll the page to bring the element into view
    }

    // Get an attribute's value from an element
    protected String getAttribute(String locator, String attribute) {
        return page.locator(locator).getAttribute(attribute); // fetch specific attribute's value
    }

    // Clear input field before typing (sometimes needed explicitly)
    protected void clearAndFillInput(String locator, String text) {
        Locator element = page.locator(locator);
        element.clear(); // clear input
        element.fill(text); // then fill
    }

    // Wait for element to be visible and enabled (ready to click)
    protected void waitForElementToBeClickable(String locator) {
        Locator element = page.locator(locator);
        element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        while (!element.isEnabled()) {
            page.waitForTimeout(100); // wait and recheck
        }
    }

    // Automatically accept JavaScript dialogs like alert, confirm, prompt
    protected void autoAcceptDialogs() {
        page.onDialog(dialog -> {
            System.out.println("Dialog message: " + dialog.message()); // log dialog message
            dialog.accept(); // accept the dialog
        });
    }

    // Automatically dismiss JavaScript dialogs like alert, confirm, prompt
    protected void autoDismissDialogs() {
        page.onDialog(dialog -> {
            System.out.println("Dialog message: " + dialog.message()); // log dialog message
            dialog.dismiss(); // dismiss the dialog
        });
    }


    // Handle and return newly opened tab after an action (like clicking a link)
    protected Page switchToNewTab(Runnable actionThatOpensTab) {
        Page newPage = context.waitForPage(() -> {
            actionThatOpensTab.run(); // perform action that opens new tab
        });
        newPage.waitForLoadState(); // wait for the new page to load
        return newPage;
    }


    // Click multiple elements in sequence (by their locators)
    protected void clickMultiple(String... locators) {
        for (String locator : locators) {
            page.locator(locator).click(); // each element clicked in order
        }
    }



}
