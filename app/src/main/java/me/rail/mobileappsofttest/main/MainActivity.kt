package me.rail.mobileappsofttest.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.rail.mobileappsofttest.NoteFragment
import me.rail.mobileappsofttest.R
import me.rail.mobileappsofttest.databinding.ActivityMainBinding
import me.rail.mobileappsofttest.db.Note


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    private val model: MainViewModel by viewModels()

    private var isKeyboardVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initializeRecyclerView()

        lifecycleScope.launch {
            model.notes.observeForever {
                val noteAdapter = NoteAdapter(
                    it,
                    onUpClick = ::onUpClick,
                    onNoteClick = ::onNoteClick,
                    onShareClick = ::onShareClick,
                    onPinClick = ::onPinClick,
                )
                binding?.recyclerview?.adapter = noteAdapter
            }
        }

        binding?.add?.setOnClickListener {
            changBackgroundColor(R.color.blur)
            toggleEditTextVisibility()
            binding?.edittext?.requestFocus()
            toggleKeyboardVisibility()
            binding?.edittext?.setOnKeyboardBackPressedListener(getOnKeyboardBackPressedListener())
        }

        binding?.create?.setOnClickListener {
            toggleEditTextVisibility()
            toggleKeyboardVisibility()
            changBackgroundColor(R.color.white)
            model.addNote(binding?.edittext?.text.toString())
            binding?.edittext?.text?.clear()
        }

        model.textForShare.observe(this, {
            onShareClick(it)
        })
    }

    private fun getOnKeyboardBackPressedListener(): MutableLiveData<Boolean> {
        val onKeyboardBackPressed = MutableLiveData<Boolean>()
        onKeyboardBackPressed.value = false

        onKeyboardBackPressed.observeForever {
            if (it) {
                isKeyboardVisible = !isKeyboardVisible
                toggleEditTextVisibility()
                changBackgroundColor(R.color.white)
                binding?.edittext?.text?.clear()
            }
        }

        return onKeyboardBackPressed
    }

    private fun changBackgroundColor(color: Int) {
        binding?.edittextBackground?.setBackgroundColor(
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

    private fun initializeRecyclerView() {
        binding?.recyclerview?.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)

        binding?.recyclerview?.addItemDecoration(
            DividerItemDecoration(
                binding?.recyclerview?.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun onUpClick(note: Note) {
        model.setNoteToTop(note)
    }

    private fun onNoteClick(note: Note) {
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, NoteFragment.newInstance(note))
            .addToBackStack(NoteFragment::class.java.name).commit()
    }

    private fun onShareClick(text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)

        startActivity(Intent.createChooser(intent, "Share Note"))
    }

    private fun onPinClick(note: Note) {
        model.togglePin(note)
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
