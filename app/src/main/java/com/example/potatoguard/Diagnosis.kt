package com.example.potatoguard

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton


class Diagnosis : Fragment() {
    private lateinit var btnDiagnosis : AppCompatButton
    private  lateinit var akurasiTextView : TextView
    private  lateinit var titleContenTextView: TextView
    private  lateinit var diagnosisImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val clasess = arrayOf("Bercak Kering","Busuk Daun", "Sehat")
        val image = arrayOf(R.drawable.bercak_kering,R.drawable.busuk_daun,R.drawable.sehat)
        val index = arguments?.getInt("index")
        val confidence = arguments?.getFloat("confidence")
        val view = inflater.inflate(R.layout.fragment_diagnosis, container, false)
        btnDiagnosis = view.findViewById(R.id.buttonDiagnosis)
        akurasiTextView = view.findViewById(R.id.akurasiTextView)
        titleContenTextView = view.findViewById(R.id.titleContentTextView)
        diagnosisImageView = view.findViewById(R.id.diagnosisImageView)
        diagnosisImageView.setImageResource(image[index!!])
        titleContenTextView.text = clasess[index!!]
        if (confidence != null) {
            akurasiTextView.text =String.format ("%.2f %S",(confidence * 100),"%")
//            akurasiTextView.text = (confidence * 100).toString().plus("%")
        }
        btnDiagnosis.setOnClickListener {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("INDEX", index.toString())
            startActivity(intent)
        }
        return view
    }
    companion object {
        fun newInstance(confidence: Float, index: Int): Diagnosis {
            val fragment = Diagnosis()
            val args = Bundle()
            args.putFloat("confidence", confidence)
            args.putInt("index", index)
            fragment.arguments = args
            return fragment
        }
    }

}