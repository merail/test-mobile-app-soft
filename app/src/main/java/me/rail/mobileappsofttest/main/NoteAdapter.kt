package me.rail.mobileappsofttest.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import me.rail.mobileappsofttest.R
import me.rail.mobileappsofttest.databinding.ItemNoteBinding
import me.rail.mobileappsofttest.db.Note


class NoteAdapter(
    private val context: Context,
    private val notes: List<Note>,
    private val onUpClick: ((Note) -> Unit)? = null,
    private val onPinClick: ((Note) -> Unit)? = null
) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemNoteBinding.inflate(inflater, viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notes[position]

        holder.binding.up.setOnClickListener {
            onUpClick?.invoke(item)
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

        holder.binding.share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, item.text)

            context.startActivity(Intent.createChooser(intent, "Share Note"))
        }
    }

    override fun getItemCount() = notes.size

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
}