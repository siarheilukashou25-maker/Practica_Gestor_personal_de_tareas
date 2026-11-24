package com.example.practica_gestor_personal_de_tareas.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.practica_gestor_personal_de_tareas.R
import com.example.practica_gestor_personal_de_tareas.datos.Tarea
import com.example.practica_gestor_personal_de_tareas.datos.TareaDao
import com.example.practica_gestor_personal_de_tareas.preferencias.guardarSoloPendientes
import com.example.practica_gestor_personal_de_tareas.preferencias.observarSoloPendientes
import kotlinx.coroutines.launch

@Composable
fun PantallaLista(
    tareas: List<Tarea>,
    dao: TareaDao,
    alCrear: () -> Unit,
    alEditar: (Tarea) -> Unit,
    alAbrirApi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Preferencia "Mostrar solo pendientes" guardada en DataStore
    val flujoFiltro = remember { observarSoloPendientes(context) }
    val soloPendientes by flujoFiltro.collectAsState(initial = false)

    // Tarea que el usuario quiere borrar. null = no hay diálogo abierto
    var tareaABorrar by remember { mutableStateOf<Tarea?>(null) }

    val tareasVisibles = if (soloPendientes) {
        tareas.filter { !it.completada }
    } else {
        tareas
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_tarea),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Mis tareas", style = MaterialTheme.typography.headlineSmall)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = soloPendientes,
                onCheckedChange = { valor ->
                    scope.launch { guardarSoloPendientes(context, valor) }
                }
            )
            Spacer(Modifier.width(8.dp))
            Text("Mostrar solo pendientes")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = alCrear) { Text("Nueva tarea") }
            OutlinedButton(onClick = alAbrirApi) { Text("Tareas de ejemplo") }
        }

        if (tareasVisibles.isEmpty()) {
            if (tareas.isEmpty()) {
                Text("Todavía no hay tareas. Pulsa «Nueva tarea».")
            } else {
                Text("No hay tareas pendientes.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareasVisibles, key = { it.id }) { tarea ->
                    FilaTarea(
                        tarea = tarea,
                        alCambiarCompletada = { marcada ->
                            scope.launch { dao.actualizar(tarea.copy(completada = marcada)) }
                        },
                        alEditar = { alEditar(tarea) },
                        alCompartir = {
                            val abierto = compartirTarea(context, tarea)
                            if (!abierto) {
                                Toast.makeText(context, "No hay ninguna aplicación para compartir", Toast.LENGTH_SHORT).show()
                            }
                        },
                        alEliminar = { tareaABorrar = tarea }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación antes de eliminar
    val tarea = tareaABorrar
    if (tarea != null) {
        AlertDialog(
            onDismissRequest = { tareaABorrar = null },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Seguro que quieres eliminar «${tarea.titulo}»?") },
            confirmButton = {
                TextButton(onClick = {
                    tareaABorrar = null
                    scope.launch { dao.eliminar(tarea) }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { tareaABorrar = null }) { Text("Cancelar") }
            }
        )
    }
}

// Una fila de la lista: título, prioridad, estado y acciones
@Composable
fun FilaTarea(
    tarea: Tarea,
    alCambiarCompletada: (Boolean) -> Unit,
    alEditar: () -> Unit,
    alCompartir: () -> Unit,
    alEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = tarea.completada,
                    onCheckedChange = alCambiarCompletada
                )
                Text(tarea.titulo, style = MaterialTheme.typography.titleMedium)
            }

            val estado = if (tarea.completada) "Completada" else "Pendiente"
            Text("Prioridad: ${tarea.prioridad} · $estado")

            Row {
                TextButton(onClick = alEditar) { Text("Editar") }
                TextButton(onClick = alCompartir) { Text("Compartir") }
                TextButton(onClick = alEliminar) { Text("Eliminar") }
            }
        }
    }
}
