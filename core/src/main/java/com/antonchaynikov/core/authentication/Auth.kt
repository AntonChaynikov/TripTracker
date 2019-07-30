package com.antonchaynikov.core.authentication

interface Auth {
    fun isSignedIn(): Boolean
    fun signOut()
}