package com.antonchaynikov.triptracker.mainscreen.uistate;

import com.antonchaynikov.triptracker.R;

import androidx.annotation.NonNull;

public final class MapActivityUiState {

    private State mState;

    private int mActionButtomTextId;

    public enum State {
        STARTED, IDLE
    }

    private MapActivityUiState() {
        mState = State.IDLE;
        mActionButtomTextId = R.string.button_act;
    }

    public static MapActivityUiState getDefaultState() {
        return new MapActivityUiState();
    }

    public MapActivityUiState transform(@NonNull State state) {
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
