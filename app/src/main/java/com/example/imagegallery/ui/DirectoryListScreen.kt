package com.example.imagegallery.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.imagegallery.data.Directory
import com.example.imagegallery.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryListScreen(
    viewModel: MainViewModel,
    onOpenDirectory: (Long) -> Unit
) {
    val directories by viewModel.directories.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("我的图片目录") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "新建目录")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(12.dp)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.setSearchKeyword(it)
                },
                label = { Text("搜索目录名称") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(directories, key = { it.id }) { dir ->
                    DirectoryRow(
                        dir = dir,
                        onClick = { onOpenDirectory(dir.id) },
                        onDelete = { viewModel.deleteDirectory(dir) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddDirectoryDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, note ->
                viewModel.addDirectory(name, note)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DirectoryRow(dir: Directory, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(dir.name, style = MaterialTheme.typography.titleMedium)
                if (dir.note.isNotBlank()) {
                    Text(dir.note, style = MaterialTheme.typography.bodySmall)
                }
            }
            TextButton(onClick = onDelete) { Text("删除") }
        }
    }
}

@Composable
fun AddDirectoryDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建目录") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("目录名称") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("备注") })
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name, note) }) { Text("确定") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
