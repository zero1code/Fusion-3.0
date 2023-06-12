package br.com.fusiondms.core.datastore.repository

import br.com.fusiondms.core.model.parametros.Parametros
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun putString(chave: String, valor: String)
    suspend fun putInt(chave: String, valor: Int)
    suspend fun putBoolean(chave: String, valor: Boolean)
    suspend fun putLocation(currentLocation: String, latitude: String, longitude: String)
    suspend fun getString(chave: String): String?
    suspend fun getInt(chave: String): Int?
    suspend fun getBoolean(chave: String): Boolean?
    fun getCurrentLocation(chave: String) : Flow<String?>
    suspend fun getParametros() : Parametros?
}