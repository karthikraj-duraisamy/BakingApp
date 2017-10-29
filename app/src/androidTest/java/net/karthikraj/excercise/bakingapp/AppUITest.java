package net.karthikraj.excercise.bakingapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import net.karthikraj.excercise.bakingapp.recipieslist.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AppUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("net.karthikraj.excercise.bakingapp", appContext.getPackageName());
    }

    @Test
    public void itemClickTest(){
        String[] recipe_names = {"Nutella Pie","Brownies","Yellow Cake","Cheesecake"};

        for(int i = 0; i< recipe_names.length;i++){
            onView(withId(R.id.recyclerView)).perform(scrollToPosition(i));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
            onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                    .check(matches(withText(recipe_names[i])));

            Espresso.pressBack();
        }
    }

    @Test
    public void videoPlayerTest() {

        waitAction(1000);
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        ViewInteraction exoPlayer = onView(allOf(withId(R.id.exoplayer),instanceOf(SimpleExoPlayerView.class))).check(matches(isDisplayed()));
        ViewInteraction exoPause = onView(withId(R.id.exo_pause)).check(matches(isDisplayed()));

        waitAction(5000);

        try {
            exoPause.perform(click());
        } catch (Exception e) {
            exoPlayer.perform(click());
            exoPause.perform(click());
        }
        onView(withId(R.id.exo_prev)).perform(click());

        waitAction(2000);

        onView(withId(R.id.exo_play)).perform(click());
        onView(withId(R.id.exo_ffwd)).perform(click());

        waitAction(4000);

        onView(allOf(withId(R.id.refresh_button), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
                .perform(click());

    }


    private void waitAction(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
