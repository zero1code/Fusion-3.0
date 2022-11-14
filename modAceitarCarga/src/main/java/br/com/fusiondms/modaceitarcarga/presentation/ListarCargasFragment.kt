package br.com.fusiondms.modaceitarcarga.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import br.com.fusiondms.modaceitarcarga.presentation.adapter.CargasAdapter
import br.com.fusiondms.modaceitarcarga.databinding.FragmentListarCargasBinding
import br.com.fusiondms.modaceitarcarga.databinding.ListaCargaVaziaBinding
import br.com.fusiondms.modaceitarcarga.presentation.viewmodel.CargasViewModel
import br.com.fusiondms.modcommon.bottomdialog.Dialog
import br.com.fusiondms.modcommon.progressdialog.showProgressBar
import br.com.fusiondms.modcommon.setDefaultStatusBarColor
import br.com.fusiondms.modcommon.setStatusBarColor
import br.com.fusiondms.modmodel.Romaneio
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListarCargasFragment : Fragment() {

    private var _binding: FragmentListarCargasBinding? = null
    private val binding get() = _binding!!

    private var _listaVaziaBinding: ListaCargaVaziaBinding? = null
    private val listaVaziaBinding get() = _listaVaziaBinding!!

    private val cargasViewModel: CargasViewModel by activityViewModels()

    private val adapter by lazy { CargasAdapter() }

    private lateinit var progressDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListarCargasBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slidingPaneLayout = binding.slidingPaneLayout
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            ListarCargasOnBackPressedCallback(slidingPaneLayout, requireActivity())
        )

        binding.apply {
            progressDialog = requireActivity().showProgressBar("Buscando romaneios")
            rvRomaneios.adapter = adapter
            _listaVaziaBinding = ltListaVazia
            cargasViewModel.getListaCarga()
        }

        bindObservers()
        bindListeners()
    }

    private fun bindObservers() {
        lifecycleScope.launchWhenStarted {
            cargasViewModel.listaCargaStatus.collect() { result ->
                when (result) {
                    CargasViewModel.CargaStatus.Empty -> avisoJornadaDeTrabalho()
                    is CargasViewModel.CargaStatus.Loading -> progessDialogState(result.isLoading)
                    is CargasViewModel.CargaStatus.Error -> Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    is CargasViewModel.CargaStatus.Deleted -> {
                        adapter.notifyItemRemoved(result.position)
                    }
                    is CargasViewModel.CargaStatus.Success -> {
                        listaVaziaBinding.root.visibility = View.GONE
                        adapter.submitList(result.lista)
                        progressDialog.dismiss()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun avisoJornadaDeTrabalho() {
        listaVaziaBinding.root.visibility = View.VISIBLE
    }

    private fun progessDialogState(isLoading: Boolean) {
        if (isLoading) {
            progressDialog.show()
        } else {
            if (progressDialog.isShowing) progressDialog.dismiss()
        }
    }

    private fun bindListeners() {
        adapter.onRomaneioClickListener = {carga, position ->
            motoristaTerceirizado(carga, position)
        }
    }

    private fun motoristaTerceirizado(carga: Romaneio, position: Int) {
        Dialog(
            "Confirmar vínculo",
            "Você é um motorista terceirizado?",
            "Sim",
            "Não",
            acaoPositiva = {
                cargasViewModel.setCargaSelecionada(carga, position)
                requireActivity().setStatusBarColor(carga.corIdentificador)
                binding.slidingPaneLayout.openPane()
            },
            acaoNegativa = {
                cargasViewModel.salvarIdCargaSelecionada(carga.idRomaneio)
                findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_listarCargasFragment_to_mapaFragment)
            }
        ).show(requireActivity().supportFragmentManager, Dialog.TAG)
    }

    class ListarCargasOnBackPressedCallback(
        private val slidingPaneLayout: SlidingPaneLayout,
        private val requireActivity: FragmentActivity
    ) : OnBackPressedCallback(
        slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen
    ), SlidingPaneLayout.PanelSlideListener {
        init {
            slidingPaneLayout.addPanelSlideListener(this)
        }

        override fun handleOnBackPressed() {
            requireActivity.setDefaultStatusBarColor()
            slidingPaneLayout.closePane()
        }

        override fun onPanelSlide(panel: View, slideOffset: Float) {}

        override fun onPanelOpened(panel: View) {
            isEnabled = true
        }

        override fun onPanelClosed(panel: View) {
            isEnabled = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cargasViewModel.resetCargaState()
        _binding = null
    }
}