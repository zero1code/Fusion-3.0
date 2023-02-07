package br.com.fusiondms.feature.jornadatrabalho.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import br.com.fusiondms.feature.jornadatrabalho.databinding.FragmentRecibosBinding
import br.com.fusiondms.feature.jornadatrabalho.presentation.adapter.RecibosAdapter
import br.com.fusiondms.feature.jornadatrabalho.presentation.viewmodel.JornadaTrabalhoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecibosFragment : Fragment() {
    private var _binding: FragmentRecibosBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy { RecibosAdapter() }

    private val jornadaViewModel: JornadaTrabalhoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecibosBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            jornadaViewModel.getListaRecibo()
            rvRecibos.adapter = adapter
        }

        bindObservers()
    }

    private fun bindObservers() {
        lifecycleScope.launchWhenStarted {
            jornadaViewModel.listaReccibo.collect { result ->
                when(result) {
                    is JornadaTrabalhoViewModel.JornadaStatus.SuccessListaRecibo -> {
                        adapter.submitList(result.listaRecibo)
                    }
                    is JornadaTrabalhoViewModel.JornadaStatus.ErroRecibo -> {
                        Toast.makeText(requireContext(), "Erro", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        jornadaViewModel.resetJornadaState()
        _binding = null
    }
}