package me.rail.mobileappsofttest

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import me.rail.mobileappsofttest.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    private val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding?.add?.setOnClickListener {
            binding?.edittext?.visibility = View.VISIBLE
            binding?.edittext?.requestFocus()
            binding?.edittext?.setOnKeyboardBackPressedListener(getOnKeyboardBackPressedListener())
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding?.edittext, InputMethodManager.SHOW_IMPLICIT)
        }

        binding?.create?.setOnClickListener {
            model.addNote(applicationContext, binding?.edittext?.text.toString())
        }
    }

    private fun getOnKeyboardBackPressedListener(): MutableLiveData<Boolean> {
        val onKeyboardBackPressed = MutableLiveData<Boolean>()
        onKeyboardBackPressed.value = false

        onKeyboardBackPressed.observeForever {
            if (it)
                binding?.main?.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.white
                    )
                )
            else
                binding?.main?.setBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.blur
                    )
                )
        }

        return onKeyboardBackPressed
    }
}