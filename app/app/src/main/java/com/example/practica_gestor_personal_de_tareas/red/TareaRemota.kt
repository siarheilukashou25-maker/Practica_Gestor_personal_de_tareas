package com.example.practica_gestor_personal_de_tareas.red

// Una tarea tal como llega del servidor JSONPlaceholder.
// Los nombres coinciden con los del JSON para que Gson la convierta sola.
data class TareaRemota(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)
