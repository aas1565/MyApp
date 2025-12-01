package com.example.hw_3

import com.example.profile.ProfileActivityProvider

class ProfileActivityProviderImpl : ProfileActivityProvider {
    override fun getMainActivityClass(): Class<*> {
        return MainActivity::class.java
    }
}


