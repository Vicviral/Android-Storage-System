package com.example.androidstorage.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.androidstorage.R
import com.example.androidstorage.adapters.InternalStorageAdapter
import com.example.androidstorage.databinding.ActivityMainBinding
import com.example.androidstorage.models.Internal
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var internalPhotosAdapter: InternalStorageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        internalPhotosAdapter = InternalStorageAdapter {
            val isDeletionSuccessful = deletePhotoFromInternalStorage(it.name)
            if (isDeletionSuccessful) {
                loadPhotosFromInternalStorageIntoRV()
                Toast.makeText(this, "Photo deleted successfully", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "Failed to delete photo", Toast.LENGTH_SHORT).show()
            }
        }

        setUpInternalStorageRecyclerView()
        loadPhotosFromInternalStorageIntoRV()

        val snapPhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            val isSwitchPrivate = binding.switchPrivate.isChecked

            if (isSwitchPrivate) {
                val isSavedSuccessfully = savePhotoToInternalStorage(UUID.randomUUID().toString(), it)
                if (isSavedSuccessfully) {
                    loadPhotosFromInternalStorageIntoRV()
                    Toast.makeText(this, "Photo saved successfully", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.snapPhoto.setOnClickListener {
            snapPhoto.launch()
        }

    }

    private fun setUpInternalStorageRecyclerView() = binding.rvPrivatePhotos.apply {
        adapter = internalPhotosAdapter
        layoutManager = StaggeredGridLayoutManager(4, RecyclerView.VERTICAL)

    }

    private fun loadPhotosFromInternalStorageIntoRV() {
        lifecycleScope.launch {
            val photos = loadPhotoFromInternalStorage()
            internalPhotosAdapter.submitList(photos)
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