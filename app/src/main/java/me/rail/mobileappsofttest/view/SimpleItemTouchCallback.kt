package me.rail.mobileappsofttest.view

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import me.rail.mobileappsofttest.main.NoteAdapter

class SimpleItemTouchCallback(
    dragDirs: Int,
    swipeDirs: Int,
    private val onDragFinished: ((Int, Int) -> Unit)? = null
) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private var fromDragPosition = -1
    private var toDragPosition = -1

    private var pinnedCount = 0

    fun setPinnedCount(pinnedCount: Int) {
        this.pinnedCount = pinnedCount
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        val adapter = recyclerView.adapter as NoteAdapter
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition

        if (fromDragPosition == -1) {
            fromDragPosition = from
        }

        toDragPosition = if (pinnedCount == 0) {
            to
        } else {
            if (fromDragPosition < pinnedCount) {
                if (to >= pinnedCount) {
                    pinnedCount - 1
                } else {
                    to
                }
            } else {
                if (to >= pinnedCount) to else pinnedCount
            }
        }

        adapter.notifyItemMoved(from, toDragPosition)

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

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.5f
        }
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1.0f
        if (fromDragPosition != toDragPosition && fromDragPosition != -1)
            onDragFinished?.invoke(fromDragPosition, toDragPosition)
        fromDragPosition = -1
    }
}