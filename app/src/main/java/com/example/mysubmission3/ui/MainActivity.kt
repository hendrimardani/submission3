package com.example.mysubmission3.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mysubmission3.R
import com.example.mysubmission3.databinding.ActivityMainBinding
import com.example.mysubmission3.ui.login.LoginActivity
import com.example.mysubmission3.ui.signup.SignUpActivity
import com.example.mysubmission3.ui.story.StoryActivity
import com.example.mysubmission3.ui.story.StoryActivity.Companion.EXTRA_ACTIVITY
import com.example.mysubmission3.ui.story.StoryActivity.Companion.EXTRA_OBJECT

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fullScreen()
        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.btnSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        viewModel.getSession().observe(this) { userModel ->
            Log.d(TAG, "Apakah sudah login?: ${userModel.isLogin}")
            Log.d(TAG, "Nama anda: ${userModel.name}")
            if (userModel.isLogin) {
                val intent = Intent(this@MainActivity, StoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(EXTRA_ACTIVITY, TAG)
                intent.putExtra(EXTRA_OBJECT, userModel)
                startActivity(intent)
            }
        }
    }

    private fun fullScreen() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

}