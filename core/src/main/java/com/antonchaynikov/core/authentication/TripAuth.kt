package com.antonchaynikov.core.authentication

import com.google.firebase.auth.FirebaseAuth

object TripAuth: Auth {
    private val mDelegate = FirebaseAuth.getInstance()

    override fun isSignedIn(): Boolean {
        return mDelegate.currentUser != null
    }

    override fun signOut() {
        mDelegate.signOut()
    }
}