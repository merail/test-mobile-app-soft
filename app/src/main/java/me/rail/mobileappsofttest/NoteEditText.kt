package me.rail.mobileappsofttest

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import androidx.lifecycle.MutableLiveData

@SuppressLint("AppCompatCustomView")
class NoteEditText : EditText {
    private lateinit var onKeyboardBackPressedListener: MutableLiveData<Boolean>

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?) : super(context)

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            onKeyboardBackPressedListener.value = true

        return super.onKeyPreIme(keyCode, event)
    }

    fun setOnKeyboardBackPressedListener(onKeyboardBackPressedListener: MutableLiveData<Boolean>) {
        this.onKeyboardBackPressedListener = onKeyboardBackPressedListener
    }
}