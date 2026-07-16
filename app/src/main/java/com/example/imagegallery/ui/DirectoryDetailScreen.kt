package com.example.imagegallery.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.imagegallery.data.ImageItem
import com.example.imagegallery.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryDetailScreen(
    dirId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val images by viewModel.getImagesForDirectory(dirId).collectAsState(initial = emptyList())
    var selectedImageId by remember { mutableStateOf<Long?>(null) }
    val selectedImage = images.find { it.id == selectedImageId }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.addImage(dirId, it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("目录详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { pickImageLauncher.launch("image/*") }) {
                Icon(Icons.Default.Add, contentDescription = "添加图片")
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding).padding(8.dp)
        ) {
            items(images, key = { it.id }) { image ->
                Card(
                    modifier = Modifier.padding(6.dp),
                    onClick = { selectedImageId = image.id }
                ) {
                    AsyncImage(
                        model = image.filePath,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                    )
                    if (image.note.isNotBlank()) {
                        Text(
                            image.note,
                            maxLines = 2,
                            modifier = Modifier.padding(4.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    selectedImage?.let { image ->
        ImageDetailDialog(
            image = image,
            onDismiss = { selectedImageId = null },
            onSaveNote = { note -> viewModel.updateImageNote(image, note) },
            onExtractText = { viewModel.extractTextForImage(image) },
            onDelete = {
                viewModel.deleteImage(image)
                selectedImageId = null
            }
        )
    }
}

@Composable
fun ImageDetailDialog(
    image: ImageItem,
    onDismiss: () -> Unit,
    onSaveNote: (String) -> Unit,
    onExtractText: () -> Unit,
    onDelete: () -> Unit
) {
    var note by remember(image.id, image.note) { mutableStateOf(image.note) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("图片详情") },
        text = {
            Column {
                AsyncImage(
                    model = image.filePath,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onExtractText) { Text("提取图片文字作为备注") }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSaveNote(note); onDismiss() }) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDelete) { Text("删除图片") } }
    )
}
