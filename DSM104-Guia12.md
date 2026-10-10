# UNIVERSIDAD DON BOSCO
**FACULTAD DE INGENIERÍA**
**ESCUELA DE COMPUTACIÓN**

| | |
|---|---|
| **CICLO** | 02-2026 |
| **MATERIA** | DESARROLLO DE SOFTWARE PARA MÓVILES |
| **PRÁCTICA** | JETPACK COMPOSE |
| **GUÍA DE LABORATORIO** | # 12 |

---

## I. OBJETIVOS

- Que el estudiante comprenda el desarrollo de una aplicación utilizando Compose.
- Que el estudiante aprenda a crear interfaces declarativas.
- Que el estudiante aplique el patrón MVVM para separar la lógica de negocio, la capa de acceso a datos y la interfaz de usuario.
- Que el estudiante integre una API REST con Retrofit y visualice información en una aplicación Android.

---

## II. INTRODUCCIÓN TEÓRICA

Jetpack Compose es el kit de herramientas moderno de Android para compilar IU nativas con menos código, herramientas potentes y APIs intuitivas de Kotlin. Simplifica y acelera el desarrollo de interfaz de usuario, incluida la adaptación a cualquier factor de forma, desde smartphones, tablets, dispositivos plegables, TVs y wearables.

Compose ofrece componentes de IU precompilados que funcionan con gráficos, animaciones y otros elementos visuales.

Una de sus principales ventajas es que permite construir la interfaz de forma declarativa, enfocándose en qué se quiere mostrar y dejando que Compose se encargue del renderizado.

---

## III. DESARROLLO DE LA PRÁCTICA

### 1. Crear un nuevo proyecto en Android Studio

1. Abra Android Studio.
2. Cree un nuevo proyecto.
3. Seleccione la plantilla **Empty Activity**.

### 2. Configurar el proyecto

Configure el proyecto con las siguientes características:

- **Nombre:** TodoCompose
- **Ubicación:** predeterminada o la que prefiera
- **Lenguaje:** Kotlin
- **Minimum SDK:** API 24 Nougat

### 3. Editar AndroidManifest.xml

Agregue el permiso de conectividad a internet.

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET"/>

    <application>
        .....
    </application>

</manifest>
```

### 4. Modificar libs.versions.toml

Cambia la vista del proyecto de **Android** a **Project** y localiza el archivo **libs.versions.toml** dentro de la carpeta **gradle**.

Necesitaremos las librerías para consumir servicios REST:

- Retrofit 2
- Retrofit 2 Converter Gson

En la sección **[versions]**, agregue la versión de Retrofit:

```toml
[versions]
retrofit = "2.9.0"
```

En la sección **[libraries]**, agregue las siguientes referencias:

```toml
[libraries]
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { module = "com.squareup.retrofit2:converter-gson", version.ref = "retrofit" }
```

### 5. Modificar build.gradle.kts (Module :app)

Agregue las dependencias en el bloque `dependencies`:

```kotlin
dependencies {
    // Retrofit para hacer peticiones HTTP
    implementation(libs.retrofit)

    // Convertidor Gson para convertir el JSON a objetos Kotlin
    implementation(libs.converter.gson)
}
```

> Importante: el nombre exacto de la dependencia puede variar según la configuración del proyecto; verifique el nombre generado en `libs.versions.toml`.

### 6. Sincronizar el proyecto con Gradle

1. Haga clic en **Sync Now**.
2. Si no aparece la opción, cierre y vuelva a abrir el proyecto.
3. Espere a que la sincronización termine.

### 7. Revisar la estructura del proyecto

Una vez finalizada la sincronización, la estructura del proyecto será parecida a esta:

```text
app
├── manifests
├── kotlin+java
│   ├── com.example.todocompose
│   │   ├── ui.theme
│   │   │   ├── Color.kt
│   │   │   ├── Theme.kt
│   │   │   └── Type.kt
│   │   └── MainActivity.kt
│   ├── com.example.todocompose (androidTest)
│   └── com.example.todocompose (test)
└── res
    ├── drawable
    ├── mipmap
    ├── values
    └── xml
