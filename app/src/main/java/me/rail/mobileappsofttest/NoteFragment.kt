package me.rail.mobileappsofttest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.rail.mobileappsofttest.databinding.FragmentNoteBinding
import me.rail.mobileappsofttest.db.Note

private const val ARG_NOTE = "note"

class NoteFragment : Fragment() {
    private var binding: FragmentNoteBinding? = null

    private var note: Note? = null

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

        binding?.edittext?.text?.insert(0, note?.text)

        return binding?.root
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