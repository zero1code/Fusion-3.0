package br.com.fusiondms.feature.cargas.presentation

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.common.bottomdialog.Dialog
import br.com.fusiondms.core.common.progressdialog.showProgressBar
import br.com.fusiondms.core.model.romaneio.Romaneio
import br.com.fusiondms.feature.cargas.databinding.FragmentDetalhesCargaBinding
import br.com.fusiondms.feature.cargas.presentation.viewmodel.CargasViewModel
import java.text.NumberFormat

class DetalhesCargaFragment : Fragment() {

    private var _binding: FragmentDetalhesCargaBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressDialog: AlertDialog
    private lateinit var cargaSelecionada: Romaneio
    private val format: NumberFormat = NumberFormat.getCurrencyInstance()

    private val cargasViewModel: CargasViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalhesCargaBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        bindListeners()
    }

    private fun bindObservers() {
        lifecycleScope.launchWhenStarted {
            cargasViewModel.cargaSelecionada.collect() { staus ->
                when (staus) {
                    is CargasViewModel.CargaStatus.Loading -> progressDialogState(staus.isLoading)
                    is CargasViewModel.CargaStatus.Selected -> {
                        cargaSelecionada = staus.romaneio
                        bindCargaInfo()
                    }
                    is CargasViewModel.CargaStatus.Error -> {
                        dialogErro(staus.message)
                    }
                    is CargasViewModel.CargaStatus.Deleted -> {
                        progressDialog.dismiss()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun bindListeners() {
        binding.btnAceitarCarga.setOnClickListener { actionMapaFragment() }

        binding.btnRecusarCarga.setOnClickListener { recusarCarga() }
    }

    private fun bindCargaInfo() {
        binding.apply {
            tvRomaneioId.text =
                Html.fromHtml(getString(R.string.label_romaneio_id, cargaSelecionada.idRomaneio), 0)
            tvDestino.text =
                Html.fromHtml(getString(R.string.label_destino, cargaSelecionada.destino), 0)
            tvPesoCarga.text =
                Html.fromHtml(getString(R.string.label_peso_carga, String.format("%.2f", 1456.00)), 0)
            tvKm.text =
                Html.fromHtml(getString(R.string.label_km_total, cargaSelecionada.kmTotal), 0)
            tvValorCarga.text =
                Html.fromHtml(getString(R.string.label_valor_carga, format.format(33456)), 0)
        }
    }

    private fun recusarCarga() {
        progressDialog = requireActivity().showProgressBar(getString(R.string.label_recusando_carga))
        cargasViewModel.recusarCarga(cargaSelecionada)
    }

    private fun actionMapaFragment() {
        cargasViewModel.salvarIdCargaSelecionada(cargaSelecionada.idRomaneio)
        cargasViewModel.resetCargaState()
        findNavController().navigate(br.com.fusiondms.core.navigation.R.id.action_listarCargasFragment_to_mapaFragment)
    }

    private fun dialogErro(message: String?) {
        Dialog(
            getString(R.string.label_erro_recusar_carga),
            message ?: "",
            getString(R.string.label_tentar_novamente),
            getString(R.string.label_cancelar),
            acaoPositiva = { recusarCarga() },
            acaoNegativa = {}
        ).show(requireActivity().supportFragmentManager, Dialog.TAG)
    }

    private fun progressDialogState(isLoading: Boolean) {
        if (isLoading) {
            progressDialog.show()
        } else {
            if (progressDialog.isShowing) progressDialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}