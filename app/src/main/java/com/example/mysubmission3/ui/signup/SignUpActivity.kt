package com.example.mysubmission3.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.mysubmission3.MyPasswordEditText.Companion.PASSWORD_LENGTH_LIMIT
import com.example.mysubmission3.R
import com.example.mysubmission3.databinding.ActivitySignUpBinding
import com.example.mysubmission3.ui.MainViewModel
import com.example.mysubmission3.ui.ViewModelFactory
import com.example.mysubmission3.ui.login.LoginActivity
import com.example.mysubmission3.ui.login.LoginActivity.Companion.EXTRA_BACK_DATA

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.isLoading().observe(this) { bool -> showLoading(bool) }
        viewModel.message().observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }

        playAnimation()
        signUpButton()

        onBackPressedDispatcher.addCallback(this) {
            showLoading(false)
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            intent.putExtra(EXTRA_BACK_DATA, 1)
            startActivity(intent)
            finish()
        }
    }

    private fun signUpButton() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.error_title_login_dialog))
                    .setContentText(getString(R.string.error_description_login_dialog))
                    .show()
            } else if (password.length < PASSWORD_LENGTH_LIMIT) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.error_title_login_dialog))
                    .setContentText(getString(R.string.error_description_password_login_dialog))
                    .show()
            } else {
                viewModel.isRegistered(name=name, email=email, password=password)
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.registration_success_title_dialog))
                    setMessage(getString(R.string.registration_success_description_dialog, email))
                    setPositiveButton(getString(R.string.registration_text_dialog)) { _, _ ->
                        finish()
                    }
                    create()
                    show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val image = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(1000)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(1000)
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(1000)
        val nameEdit = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(1000)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val pass = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(1000)
        val passEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val daftar = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            playSequentially(image, title, name, nameEdit, email, emailEdit, pass, passEdit, daftar)
            start()
        }
    }
}