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
public class AddFriendsTest {


    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


    @Test
    public void addFriendsTest() {


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

        ViewInteraction profileFriendButton = onView(
                allOf(withId(R.id.friendsButton),
                        isDisplayed()));
        profileFriendButton.perform(click());

        ViewInteraction searchEditText = onView(
                allOf(withId(R.id.searchTxt),
                        isDisplayed()));
        searchEditText.perform(replaceText("bob@gmail.com"), closeSoftKeyboard());

        ViewInteraction searchButton = onView(
                allOf(withId(R.id.searchBtn),
                        isDisplayed()));
        searchButton.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction addFriendButton = onView(
                allOf(withId(R.id.addFriendBtn), withText(R.string.add_friend),
                        isDisplayed()));
        addFriendButton.perform(click());

        ViewInteraction closePopUpButton = onView(
                allOf(withId(R.id.close_popup), withText(R.string.x),
                        isDisplayed()));
        closePopUpButton.perform(click());

        ViewInteraction backButton = onView(
                allOf(withId(R.id.backBtn), withText(R.string.Back),
                        isDisplayed()));
        backButton.perform(click());


        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.signOutBtn), withText(R.string.sign_out),
                        isDisplayed()));
        appCompatButton4.perform(click());

    }

}
