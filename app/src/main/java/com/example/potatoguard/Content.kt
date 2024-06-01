package com.example.potatoguard

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat


class Content : Fragment() {
    private lateinit var btnSehat: AppCompatButton
    private lateinit var btnBercakKering: AppCompatButton
    private lateinit var btnBusukDaun: AppCompatButton
    private lateinit var detailButton: AppCompatButton // Detail button
    private lateinit var titleTextView: TextView
    private lateinit var contentImageView: ImageView
    private var currentCondition: String = "" // Variabel untuk menyimpan kondisi saat ini

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_content, container, false)
        // Inflate layout, initialize views, set initial text and image, etc.
        detailButton = view.findViewById(R.id.button)
        btnSehat = view.findViewById(R.id.sehatButton)
        btnBercakKering = view.findViewById(R.id.bercakKeringButton)
        btnBusukDaun = view.findViewById(R.id.busukDaunButton)
        titleTextView = view.findViewById(R.id.titleContentTextView)
        contentImageView = view.findViewById(R.id.contentImageView)
        updateUI("Sehat", R.drawable.sehat)
        var index:Int = 2
        btnSehat.setOnClickListener {
            updateUI("Sehat", R.drawable.sehat)
            updateButtonBackground(btnSehat)
            index = 2
        }
        btnBercakKering.setOnClickListener {
            updateUI("Bercak Kering", R.drawable.bercak_kering)
            updateButtonBackground(btnBercakKering)
            index = 0
        }
        btnBusukDaun.setOnClickListener {
            updateUI("Busuk Daun", R.drawable.busuk_daun)
            updateButtonBackground(btnBusukDaun)
            index = 1
        }

        detailButton.setOnClickListener {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("INDEX", index.toString())
            startActivity(intent)
        }

        return view
    }

    private fun updateUI(condition: String, imageResId: Int) {
        titleTextView.text = condition
        contentImageView.setImageResource(imageResId)
        currentCondition = condition // Update the current condition
    }
    private fun updateButtonBackground(selectedButton: AppCompatButton) {
        // Reset backgrounds
        btnSehat.background = ContextCompat.getDrawable(requireContext(), R.drawable.content_button_background_inactive)
        btnBusukDaun.background = ContextCompat.getDrawable(requireContext(), R.drawable.content_button_background_inactive)
        btnBercakKering.background = ContextCompat.getDrawable(requireContext(), R.drawable.content_button_background_inactive)

        // Set background of selected button
        selectedButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.content_button_background)
    }

}