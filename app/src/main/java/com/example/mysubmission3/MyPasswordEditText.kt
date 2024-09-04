package com.example.mysubmission3

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat

class MyPasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var showImage: Drawable

    init {
        showImage = ContextCompat.getDrawable(context, R.drawable.ic_error_24px) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < PASSWORD_LENGTH_LIMIT) setError("Password tidak boleh kurang dari 8 karakter")
            }
            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (compoundDrawables[2] != null) {
            val imageStart: Float
            val imageEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                imageEnd = (showImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event!!.x < imageEnd -> isClearButtonClicked = true
                }
            } else {
                imageStart = (width - paddingEnd - showImage.intrinsicWidth).toFloat()
                when {
                    event!!.x > imageStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        showImage = ContextCompat.getDrawable(context, R.drawable.ic_error_24px) as Drawable
                        showError()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        showImage = ContextCompat.getDrawable(context, R.drawable.ic_error_24px) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideError()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Masukkan password anda"
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
    }

    private fun showError() {
        setButtonDrawables(endOfTheText = showImage)
    }

    private fun hideError() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText:Drawable? = null,
        endOfTheText:Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    companion object {
        const val PASSWORD_LENGTH_LIMIT = 8
    }

}