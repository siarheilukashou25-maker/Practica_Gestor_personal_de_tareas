package com.example.practica_gestor_personal_de_tareas.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Tarea::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tareaDao(): TareaDao

    // companion object = la parte "static" de la clase
    companion object {
        private var instancia: AppDatabase? = null

        // Devuelve siempre la misma base de datos (patrón singleton)
        fun obtener(context: Context): AppDatabase {
            if (instancia == null) {
                instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tareas.db"
                ).build()
            }
            return instancia!!
        }
    }
}
