package com.example.quartzevents

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class AdminTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>() // access MainActivity

    @Test
    fun log_in_as_admin(){
        composeTestRule
            .onNodeWithTag("emailField")
            .performTextInput("quartztest01@example.com")

        composeTestRule
            .onNodeWithTag("passField")
            .performTextInput("123456")

        composeTestRule
            .onNodeWithTag("signInButton")
            .performClick()

        composeTestRule
            .onNodeWithText("Admin Dashboard")
            .assertExists()

    }

    @Test
    fun admin_dashboard_to_manage_campsites(){
        composeTestRule
            .onNodeWithTag("emailField")
            .performTextInput("quartztest01@example.com")

        composeTestRule
            .onNodeWithTag("passField")
            .performTextInput("123456")

        composeTestRule
            .onNodeWithTag("signInButton")
            .performClick()

        composeTestRule
            .onNodeWithText("Admin Dashboard")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Manage Campsites Box")
            .performClick()

        composeTestRule
            .onNodeWithText("Manage Campsites")
            .assertExists()

    }

    @Test
    fun admin_dashboard_to_manage_profiles(){
        composeTestRule
            .onNodeWithTag("emailField")
            .performTextInput("quartztest01@example.com")

        composeTestRule
            .onNodeWithTag("passField")
            .performTextInput("123456")

        composeTestRule
            .onNodeWithTag("signInButton")
            .performClick()

        composeTestRule
            .onNodeWithText("Admin Dashboard")
            .assertExists()

        composeTestRule
            .onNodeWithTag("Manage Profiles Box")
            .performClick()

        composeTestRule
            .onNodeWithText("Manage Profiles")
            .assertExists()

    }

    @Test
    fun log_out_as_admin(){
        composeTestRule
            .onNodeWithTag("emailField")
            .performTextInput("quartztest01@example.com")

        composeTestRule
            .onNodeWithTag("passField")
            .performTextInput("123456")

        composeTestRule
            .onNodeWithTag("signInButton")
            .performClick()

        composeTestRule
            .onNodeWithText("Admin Dashboard")
            .assertExists()

        composeTestRule
            .onNodeWithTag("AdminLogOut")
            .performClick()

        composeTestRule
            .onNodeWithText("Sign In")
            .assertExists()

    }
}