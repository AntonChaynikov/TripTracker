package com.antonchaynikov.tripslist;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;

import static org.junit.Assert.assertEquals;

public class RecyclerViewItemCountAssertion implements ViewAssertion {

    private int mExpectedItemsCount;

    public RecyclerViewItemCountAssertion(int expectedItemsCount) {
        mExpectedItemsCount = expectedItemsCount;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }

        RecyclerView recyclerView = (RecyclerView) view;
        assertEquals(mExpectedItemsCount, recyclerView.getAdapter().getItemCount());
    }
}
