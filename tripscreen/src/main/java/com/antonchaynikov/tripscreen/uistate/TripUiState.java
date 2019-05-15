package com.antonchaynikov.tripscreen.uistate;

import com.antonchaynikov.tripscreen.R;

import androidx.annotation.NonNull;

public final class TripUiState {

    private State mState;

    private int mActionButtomTextId;

    public enum State {
        STARTED, IDLE
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
                mState = State.STARTED;
                break;
            }
            default: {
                mActionButtomTextId = R.string.button_act;
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
