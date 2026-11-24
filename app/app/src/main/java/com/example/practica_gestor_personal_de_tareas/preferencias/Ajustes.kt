package com.example.practica_gestor_personal_de_tareas.preferencias

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Archivo de preferencias "ajustes". Se declara una sola vez, fuera de cualquier clase.
val Context.ajustes by preferencesDataStore(name = "ajustes")

// Clave de la preferencia "Mostrar solo pendientes"
private val SOLO_PENDIENTES = booleanPreferencesKey("solo_pendientes")

// Lectura: si todavía no existe la clave, el valor inicial es false (mostrar todas)
fun observarSoloPendientes(context: Context): Flow<Boolean> {
    return context.ajustes.data.map { preferencias ->
        preferencias[SOLO_PENDIENTES] ?: false
    }
}

// Escritura: es suspend porque escribe en disco
suspend fun guardarSoloPendientes(context: Context, valor: Boolean) {
    context.ajustes.edit { preferencias ->
        preferencias[SOLO_PENDIENTES] = valor
    }
}
