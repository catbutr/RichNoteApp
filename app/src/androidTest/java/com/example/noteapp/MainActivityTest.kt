package com.example.noteapp


import android.app.Activity
import android.app.Instrumentation
import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private fun pauseTestFor(milliseconds:Long) { Thread.sleep(milliseconds) }

@RunWith(AndroidJUnit4::class)
class AddEditNoteActivityStartedTest : TestCase() {

    @JvmField
    @Rule
    var activityScenarioRule:ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun fabTest() {
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        Intents.init()
        onView(withId(R.id.idFAB)).perform(click())
        Intents.intending(IntentMatchers.hasComponent(AddEditNoteActivity::class.java.name)).respondWith(result)
        Intents.release();
    }

    @Test
    fun recyclerViewTest(){
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        Intents.init()
        onView(withId(R.id.notesRV)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,click()))
        Intents.intending(IntentMatchers.hasComponent(AddEditNoteActivity::class.java.name)).respondWith(result)
        Intents.release();
    }

}

@RunWith(AndroidJUnit4::class)
class SettingsTest : TestCase(){
    @JvmField
    @Rule
    var activityScenarioRule:ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)
    @Test
    fun changeTheme(){
        var selectedTheme:Int = R.style.Theme_NoteApp
        var expectedTheme:Int = R.style.Theme_NoteApp_Red
        onView(withId(R.id.nav_view))
            .check(matches(isClosed(Gravity.LEFT)))
            .perform(DrawerActions.open());
        onView(withId(R.id.nav_view2))
            .perform(NavigationViewActions.navigateTo(R.id.nav_settings));
        onView(withText("Change theme")).perform(click())
        onView(withText("Red")).perform(click())
        activityScenarioRule.scenario.onActivity { activity->
            run {
                selectedTheme = activity.selectedTheme
            }
        }
        Assert.assertEquals(expectedTheme, selectedTheme)
    }
    @Test
    fun changeFont(){
        var selectedTheme:Int = R.style.Aldrich
        var expectedTheme:Int = R.style.Montserrat
        onView(withId(R.id.nav_view))
            .check(matches(isClosed(Gravity.LEFT)))
            .perform(DrawerActions.open());
        onView(withId(R.id.nav_view2))
            .perform(NavigationViewActions.navigateTo(R.id.nav_settings));
        onView(withText("Change font")).perform(click())
        onView(withText("Montserrat")).perform(click())
        activityScenarioRule.scenario.onActivity { activity->
            run {
                selectedTheme = activity.selectedFont
            }
        }
        Assert.assertEquals(expectedTheme, selectedTheme)
    }
}

@RunWith(AndroidJUnit4::class)
class NotebookCase : TestCase(){
    @JvmField
    @Rule
    var activityScenarioRule:ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun addChangeRemoveNotebook(){
        var expextedString = "Gares"
        lateinit var menuString: MutableList<String>
        var lastNotebookSelected:String = ""
        //Add
        onView(withId(R.id.nav_view))
            .check(matches(isClosed(Gravity.LEFT)))
            .perform(DrawerActions.open());
        onView(withId(R.id.nav_view2))
            .perform(NavigationViewActions.navigateTo(R.id.nav_new_notebook));
        onView(withId(R.id.input_text)).inRoot(isDialog()).perform(replaceText(expextedString));
        onView(withText("OK")).perform(click())
        activityScenarioRule.scenario.onActivity { activity->
            run {
                menuString = activity.addedDrawerMenuItems
            }
        }
        val isContainted = menuString.contains(expextedString)
        org.junit.Assert.assertEquals(true,isContainted)
        //Change
        onView(withText(expextedString)).perform(click())
        expextedString = "Oompla"
        onView(withId(R.id.nav_view2))
            .perform(NavigationViewActions.navigateTo(R.id.nav_change_notebook));
        onView(withId(R.id.input_text)).inRoot(isDialog()).perform(replaceText(expextedString));
        onView(withText("OK")).perform(click())
        activityScenarioRule.scenario.onActivity { activity->
            run {
                menuString = activity.addedDrawerMenuItems
            }
        }
        val isChanged = menuString.contains(expextedString)
        org.junit.Assert.assertEquals(true,isChanged)
        //Delete
        onView(withText(expextedString)).perform(click())
        activityScenarioRule.scenario.onActivity { activity->
            run {
                lastNotebookSelected = activity.selectedNotebook
            }
        }
        onView(withId(R.id.nav_delete_notebook)).perform(click())
        onView(withText("ACCEPT")).perform(click())
        activityScenarioRule.scenario.onActivity { activity->
            run {
                menuString = activity.addedDrawerMenuItems
            }
        }
        val isDeleted = menuString.contains(expextedString)
        org.junit.Assert.assertEquals(false,isDeleted)

    }
}