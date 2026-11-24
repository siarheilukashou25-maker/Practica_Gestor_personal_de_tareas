package com.example.practica_gestor_personal_de_tareas.datos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Operaciones permitidas sobre la tabla. Room escribe el código por nosotros.
@Dao
interface TareaDao {

    // Flow: cada vez que cambia la tabla, llega la lista nueva
    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun observarTodas(): Flow<List<Tarea>>

    @Insert
    suspend fun insertar(tarea: Tarea)

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)
}
