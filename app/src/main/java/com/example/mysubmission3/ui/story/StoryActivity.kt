package com.example.mysubmission3.ui.story

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mysubmission3.R
import com.example.mysubmission3.data.api.response.LoginResult
import com.example.mysubmission3.databinding.ActivityStoryBinding
import com.example.mysubmission3.datastore.user.UserModel
import com.example.mysubmission3.ui.MainActivity
import com.example.mysubmission3.ui.MainViewModel
import com.example.mysubmission3.ui.ViewModelFactory
import com.example.mysubmission3.ui.login.LoginActivity

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.story)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getDataExtra()
        btnLogout()
    }

    private fun btnLogout() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun getDataExtra() {
        val getActivityData = intent.getStringExtra(EXTRA_ACTIVITY)
        if (getActivityData == "LoginActivity") {
            val getTokenData = intent.getParcelableExtra<LoginResult>(EXTRA_OBJECT)
            binding.tvTest.text = getTokenData.toString()
        } else {
            val getTokenData = intent.getParcelableExtra<UserModel>(EXTRA_OBJECT)
            binding.tvTest.text = getTokenData.toString()
        }
    }

    companion object {
        const val EXTRA_OBJECT = "extra_object"
        const val EXTRA_ACTIVITY = "extra_activity"
    }
}