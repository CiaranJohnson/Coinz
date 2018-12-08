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

@LargeTest
@RunWith(AndroidJUnit4.class)

public class SignInTest{

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


    @Test
    public void signInTest() {


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

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.ProfileBtn), withText(R.string.Profile),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.signOutBtn), withText(R.string.sign_out),
                        isDisplayed()));
        appCompatButton4.perform(click());

//        ViewInteraction button = onView(
//                allOf(withId(R.id.signInButton),
//                        isDisplayed()));
//        button.check(matches(isDisplayed()));
    }

}
