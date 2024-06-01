package com.example.potatoguard

import android.Manifest

import android.content.Intent
import android.content.pm.PackageManager

import android.graphics.Bitmap

import android.os.Build
import android.os.Bundle

import android.widget.Toast

import androidx.activity.enableEdgeToEdge

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.fragment.app.FragmentTransaction
import com.example.potatoguard.ml.AutoModel2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream



class MainActivity : AppCompatActivity(),Detector.DetectorListener {
    private lateinit var cameraActivatorButton: FloatingActionButton
    private lateinit var detector: Detector
    private val MODEL_PATH = "modd.tflite"
    private val LABELS_PATH = "labels.txt"
    private lateinit var cameraActivityResultLauncher: ActivityResultLauncher<Void?>

    // Permintaan izin kamera
    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        }

        detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
        detector.setup()

        cameraActivatorButton = findViewById(R.id.cameraActivatorButton)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.add(R.id.container, Content(), "Content")
        transaction.commit()

        cameraActivatorButton.setOnClickListener {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                )
            }
        }

        cameraActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap: Bitmap? ->
            bitmap?.let {
                    detector.detect(it)
            }
        }
    }

    private fun startCamera() {
        cameraActivityResultLauncher.launch(null)
    }


    private fun classification(bitmap: Bitmap) {
        // Jika daun terdeteksi, lanjutkan dengan deteksi penyakit
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true)
        val normalizedBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
        for (x in 0 until 256) {
            for (y in 0 until 256) {
                val pixel = resizedBitmap.getPixel(x, y)
                val r = (pixel shr 16 and 0xFF) / 255.0f
                val g = (pixel shr 8 and 0xFF) / 255.0f
                val b = (pixel and 0xFF) / 255.0f
                val normalizedPixel = (0xFF shl 24) or ((r * 255).toInt() shl 16) or ((g * 255).toInt() shl 8) or (b * 255).toInt()
                normalizedBitmap.setPixel(x, y, normalizedPixel)
            }
        }
        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(normalizedBitmap)
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)
        val model = AutoModel2.newInstance(this)
        // Memproses gambar melalui model
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        val max = outputFeature0.floatArray

        // Mencari indeks dan nilai maksimum pada hasil prediksi
        var maxPos = 0
        var maxConfidence = 0.0f
        for (i in max.indices) {
            if (max[i] > maxConfidence) {
                maxConfidence = max[i]
                maxPos = i
            }
        }
        val bitmapByteArray = bitmapToByteArray(normalizedBitmap)
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("EXTRA_BITMAP", bitmapByteArray)
        intent.putExtra("EXTRA_MAXPOS",maxPos)
        intent.putExtra("EXTRA_CONF",maxConfidence)
        startActivity(intent)

        model.close()
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Tangani permintaan izin
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Akses kamera ditolak. Aplikasi tidak dapat berfungsi",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    override fun onEmptyDetect() {
        Toast.makeText(
            this,
            "Sepertinya gambar yang diambil bukan daun, silahkan coba lagi!",
            Toast.LENGTH_LONG
        ).show()
        startCamera()
    }

    override fun onDetect(image: Bitmap) {
        classification(image)
    }


}