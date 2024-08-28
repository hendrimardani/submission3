package com.example.mysubmission3.di

import android.content.Context
import com.example.mysubmission3.data.api.retrofit.ApiConfig
import com.example.mysubmission3.datastore.user.UserPreference
import com.example.mysubmission3.datastore.user.UserRepository
import com.example.mysubmission3.datastore.user.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }
}