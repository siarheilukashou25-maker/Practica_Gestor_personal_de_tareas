package com.example.practica_gestor_personal_de_tareas.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.example.practica_gestor_personal_de_tareas.datos.Tarea

// Abre el selector de apps para compartir la tarea como texto.
// Devuelve false si no hay ninguna app compatible.
// OJO: abrir el selector NO significa que se haya compartido.
fun compartirTarea(context: Context, tarea: Tarea): Boolean {
    val texto = "Tarea: ${tarea.titulo}\n" +
            "Descripción: ${tarea.descripcion}\n" +
            "Prioridad: ${tarea.prioridad}"

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, texto)

    return try {
        context.startActivity(Intent.createChooser(intent, "Compartir tarea"))
        true
    } catch (e: ActivityNotFoundException) {
        false
    }
}
