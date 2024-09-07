package com.example.mysubmission3.ui.add

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.mysubmission3.R
import com.example.mysubmission3.ResultState
import com.example.mysubmission3.databinding.ActivityAddBinding
import com.example.mysubmission3.databinding.DialogCustomeForSuccessResultBinding
import com.example.mysubmission3.getImageUri
import com.example.mysubmission3.reduceFileImage
import com.example.mysubmission3.ui.MainViewModel
import com.example.mysubmission3.ui.ViewModelFactory
import com.example.mysubmission3.ui.story.StoryActivity
import com.example.mysubmission3.ui.story.StoryActivity.Companion.EXTRA_ACTIVITY
import com.example.mysubmission3.ui.story.StoryActivity.Companion.EXTRA_OBJECT
import com.example.mysubmission3.uriToFile

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null
    private var customDialog: Dialog? = null
    private var sweetAlertDialog: SweetAlertDialog? = null

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.progressBar.visibility = View.INVISIBLE
        viewModel.isLoading().observe(this) { bool -> showLoading(bool) }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnUpload.setOnClickListener { uploadImage() }
        binding.btnGallery.setOnClickListener { startGallery() }
    }
    
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    @SuppressLint("SuspiciousIndentation")
    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.etDescriptionAdd.text.toString()

            if (description.isNotEmpty()) {
                viewModel.uploadImage(imageFile, description).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultState.Loading -> { }
                            is ResultState.Success -> {
                                showDialog(true, getString(R.string.title_add_success), getString(R.string.message_add_success, result.data.message))
                            }
                            is ResultState.Error -> {
                                showDialog(false, getString(R.string.title_add_error), getString(R.string.message_add_error))
                            }
                        }
                    }
                }
            } else {
                sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    sweetAlertDialog!!.setTitleText(getString(R.string.upload_error_title_dialog))
                    sweetAlertDialog!!.setContentText(getString(R.string.upload_error_description_dialog))
                    sweetAlertDialog!!.show()
            }
        } ?: showDialog(false, getString(R.string.title_add_error), getString(R.string.message_add_error))
    }

    private fun startCamera() {
        currentImageUri =getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivAdd.setImageURI(it)
        }
    }

    private fun showLoading(isLoading:Boolean = false) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showDialog(isSuccess: Boolean, title: String, message: String) {
        if (isSuccess) {
            customDialogForSuccessResult()
        } else {
            sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                sweetAlertDialog!!.setTitleText(title)
                sweetAlertDialog!!.setContentText(message)
                sweetAlertDialog!!.show()
        }
    }

    private fun customDialogForSuccessResult() {
        customDialog = Dialog(this)
        val dialogBinding = DialogCustomeForSuccessResultBinding.inflate(layoutInflater)

        customDialog!!.setContentView(dialogBinding.root)
        customDialog!!.setCanceledOnTouchOutside(false)
        customDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.tvTitle.text = getString(R.string.title_dialog_custome)
        dialogBinding.tvDescription.text = getString(R.string.description_dialog_custome)

        dialogBinding.tvYes.setOnClickListener {
            viewModel.getSession().observe(this@AddActivity) { userModel ->
                val intent = Intent(this, StoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(EXTRA_ACTIVITY, TAG)
                intent.putExtra(EXTRA_OBJECT, userModel)
                startActivity(intent)
            }
        }
        customDialog!!.setCancelable(false)
        customDialog!!.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (customDialog != null) customDialog!!.dismiss()
        if (sweetAlertDialog != null) sweetAlertDialog!!.dismiss()
    }

    companion object {
        private val TAG = AddActivity::class.java.simpleName
    }
}