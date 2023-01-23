package br.com.fusiondms.modjornadatrabalho.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.modcommon.converterDataParaHorasMinutos
import br.com.fusiondms.modjornadatrabalho.R
import br.com.fusiondms.modjornadatrabalho.databinding.FragmentJornadaTrabalhoBinding
import br.com.fusiondms.modjornadatrabalho.presentation.viewmodel.JornadaTrabalhoViewModel
import br.com.fusiondms.modnavegacao.R.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class JornadaTrabalhoFragment : Fragment() {
    private var _binding: FragmentJornadaTrabalhoBinding? = null
    private val  binding get() = _binding!!

    private val jornadaViewModel: JornadaTrabalhoViewModel by activityViewModels()
    private lateinit var timer: Timer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJornadaTrabalhoBinding.inflate(inflater)
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
    }

    private fun bindListeners() {
        timer = Timer()
        timer.scheduleAtFixedRate(timerTask() {
            jornadaViewModel.atualizarData()
        }, 1000, 5000)

        binding.btnRegistrarPonto.setOnClickListener {
            findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_jornadaTrabalhoFragment_to_registrarPontoFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        _binding = null
    }
}