package com.example.mysubmission3.datastore.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel (
    val userId: String,
    val name: String,
    val token: String,
    val isLogin: Boolean = false
): Parcelable