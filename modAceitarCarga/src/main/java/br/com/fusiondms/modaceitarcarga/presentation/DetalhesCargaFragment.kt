package br.com.fusiondms.modaceitarcarga.presentation

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
import br.com.fusiondms.modaceitarcarga.databinding.FragmentDetalhesCargaBinding
import br.com.fusiondms.modaceitarcarga.presentation.viewmodel.CargasViewModel
import br.com.fusiondms.modcommon.bottomdialog.Dialog
import br.com.fusiondms.modcommon.progressdialog.showProgressBar
import br.com.fusiondms.modmodel.Romaneio
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
        binding.btnAceitarCarga.setOnClickListener {
            cargasViewModel.resetCargaState()
            findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_listarCargasFragment_to_mapaFragment)
        }

        binding.btnRecusarCarga.setOnClickListener { recusarCarga() }
    }

    private fun bindCargaInfo() {
        binding.apply {
            tvRomaneioId.text = Html.fromHtml("<b>ID Romaneio:<br></b>${cargaSelecionada.id}", 0)
            tvDestino.text = Html.fromHtml("<b>Destino:<br></b>${cargaSelecionada.destino}", 0)
            tvPesoCarga.text = Html.fromHtml(
                "<b>Peso da carga:<br></b>${
                    String.format(
                        "%.2f",
                        cargaSelecionada.pesoTotal
                    )
                } kg", 0
            )
            tvKm.text = Html.fromHtml("<b>Total km:<br></b>${cargaSelecionada.kmTotal} km", 0)
            tvValorCarga.text = Html.fromHtml(
                "<b>Valor carga:<br></b>${format.format(cargaSelecionada.valorTotal)}",
                0
            )
        }
    }

    private fun recusarCarga() {
        progressDialog = requireActivity().showProgressBar("Recusando a carga")
        cargasViewModel.recusarCarga(cargaSelecionada)
    }

    private fun dialogErro(message: String?) {
        Dialog(
            "Erro ao recusar carga",
            message ?: "",
            "Tentar novamente",
            "Cancelar",
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