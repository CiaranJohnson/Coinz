package com.example.tech.coinz;

import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


/**
 * These test to see if all the map buttons in different activities work as expected.
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MapButtonTest {


    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


    @Test
    public void profileMapButtonTest() {


        ViewInteraction appCompatButton1 = onView(
                allOf(withId(R.id.btnSignIn), withText(R.string.sign_in),
                        isDisplayed()));
        appCompatButton1.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editEmail),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("user@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText1 = onView(
                allOf(withId(R.id.editPassword),
                        isDisplayed()));
        appCompatEditText1.perform(replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.signInButton), withText(R.string.sign_in),
                        isDisplayed()));
        appCompatButton2.perform(click());

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.ProfileBtn), withText(R.string.Profile),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction profileMapButton = onView(
                allOf(withId(R.id.mapButton),
                        isDisplayed()));
        profileMapButton.perform(click());

        appCompatButton3.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.signOutBtn), withText(R.string.sign_out),
                        isDisplayed()));
        appCompatButton4.perform(click());

    }

    @Test
    public void BankMapButtonTest() {


        ViewInteraction appCompatButton1 = onView(
                allOf(withId(R.id.btnSignIn), withText(R.string.sign_in),
                        isDisplayed()));
        appCompatButton1.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editEmail),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("user@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText1 = onView(
                allOf(withId(R.id.editPassword),
                        isDisplayed()));
        appCompatEditText1.perform(replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.signInButton), withText(R.string.sign_in),
                        isDisplayed()));
        appCompatButton2.perform(click());

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.BankBtn), withText(R.string.Bank),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction bankMapButton = onView(
                allOf(withId(R.id.mapButton), withText(R.string.Map),
                        isDisplayed()));
        bankMapButton.perform(click());

        ViewInteraction profileButton = onView(
                allOf(withId(R.id.ProfileBtn), withText(R.string.Profile),
                        isDisplayed()));
        profileButton.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.signOutBtn), withText(R.string.sign_out),
                        isDisplayed()));
        appCompatButton4.perform(click());

    }

    @Test
    public void gameMapButtonTest() {


        ViewInteraction appCompatButton1 = onView(
                allOf(withId(R.id.btnSignIn), withText(R.string.sign_in),
                        isDisplayed()));
        appCompatButton1.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editEmail),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("user@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText1 = onView(
                allOf(withId(R.id.editPassword),
                        isDisplayed()));
        appCompatEditText1.perform(replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.signInButton), withText(R.string.sign_in),
                        isDisplayed()));
        appCompatButton2.perform(click());

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.GamesBtn), withText(R.string.Games),
                        isDisplayed()));
        appCompatButton3.perform(click());


        ViewInteraction gamesMapButton = onView(
                allOf(withId(R.id.mapBtn), withText(R.string.Map),
                        isDisplayed()));
        gamesMapButton.perform(click());

        ViewInteraction profileButton = onView(
                allOf(withId(R.id.ProfileBtn), withText(R.string.Profile),
                        isDisplayed()));
        profileButton.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.signOutBtn), withText(R.string.sign_out),
                        isDisplayed()));
        appCompatButton4.perform(click());

    }

}
;