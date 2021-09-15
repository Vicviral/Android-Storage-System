package com.example.androidstorage.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.lifecycle.lifecycleScope
import com.example.androidstorage.R
import com.example.androidstorage.adapters.InternalStorageAdapter
import com.example.androidstorage.databinding.ActivityMainBinding
import com.example.androidstorage.models.Internal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var internalPhotos: InternalStorageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val snapPhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            val isSwitchPrivate = binding.switchPrivate.isChecked

            if (isSwitchPrivate) {
                //Do something
            }
        }

        binding.snapPhoto.setOnClickListener {
            snapPhoto.launch()
        }

    }

    private fun loadPhotosFromInternalStorageIntoRV() {
        lifecycleScope.launch {
            val photos = loadPhotoFromInternalStorage()
            internalPhotos.submitList(photos)
        }
    }

    private fun deletePhotoFromInternalStorage(filename: String): Boolean {
        return try {
            deleteFile(filename)

        }catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun loadPhotoFromInternalStorage(): List<Internal> {
        return  withContext(Dispatchers.IO) {
            val files = filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                Internal(it.name, bitmap)
            } ?: listOf()
        }
    }

    private fun savePhotoToInternalStorage(filename: String, bitmap: Bitmap): Boolean {
        return try {
            openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap")
                }
            }
            true
        }catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

}