package br.com.fusiondms.modjornadatrabalho.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.modcommon.converterDataParatextoLegivel
import br.com.fusiondms.modjornadatrabalho.databinding.FragmentRegistrarPontoBinding
import br.com.fusiondms.modjornadatrabalho.presentation.adapter.JornadaColaboradoresAdapter
import br.com.fusiondms.modjornadatrabalho.presentation.viewmodel.JornadaTrabalhoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrarPontoFragment : Fragment() {
    private var _binding: FragmentRegistrarPontoBinding? = null
    private val binding get() = _binding!!

    private val jornadaViewModel: JornadaTrabalhoViewModel by activityViewModels()

    private val adapter by lazy { JornadaColaboradoresAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrarPontoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvColaboradores.adapter = adapter
            jornadaViewModel.getRegistroPonto()
            tvData.text = converterDataParatextoLegivel(jornadaViewModel.horaAtual.value)
        }

        bindObservers()
        bindListeners()
    }

    private fun bindObservers() {
        lifecycleScope.launchWhenStarted {
            jornadaViewModel.listaReccibo.collect { result ->
                when (result) {
                    is JornadaTrabalhoViewModel.JornadaStatus.SuccessListaRegistro -> adapter.submitList(result.listaRegistroPonto)
                    else -> Unit
                }

            }
        }
    }

    private fun bindListeners() {
        adapter.onColaboradorClickListener = { colaborador ->
            jornadaViewModel.setColaboradorSelecionado(colaborador)
            findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_registrarPontoFragment_to_confirmarRegistroPontoFragment)
        }

        binding.btnAdicionarColaborador.setOnClickListener {
            findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_registrarPontoFragment_to_adicionarColaboradorFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}