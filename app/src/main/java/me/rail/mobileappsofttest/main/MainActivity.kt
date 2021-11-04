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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import me.rail.mobileappsofttest.NoteFragment
import me.rail.mobileappsofttest.R
import me.rail.mobileappsofttest.databinding.ActivityMainBinding
import me.rail.mobileappsofttest.db.Note
import me.rail.mobileappsofttest.view.SimpleItemTouchCallback


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    private val model: MainViewModel by viewModels()

    private var isKeyboardVisible = false

    private var adapter: NoteAdapter? = null

    private var simpleItemTouchCallback: SimpleItemTouchCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initializeRecyclerView()

        model.notes.observeForever {
            adapter?.setItems(it)
        }

        model.pinnedCount.observeForever {
            simpleItemTouchCallback?.setPinnedCount(it)
        }

        binding?.add?.setOnClickListener {
            onAddClick()
        }

        binding?.create?.setOnClickListener {
            toggleEditTextVisibility()
            toggleKeyboardVisibility()
            changBackgroundColor(R.color.transparent)

            addNote()
        }

        model.textForShare.observe(this, {
            onShareClick(it)
        })

        model.addingNoteFromSelectedNote.observe(this, {
            binding?.edittextBackground?.bringToFront()
            onAddClick()
        })

        binding?.edittextBackground?.setOnClickListener {
            addNote()

            toggleKeyboardVisibility()
            onKeyboardBackPressed()
        }
    }

    private fun onAddClick() {
        changBackgroundColor(R.color.blur)
        toggleEditTextVisibility()
        binding?.edittext?.bringToFront()
        binding?.create?.bringToFront()
        binding?.edittext?.requestFocus()
        toggleKeyboardVisibility()
        binding?.edittext?.setOnKeyboardBackPressedListener(getOnKeyboardBackPressedListener())
    }

    private fun getOnKeyboardBackPressedListener(): MutableLiveData<Unit> {
        val onKeyboardBackPressed = MutableLiveData<Unit>()

        onKeyboardBackPressed.observeForever {
            isKeyboardVisible = !isKeyboardVisible
            onKeyboardBackPressed()
        }

        return onKeyboardBackPressed
    }

    private fun onKeyboardBackPressed() {
        toggleEditTextVisibility()
        changBackgroundColor(R.color.transparent)
        binding?.edittext?.text?.clear()
    }

    private fun changBackgroundColor(color: Int) {
        binding?.edittextBackground?.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                color
            )
        )

        toggleEditTextBackgroundVisibility()
    }

    private fun toggleEditTextBackgroundVisibility() {
        binding?.edittextBackground?.visibility =
            if (binding?.edittextBackground?.visibility == View.GONE) View.VISIBLE else View.GONE
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
        simpleItemTouchCallback = SimpleItemTouchCallback(
            UP or
                    DOWN or
                    START or
                    END, 0,
            onDragFinished = ::onDragFinished
        )

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback!!)

        itemTouchHelper.attachToRecyclerView(binding?.recyclerview)

        binding?.recyclerview?.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)

        binding?.recyclerview?.addItemDecoration(
            DividerItemDecoration(
                binding?.recyclerview?.context,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter = NoteAdapter(
            onUpClick = ::onUpClick,
            onNoteClick = ::onNoteClick,
            onShareClick = ::onShareClick,
            onPinClick = ::onPinClick,
        )

        binding?.recyclerview?.adapter = adapter
    }

    private fun onUpClick(note: Note) {
        model.setNoteToTop(note)
    }

    private fun onNoteClick(note: Note) {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, NoteFragment.newInstance(note))
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

    private fun onDragFinished(fromDragPosition: Int, toDragPosition: Int) {
        model.onNoteMove(fromDragPosition, toDragPosition)
    }

    private fun addNote() {
        val text = binding?.edittext?.text.toString()
        if (text.isNotEmpty()) {
            model.addNote(binding?.edittext?.text.toString())
            binding?.edittext?.text?.clear()
        }
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
