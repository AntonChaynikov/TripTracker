package com.antonchaynikov.tripscreen.uistate;

import com.antonchaynikov.tripscreen.R;

import androidx.annotation.NonNull;

public final class TripUiState {

    private State mState;

    private int mActionButtomTextId;
    private boolean mIsButtonEnabled;

    public enum State {
        STARTED, IDLE, STARTING
    }

    private TripUiState() {
        mState = State.IDLE;
        mActionButtomTextId = R.string.button_act;
    }

    public static TripUiState getDefaultState() {
        return new TripUiState();
    }

    public TripUiState transform(@NonNull State state) {
        switch(state) {
            case STARTED: {
                mActionButtomTextId = R.string.button_stop;
                mIsButtonEnabled = true;
                mState = State.STARTED;
                break;
            }
            case STARTING: {
                mActionButtomTextId = R.string.button_act;
                mIsButtonEnabled = false;
                mState = State.STARTING;
                break;
            }
            default: {
                mActionButtomTextId = R.string.button_act;
                mIsButtonEnabled = true;
                mState = State.IDLE;
                break;
            }
        }
        return this;
    }

    public State getState() {
        return mState;
    }

    public int getActionButtomTextId() {
        return mActionButtomTextId;
    }
}
