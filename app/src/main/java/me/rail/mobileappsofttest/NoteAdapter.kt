package me.rail.mobileappsofttest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.rail.mobileappsofttest.databinding.ItemNoteBinding

class NoteAdapter(private val notes: ArrayList<String>) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemNoteBinding.inflate(inflater, viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
    }

    override fun getItemCount() = notes.size

}