package com.example.mysubmission3.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.mysubmission3.MyPasswordEditText.Companion.PASSWORD_LENGTH_LIMIT
import com.example.mysubmission3.R
import com.example.mysubmission3.ResultState
import com.example.mysubmission3.databinding.ActivityLoginBinding
import com.example.mysubmission3.datastore.user.UserModel
import com.example.mysubmission3.ui.MainActivity
import com.example.mysubmission3.ui.MainViewModel
import com.example.mysubmission3.ui.ViewModelFactory
import com.example.mysubmission3.ui.signup.SignUpActivity
import com.example.mysubmission3.ui.story.StoryActivity
import com.example.mysubmission3.ui.story.StoryActivity.Companion.EXTRA_ACTIVITY
import com.example.mysubmission3.ui.story.StoryActivity.Companion.EXTRA_OBJECT


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var sweetAlertDialog: SweetAlertDialog? = null
    private var alertDialog: AlertDialog.Builder? = null

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
        if (intent.getIntExtra(EXTRA_BACK_DATA, 0) == 1)
//            showLoading(false)

        backButtonCallback()
        playAnimation()
        loginButton()
        validationPassword()
    }

    private fun backButtonCallback() {
        onBackPressedDispatcher.addCallback(this) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
    }

    private fun validationPassword() {
        binding.passwordEditText.addTextChangedListener { object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) { }
            }
        }
    }

    private fun setMyButtonEnable() {
        val result = binding.passwordEditText.text
        binding.loginButton.isEnabled = result != null && result.isNotEmpty()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun loginButton() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    sweetAlertDialog!!.setTitleText(getString(R.string.error_title_login_dialog))
                    sweetAlertDialog!!.setContentText(getString(R.string.error_description_login_dialog))
                    .show()
            } else if (password.length < PASSWORD_LENGTH_LIMIT) {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.error_title_login_dialog))
                    .setContentText(getString(R.string.error_description_password_login_dialog))
                    .show()
            } else {
                viewModel.login(email = email, password = password).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultState.Loading -> {
//                                showLoading(true)
                            }
                            is ResultState.Error -> {
                                showError()
//                                showLoading(false)
                            }
                            is ResultState.Success -> {
                                val loginResult = result.data!!
                                viewModel.saveSession(UserModel(loginResult.userId.toString(), loginResult.name.toString(), loginResult.token.toString()))
//                                showLoading(false)
                                Log.d(TAG, "onLoginSucces: ${loginResult.name}")
                                val intent = Intent(this@LoginActivity, StoryActivity::class.java)
                                intent.putExtra(EXTRA_ACTIVITY, TAG)
                                intent.putExtra(EXTRA_OBJECT, loginResult)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showError() {
        alertDialog = AlertDialog.Builder(this@LoginActivity)
        alertDialog!!.setTitle(getString(R.string.error_title_signup_account_login_dialog))
        alertDialog!!.setMessage(getString(R.string.error_description_signup_account_login_dialog))
        alertDialog!!.setPositiveButton(getString(R.string.next_signup_account)) { _, _ ->
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
        alertDialog!!.setCancelable(false)
        alertDialog!!.create()
        alertDialog!!.show()
    }

//    private fun showLoading(isLoading: Boolean) {
//        binding.loading.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
//    }

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

    override fun onDestroy() {
        super.onDestroy()
        if (alertDialog != null) alertDialog = null
        if (sweetAlertDialog != null) sweetAlertDialog = null
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        const val EXTRA_BACK_DATA = "extra_back_data"
    }
}