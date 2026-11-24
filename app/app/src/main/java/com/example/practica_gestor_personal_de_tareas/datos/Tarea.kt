package com.example.practica_gestor_personal_de_tareas.datos

import androidx.room.Entity
import androidx.room.PrimaryKey

// Las tres prioridades permitidas
val PRIORIDADES = listOf("Baja", "Media", "Alta")

// Modelo de una tarea y, a la vez, tabla "tareas" de Room
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,            // 0 = tarea nueva, Room genera el id real
    val titulo: String,
    val descripcion: String,
    val prioridad: String,       // "Baja", "Media" o "Alta"
    val completada: Boolean = false
)
