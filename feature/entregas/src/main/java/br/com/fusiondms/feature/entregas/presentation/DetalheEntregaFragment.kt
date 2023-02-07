package br.com.fusiondms.feature.entregas.presentation

import android.os.Bundle
import android.text.Html
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.common.getColorFromAttr
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.feature.entregas.databinding.FragmentDetalheEntregaBinding
import br.com.fusiondms.feature.entregas.databinding.LayoutStatusEntregaBinding
import br.com.fusiondms.feature.entregas.presentation.viewmodel.EntregaViewModel
import java.util.concurrent.TimeUnit

class DetalheEntregaFragment : Fragment() {

    private var _binding: FragmentDetalheEntregaBinding? = null
    private val binding get() = _binding!!

    private var _bindingStatus: LayoutStatusEntregaBinding? = null
    private val bindingStatus get() = _bindingStatus!!

    private val args: DetalheEntregaFragmentArgs by navArgs()

    private val entregaViewModel: EntregaViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        postponeEnterTransition(250, TimeUnit.MILLISECONDS)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalheEntregaBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            _bindingStatus = layoutStatusEntrega
        }

//        ViewCompat.setTransitionName(bindingStatus.tvStatus, "status_${args.entregaId}")
//        ViewCompat.setTransitionName(bindingStatus.tvOrdemEntrega, "ordem_entrega_${args.entregaId}")
        ViewCompat.setTransitionName(bindingStatus.root, "card_${args.entregaId}")

        bindObservers()
    }

    private fun bindObservers() {
        lifecycleScope.launchWhenCreated {
            entregaViewModel.entregaSelecionada.collect { result ->
                when(result) {
                    is EntregaViewModel.EntregaResult.Selected -> bindEntrega(result.entrega)
                    else -> Unit
                }
            }
        }
    }

    private fun bindEntrega(entrega: Entrega) {
        bindingStatus.tvOrdemEntrega.text = getString(R.string.label_ordem_entrega, 1)
        bindStatus(entrega.statusEntrega.toInt())
    }

    private fun bindStatus(statusEntrega: Int) {
        when(statusEntrega) {
            0 -> {
                bindingStatus.root.setCardBackgroundColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
                bindingStatus.tvStatus.text = Html.fromHtml("<b>${getString(R.string.label_pendente)}</b>", 0)
                bindingStatus.tvStatus.setTextColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnSurface))
            }
            1 -> {
                bindingStatus.root.setCardBackgroundColor(requireContext().getColor(R.color.brand_gomboje_orange_ripple))
                bindingStatus.tvStatus.text = Html.fromHtml("<b>${getString(R.string.label_aguardando)}</b>", 0)
                bindingStatus.tvStatus.setTextColor(requireContext().getColor(R.color.brand_gomboje_orange))
            }
            2 -> {
                bindingStatus.root.setCardBackgroundColor(requireContext().getColor(R.color.brand_celtic_blue_ripple))
                bindingStatus.tvStatus.text = Html.fromHtml("<b>${getString(R.string.label_em_andamento)}</b>", 0)
                bindingStatus.tvStatus.setTextColor(requireContext().getColor(R.color.brand_celtic_blue))
            }
            3 -> {
                bindingStatus.root.setCardBackgroundColor(requireContext().getColor(R.color.brand_green_success_ripple))
                bindingStatus.tvStatus.text = Html.fromHtml("<b>${getString(R.string.label_entregue)}</b>", 0)
                bindingStatus.tvStatus.setTextColor(requireContext().getColor(R.color.brand_green_success))
            }
            4 -> {
                bindingStatus.root.setCardBackgroundColor(requireContext().getColor(R.color.brand_red_ripple))
                bindingStatus.tvStatus.text = Html.fromHtml("<b>${getString(R.string.label_devolvida)}</b>", 0)
                bindingStatus.tvStatus.setTextColor(requireContext().getColor(R.color.brand_red))
            }
            5 -> {
                bindingStatus.root.setCardBackgroundColor(requireContext().getColor(R.color.brand_selective_yellow_ripple))
                bindingStatus.tvStatus.text = Html.fromHtml("<b>${getString(R.string.label_adiada)}</b>", 0)
                bindingStatus.tvStatus.setTextColor(requireContext().getColor(R.color.brand_selective_yellow))
            }
            else -> {
                bindingStatus.root.setCardBackgroundColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
                bindingStatus.tvStatus.text = Html.fromHtml("<b>${getString(R.string.label_pendente)}</b>", 0)
                bindingStatus.tvStatus.setTextColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnSurface))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _bindingStatus = null
    }
}