package com.vicen.webel.components.wartish

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ComposeNavigationTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeNavigatesToCollectionBoard() {
        compose.onNodeWithText("Recolección").performClick()
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("MOVIMIENTOS").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("MOVIMIENTOS").assertIsDisplayed()
    }
}
