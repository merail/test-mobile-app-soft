package me.rail.mobileappsofttest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import me.rail.mobileappsofttest.databinding.FragmentNoteBinding
import me.rail.mobileappsofttest.db.Note
import me.rail.mobileappsofttest.db.NotesDao
import me.rail.mobileappsofttest.main.MainViewModel
import javax.inject.Inject

private const val ARG_NOTE = "note"

@AndroidEntryPoint
class NoteFragment : Fragment() {
    private var binding: FragmentNoteBinding? = null

    private var note: Note? = null

    private val mainViewModel: MainViewModel by activityViewModels()

    private var isPinned = false

    @Inject
    lateinit var notesDao: NotesDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            note = it.getParcelable(ARG_NOTE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(inflater, container, false)

        binding?.back?.setOnClickListener {
            requireActivity().onBackPressed()
        }

        isPinned = note?.pin == true

        setPinButtonBackground(isPinned)

        binding?.pin?.setOnClickListener {
            note?.let {
                isPinned = !isPinned
                setPinButtonBackground(isPinned)
                mainViewModel.togglePin(it)
                val position = if (isPinned) 0 else it.positionBeforePin
                note = note?.copy(position = position, pin = isPinned)
            }
        }

        binding?.share?.setOnClickListener {
            note?.let {
                mainViewModel.selectTextForShare(it.text)
            }
        }

        binding?.edittext?.text?.insert(0, note?.text)

        return binding?.root
    }

    private fun setPinButtonBackground(pin: Boolean) {
        binding?.pin?.apply {
            background =
                if (pin)
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.pin_filled
                    ) else AppCompatResources.getDrawable(requireContext(), R.drawable.pin)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(note: Note) =
            NoteFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_NOTE, note)
                }
            }
    }
}