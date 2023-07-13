package br.com.fusiondms.core.datastore.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import br.com.fusiondms.core.datastore.chaves.DataStoreChaves
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_APROVACAO
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_CORTE
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_EXCLUIR
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_EXIGIR_EM_COLETA
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_OBRIGATORIO
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_QUALIDADE
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_ADIAR_TODAS
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_EVENTO_CLIENTES
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_INDICAR_COLETA
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_POR_CLIENTE
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_SOLICITAR_COLETA
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_TEMPO_ESPERA
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_LOCALIZACAO_TEMPO_ENVIO
import br.com.fusiondms.core.model.parametros.Parametros
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PREFERENCES_NAME = "fusion_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class DataStoreRepositoryImpl @Inject constructor(
    private val context: Context
) : DataStoreRepository {

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

    override suspend fun putLocation(
        currentLocation: String,
        latitude: String,
        longitude: String,
        velocidade: Int
    ) {
        putString(DataStoreChaves.KEY_CURRENT_LOCATION, currentLocation)
        putString(DataStoreChaves.KEY_CURRENT_LATITUDE, latitude)
        putString(DataStoreChaves.KEY_CURRENT_LONGITUDE, longitude)
        putInt(DataStoreChaves.KEY_CURRENT_SPEED, velocidade)
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

    override fun getCurrentLocation(chave: String): Flow<String?> {
        return try {
            val preferencesKey = stringPreferencesKey(chave)
            context.dataStore.data.map { preferences ->
                preferences[preferencesKey]
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyFlow()
        }
    }

    override fun getCurrentVelocidade(chave: String): Flow<Int?> {
        return try {
            val preferencesKey = intPreferencesKey(chave)
            context.dataStore.data.map { preferences ->
                preferences[preferencesKey]
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyFlow()
        }
    }

    override suspend fun getLastLocation(chave: String): String? {
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

    override suspend fun getParametros(): Parametros? {
        return try {
            Parametros(
                canhotoObrigatorio = getBoolean(KEY_CANHOTO_OBRIGATORIO) ?: false,
                canhotoAprovacao = getBoolean(KEY_CANHOTO_APROVACAO) ?: false,
                canhotoQualidade = getBoolean(KEY_CANHOTO_QUALIDADE) ?: false,
                canhotoCorte = getBoolean(KEY_CANHOTO_CORTE) ?: false,
                canhotoExigirEmColeta = getBoolean(KEY_CANHOTO_EXIGIR_EM_COLETA) ?: false,
                canhotoExcluir = getBoolean(KEY_CANHOTO_EXCLUIR) ?: false,
                entregasPorCliente = getBoolean(KEY_ENTREGAS_POR_CLIENTE) ?: false,
                entregasAdiarTodas = getBoolean(KEY_ENTREGAS_ADIAR_TODAS) ?: false,
                entregasIndicarColeta = getBoolean(KEY_ENTREGAS_INDICAR_COLETA) ?: false,
                entregasSolicitarColeta = getBoolean(KEY_ENTREGAS_SOLICITAR_COLETA) ?: false,
                entregasEventoClientes = getBoolean(KEY_ENTREGAS_EVENTO_CLIENTES) ?: false,
                entregasTempoEspera = getString(KEY_ENTREGAS_TEMPO_ESPERA) ?: "4",
                localizacaoTempoEnvio = getInt(KEY_LOCALIZACAO_TEMPO_ENVIO) ?: 20
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}