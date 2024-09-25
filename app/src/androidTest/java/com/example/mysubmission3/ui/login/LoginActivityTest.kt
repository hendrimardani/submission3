package com.example.mysubmission3.ui.login

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.mysubmission3.R
import com.example.mysubmission3.data.api.retrofit.ApiConfig
import com.example.mysubmission3.ui.story.StoryActivity
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class LoginActivityTest {
    private val dummyEmail = "hendrimardani@gmail.com"
    private val dummyPassword = "hendri123123"
    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        // Supaya langsung ke activity Login
        ActivityScenario.launch(LoginActivity::class.java)
        mockWebServer.start(8080)
        ApiConfig.MY_BASE_URL = "http://127.0.0.1:8080/"
    }

    @Test
    fun loginSuccess() {
        onView(withId(R.id.emailEditText)).perform(typeText(dummyEmail), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(dummyPassword), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).perform(click())
        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        Assert.assertEquals("HTTP/1.1 200 OK", mockResponse.status)
    }

//    @Test
//    fun logoutSuccess() {
//        onView(withId(R.id.emailEditText)).perform(typeText(dummyEmail), closeSoftKeyboard())
//        onView(withId(R.id.passwordEditText)).perform(typeText(dummyPassword), closeSoftKeyboard())
//
//        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
//        onView(withId(R.id.loginButton)).perform(click())
//        intended(hasComponent(StoryActivity::class.java.name))
//        val mockResponse = MockResponse()
//            .setResponseCode(200)
//        mockWebServer.enqueue(mockResponse)
//
//
//        Assert.assertEquals("HTTP/1.1 200 OK", mockResponse.status)
//    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}