```

En este punto se observan las clases de Kotlin y recursos, pero aún no hay estructura para una vista XML ni lógica de acceso a datos.

### 8. Abrir MainActivity.kt

Abra el archivo **MainActivity.kt** para ver el código generado por la plantilla.

Es posible visualizar el resultado de cada función decorada con el atributo `@Composable` agregando `@Preview` si la función no recibe parámetros. Si recibe parámetros, se usa `@PreviewParameter()`.

### 9. Crear paquetes principales

Dentro del paquete principal `com.example.todocompose`, cree los paquetes:

- `data`
- `repository`

Dentro de `data`, cree también los subpaquetes:

- `model`
- `network`

La estructura final debería ser similar a esta:

```text
com.example.todocompose
├── data
│   ├── model
│   └── network
├── repository
├── MainActivity.kt
├── TodoViewModel.kt
└── ui
    └── theme
        └── composables
```

### 10. Crear la clase modelo Todo

Dentro del paquete `data.model`, agregue una nueva clase Kotlin llamada **Todo**:

```kotlin
package com.example.todocompose.data.model

data class Todo (
    val id: String,
    val title: String,
    val description: String,
    val done: Boolean
)
```

Esta clase representa cada tarea de la aplicación.

### 11. Crear la interfaz Retrofit con TodoApiService

En el paquete `data.model.network`, agregue una interface llamada **TodoApiService**.

```kotlin
package com.example.todocompose.data.model.network

import com.example.todocompose.data.model.Todo
import retrofit2.http.*

interface TodoApiService {

    @GET("/to-do")
    suspend fun getTodos(): List<Todo>

}
```

Observe que solo se ha declarado la firma del método para obtener la lista de tareas desde la API.

### 12. Crear la clase repository

En el paquete `repository`, agregue un archivo llamado **TodoRepository.kt**.

```kotlin
package com.example.todocompose.repository

import com.example.todocompose.data.model.Todo
import com.example.todocompose.data.model.network.TodoApiService

class TodoRepository(private val api: TodoApiService) {

    suspend fun fetchTodos(): List<Todo> {
        return api.getTodos()
    }
}
```

La clase `TodoRepository` se encarga de encapsular la obtención de tareas desde la API, separando la lógica de negocio del acceso a datos.

### 13. Crear el ViewModel

En el paquete principal, cree una clase llamada **TodoViewModel.kt**:

```kotlin
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

    fun loadTodos() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.fetchTodos()
                _todos.value = result
            } catch (e: Exception) {
                Log.e("TodoViewModel", e.message.toString())
                _todos.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

}
```

La clase `ViewModel` sirve como puente entre la interfaz de usuario y la capa de acceso a datos de forma desacoplada. Además, expone estados reactivos a la UI mediante `StateFlow`.

### 14. Crear el paquete composables

Dentro del paquete `ui.theme`, agregue un nuevo paquete llamado **composables**. Luego, agregue dos archivos:

- `Components.kt`
- `TodoScreen.kt`

```text
ui.theme
└── composables
    ├── Components.kt
    └── TodoScreen.kt
```

### 15. Modificar Components.kt

Agregue el siguiente contenido:

```kotlin
package com.example.todocompose.ui.theme.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todocompose.data.model.Todo

@Composable
fun TodoList(todos: List<Todo>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(todos) { todo ->
            TodoItem(todo = todo)
        }
    }
}

@Composable
fun TodoItem(todo: Todo) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ID: ${todo.id}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = if (todo.done) "Completada" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (todo.done) MaterialTheme.colorScheme.primary else
                        MaterialTheme.colorScheme.error
                )
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
        }
    }
}
```

En esta clase se definen dos métodos decorados con `@Composable`:

- `TodoItem`: representa una tarjeta individual para cada tarea.
- `TodoList`: dibuja la lista completa de tareas.

### 16. Modificar TodoScreen.kt

Agregue el siguiente contenido:

```kotlin
package com.example.todocompose.ui.theme.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todocompose.TodoViewModel

