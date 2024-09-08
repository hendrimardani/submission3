package com.example.mysubmission3.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mysubmission3.data.api.response.ListStoryItem
import com.example.mysubmission3.data.api.response.LoginResult
import com.example.mysubmission3.data.api.response.Story
import com.example.mysubmission3.datastore.user.UserModel
import com.example.mysubmission3.datastore.user.UserRepository
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val userRepository: UserRepository): ViewModel() {
    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(userModel)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    fun isLoading(): LiveData<Boolean> {
        return userRepository.isLoading
    }

    fun message(): LiveData<String> {
        return userRepository.message
    }

    fun getLoginResult(): LiveData<LoginResult> {
        return userRepository.loginResult
    }

    fun isRegistered(name: String, email: String, password: String) {
        viewModelScope.launch {
            userRepository.isRegisteredUser(name, email, password)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            userRepository.login(email, password)
        }
    }

    fun getListStoryItem(): LiveData<List<ListStoryItem>> {
        return userRepository.getListStoryItem
    }

    fun getAllStoryItem() {
        viewModelScope.launch {
            userRepository.getAllStories()
        }
    }

    fun detailStory(id: String) {
        viewModelScope.launch {
            userRepository.detailStory(id)
        }
    }

    fun getDetailStory(): LiveData<Story> {
        return userRepository.story
    }

    fun uploadImage(file: File, description: String) = userRepository.uploadImage(file, description)
}