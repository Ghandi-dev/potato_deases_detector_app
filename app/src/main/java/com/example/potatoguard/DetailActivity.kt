package com.example.potatoguard

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.potatoguard.databinding.ActivityDetailBinding
import kotlin.collections.ArrayList

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        }
        val typeCase = intent.getStringExtra("INDEX")?:"0"
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        displayDataBasedOnTypeCase(typeCase)

        binding.backButton.setOnClickListener{
            onBackPressed()
        }
    }

    private fun createCustomBulletList(items: Array<String>): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()
        val bulletIcon: Drawable? = ContextCompat.getDrawable(this, R.drawable.search)
        bulletIcon?.setBounds(0, 0, 40, 40)

        items.forEach { item ->
            val start = spannableStringBuilder.length
            spannableStringBuilder.append("  ") // Placeholder for the image span
            spannableStringBuilder.append(item).append("\n\n")
            val end = start + 1 // The image span will replace the first space

            bulletIcon?.let {
                val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BOTTOM)
                spannableStringBuilder.setSpan(
                    imageSpan,
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        return spannableStringBuilder
    }

    private fun displayDataBasedOnTypeCase(typeCase: String) {
        val index = typeCase.toIntOrNull() ?: 0
        val plantConditions = getPlantConditions()
        val slideModels = ArrayList<SlideModel>()
        if (index in plantConditions.indices) {
            val selectedCondition = plantConditions[index]
            if(index == 3 ){
                binding.layout3.visibility = View.GONE
            }
            binding.detailTextView.text = selectedCondition.name
            binding.detailTitleTextView.text = selectedCondition.name
            binding.ilmiahTextView.text = selectedCondition.scientificName
            binding.textView1Name.text = selectedCondition.textView1Name
            binding.textView2Name.text = selectedCondition.textView2Name
            binding.textView1.text = selectedCondition.textView1
            binding.textView2.text = createCustomBulletList(selectedCondition.textView2)
            binding.textView3Name.text = selectedCondition.textView3Name
            binding.textView3.text = createCustomBulletList(selectedCondition.textView3)
            val drawables: TypedArray = selectedCondition.image
            for (i in 0 until drawables.length()) {
                val drawableId = drawables.getResourceId(i, -1)
                if (drawableId != -1) {
                    slideModels.add(SlideModel(drawableId, scaleType = ScaleTypes.FIT))
                }
            }
            drawables.recycle()
            binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT)

        } else {
            Toast.makeText(this, "Invalid type case provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPlantConditions(): List<PotatoModel> {
        return listOf(
            PotatoModel(getString(R.string.bercak_kering),
                "Alternaria Solani",
                "Penyebab",
                "Gejala",
                "Pengendalian",
                resources.getString(R.string.penyebab_bercak_kering),
                resources.getStringArray(R.array.gejala_bercak_kering),
                resources.getStringArray(R.array.pencegahan_bercak_kering),
                resources.obtainTypedArray(R.array.image_bercak_kering)),
            PotatoModel(getString(R.string.busuk_daun),
                "Phytophthora Infestans",
                "Penyebab",
                "Gejala",
                "Pengendalian",
                resources.getString(R.string.penyebab_busuk_daun),
                resources.getStringArray(R.array.gejala_busuk_daun),
                resources.getStringArray(R.array.pencegahan_busuk_daun),
                resources.obtainTypedArray(R.array.image_busuk_daun)),
            PotatoModel("Daun Sehat",
                "Solanum Tuberosum",
                "Tentang Tanaman Kentang",
                "Tips Merawat Tanaman Kentang",
                "",
                resources.getString(R.string.tentang_kentang),
                resources.getStringArray(R.array.cara_merawat_kentang),
                arrayOf(),
                resources.obtainTypedArray(R.array.image_sehat)
            ),
        )
    }
    override fun onBackPressed() {
        super.onBackPressed()
        // Optional: Add custom behavior here if needed
    }
}
