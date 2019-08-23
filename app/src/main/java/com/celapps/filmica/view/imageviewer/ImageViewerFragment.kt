package com.celapps.filmica.view.imageviewer

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.celapps.filmica.R
import com.celapps.filmica.data.ApiConstants
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_image_viewer.*
import java.lang.Exception

class ImageViewerFragment : Fragment() {

    private lateinit var id: String
    private var imageUrl: String? = null
    private var tagFrag: String? = null

    private lateinit var listener: OnBackClickListener

    companion object {
        const val PARAM_ID = "ID"
        const val PARAM_IMAGE_URL = "IMAGE_URL"
        const val PARAM_TAG = "TAG"

        @JvmStatic
        fun newInstance(id: String, imageUrl: String, tag: String) =
            ImageViewerFragment().apply {
                arguments = Bundle().apply {
                    putString(PARAM_ID, id)
                    putString(PARAM_IMAGE_URL, imageUrl)
                    putString(PARAM_TAG, tag)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            id = it.getString(PARAM_ID) ?: ""
            imageUrl = it.getString(PARAM_IMAGE_URL)
            tagFrag = it.getString(PARAM_TAG)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageUrl?.let { url ->
            progressBar.visibility = View.VISIBLE

            Picasso.get()
                .load(url.replace(ApiConstants.SIZE_POSTER_500, ApiConstants.SIZE_POSTER_ORIGINAL))
                .placeholder(android.R.color.black)
                .error(R.drawable.film_placeholder)
                .into(imageViewDetail, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        progressBar.visibility = View.GONE
                    }

                })
        }

        imageViewClose.setOnClickListener { listener.onBackClicked(id) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_viewer, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnBackClickListener) {
            listener = context
        }
    }

    interface OnBackClickListener {
        fun onBackClicked(id: String)
    }
}
