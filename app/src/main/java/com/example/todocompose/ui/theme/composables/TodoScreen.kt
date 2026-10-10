package com.example.todocompose.ui.theme.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todocompose.TodoViewModel
import com.example.todocompose.data.model.Todo

@Composable
fun TodoScreen(viewModel: TodoViewModel) {
    val todos by viewModel.todos.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var titleInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }

    var editingTodo by remember { mutableStateOf<Todo?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTodos()
    }

    // Diálogo emergente para editar la tarea seleccionada
    if (editingTodo != null) {
        val todoToEdit = editingTodo!!
        var editTitle by remember(todoToEdit) { mutableStateOf(todoToEdit.title) }
        var editDescription by remember(todoToEdit) { mutableStateOf(todoToEdit.description) }

        AlertDialog(
            onDismissRequest = { editingTodo = null },
            title = { Text("Editar Tarea") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Titulo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Descripcion") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateTodoDetails(todoToEdit.id, editTitle, editDescription)
                        editingTodo = null
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTodo = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mis Tareas",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
        )

        TextField(
            value = titleInput,
            onValueChange = { titleInput = it },
            label = { Text("Titulo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = descriptionInput,
            onValueChange = { descriptionInput = it },
            label = { Text("Descripcion") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.addTodo(titleInput, descriptionInput)
                titleInput = ""
                descriptionInput = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (loading) {
            CircularProgressIndicator()
        } else {
            TodoList(
                todos = todos,
                modifier = Modifier.weight(1f),
                onToggleDone = { todo -> viewModel.toggleTodoDone(todo) },
                onEdit = { todo -> editingTodo = todo },
                onDelete = { id -> viewModel.deleteTodo(id) }
            )
        }
    }
}
