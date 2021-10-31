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
import dagger.hilt.android.AndroidEntryPoint
import me.rail.mobileappsofttest.databinding.ActivityMainBinding


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    private val model: MainViewModel by viewModels()

    private var isKeyboardVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        model.getAllNotes()

        binding?.add?.setOnClickListener {
            changeMainBackgroundColor(R.color.blur)
            toggleEditTextVisibility()
            binding?.edittext?.requestFocus()
            toggleKeyboardVisibility()
            binding?.edittext?.setOnKeyboardBackPressedListener(getOnKeyboardBackPressedListener())
        }

        binding?.create?.setOnClickListener {
            toggleEditTextVisibility()
            toggleKeyboardVisibility()
            changeMainBackgroundColor(R.color.white)
            model.addNote(applicationContext, binding?.edittext?.text.toString())
        }
    }

    private fun getOnKeyboardBackPressedListener(): MutableLiveData<Boolean> {
        val onKeyboardBackPressed = MutableLiveData<Boolean>()
        onKeyboardBackPressed.value = false

        onKeyboardBackPressed.observeForever {
            if (it) {
                isKeyboardVisible = !isKeyboardVisible
                toggleEditTextVisibility()
                changeMainBackgroundColor(R.color.white)
            }
        }

        return onKeyboardBackPressed
    }

    private fun changeMainBackgroundColor(color: Int) {
        binding?.main?.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                color
            )
        )
    }

    private fun toggleEditTextVisibility() {
        binding?.edittext?.visibility =
            if (binding?.edittext?.visibility == View.GONE) View.VISIBLE else View.GONE
        binding?.create?.visibility =
            if (binding?.create?.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    private fun toggleKeyboardVisibility() {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (isKeyboardVisible)
            imm.hideSoftInputFromWindow(
                binding?.root?.windowToken,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        else
            imm.showSoftInput(binding?.edittext, InputMethodManager.SHOW_IMPLICIT)

        isKeyboardVisible = !isKeyboardVisible
    }
}