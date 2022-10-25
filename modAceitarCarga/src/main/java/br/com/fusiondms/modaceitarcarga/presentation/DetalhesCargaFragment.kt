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
import br.com.fusiondms.modaceitarcarga.R
import br.com.fusiondms.modaceitarcarga.databinding.FragmentDetalhesCargaBinding
import br.com.fusiondms.modaceitarcarga.viewmodel.CargasViewModel
import br.com.fusiondms.modcommon.bottomdialog.DialogAcao
import br.com.fusiondms.modcommon.bottomdialog.DialogAviso
import br.com.fusiondms.modcommon.progressdialog.showProgressBar
import br.com.fusiondms.modnavegacao.R.*
import kotlinx.coroutines.delay
import java.text.NumberFormat

class DetalhesCargaFragment : Fragment() {

    private var _binding : FragmentDetalhesCargaBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressDialog: AlertDialog

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
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        progressDialog = requireActivity().showProgressBar("Recusando carga")
        cargasViewModel.currentSport.observe(this.viewLifecycleOwner) {
            binding.apply {
                tvRomaneioId.text = Html.fromHtml("<b>ID Romaneio:<br></b>${it.id}", 0)
                tvDestino.text = Html.fromHtml("<b>Destino:<br></b>${it.destino}", 0)
                tvPesoCarga.text = Html.fromHtml("<b>Peso da carga:<br></b>${String.format("%.2f", it.pesoTotal)} kg", 0)
                tvKm.text = Html.fromHtml("<b>Total km:<br></b>${it.kmTotal} km", 0)
                tvValorCarga.text = Html.fromHtml("<b>Valor carga:<br></b>${format.format(it.valorTotal)}", 0)
            }
        }

        binding.btnAceitarCarga.setOnClickListener {
            findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_listarCargasFragment_to_mapaFragment)
        }

        binding.btnRecusarCarga.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                progressDialog.show()
                delay(3000)
                progressDialog.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}