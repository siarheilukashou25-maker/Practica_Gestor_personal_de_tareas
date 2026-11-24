package com.example.practica_gestor_personal_de_tareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.practica_gestor_personal_de_tareas.datos.AppDatabase
import com.example.practica_gestor_personal_de_tareas.datos.TareaDao
import com.example.practica_gestor_personal_de_tareas.ui.PantallaApi
import com.example.practica_gestor_personal_de_tareas.ui.PantallaFormulario
import com.example.practica_gestor_personal_de_tareas.ui.PantallaLista

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtenemos el DAO una sola vez y lo pasamos a las pantallas
        val dao = AppDatabase.obtener(applicationContext).tareaDao()

        setContent {
            MaterialTheme {
                Scaffold { padding ->
                    App(dao, Modifier.padding(padding))
                }
            }
        }
    }
}

// Navegación sencilla basada en estado: una variable decide qué pantalla se ve
@Composable
fun App(dao: TareaDao, modifier: Modifier = Modifier) {
    // "lista", "formulario" o "api"
    var pantalla by rememberSaveable { mutableStateOf("lista") }
    // id de la tarea que se está editando. 0 = tarea nueva
    var idEditando by rememberSaveable { mutableLongStateOf(0L) }

    // Las tareas llegan de Room y se actualizan solas
    val flujoTareas = remember { dao.observarTodas() }
    val tareas by flujoTareas.collectAsState(initial = emptyList())

    // Botón "atrás" del sistema: desde cualquier pantalla vuelve a la lista
    BackHandler(enabled = pantalla != "lista") {
        pantalla = "lista"
    }

    if (pantalla == "lista") {
        PantallaLista(
            tareas = tareas,
            dao = dao,
            alCrear = {
                idEditando = 0L
                pantalla = "formulario"
            },
            alEditar = { tarea ->
                idEditando = tarea.id
                pantalla = "formulario"
            },
            alAbrirApi = { pantalla = "api" },
            modifier = modifier
        )
    } else if (pantalla == "formulario") {
        // null si es una tarea nueva
        val tareaEditando = tareas.find { it.id == idEditando }
        PantallaFormulario(
            tarea = tareaEditando,
            dao = dao,
            alTerminar = { pantalla = "lista" },
            modifier = modifier
        )
    } else {
        PantallaApi(
            alVolver = { pantalla = "lista" },
            modifier = modifier
        )
    }
}
