package com.example.tests;

import com.base.BaseTest;
import com.example.pages.GooglePage;
import org.junit.jupiter.api.*;

public class GoogleTest extends BaseTest {

    @Test
    void searchForNTNU() {
        // Go to Google
        goTo("https://www.google.com");

        // Accept cookie popup if it's visible
        if (isVisible(GooglePage.acceptAll)) {
            click(GooglePage.acceptAll);
        }

        // Fill the search field
        fillInput(GooglePage.searchInput, "NTNU");
        pressEnter();

        // Wait for the result to load
        waitForElement(GooglePage.NTNUtext);

        // Verify the result is visible
        Assertions.assertTrue(
                isVisible(GooglePage.NTNUtext),
                "NTNU text is not visible!"
        );
    }
}
