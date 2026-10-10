package com.example.todocompose.ui.theme.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todocompose.data.model.Todo

@Composable
fun TodoList(
    todos: List<Todo>,
    modifier: Modifier = Modifier,
    onToggleDone: ((Todo) -> Unit)? = null,
    onEdit: ((Todo) -> Unit)? = null,
    onDelete: ((String) -> Unit)? = null
) {
    if (todos.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay tareas pendientes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(todos, key = { it.id }) { todo ->
                TodoItem(
                    todo = todo,
                    onToggleDone = onToggleDone,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    onToggleDone: ((Todo) -> Unit)? = null,
    onEdit: ((Todo) -> Unit)? = null,
    onDelete: ((String) -> Unit)? = null
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${todo.id}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (todo.done) "Completada" else "Pendiente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (todo.done) MaterialTheme.colorScheme.primary else
                            MaterialTheme.colorScheme.error
                    )
                    if (onToggleDone != null) {
                        Checkbox(
                            checked = todo.done,
                            onCheckedChange = { onToggleDone(todo) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Titulo: ${todo.title}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Descripcion: ${todo.description}",
                style = MaterialTheme.typography.bodySmall
            )

            if (onEdit != null || onDelete != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onEdit != null) {
                        TextButton(onClick = { onEdit(todo) }) {
                            Text("Editar")
                        }
                    }
                    if (onDelete != null) {
                        TextButton(onClick = { onDelete(todo.id) }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoPreviewScreen() {
    val sampleTodos = listOf(
        Todo(
            id = "1",
            title = "Comprar leche",
            description = "Ir al supermercado antes de las 6 PM.",
            done = false
        ),
        Todo(
            id = "2",
            title = "Estudiar Kotlin",
            description = "Repasar funciones de alto orden y lambdas.",
            done = true
        ),
        Todo(
            id = "3",
            title = "Hacer ejercicio",
            description = "Salir a correr al parque por 30 minutos.",
            done = false
        )
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sampleTodos) { todo ->
            TodoItem(todo = todo)
        }
    }
}
