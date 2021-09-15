package com.example.androidstorage.ui

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import com.example.androidstorage.R
import com.example.androidstorage.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

}