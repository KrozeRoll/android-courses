package com.example.myrecycleview

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.android.synthetic.main.activity_main.*
import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UITests {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.myrecycleview", appContext.packageName)
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule var permissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.READ_CONTACTS
    )

    @Test
    fun listLoad() {
        onView(withId(R.id.myRecyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun reloadRecyclerView() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.recreate()
        onView(withId(R.id.myRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.searchView)).check(matches(isDisplayed()))
    }
}