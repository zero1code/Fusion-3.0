package br.com.fusiondms.core.datastore.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private const val PREFERENCES_NAME = "fusion_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class DataStoreRepositoryImpl @Inject constructor(
    private val context: Context
) : DataStoreRepository {

    private object PreferencesKeys {
        val LAT = doublePreferencesKey("lat")
        val LON = doublePreferencesKey("lon")
    }

    val preferencesFlow: Flow<Teste> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val lat = preferences[PreferencesKeys.LAT] ?: 0.0
            val lon = preferences[PreferencesKeys.LON] ?: 0.0
            Teste(lat, lon)
        }

    override suspend fun putString(chave: String, valor: String) {
        val preferencesKey = stringPreferencesKey(chave)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = valor
        }
    }

    override suspend fun putInt(chave: String, valor: Int) {
        val preferencesKey = intPreferencesKey(chave)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = valor
        }
    }

    override suspend fun putBoolean(chave: String, valor: Boolean) {
        val preferencesKey = booleanPreferencesKey(chave)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = valor
        }
    }

    override suspend fun getString(chave: String): String? {
        return try {
            val preferencesKey = stringPreferencesKey(chave)
            context.dataStore.data.map { preferences ->
                preferences[preferencesKey]
            }.first()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getInt(chave: String): Int? {
        return try {
            val preferencesKey = intPreferencesKey(chave)
            context.dataStore.data.map { preferences ->
                preferences[preferencesKey] ?: 0
            }.first()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getBoolean(chave: String): Boolean? {
        return try {
            val preferencesKey = booleanPreferencesKey(chave)
            context.dataStore.data.map { preferences ->
                preferences[preferencesKey]
            }.first()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class Teste(val lat: Double, val lon: Double)