package com.example.mysubmission3.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.mysubmission3.R
import com.example.mysubmission3.data.api.response.LoginResult
import com.example.mysubmission3.databinding.ActivityLoginBinding
import com.example.mysubmission3.datastore.user.UserModel
import com.example.mysubmission3.ui.MainViewModel
import com.example.mysubmission3.ui.ViewModelFactory
import com.example.mysubmission3.ui.signup.SignUpActivity
import com.example.mysubmission3.ui.story.StoryActivity
import com.example.mysubmission3.ui.story.StoryActivity.Companion.EXTRA_ACTIVITY
import com.example.mysubmission3.ui.story.StoryActivity.Companion.EXTRA_OBJECT
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.isLoading().observe(this) { bool -> showLoading(bool) }

        playAnimation()
        loginButton()
    }

    private fun loginButton() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.login(token = "", email = email, password = password)

            lifecycleScope.launch {
                delay(2000)
                if (!ERROR_RESPONSE) {
                    AlertDialog.Builder(this@LoginActivity).apply {
                        setTitle("Anda berhasil login.")
                        setMessage("Silahkan bagikan momen anda.")
                        setPositiveButton("Lanjut") { _, _ ->
                            viewModel.getLoginResult().observe(this@LoginActivity) {
                                viewModel.saveSession(UserModel(it.userId.toString(), it.name.toString(), it.token.toString()))
                                val intent = Intent(this@LoginActivity, StoryActivity::class.java)
                                intent.putExtra(EXTRA_ACTIVITY, TAG)
                                intent.putExtra(EXTRA_OBJECT, it)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                        create()
                        show()
                    }
                } else {
                    AlertDialog.Builder(this@LoginActivity).apply {
                        setTitle("Akun anda belum terdaftar.")
                        setMessage("Silahkan buat akun terlebih dahulu.")
                        setPositiveButton("Lanjut Daftar Akun") { _, _ ->
                            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
                        }
                        create()
                        show()
                    }
                    ERROR_RESPONSE = false
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
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(1000)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(1000)
        val emailEdit = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val pass = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(1000)
        val passEdit = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            playSequentially(image, title, message, email, emailEdit, pass, passEdit, login)
            start()
        }
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        var ERROR_RESPONSE = false
    }
}