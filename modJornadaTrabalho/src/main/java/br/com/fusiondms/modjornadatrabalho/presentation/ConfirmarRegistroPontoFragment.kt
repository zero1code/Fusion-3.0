package br.com.fusiondms.modjornadatrabalho.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.modcommon.converterDataParaHorasMinutos
import br.com.fusiondms.modcommon.snackbar.TipoMensagem
import br.com.fusiondms.modcommon.snackbar.showMessage
import br.com.fusiondms.modjornadatrabalho.databinding.FragmentConfirmarRegistroPontoBinding
import br.com.fusiondms.modjornadatrabalho.presentation.viewmodel.JornadaTrabalhoViewModel
import br.com.fusiondms.modmodel.jornadatrabalho.Colaborador
import br.com.fusiondms.modmodel.jornadatrabalho.RegistroPonto
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