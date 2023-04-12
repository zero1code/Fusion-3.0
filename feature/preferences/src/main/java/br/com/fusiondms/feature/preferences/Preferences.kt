package br.com.fusiondms.feature.preferences

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.*
import br.com.fusiondms.core.common.R.*
import br.com.fusiondms.core.datastore.repository.DataStoreRepository
import br.com.fusiondms.core.datastore.repository.DataStoreRepositoryImpl
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.CATEGORIA_CANHOTO
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.CATEGORIA_ENTREGAS
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.CATEGORIA_LOCALIZACAO
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_APROVACAO
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_CORTE
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_ADIAR_TODAS
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_EVENTO_CLIENTES
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_INDICAR_COLETA
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_POR_CLIENTE
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_SOLICITAR_COLETA
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_EXCLUIR
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_EXIGIR_EM_COLETA
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_LOCALIZACAO_TEMPO_ENVIO
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_OBRIGATORIO
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_QUALIDADE
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_TEMPO_ESPERA
import br.com.fusiondms.core.model.parametros.Parametros
import kotlinx.coroutines.runBlocking

class Preferences : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var parametros: Parametros

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        bindDataStore()
        bindPreferencesScreen()
    }

    private fun bindDataStore() {
        runBlocking {
            dataStoreRepository = DataStoreRepositoryImpl(this@Preferences.requireContext())
            parametros = dataStoreRepository.getParametros() ?: Parametros()
        }
    }

    private fun bindPreferencesScreen() {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)
        preferenceScreen = screen

        bindPrefencesCanhoto(context, screen)
        bindPrefencesEntrega(context, screen)
        bindPrefencesLocalizacao(context, screen)
    }

    private fun bindPrefencesCanhoto(context: Context, screen: PreferenceScreen) {
        val categoriaCanhotoExpansivel = PreferenceCategory(context).apply {
            key = CATEGORIA_CANHOTO
            title = context.getString(string.label_canhoto_categoria)
            initialExpandedChildrenCount = 3
        }
        screen.addPreference(categoriaCanhotoExpansivel)

        val prefObrigatorioCanhoto = SwitchPreference(context).apply {
            key = KEY_CANHOTO_OBRIGATORIO
            title = context.getString(string.label_canhoto_obrigatorio)
            summary = context.getString(string.label_canhoto_obrigatorio_resumo)
            setDefaultValue(parametros.canhotoObrigatorio)
            icon = ContextCompat.getDrawable(context, drawable.ic_camera)
            isSingleLineTitle = true
        }
        prefObrigatorioCanhoto.onPreferenceChangeListener = this@Preferences

        val prefAprovacaoCanhoto = SwitchPreference(context).apply {
            key = KEY_CANHOTO_APROVACAO
            title = context.getString(string.label_canhoto_aprovacao)
            summary = context.getString(string.label_canhoto_aprovacao_resumo)
            setDefaultValue(parametros.canhotoAprovacao)
            icon = ContextCompat.getDrawable(context, drawable.ic_check)
            isSingleLineTitle = true
        }
        prefAprovacaoCanhoto.onPreferenceChangeListener = this@Preferences

        val prefQualidadeCanhoto = SwitchPreference(context).apply {
            key = KEY_CANHOTO_QUALIDADE
            title = context.getString(string.label_canhoto_qualidade)
            summary = context.getString(string.label_canhoto_qualidade_resumo)
            setDefaultValue(parametros.canhotoQualidade)
            icon = ContextCompat.getDrawable(context, drawable.ic_qualidade_imagem)
            isSingleLineTitle = true
        }
        prefQualidadeCanhoto.onPreferenceChangeListener = this@Preferences

        val prefCorteCanhoto = SwitchPreference(context).apply {
            key = KEY_CANHOTO_CORTE
            title = context.getString(string.label_canhoto_recorte)
            summary = context.getString(string.label_canhoto_recorte_resumo)
            setDefaultValue(parametros.canhotoCorte)
            icon = ContextCompat.getDrawable(context, drawable.ic_ajustar_imagem)
            isSingleLineTitle = true
        }
        prefCorteCanhoto.onPreferenceChangeListener = this@Preferences

        val prefExigirCanhotoColeta = SwitchPreference(context).apply {
            key = KEY_CANHOTO_EXIGIR_EM_COLETA
            title = context.getString(string.label_canhoto_em_coleta)
            summary = context.getString(string.label_canhoto_em_coleta_resumo)
            setDefaultValue(parametros.canhotoExigirEmColeta)
            isSingleLineTitle = true
        }
        prefExigirCanhotoColeta.onPreferenceChangeListener = this@Preferences

        val prefExcluirCanhoto = SwitchPreference(context).apply {
            key = KEY_CANHOTO_EXCLUIR
            title = context.getString(string.label_canhoto_excluir)
            summary = context.getString(string.label_canhoto_excluir_resumo)
            setDefaultValue(parametros.canhotoExcluir)
            icon = ContextCompat.getDrawable(context, drawable.ic_lixeira)
            isSingleLineTitle = true
        }
        prefExcluirCanhoto.onPreferenceChangeListener = this@Preferences

        categoriaCanhotoExpansivel.apply {
            addPreference(prefObrigatorioCanhoto)
            addPreference(prefAprovacaoCanhoto)
            addPreference(prefQualidadeCanhoto)
            addPreference(prefCorteCanhoto)
            addPreference(prefExigirCanhotoColeta)
            addPreference(prefExcluirCanhoto)
        }
    }

    private fun bindPrefencesEntrega(context: Context, screen: PreferenceScreen) {

        val categoriaEntregasExpansivel = PreferenceCategory(context).apply {
            key = CATEGORIA_ENTREGAS
            title = context.getString(string.label_entregas_categoria)
            initialExpandedChildrenCount = 3
        }
        screen.addPreference(categoriaEntregasExpansivel)

        val prefEntregasPorCliente = SwitchPreference(context).apply {
            key = KEY_ENTREGAS_POR_CLIENTE
            title = context.getString(string.label_entregas_eventos_nota)
            summary = context.getString(string.label_entregas_eventos_nota_resumo)
            setDefaultValue(parametros.entregasPorCliente)
            isSingleLineTitle = true
        }
        prefEntregasPorCliente.onPreferenceChangeListener = this@Preferences
        screen.addPreference(prefEntregasPorCliente)

        val prefEntregasAdiarTodas = SwitchPreference(context).apply {
            key = KEY_ENTREGAS_ADIAR_TODAS
            title = context.getString(string.label_entregas_adiar)
            summary = context.getString(string.label_entregas_adiar_resumo)
            setDefaultValue(parametros.entregasAdiarTodas)
            isSingleLineTitle = true
        }
        prefEntregasAdiarTodas.onPreferenceChangeListener = this@Preferences
        screen.addPreference(prefEntregasAdiarTodas)

        val prefEntregasIndicarColeta = SwitchPreference(context).apply {
            key = KEY_ENTREGAS_INDICAR_COLETA
            title = context.getString(string.label_entregas_indicar_coleta)
            summary = context.getString(string.label_entregas_indicar_coleta_resumo)
            setDefaultValue(parametros.entregasIndicarColeta)
            isSingleLineTitle = true
        }
        prefEntregasIndicarColeta.onPreferenceChangeListener = this@Preferences
        screen.addPreference(prefEntregasIndicarColeta)

        val prefEntregasSolicitarColeta = SwitchPreference(context).apply {
            key = KEY_ENTREGAS_SOLICITAR_COLETA
            title = context.getString(string.label_entregas_solicitar_coleta)
            summary = context.getString(string.label_entregas_solicitar_coleta_resumo)
            setDefaultValue(parametros.entregasSolicitarColeta)
            isSingleLineTitle = true
        }
        prefEntregasSolicitarColeta.onPreferenceChangeListener = this@Preferences
        screen.addPreference(prefEntregasSolicitarColeta)

        val prefEntregasEventoEmClientes = SwitchPreference(context).apply {
            key = KEY_ENTREGAS_EVENTO_CLIENTES
            title = context.getString(string.label_entregas_eventos_clientes)
            summary = context.getString(string.label_entregas_eventos_clientes_resumo)
            setDefaultValue(parametros.entregasEventoClientes)
            isSingleLineTitle = true
        }
        prefEntregasEventoEmClientes.onPreferenceChangeListener = this@Preferences
        screen.addPreference(prefEntregasEventoEmClientes)

        val tipos = arrayOf("3" to "Entrega Iniciada", "4" to "Entrega Realizada")
        val prefEntregasTempoEspera = ListPreference(context).apply {
            val tipoEscolhido = tipos.find { it.first == parametros.entregasTempoEspera }?.second
            key = KEY_ENTREGAS_TEMPO_ESPERA
            title = context.getString(string.label_entregas_tempo_espera)
            summary = context.getString(string.label_entregas_tempo_espera_resumo, tipoEscolhido)
            dialogTitle = context.getString(string.label_entregas_tempo_espera_resumo2)
            icon = ContextCompat.getDrawable(context, drawable.ic_timer_off)
            entries = arrayOf(tipos[0].second, tipos[1].second)
            entryValues = arrayOf(tipos[0].first, tipos[1].first)
            isSingleLineTitle = true
        }
        prefEntregasTempoEspera.setOnPreferenceChangeListener { preference, newValue ->
            val tipoEscolhido = tipos.find { it.first == newValue }?.second
            preference.summary = context.getString(string.label_entregas_tempo_espera_resumo, tipoEscolhido)
            atualizarPreferencia(preference.key, newValue)
            true
        }
        screen.addPreference(prefEntregasTempoEspera)
    }

    private fun bindPrefencesLocalizacao(context: Context, screen: PreferenceScreen) {

        val categoriaLocalizacao = PreferenceCategory(context).apply {
            key = CATEGORIA_LOCALIZACAO
            title = context.getString(string.label_localizacao_categoria)
        }
        screen.addPreference(categoriaLocalizacao)

        var tempoDefinido = parametros.localizacaoTempoEnvio
        val prefTempoEnvioLocal = SeekBarPreference(context).apply {
            key = KEY_LOCALIZACAO_TEMPO_ENVIO
            title = context.getString(string.label_localizacao_tempo_envio)
            icon = ContextCompat.getDrawable(context, drawable.ic_location)
            min = 20
            max = 60
            tempoDefinido = if (tempoDefinido < min) min else tempoDefinido
            tempoDefinido = if (tempoDefinido > max) max else tempoDefinido
            summary = context.getString(string.label_localizacao_tempo_envio_resumo, tempoDefinido)
            showSeekBarValue = true
            setDefaultValue(tempoDefinido)
        }
        screen.addPreference(prefTempoEnvioLocal)
        prefTempoEnvioLocal.value = tempoDefinido

        prefTempoEnvioLocal.setOnPreferenceChangeListener { preference, newValue ->
            preference.summary = context.getString(string.label_localizacao_tempo_envio_resumo, newValue as Int)
            preference.setDefaultValue(newValue)
            atualizarPreferencia(preference.key, newValue)
            true
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        atualizarPreferencia(preference.key, newValue)
        return true
    }

    private fun atualizarPreferencia(prefenceKey: String, value: Any?) {
        runBlocking {
            try {
                when (value) {
                    is Boolean -> dataStoreRepository.putBoolean(prefenceKey, value)
                    is Int -> dataStoreRepository.putInt(prefenceKey, value)
                    is String -> dataStoreRepository.putString(prefenceKey, value)
                    else -> throw IllegalArgumentException(requireContext().getString(string.label_erro_salvar_preferencia, value?.javaClass))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}