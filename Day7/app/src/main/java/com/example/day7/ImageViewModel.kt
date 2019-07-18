package com.example.day7

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ImageViewModel(application: Application): AndroidViewModel(application) {
    private val repository = MediaRepository(application)

    public fun getExternalImages(): List<String>? = repository.getExternalPublicImages()


}