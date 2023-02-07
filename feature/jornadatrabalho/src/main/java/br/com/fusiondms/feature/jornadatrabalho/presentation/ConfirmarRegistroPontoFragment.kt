package br.com.fusiondms.feature.jornadatrabalho.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.core.common.converterDataParaHorasMinutos
import br.com.fusiondms.core.common.snackbar.TipoMensagem
import br.com.fusiondms.core.common.snackbar.showMessage
import br.com.fusiondms.core.model.jornadatrabalho.Colaborador
import br.com.fusiondms.core.model.jornadatrabalho.RegistroPonto
import br.com.fusiondms.feature.jornadatrabalho.databinding.FragmentConfirmarRegistroPontoBinding
import br.com.fusiondms.feature.jornadatrabalho.presentation.viewmodel.JornadaTrabalhoViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class ConfirmarRegistroPontoFragment : Fragment() {
    private var _binding: FragmentConfirmarRegistroPontoBinding? = null
    private val  binding get() = _binding!!

    private val jornadaViewModel: JornadaTrabalhoViewModel by activityViewModels()
    private lateinit var timer: Timer

    private lateinit var colaboradorSelecionado: Colaborador

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmarRegistroPontoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindObservers()
        bindListeners()
    }

    private fun bindObservers() {
        lifecycleScope.launchWhenStarted {
            jornadaViewModel.horaAtual.collect() { data ->
                binding.tvHoraAtual.text = converterDataParaHorasMinutos(data)
            }
        }

        lifecycleScope.launchWhenStarted {
            jornadaViewModel.colaboradorSelecionado.collect { result ->
                when (result) {
                    is JornadaTrabalhoViewModel.JornadaStatus.Selected -> {
                        colaboradorSelecionado = result.colaborador
                        binding.apply {
                            tvNome.text = result.colaborador.nome
                            tvFuncao.text = result.colaborador.funcao
                        }
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            jornadaViewModel.registrarPonto.collect { result ->
                when (result) {
                    is JornadaTrabalhoViewModel.JornadaStatus.SuccessRegistroPonto -> {
                        binding.root.showMessage("Ponto registrado com sucesso", TipoMensagem.SUCCESS)
                        findNavController().navigateUp()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun bindListeners() {
        timer = Timer()
        timer.scheduleAtFixedRate(timerTask() {
            jornadaViewModel.atualizarData()
        }, 1000, 5000)

        binding.btnRegistrarPonto.setOnClickListener {
            val registroPonto = RegistroPonto(
                0,
                colaboradorSelecionado.matricula,
                jornadaViewModel.horaAtual.value,
                false

            )
            jornadaViewModel.inserirRegistroPonto(registroPonto)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        jornadaViewModel.resetJornadaState()
        _binding = null
    }
}