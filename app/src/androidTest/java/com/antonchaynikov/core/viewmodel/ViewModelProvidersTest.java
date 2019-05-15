package com.antonchaynikov.core.viewmodel;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.annotation.NonNull;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.antonchaynikov.triptracker.TripActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ViewModelProvidersTest {

    private ActivityTestRule<TripActivity> viewModelActivityActivityTestRule = new ActivityTestRule<>(TripActivity.class);
    private TripActivity mActivity;

    @Before
    public void setUp() {
        mActivity = viewModelActivityActivityTestRule.launchActivity(
                new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), TripActivity.class));

    }

    @After
    public void tearDown() {
        viewModelActivityActivityTestRule.finishActivity();
    }

    @Test
    public void get_shouldProvideViewModel() {
        BasicViewModel vm = ViewModelProviders.of(mActivity, getDefaultFactory()).get(BasicViewModel.class);
        assertNotNull(vm);
    }

    @Test
    public void get_shouldProvideSameViewModel_whenConfigurationChanges() throws InterruptedException {
        BasicViewModel vm = ViewModelProviders.of(mActivity, getDefaultFactory()).get(BasicViewModel.class);

        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        assertSame(vm, ViewModelProviders.of(mActivity, getDefaultFactory()).get(BasicViewModel.class));
    }

    private ViewModelFactory getDefaultFactory() {
        return new ViewModelFactory() {
            @Override
            public <T extends BasicViewModel> T create(@NonNull Class<T> clazz) {
                return (T) new BasicViewModel();
            }
        };
    }

}