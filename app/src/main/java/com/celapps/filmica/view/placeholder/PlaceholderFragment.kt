package com.celapps.filmica.view.placeholder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.celapps.filmica.R

class PlaceholderFragment: Fragment() {

    // Método estático dentro de la clase
    companion object {
        fun newInstance() : PlaceholderFragment {
            val instance = PlaceholderFragment()

            return instance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_placeholder, container, false)
    }

}
