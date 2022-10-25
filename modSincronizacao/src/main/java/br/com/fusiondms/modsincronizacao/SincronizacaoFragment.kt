package br.com.fusiondms.modsincronizacao


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.modcommon.fadeInAnimation
import br.com.fusiondms.modcommon.fadeOutAnimation
import br.com.fusiondms.modnavegacao.R.*
import br.com.fusiondms.modsincronizacao.R.menu
import br.com.fusiondms.modsincronizacao.databinding.FragmentSincronizacaoBinding
import kotlinx.coroutines.delay

class SincronizacaoFragment : Fragment() {

    private var _binding: FragmentSincronizacaoBinding? = null
    private val binding get() = _binding!!
    private val msg = arrayListOf("Sincronizando cargas...", "Aguarde um momento...", "Conectando ao servidor...", "Tudo pronto!")
    private var textos = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSincronizacaoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textos.addAll(msg)
        binding.apply {
            btnAtualizarCargas.setOnClickListener {
                duploClique(false)
                findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_sincronizacaoFragment_to_listarCargasFragment)
//                atualizarCargas()
            }
            fabMoreOptions.setOnClickListener {
                showMenu(it, menu.menu_sincronizacao_fragment)
            }
        }

    }

    private fun duploClique(isClickable: Boolean) {
        binding.linearProgressIndicator.visibility = if (isClickable) View.INVISIBLE else View.VISIBLE
        binding.btnFinalizar.isClickable = isClickable
        binding.btnAtualizarCargas.isClickable = isClickable
        binding.btnSolicitarCarga.isClickable = isClickable
    }

    private fun atualizarCargas() {
        if (textos.size != 0) {
            lifecycleScope.launchWhenCreated {
                binding.txtAcao.fadeOutAnimation()
                delay(500)
                binding.txtAcao.text = textos[0]
                textos.removeAt(0)
                binding.txtAcao.fadeInAnimation()
                delay(3000)
                if (textos.size == 0) {
                    duploClique(true)
                    findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_sincronizacaoFragment_to_listarCargasFragment)
                } else {
                    atualizarCargas()
                }
            }
        }
    }

    private fun showMenu(view: View, @MenuRes menu: Int) {
        val popup = PopupMenu(requireContext(), view)

        popup.menuInflater.inflate(menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId) {
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