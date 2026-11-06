package com.example.quartzevents

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quartzevents.ui.theme.QuartzEventsTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Basic entrant page interactions. */
@RunWith(AndroidJUnit4::class)
class EntrantUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun navigate_to_filters_apply_and_return_to_search() {
        composeTestRule.setContent {
            QuartzEventsTheme { EntrantSearchScreen(onBack = {}) }
        }

        composeTestRule.onNodeWithTag("campsiteSearchInput").performTextInput("Pine")
        composeTestRule.onNodeWithText("Filters").performClick()
        composeTestRule.onNodeWithText("Filter Campsites").assertExists()

        composeTestRule.onNodeWithTag("locationInput").performTextInput("Alberta")
        composeTestRule.onNodeWithTag("guestInput").performScrollTo().performTextInput("2")
        composeTestRule.onNodeWithText("Apply Filters").performClick()

        composeTestRule.onNodeWithText("Search Results").assertExists()
        composeTestRule.onNodeWithText("Alberta · Guests: 2").assertExists()
        composeTestRule.onNodeWithText("Back to Search").performClick()
        composeTestRule.onNodeWithText("Find a Campsite").assertExists()
        composeTestRule.onNodeWithTag("campsiteSearchInput").assertTextContains("Pine")
    }

    @Test
    fun reset_filters_clears_location_and_guests() {
        var appliedFilters: CampsiteFilters? = null
        composeTestRule.setContent {
            QuartzEventsTheme {
                EntrantFilterScreen(CampsiteFilters(location = "Alberta", guests = 2), {}, { appliedFilters = it })
            }
        }

        composeTestRule.onNodeWithText("Reset").performClick()
        composeTestRule.onNodeWithText("Apply Filters").performClick()
        composeTestRule.runOnIdle { assertEquals(CampsiteFilters(), appliedFilters) }
    }

    @Test
    fun results_show_summary_and_select_campsite() {
        val campsite = CampsiteInfo("pine", "Pine Ridge Campground", "Hinton, Alberta", 8, 4.5)
        var selectedId = ""
        composeTestRule.setContent {
            QuartzEventsTheme {
                EntrantSearchResultsScreen("Pine", CampsiteFilters(), listOf(campsite), false, null,
                    onRetry = {}, onBack = {}, onCampsiteClick = { selectedId = it })
            }
        }

        composeTestRule.onNodeWithText("1 campsite found").assertExists()
        composeTestRule.onNodeWithText("Hinton, Alberta · Up to 8 guests").assertExists()
        composeTestRule.onNodeWithText("★ 4.5 / 5").assertExists()
        composeTestRule.onNodeWithText("Pine Ridge Campground").performClick()
        composeTestRule.runOnIdle { assertEquals("pine", selectedId) }
    }

    @Test
    fun results_without_matches_show_message() {
        val campsite = CampsiteInfo("pine", "Pine Ridge Campground", "Hinton, Alberta", 8)
        composeTestRule.setContent {
            QuartzEventsTheme {
                EntrantSearchResultsScreen("Cedar", CampsiteFilters(), listOf(campsite), false, null, {}, {}, {})
            }
        }

        composeTestRule.onNodeWithText("0 campsites found").assertExists()
        composeTestRule.onNodeWithText("No campsites match. Try another name, location or guest count.").assertExists()
    }

    @Test
    fun details_show_campsite_information() {
        val campsite = CampsiteInfo("pine", "Pine Ridge Campground", "Hinton, Alberta", 8,
            description = "Drinking water and picnic tables.", rules = "Quiet hours begin at 10 pm.")
        composeTestRule.setContent {
            QuartzEventsTheme { CampsiteDetailsContent(campsite) }
        }

        composeTestRule.onNodeWithText("Pine Ridge Campground").assertExists()
        composeTestRule.onNodeWithText("Hinton, Alberta").assertExists()
        composeTestRule.onNodeWithText("Maximum occupancy: 8 guests").assertExists()
        composeTestRule.onNodeWithText("Drinking water and picnic tables.").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Quiet hours begin at 10 pm.").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun campsite_link_returns_document_id() {
        val id = campsiteIdFromLink("quartz://campsite/pine")

        assertEquals("pine", id)
    }

    @Test
    fun campsite_link_without_id_returns_null() {
        val id = campsiteIdFromLink("quartz://campsite/")

        assertNull(id)
    }
}
