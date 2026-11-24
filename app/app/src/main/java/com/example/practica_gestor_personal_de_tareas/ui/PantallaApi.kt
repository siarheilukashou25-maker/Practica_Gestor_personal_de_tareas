package com.example.practica_gestor_personal_de_tareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.practica_gestor_personal_de_tareas.red.ServicioTareas
import com.example.practica_gestor_personal_de_tareas.red.TareaRemota
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// Pantalla "Tareas de ejemplo": datos del servidor, NO se guardan en Room
@Composable
fun PantallaApi(alVolver: () -> Unit, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    // null = todavía no se ha consultado
    var tareasRemotas by remember { mutableStateOf<List<TareaRemota>?>(null) }

    fun cargar() {
        if (cargando) return       // evita dos peticiones a la vez
        cargando = true
        error = null
        scope.launch {
            try {
                tareasRemotas = ServicioTareas.api.listarTareas(1)
            } catch (e: IOException) {
                error = "No se pudo conectar con el servidor. Comprueba tu conexión."
            } catch (e: HttpException) {
                error = "El servidor respondió con un error (${e.code()})."
            } finally {
                cargando = false
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Tareas de ejemplo (servidor)", style = MaterialTheme.typography.headlineSmall)
        Text("Estas tareas vienen de JSONPlaceholder. No son tus tareas personales y no se guardan.")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { cargar() }, enabled = !cargando) { Text("Cargar tareas") }
            OutlinedButton(onClick = alVolver) { Text("Volver") }
        }

        val textoError = error
        val lista = tareasRemotas

        if (cargando) {
            CircularProgressIndicator()
        } else if (textoError != null) {
            Text(textoError, color = MaterialTheme.colorScheme.error)
            Button(onClick = { cargar() }) { Text("Reintentar") }
        } else if (lista == null) {
            Text("Pulsa «Cargar tareas» para consultar el servidor.")
        } else if (lista.isEmpty()) {
            Text("El servidor no ha devuelto ninguna tarea.")
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(lista, key = { it.id }) { tareaRemota ->
                    val estado = if (tareaRemota.completed) "Completada" else "Pendiente"
                    Text(
                        text = "#${tareaRemota.id} · ${tareaRemota.title} · $estado",
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}
