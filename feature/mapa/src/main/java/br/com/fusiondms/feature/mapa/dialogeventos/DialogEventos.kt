package br.com.fusiondms.feature.mapa.dialogeventos

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.model.entrega.Acao
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.model.entrega.Evento
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_ADIADA
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_CHEGADA_CLIENTE
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_DEVOLVIDA
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_DEVOLVIDA_PARCIAL
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_DEVOLVIDA_TOTAL
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_INICIADA
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_PENDENTE
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_REALIZADA
import br.com.fusiondms.feature.mapa.databinding.DialogEventosBinding
import br.com.fusiondms.feature.mapa.dialogeventos.adapter.AcoesAdapter
import br.com.fusiondms.feature.mapa.dialogeventos.adapter.EventosAdapter
import br.com.fusiondms.feature.mapa.dialogeventos.interfaces.IDialogEventos
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogEventos(
    private val entrega: Entrega, context: Context) : BottomSheetDialogFragment() {
    private var _binding: DialogEventosBinding? = null
    private val binding get() = _binding!!

    private lateinit var onEventoClick : IDialogEventos

    private val listaEvento = arrayListOf(
        Evento(
            context.getString(R.string.label_cheguei_no_cliente),
            R.drawable.ic_local_cliente,
            ENTREGA_CHEGADA_CLIENTE
        ),
        Evento(
            context.getString(R.string.label_entrega_iniciada),
            R.drawable.ic_caminhao,
            ENTREGA_INICIADA
        ),
        Evento(
            context.getString(R.string.label_entrega_realizada),
            R.drawable.ic_check_circle,
            ENTREGA_REALIZADA
        ),
        Evento(
            context.getString(R.string.label_cliente_devolveu),
            R.drawable.ic_devolution,
            ENTREGA_DEVOLVIDA
        ),
        Evento(
            context.getString(R.string.label_adiar_entrega),
            R.drawable.ic_more_time,
            ENTREGA_ADIADA
        )
    )

    private val listaAcao = arrayListOf(
        Acao(
            context.getString(R.string.label_volumes),
            R.drawable.ic_volume,
            9
        ),
        Acao(
            context.getString(R.string.label_enviar_nf_email),
            R.drawable.ic_email,
            10
        ),
        Acao(
            context.getString(R.string.label_alarme_chegada),
            R.drawable.ic_notificacao_ativa,
            11
        ),
        Acao(
            context.getString(R.string.label_enviar_foto),
            R.drawable.ic_camera,
            12
        ),
        Acao(
            context.getString(R.string.label_solicitar_ligacao),
            R.drawable.ic_ligacao,
            13
        ),
        Acao(
            context.getString(R.string.label_tirar_foto_canhoto),
            R.drawable.ic_canhoto,
            14
        )
    )

    private val adapterEventos by lazy { EventosAdapter() }
    private val adapterAcoes by lazy { AcoesAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEventosBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)

        binding.apply {
            tvNotaFiscal.text = Html.fromHtml(getString(R.string.label_nf_pernota, entrega.numeroNotaFiscal), 0)
            rvEventos.adapter = adapterEventos
            rvEventos.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

            rvAcoes.adapter = adapterAcoes
            rvAcoes.layoutManager = GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false)
        }

        checarStatusEntrega(entrega.statusEntrega.toInt())
        bindListeners()
    }

    private fun checarStatusEntrega(statusEntrega: Int) {
        when(statusEntrega) {
            ENTREGA_PENDENTE -> {
                listaEvento.removeAll {
                    it.idEvento > ENTREGA_CHEGADA_CLIENTE
                }
            }
            ENTREGA_CHEGADA_CLIENTE -> {
                listaEvento[0].isCliked = true
                listaEvento.removeAll {
                    it.idEvento in ENTREGA_REALIZADA..ENTREGA_ADIADA
                }
            }
            ENTREGA_INICIADA -> {
                listaEvento[0].isCliked = true
                listaEvento[1].isCliked = true
            }
            ENTREGA_REALIZADA -> {
                listaEvento[0].isCliked = true
                listaEvento[1].isCliked = true
                listaEvento[2].isCliked = true
                listaEvento.removeAll {
                    it.idEvento > ENTREGA_REALIZADA
                }
            }
            ENTREGA_DEVOLVIDA_PARCIAL, ENTREGA_DEVOLVIDA_TOTAL -> {
                listaEvento[0].isCliked = true
                listaEvento[1].isCliked = true
                listaEvento[3].isCliked = true
                listaEvento.removeAll {
                    it.idEvento == ENTREGA_REALIZADA || it.idEvento == ENTREGA_ADIADA
                }
            }
            ENTREGA_ADIADA -> {
                listaEvento[0].isCliked = false
                listaEvento[4].isCliked = true
                listaEvento.removeAll {
                    it.idEvento in ENTREGA_INICIADA..ENTREGA_DEVOLVIDA
                }
            }
        }

        adapterEventos.submitList(listaEvento)
        adapterAcoes.submitList(listaAcao)
    }

    private fun bindListeners() {
        adapterEventos.onEventoClickListener = { idEvento ->
            onEventoClick.onEventoClick(idEvento)
            dismiss()
        }
        adapterAcoes.onAcaoClickListener = { idAcao ->
            onEventoClick.onEventoClick(idAcao)
            dismiss()
        }
    }

    fun initListener(iDialogEventos: IDialogEventos) {
        onEventoClick = iDialogEventos
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "Dialog"
    }
}