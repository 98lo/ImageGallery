package com.example.imagegallery.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.data.AppDatabase
import com.example.imagegallery.data.Directory
import com.example.imagegallery.data.ImageItem
import com.example.imagegallery.utils.FileUtils
import com.example.imagegallery.utils.OcrUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).dao()

    private val searchKeyword = MutableStateFlow("")

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val directories = searchKeyword.flatMapLatest { keyword ->
        if (keyword.isBlank()) dao.getAllDirectories() else dao.searchDirectories(keyword)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setSearchKeyword(keyword: String) {
        searchKeyword.value = keyword
    }

    fun addDirectory(name: String, note: String) {
        viewModelScope.launch {
            dao.insertDirectory(Directory(name = name, note = note))
        }
    }

    fun deleteDirectory(dir: Directory) {
        viewModelScope.launch {
            dao.deleteImagesForDirectory(dir.id)
            dao.deleteDirectory(dir)
        }
    }

    fun getImagesForDirectory(dirId: Long) = dao.getImagesForDirectory(dirId)

    fun addImage(dirId: Long, uri: Uri) {
        viewModelScope.launch {
            val path = FileUtils.copyImageToInternalStorage(getApplication(), uri)
            if (path != null) {
                dao.insertImage(ImageItem(dirId = dirId, filePath = path))
            }
        }
    }

    fun updateImageNote(image: ImageItem, note: String) {
        viewModelScope.launch {
            dao.updateImage(image.copy(note = note))
        }
    }

    fun extractTextForImage(image: ImageItem) {
        viewModelScope.launch {
            val text = OcrUtils.recognizeText(image.filePath)
            if (text.isNotBlank()) {
                dao.updateImage(image.copy(note = text))
            }
        }
    }

    fun deleteImage(image: ImageItem) {
        viewModelScope.launch {
            dao.deleteImage(image)
        }
    }
}
