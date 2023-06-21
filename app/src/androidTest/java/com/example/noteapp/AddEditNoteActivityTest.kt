package com.example.noteapp


import android.app.Activity
import android.app.Instrumentation
import android.graphics.Color
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddEditNoteActivityTest : TestCase() {

    @JvmField
    @Rule
    var activityScenarioRule: ActivityScenarioRule<AddEditNoteActivity> = ActivityScenarioRule(AddEditNoteActivity::class.java)

    @Test
    fun saveTest() {
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.save)).perform(ViewActions.click())
        Intents.intending(IntentMatchers.hasComponent(MainActivity::class.java.name)).respondWith(result)
        Intents.release();
    }
    @Test
    fun favouriteTet(){
        var isFavourite = false
        Espresso.onView(ViewMatchers.withId(R.id.favourite)).perform(ViewActions.click())
        activityScenarioRule.scenario.onActivity { activity->
            run {
                isFavourite = activity.isNoteFavourite
            }
        }
        org.junit.Assert.assertEquals(true,isFavourite)
    }
    @Test
    fun colorTest(){
        var unexpectedColor = Color.BLUE
        var selectedColor = Color.BLUE
        activityScenarioRule.scenario.onActivity { activity->
            run {
                activity.selectedColor = Color.BLUE
            }
        }
        Espresso.onView(ViewMatchers.withId(R.id.colorPicker)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("ВЫБРАТЬ")).perform(ViewActions.click())
        activityScenarioRule.scenario.onActivity { activity->
            run {
                selectedColor = activity.selectedColor
            }
        }
        org.junit.Assert.assertNotEquals(unexpectedColor,selectedColor)
    }

}