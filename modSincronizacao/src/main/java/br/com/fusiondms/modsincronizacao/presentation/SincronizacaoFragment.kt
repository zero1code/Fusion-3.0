package br.com.fusiondms.modsincronizacao.presentation


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.modcommon.bottomdialog.Dialog
import br.com.fusiondms.modcommon.fadeInAnimation
import br.com.fusiondms.modcommon.fadeOutAnimation
import br.com.fusiondms.modsincronizacao.R
import br.com.fusiondms.modsincronizacao.R.menu
import br.com.fusiondms.modsincronizacao.databinding.FragmentSincronizacaoBinding
import br.com.fusiondms.modsincronizacao.presentation.viewmodel.SincronizacaoViewmodel
import br.com.fusiondms.modsincronizacao.util.boaNoite
import br.com.fusiondms.modsincronizacao.util.boaTarde
import br.com.fusiondms.modsincronizacao.util.bomDia
import br.com.fusiondms.modsincronizacao.util.frasesSincronizacao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@AndroidEntryPoint
class SincronizacaoFragment : Fragment() {

    private var _binding: FragmentSincronizacaoBinding? = null
    private val binding get() = _binding!!
    private val sincronizacaoViewmodel: SincronizacaoViewmodel by viewModels()

    private val frasesSincronizacao = frasesSincronizacao()
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSincronizacaoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindListeners()
        bindObservers()
    }

    private fun bindObservers() {
        lifecycleScope.launchWhenCreated {
            sincronizacaoViewmodel.sincronizacaoCompleta.collect { status ->
                when (status) {
                    is SincronizacaoViewmodel.SincronizacaoStatus.Nothing -> Unit
                    is SincronizacaoViewmodel.SincronizacaoStatus.Loading -> isLoading =
                        status.isLoading
                    is SincronizacaoViewmodel.SincronizacaoStatus.Success -> actionListaRomaneioFragment()
                    is SincronizacaoViewmodel.SincronizacaoStatus.Error -> dialogErro(status.message)
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            sincronizacaoViewmodel.horarioDia.collect {
                horarioDoDia(it)
            }
        }
    }

    private fun bindListeners() {
        binding.btnAtualizarCargas.setOnClickListener {
//            startSincronizacao()
            findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_sincronizacaoFragment_to_listarCargasFragment)
        }

        binding.fabMoreOptions.setOnClickListener {
            showMenu(it, menu.menu_sincronizacao_fragment)
        }
    }

    private fun startSincronizacao() {
        liberarClique(false)
        sincronizacaoViewmodel.getSincronizacao()
        binding.linearProgressIndicator.visibility = View.VISIBLE
        startLoading()
    }

    private fun actionListaRomaneioFragment() {
        lifecycleScope.launch {
            sincronizacaoViewmodel.resetSincronizacaoState()
            delay(3500)
            binding.linearProgressIndicator.visibility = View.GONE
            binding.txtAcao.text = "Vamos começar!"
            binding.txtAcao.fadeInAnimation()
            delay(3000)
            findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_sincronizacaoFragment_to_listarCargasFragment)
        }
    }

    private fun dialogErro(message: String?) {
        liberarClique(true)
        binding.linearProgressIndicator.visibility = View.GONE
        binding.txtAcao.text = "Algo deu errado."
        Dialog(
            "Não foi possível sincronizar",
            message ?: "",
            "Tentar novamente",
            "Cancelar",
            acaoPositiva = { startSincronizacao() },
            acaoNegativa = {}
        ).show(requireActivity().supportFragmentManager, Dialog.TAG)
    }

    private fun liberarClique(isClickable: Boolean) {
        binding.btnFinalizar.isClickable = isClickable
        binding.btnAtualizarCargas.isClickable = isClickable
        binding.btnSolicitarCarga.isClickable = isClickable
        binding.fabMoreOptions.isClickable = isClickable
    }

    private fun startLoading() {
        lifecycleScope.launchWhenCreated {
            while (isLoading) {
                binding.txtAcao.fadeOutAnimation()
                delay(500)
                binding.txtAcao.text = frasesSincronizacao[0]
                frasesSincronizacao.removeAt(0)
                binding.txtAcao.fadeInAnimation()
                delay(3000)
                if (frasesSincronizacao.size == 0) {
                    frasesSincronizacao.addAll(frasesSincronizacao())
                }
            }
        }
    }

    private fun horarioDoDia(horario: String) {
        var lista = listOf<String>()
        when (horario) {
            "MANHA" -> { lista = bomDia() }
            "TARDE" -> { lista = boaTarde() }
            "NOITE" -> { lista = boaNoite() }
        }
        val mensagem = lista[Random.nextInt(lista.size - 1)]
        binding.txtOla.text = "${mensagem} Airton"
    }

    private fun showMenu(view: View, @MenuRes menu: Int) {
        val popup = PopupMenu(requireContext(), view)

        popup.menuInflater.inflate(menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_configuracao_registrar_GCM -> {
                    Toast.makeText(requireContext(), "Ola", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }

            true
        }

        popup.setOnDismissListener {}

        popup.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}