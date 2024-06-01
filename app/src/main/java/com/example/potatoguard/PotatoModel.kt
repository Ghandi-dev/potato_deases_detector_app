package com.example.potatoguard

import android.content.res.TypedArray

data class PotatoModel(
    val name: String,
    val scientificName: String,
    val textView1Name: String,
    val textView2Name: String,
    val textView3Name: String,
    val textView1: String,
    val textView2: Array<String>,
    val textView3: Array<String>,
    val image: TypedArray
    )