@Composable
fun TodoScreen(viewModel: TodoViewModel) {
    val todos by viewModel.todos.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTodos()
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

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {
            TodoList(todos = todos)
        }
    }
}
```

La función `TodoScreen` es el componente principal de la aplicación y se encarga de mostrar la lista de tareas a través del ViewModel.

### 17. Configurar MainActivity.kt

Modifique el archivo `MainActivity.kt` con el siguiente código:

```kotlin
package com.example.todocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.todocompose.data.model.network.TodoApiService
import com.example.todocompose.repository.TodoRepository
import com.example.todocompose.ui.theme.composables.TodoScreen
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://68163b7232debfe95dbdd500.mockapi.io/academic/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(TodoApiService::class.java)
        val repository = TodoRepository(apiService)
        val viewModel = TodoViewModel(repository)

        setContent {
            TodoScreen(viewModel)
        }
    }
}
```

### 18. Ejecutar la aplicación

1. Compile la aplicación con el botón **Run**.
2. Seleccione un emulador o dispositivo conectado.
3. Espere que termine la compilación.
4. Verifique que la lista de tareas se muestre en la pantalla.

---

## PREVISUALIZACIÓN DE UNA FUNCIÓN COMPOSABLE

Es posible previsualizar el diseño de las funciones composables decorando la función con el atributo `@Preview`.

### 1. Agregue la vista previa a Components.kt

```kotlin
@Preview
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
```

### 2. Visualizar la previsualización

1. Guarde los cambios.
2. Seleccione la opción **Split** en la vista de diseño.
3. Observe simultáneamente el código y la previsualización del componente.

---

## CREAR UNA NUEVA TAREA

Para agregar una nueva tarea desde la aplicación, es necesario ampliar la API y la lógica del ViewModel.

### 1. Modificar TodoApiService

```kotlin
package com.example.todocompose.data.model.network

import com.example.todocompose.data.model.Todo
import retrofit2.http.*

interface TodoApiService {

    @GET("/to-do")
    suspend fun getTodos(): List<Todo>

    @POST("/to-do")
    suspend fun addTodo(@Body todo: Todo): Todo
}
```

### 2. Modificar TodoRepository.kt

```kotlin
package com.example.todocompose.repository

import com.example.todocompose.data.model.Todo
import com.example.todocompose.data.model.network.TodoApiService

class TodoRepository(private val api: TodoApiService) {

    suspend fun fetchTodos(): List<Todo> {
        return api.getTodos()
    }

    suspend fun addTodo(todo: Todo): Todo {
        return api.addTodo(todo)
    }
}
```

### 3. Agregar el método addTodo en TodoViewModel

```kotlin
package com.example.todocompose

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todocompose.data.model.Todo
import com.example.todocompose.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadTodos() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.fetchTodos()
                _todos.value = result
            } catch (e: Exception) {
                Log.e("TodoViewModel", e.message.toString())
                _todos.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun addTodo(title: String, description: String) {
        if (title.isBlank()) return

        val newTodo = Todo(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            done = false
        )

        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.addTodo(newTodo)
                _todos.value += result
            } catch (e: Exception) {
                Log.e("TodoViewModel", e.message.toString())
                _todos.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

}
```

### 4. Modificar TodoScreen para capturar input del usuario

```kotlin
package com.example.todocompose.ui.theme.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todocompose.TodoViewModel

@Composable
fun TodoScreen(viewModel: TodoViewModel) {
    val todos by viewModel.todos.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var titleInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadTodos()
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

        if (loading) {
            CircularProgressIndicator()
        } else {
            TodoList(todos = todos)
        }
    }
}
```

### 5. Guardar y ejecutar la aplicación

1. Guarde todos los cambios.
2. Compile y ejecute la aplicación.
3. Ingrese una nueva tarea desde la pantalla principal.
4. Verifique que la tarea se agregue correctamente a la lista.

---

## IV. BIBLIOGRAFÍA

- Como comenzar con Jetpack Compose  
  <https://developer.android.com/develop/ui/compose/documentation?hl=es-419>

- Instructivo de Jetpack Compose  
  <https://developer.android.com/develop/ui/compose/tutorial?hl=es-419>

- Ejemplos de Compose  
  <https://developer.android.com/develop/ui/compose/samples?hl=es-419>

---

## V. CONCLUSIÓN

Durante esta guía se aprendió a crear una aplicación con Jetpack Compose, estructurar el proyecto con MVVM, consumir servicios REST con Retrofit y presentar la información en una interfaz declarativa. También se exploró la forma de crear componentes reutilizables, manejar estados reactivos y mostrar listas dinámicas en la aplicación.

Esta base es fundamental para el desarrollo de apps modernas en Android, ya que permite trabajar con interfaces más limpias, mantenibles y escalables.
