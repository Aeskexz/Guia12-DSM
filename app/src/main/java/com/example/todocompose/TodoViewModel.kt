package com.example.todocompose

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todocompose.data.model.Todo
import com.example.todocompose.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadTodos() {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                val result = repository.fetchTodos()
                _todos.value = result
            } catch (e: Exception) {
                Log.e("TodoViewModel", "Error cargando tareas desde la API: ${e.message}", e)
                _errorMessage.value = "Servidor API no disponible. Mostrando tareas locales."
                if (_todos.value.isEmpty()) {
                    _todos.value = listOf(
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
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun addTodo(title: String, description: String) {
        if (title.isBlank()) return

        // Generar un ID secuencial idéntico al formato de la guía ("1", "2", "3", "4"...)
        val maxNumericId = _todos.value.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: _todos.value.size
        val newId = (maxNumericId + 1).toString()

        val newTodo = Todo(
            id = newId,
            title = title,
            description = description,
            done = false
        )

        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                val result = repository.addTodo(newTodo)
                _todos.value += result
            } catch (e: Exception) {
                Log.e("TodoViewModel", "Error al agregar en API, guardando localmente: ${e.message}", e)
                _todos.value += newTodo
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateTodoDetails(id: String, newTitle: String, newDescription: String) {
        val existing = _todos.value.find { it.id == id } ?: return
        val updatedTodo = existing.copy(title = newTitle, description = newDescription)

        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.updateTodo(id, updatedTodo)
                _todos.value = _todos.value.map { if (it.id == id) result else it }
            } catch (e: Exception) {
                Log.e("TodoViewModel", "Error al actualizar en API, guardando localmente: ${e.message}", e)
                _todos.value = _todos.value.map { if (it.id == id) updatedTodo else it }
            } finally {
                _loading.value = false
            }
        }
    }

    fun toggleTodoDone(todo: Todo) {
        val updatedTodo = todo.copy(done = !todo.done)
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.updateTodo(todo.id, updatedTodo)
                _todos.value = _todos.value.map { if (it.id == todo.id) result else it }
            } catch (e: Exception) {
                Log.e("TodoViewModel", "Error al actualizar en API, guardando localmente: ${e.message}", e)
                _todos.value = _todos.value.map { if (it.id == todo.id) updatedTodo else it }
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteTodo(id: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.deleteTodo(id)
                _todos.value = _todos.value.filter { it.id != id }
            } catch (e: Exception) {
                Log.e("TodoViewModel", "Error al eliminar en API, guardando localmente: ${e.message}", e)
                _todos.value = _todos.value.filter { it.id != id }
            } finally {
                _loading.value = false
            }
        }
    }
}
