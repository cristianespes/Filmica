package com.celapps.filmica.view.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.View
import com.celapps.filmica.R

// dragDirs => Soporte para arrastre
// swipeDirs => Soporte para swipe
abstract class SwipeToDeleteCallback: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        return false // no queremos que nos avise cuando se muevan los elementos
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val context = recyclerView.context
        val itemView = viewHolder.itemView

        setupBackground(context, itemView, dX, c)

        setupIcon(context, itemView, c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun setupBackground(
        context: Context,
        itemView: View,
        dX: Float,
        c: Canvas
    ) {
        // Pintar Background
        val color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        val background = ColorDrawable(color)
        // Dimensiones
        background.setBounds(
            itemView.left,
            itemView.top,
            itemView.left + dX.toInt(),
            itemView.bottom
        )
        // Añadir background a canvas
        background.draw(c)
    }

    private fun setupIcon(context: Context, itemView: View, c: Canvas) {
        // Pintar icono
        val checkIcon = ContextCompat.getDrawable(context, R.drawable.ic_check)!!
        // Determinar el margen del icono
        val iconMargin = (itemView.height - checkIcon.intrinsicHeight) / 3
        // Coordenadas del icono
        val iconTop = itemView.top + (itemView.height - checkIcon.intrinsicHeight) / 2
        val iconLeft = itemView.left + iconMargin
        val iconRight = itemView.left + iconMargin + checkIcon.intrinsicWidth
        val iconBottom = iconTop + checkIcon.intrinsicHeight
        // Dimensiones
        checkIcon.setBounds(
            iconLeft,
            iconTop,
            iconRight,
            iconBottom
        )
        // Añadir el icono al canvas
        checkIcon.draw(c)
    }

}