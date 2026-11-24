package com.example.practica_gestor_personal_de_tareas.ui

import android.database.sqlite.SQLiteException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.practica_gestor_personal_de_tareas.datos.PRIORIDADES
import com.example.practica_gestor_personal_de_tareas.datos.Tarea
import com.example.practica_gestor_personal_de_tareas.datos.TareaDao
import kotlinx.coroutines.launch

// Validaciones: devuelven el texto del error, o null si todo está bien
fun validarTitulo(titulo: String): String? {
    val limpio = titulo.trim()
    if (limpio.isEmpty()) return "El título no puede estar vacío"
    if (limpio.length < 3) return "El título debe tener al menos 3 caracteres"
    return null
}

fun validarPrioridad(prioridad: String): String? {
    if (prioridad in PRIORIDADES) return null
    return "Elige una prioridad"
}

// Formulario para crear (tarea == null) o editar (tarea != null)
@Composable
fun PantallaFormulario(
    tarea: Tarea?,
    dao: TareaDao,
    alTerminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // Campos del formulario. Si editamos, empiezan con los datos actuales.
    var titulo by rememberSaveable { mutableStateOf(tarea?.titulo ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(tarea?.descripcion ?: "") }
    var prioridad by rememberSaveable { mutableStateOf(tarea?.prioridad ?: "") }

    var intento by rememberSaveable { mutableStateOf(false) }   // ¿ya se pulsó Guardar?
    var guardando by remember { mutableStateOf(false) }         // evita guardados dobles
    var errorGuardar by remember { mutableStateOf<String?>(null) }

    val errorTitulo = validarTitulo(titulo)
    val errorPrioridad = validarPrioridad(prioridad)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (tarea == null) "Nueva tarea" else "Editar tarea",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            singleLine = true,
            isError = intento && errorTitulo != null,
            supportingText = {
                if (intento && errorTitulo != null) Text(errorTitulo)
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        SelectorPrioridad(
            prioridad = prioridad,
            alElegir = { prioridad = it }
        )
        if (intento && errorPrioridad != null) {
            Text(errorPrioridad, color = MaterialTheme.colorScheme.error)
        }

        val textoErrorGuardar = errorGuardar
        if (textoErrorGuardar != null) {
            Text(textoErrorGuardar, color = MaterialTheme.colorScheme.error)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                enabled = !guardando,
                onClick = {
                    intento = true
                    if (errorTitulo == null && errorPrioridad == null && !guardando) {
                        guardando = true
                        scope.launch {
                            try {
                                if (tarea == null) {
                                    dao.insertar(
                                        Tarea(
                                            titulo = titulo.trim(),
                                            descripcion = descripcion.trim(),
                                            prioridad = prioridad
                                        )
                                    )
                                } else {
                                    // copy conserva el id: Room sabe qué fila cambiar
                                    dao.actualizar(
                                        tarea.copy(
                                            titulo = titulo.trim(),
                                            descripcion = descripcion.trim(),
                                            prioridad = prioridad
                                        )
                                    )
                                }
                                alTerminar()
                            } catch (e: SQLiteException) {
                                errorGuardar = "No se pudo guardar la tarea"
                            } finally {
                                guardando = false
                            }
                        }
                    }
                }
            ) {
                Text("Guardar")
            }

            OutlinedButton(onClick = alTerminar) {
                Text("Cancelar")
            }
        }
    }
}

// Botón que abre un menú desplegable con Baja / Media / Alta
@Composable
fun SelectorPrioridad(prioridad: String, alElegir: (String) -> Unit) {
    var abierto by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { abierto = true }) {
            if (prioridad == "") {
                Text("Elegir prioridad")
            } else {
                Text("Prioridad: $prioridad")
            }
        }
        DropdownMenu(
            expanded = abierto,
            onDismissRequest = { abierto = false }
        ) {
            for (opcion in PRIORIDADES) {
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        alElegir(opcion)
                        abierto = false
                    }
                )
            }
        }
    }
}
