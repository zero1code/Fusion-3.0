package br.com.fusiondms.core.datastore.repository

interface DataStoreRepository {
    suspend fun putString(chave: String, valor: String)
    suspend fun putInt(chave: String, valor: Int)
    suspend fun putBoolean(chave: String, valor: Boolean)
    suspend fun getString(chave: String): String?
    suspend fun getInt(chave: String): Int?
    suspend fun getBoolean(chave: String): Boolean?
}