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


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    private val model: MainViewModel by viewModels()

    private var isKeyboardVisible = false

    private var fromDragPosition = -1
    private var toDragPosition = -1

    private var adapter: NoteAdapter? = null

    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                UP or
                        DOWN or
                        START or
                        END, 0
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    val adapter = recyclerView.adapter as NoteAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    if (this@MainActivity.fromDragPosition == -1)
                        this@MainActivity.fromDragPosition = from
                    this@MainActivity.toDragPosition = to

                    adapter.notifyItemMoved(from, to)

                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {

                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                    model.onNoteMove(fromDragPosition, toDragPosition)
                    fromDragPosition = -1
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initializeRecyclerView()

        model.notes.observeForever {
            adapter?.setItems(it)
        }

        binding?.add?.setOnClickListener {
            onAddClick()
        }

        binding?.create?.setOnClickListener {
            toggleEditTextVisibility()
            toggleKeyboardVisibility()
            changBackgroundColor(R.color.transparent)
            model.addNote(binding?.edittext?.text.toString())
            binding?.edittext?.text?.clear()
        }

        model.textForShare.observe(this, {
            onShareClick(it)
        })

        model.addingNoteFromSelectedNote.observe(this, {
            binding?.edittextBackground?.bringToFront()
            onAddClick()
        })
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

    private fun getOnKeyboardBackPressedListener(): MutableLiveData<Boolean> {
        val onKeyboardBackPressed = MutableLiveData<Boolean>()
        onKeyboardBackPressed.value = false

        onKeyboardBackPressed.observeForever {
            if (it) {
                isKeyboardVisible = !isKeyboardVisible
                toggleEditTextVisibility()
                changBackgroundColor(R.color.transparent)
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

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
