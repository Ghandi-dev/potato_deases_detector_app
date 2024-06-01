package com.example.potatoguard


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.potatoguard.databinding.ActivityCameraBinding
import com.example.potatoguard.ml.AutoModel2
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

import java.io.FileDescriptor
import java.io.IOException
import java.util.Arrays
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dapatkan data dari Intent
        val byteArray = intent.getByteArrayExtra("EXTRA_BITMAP")
        val classes = intent.getIntExtra("EXTRA_MAXPOS", 0)
        val conf = intent.getFloatExtra("EXTRA_CONF", 0.0f)

        // Tampilkan bitmap jika ada
        if (byteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            binding.capturedImageView.setImageBitmap(bitmap)
        }

        // Buat instance Diagnosis fragment dengan data yang diperlukan
        val diagnosis = Diagnosis.newInstance(conf, classes)

        // Ganti fragment di containerCamera dengan Diagnosis fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerCamera, diagnosis, "diagnosis")
            .commit()
    }
}
