package me.rail.mobileappsofttest.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import me.rail.mobileappsofttest.R
import me.rail.mobileappsofttest.databinding.ItemNoteBinding
import me.rail.mobileappsofttest.db.Note


class NoteAdapter(
    private val onUpClick: ((Note) -> Unit)? = null,
    private val onNoteClick: ((Note) -> Unit)? = null,
    private val onShareClick: ((String) -> Unit)? = null,
    private val onPinClick: ((Note) -> Unit)? = null,
) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private var items: MutableList<Note>? = null

    class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    fun setItems(updatedItems: List<Note>) {
        if (items == null) {
            items = updatedItems.toMutableList()
            notifyItemRangeInserted(0, itemCount)
            return
        }

        items?.forEachIndexed { index, item ->
            val newItem = updatedItems.find { it.position == item.position }
            if (newItem != null && newItem != item) {
                items?.set(index, newItem)
                notifyItemChanged(index)
            }
        }

        val oldCount = itemCount
        val newItems = updatedItems.filter { updatedItem ->
            items?.none { item -> item.position == updatedItem.position } == true
        }
        items?.addAll(newItems)
        notifyItemRangeInserted(oldCount, itemCount - oldCount)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemNoteBinding.inflate(inflater, viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items?.get(position) ?: return

        holder.binding.up.setOnClickListener {
            onUpClick?.invoke(item)
        }

        holder.binding.text.setOnClickListener {
            onNoteClick?.invoke(item)
        }

        holder.binding.share.setOnClickListener {
            onShareClick?.invoke(item.text)
        }

        holder.binding.pin.setOnClickListener {
            onPinClick?.invoke(item)
        }

        holder.binding.pin.apply {
            background =
                if (item.pin)
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.pin_filled
                    ) else AppCompatResources.getDrawable(context, R.drawable.pin)
        }

        holder.binding.text.text =
            HtmlCompat.fromHtml(getEditedText(item.text), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun getItemCount() = items?.size ?: 0

    private fun getEditedText(text: String): String {
        val textList = text.split(" ")
        var editedText = "<b>" + textList[0]
        if (textList.size < 2)
            editedText = "$editedText</b"
        else {
            editedText = "$editedText ${textList[1]}</b>"
            for (i in 2 until textList.size) {
                editedText = "$editedText ${textList[i]}"
            }
        }

        return editedText
    }

    fun moveItem(from: Int, to: Int) {
//        val fromEmoji = items?.get(from) ?: return
//        items.removeAt(from)
//        Log.d("test", "$from $to")
//        if (to < from) {
//            items.add(to, fromEmoji)
//        } else {
//            items.add(to - 1, fromEmoji)
//        }
    }
}