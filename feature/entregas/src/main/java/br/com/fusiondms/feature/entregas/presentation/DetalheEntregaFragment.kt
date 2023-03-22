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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.common.getColorFromAttr
import br.com.fusiondms.core.common.toMoedaLocal
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.model.entrega.EntregaItem
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.feature.entregas.databinding.FragmentDetalheEntregaBinding
import br.com.fusiondms.feature.entregas.databinding.LayoutDetalhesClienteBinding
import br.com.fusiondms.feature.entregas.databinding.LayoutDetalhesFiscaisBinding
import br.com.fusiondms.feature.entregas.databinding.LayoutDetalhesItensBinding
import br.com.fusiondms.feature.entregas.databinding.LayoutDetalhesRecebimentosBinding
import br.com.fusiondms.feature.entregas.databinding.LayoutDetalhesVendaBinding
import br.com.fusiondms.feature.entregas.databinding.LayoutStatusEntregaBinding
import br.com.fusiondms.feature.entregas.presentation.adapter.EntregasItensAdapter
import br.com.fusiondms.feature.entregas.presentation.adapter.RecebimentosAdapter
import br.com.fusiondms.feature.entregas.presentation.viewmodel.DetalheEntregaViewModel
import br.com.fusiondms.feature.entregas.presentation.viewmodel.EntregaViewModel
import br.com.fusiondms.feature.entregas.util.CustomHorizontalGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class DetalheEntregaFragment : Fragment() {

    private var _binding: FragmentDetalheEntregaBinding? = null
    private val binding get() = _binding!!

    private var _bindingStatus: LayoutStatusEntregaBinding? = null
    private val bindingStatus get() = _bindingStatus!!

    private var _bindingItens: LayoutDetalhesItensBinding? = null
    private val bindingItens get() = _bindingItens!!

    private var _bindingCliente: LayoutDetalhesClienteBinding? = null
    private val bindingCliente get() = _bindingCliente!!

    private var _bindingFiscal: LayoutDetalhesFiscaisBinding? = null
    private val bindingFiscal get() = _bindingFiscal!!

    private var _bindingVenda: LayoutDetalhesVendaBinding? = null
    private val bindingVenda get() = _bindingVenda!!

    private var _bindingRecebimentos: LayoutDetalhesRecebimentosBinding? = null
    private val bindingRecebimentos get() = _bindingRecebimentos!!


    private val entregaItensAdapter by lazy { EntregasItensAdapter() }
    private val recebimentosAdapter by lazy { RecebimentosAdapter() }

    private val args: DetalheEntregaFragmentArgs by navArgs()

    private val entregaViewModel: EntregaViewModel by activityViewModels()
    private val detalheEntregaViewModel: DetalheEntregaViewModel by viewModels()
    
    private lateinit var entregaSelecionada: Entrega

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
            _bindingItens = layoutDetalhesItens
            _bindingFiscal = layoutDetalhesFiscais
            _bindingCliente = layoutDetalhesCliente
            _bindingVenda = layoutDetalhesVenda
            _bindingRecebimentos = layoutDetalhesRecebimentos
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
                    is EntregaViewModel.EntregaResult.Selected -> bindDetalheEntrega(result.entrega)
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            detalheEntregaViewModel.detalheEntrega.collect { result ->
                when (result) {
                    is DetalheEntregaViewModel.Status.Success -> bindDetalheItens(result.detalheEntrega.listaEntregaItem)
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            detalheEntregaViewModel.listaRecebimento.collect { lista ->
                recebimentosAdapter.submitList(lista)
                bindDetalhesRecebimentos(lista)
            }
        }
    }

    private fun bindDetalheEntrega(entrega: Entrega) {
        entregaSelecionada = entrega
        detalheEntregaViewModel.getDetalheEntrega(entrega)
        detalheEntregaViewModel.getListaRecebimento(entrega)
        bindDetalhesCliente()
        bindDetalhesFiscais()
        bindDetalhesVenda()
        bindingStatus.tvOrdemEntrega.text = getString(R.string.label_ordem_entrega, 1)
        bindStatus(entrega.statusEntrega.toInt())
    }

    private fun bindDetalheItens(listaItem: List<EntregaItem>) {
        entregaItensAdapter.submitList(listaItem)
        bindingItens.apply {
            rvItens.adapter = entregaItensAdapter
            rvItens.layoutManager =
                if (listaItem.size > 2) CustomHorizontalGridLayoutManager(requireContext())
                else GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false)
        }
    }

    private fun bindDetalhesCliente() {
        bindingCliente.apply {
            tvEnderecoEntrega.text = Html.fromHtml("<b>Endereço de entrega:</b> Rua Maria Luiza Reis Monteiro dos Santos, n 105.", 0)
            tvBairroEntrega.text = Html.fromHtml("<b>Bairro:</b><br>Recreio São Jorge.", 0)
            tvCidadeEntrega.text = Html.fromHtml("<b>Cidade:</b><br>Guarulhos - SP.", 0)
            tvReferenciaEntrega.text = Html.fromHtml("<b>Referência de entrega:</b><br>Próximo a padaria Fernandes.", 0)
            tvEmails.text = Html.fromHtml("<b>Emails:</b><br>email1@gmail.com<br>email2@gmail.com<br>email3@gmail.com", 0)
            tvTelefones.text = Html.fromHtml("<b>Telefones para Contato:</b><br>11-91234-3456<br>11-2456-6789<br>11-98765-4321", 0)
        }
    }

    private fun bindDetalhesFiscais() {
        bindingFiscal.apply {
            tvCnpjCliente.text = Html.fromHtml("<b>CNPJ:</b><br>12.345.567/0001-89", 0)
            tvNotaFiscal.text = Html.fromHtml("<b>Nota fiscal:</b><br>123456", 0)
            tvFilialFatura.text = Html.fromHtml("<b>Filial de fatura:</b><br>123456", 0)
            tvValorNota.text = Html.fromHtml("<b>Valor da nota:</b><br>R\$ 1.549,55", 0)
            tvQtdItens.text = Html.fromHtml("Itens<br>34", 0)
            tvQtdVolumes.text = Html.fromHtml("Volumes<br>20", 0)
            tvQtdPallets.text = Html.fromHtml("Pallets<br>4", 0)
            tvQtdPeso.text = Html.fromHtml("Peso<br>400.00 kg", 0)
            tvValorItensEntregues.text = Html.fromHtml("<b>Itens entregues:</b> R\$ 549,55", 0)
            tvValorTotalItens.text = Html.fromHtml("<b>Total dos itens:</b> R\$ 1.549,55", 0)
        }
    }

    private fun bindDetalhesVenda() {
        bindingVenda.apply {
            tvNumeroPedido.text = Html.fromHtml("<b>Número do pedido:</b><br>123456", 0)
            tvNumeroCargaErp.text = Html.fromHtml("<b>Carga ERP:</b><br>98765", 0)
            tvNumeroRomaneioErp.text = Html.fromHtml("<b>Romaneio ERP:</b><br>54321", 0)
            tvTelefoneVendedor.text = Html.fromHtml("<b>Telefone:</b><br>11-912345678", 0)
            tvVendedor.text = Html.fromHtml("<b>Vendedor:</b> Airton de Sousa Oliveira", 0)
            tvEmailVendedor.text = Html.fromHtml("<b>Email:</b> vendedor@gmail.com", 0)
            tvObservacao.text = Html.fromHtml("<b>Observação:</b><br>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat", 0)
        }
    }

    private fun bindDetalhesRecebimentos(listaRecebimento: List<Recebimento>) {
        bindingRecebimentos.apply {
            var valorRecebido = BigDecimal.ZERO
            listaRecebimento.forEach { recebimento -> valorRecebido = valorRecebido.add(recebimento.valor) }
            val  valorRestante = entregaSelecionada.valor.subtract(valorRecebido)

            tvValorEntrega.text = entregaSelecionada.valor.toMoedaLocal()
            tvValorRecebido.text = valorRecebido.toMoedaLocal()
            tvValorRestante.text = valorRestante.toMoedaLocal()

            tvFormaPagamentoPreferencial.text = Html.fromHtml("<b>Recebimento preferencial:</b><br>Dineheiro", 0)
            rvTransacoes.adapter = recebimentosAdapter
            rvTransacoes.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
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
        detalheEntregaViewModel.resetViewModel()
        _binding = null
        _bindingStatus = null
    }
}