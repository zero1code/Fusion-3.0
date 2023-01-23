package br.com.fusiondms.modmapa.dialogeventos

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
import br.com.fusiondms.modcommon.R
import br.com.fusiondms.modmapa.databinding.DialogEventosBinding
import br.com.fusiondms.modmapa.dialogeventos.adapter.AcoesAdapter
import br.com.fusiondms.modmapa.dialogeventos.adapter.EventosAdapter
import br.com.fusiondms.modmapa.dialogeventos.interfaces.IDialogEventos
import br.com.fusiondms.modmodel.entrega.Acao
import br.com.fusiondms.modmodel.entrega.Entrega
import br.com.fusiondms.modmodel.entrega.Evento
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
            1
        ),
        Evento(
            context.getString(R.string.label_entrega_iniciada),
            R.drawable.ic_caminhao,
            2
        ),
        Evento(
            context.getString(R.string.label_entrega_realizada),
            R.drawable.ic_check_circle,
            3
        ),
        Evento(
            context.getString(R.string.label_cliente_devolveu),
            R.drawable.ic_devolution,
            4
        ),
        Evento(
            context.getString(R.string.label_adiar_entrega),
            R.drawable.ic_more_time,
            5
        )
    )

    private val listaAcao = arrayListOf(
        Acao(
            context.getString(R.string.label_volumes),
            R.drawable.ic_volume,
            1
        ),
        Acao(
            context.getString(R.string.label_enviar_nf_email),
            R.drawable.ic_email,
            2
        ),
        Acao(
            context.getString(R.string.label_alarme_chegada),
            R.drawable.ic_notificacao_ativa,
            3
        ),
        Acao(
            context.getString(R.string.label_enviar_foto),
            R.drawable.ic_camera,
            4
        ),
        Acao(
            context.getString(R.string.label_solicitar_ligacao),
            R.drawable.ic_ligacao,
            5
        ),
        Acao(
            context.getString(R.string.label_tirar_foto_canhoto),
            R.drawable.ic_canhoto,
            6
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

        checarStatusEntrega(entrega.statusEntrega)
        bindListeners()
    }

    private fun checarStatusEntrega(statusEntrega: String) {
        when(statusEntrega.toInt()) {
            0 -> {
                listaEvento.removeAll {
                    it.idEvento > 1
                }
            }
            1 -> {
                listaEvento[0].isCliked = true
                listaEvento.removeAll {
                    it.idEvento in 3..4
                }
            }
            2 -> {
                listaEvento[0].isCliked = true
                listaEvento[1].isCliked = true
            }
            3 -> {
                listaEvento[0].isCliked = true
                listaEvento[1].isCliked = true
                listaEvento[2].isCliked = true
                listaEvento.removeAll {
                    it.idEvento > 3
                }
            }
            4 -> {
                listaEvento[0].isCliked = true
                listaEvento[1].isCliked = true
                listaEvento[3].isCliked = true
                listaEvento.removeAll {
                    it.idEvento == 3 || it.idEvento == 5
                }
            }
            5 -> {
                listaEvento[0].isCliked = true
                listaEvento[1].isCliked = true
                listaEvento[4].isCliked = true
                listaEvento.removeAll {
                    it.idEvento == 3 || it.idEvento == 4
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