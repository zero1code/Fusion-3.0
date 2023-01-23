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
import br.com.fusiondms.modcommon.R.array.*
import br.com.fusiondms.modcommon.R.string.*
import br.com.fusiondms.modcommon.bottomdialog.Dialog
import br.com.fusiondms.modcommon.fadeInMoveAnimation
import br.com.fusiondms.modcommon.fadeOutMoveAnimation
import br.com.fusiondms.modsincronizacao.R
import br.com.fusiondms.modsincronizacao.R.menu
import br.com.fusiondms.modsincronizacao.databinding.FragmentSincronizacaoBinding
import br.com.fusiondms.modsincronizacao.presentation.viewmodel.SincronizacaoViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@AndroidEntryPoint
class SincronizacaoFragment : Fragment() {

    private var _binding: FragmentSincronizacaoBinding? = null
    private val binding get() = _binding!!
    private val sincronizacaoViewmodel: SincronizacaoViewmodel by viewModels()

    private lateinit var frasesSincronizacao: MutableList<String>
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
        frasesSincronizacao = resources.getStringArray(frases_sincronizacao).toMutableList()
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
            binding.txtAcao.text = getString(label_vamos_comecar)
            binding.txtAcao.fadeInMoveAnimation()
            delay(3000)
            findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_sincronizacaoFragment_to_listarCargasFragment)
        }
    }

    private fun dialogErro(message: String?) {
        lifecycleScope.launch {
            liberarClique(true)
            binding.linearProgressIndicator.visibility = View.GONE
            Dialog(
                getString(label_erro_ao_sincronizar),
                message ?: "",
                getString(label_tentar_novamente),
                getString(label_cancelar),
                acaoPositiva = { startSincronizacao() },
                acaoNegativa = {}
            ).show(requireActivity().supportFragmentManager, Dialog.TAG)
            delay(500)
            binding.txtAcao.text = getString(label_algo_deu_errado)
        }
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
                binding.txtAcao.fadeOutMoveAnimation()
                delay(500)
                binding.txtAcao.text = frasesSincronizacao[0]
                frasesSincronizacao.removeAt(0)
                binding.txtAcao.fadeInMoveAnimation()
                delay(3000)
                if (frasesSincronizacao.size == 0) {
                    frasesSincronizacao.addAll(resources.getStringArray(frases_sincronizacao).toMutableList())
                }
            }
        }
    }

    private fun horarioDoDia(horario: String) {
        var lista = listOf<String>()
        when (horario) {
            MANHA -> { lista = resources.getStringArray(bom_dia).toList() }
            TARDE -> { lista = resources.getStringArray(boa_tarde).toList() }
            NOITE -> { lista = resources.getStringArray(boa_noite).toList() }
        }
        val mensagem = lista[Random.nextInt(lista.size - 1)]
        binding.txtOla.text = getString(label_horario_do_dia, mensagem, "Airton")
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

    companion object {
        const val MANHA = "MANHA"
        const val TARDE = "TARDE"
        const val NOITE = "NOITE"
    }
}