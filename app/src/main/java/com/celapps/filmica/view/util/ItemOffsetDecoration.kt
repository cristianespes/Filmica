package com.celapps.filmica.view.util

import android.graphics.Rect
import android.support.annotation.DimenRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class ItemOffsetDecoration(@DimenRes val offsetId: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val offset: Int = view.context.resources.getDimensionPixelSize(offsetId) // Obtenemos el offset en pixeles
        // view.context.resources.getDimensionPixelOffset(R.dimen.offset_grid) // Obtenemos la diferencia, no válido

        val position = parent.getChildAdapterPosition(view) // Posición global
        //parent.getChildLayoutPosition() // Posición según la vista, el tercer item, si aparece primero, será el item 0

        // Total de items
        val items = parent.adapter?.itemCount ?: 0 // El adaptador es quien nos da la cantidad de items y puede no tener un getCount
        //parent.childCount // item que se están visualizando

        // Si el RecyclerView es un Grid
        if (parent.layoutManager is GridLayoutManager) {

            val columns = (parent.layoutManager as GridLayoutManager).spanCount
            val rows = ( items + 1 / columns )

            val column = getColumn(position, columns) // +1 por conveniencia, podriamos omitirlo
            val row = getRow(position, columns) // Tenemos que redondear el valor hacía arriba

            val topOffset = if (row == 1) offset else offset / 2 // Margen superior de los item de la primera fila
            val leftOffset = if (column == 1) offset else offset / 2

            val bottomOffset = if (row == rows) offset else offset / 2
            val rightOffset = if (column == columns) offset else offset / 2

            outRect.set(leftOffset, topOffset, rightOffset, bottomOffset) // Int en pixeles

        } else if (parent.layoutManager is LinearLayoutManager) { // Si no lo es, es un LinearLayout

            val top = if (position == 0) offset else 0

            outRect.set(offset, top, offset, offset)

        }


    }

    private fun getRow(position: Int, columns: Int) =
        Math.ceil((position.toDouble() + 1) / columns.toDouble()).toInt()

    private fun getColumn(position: Int, columns: Int) = (position % columns) + 1

}