package br.com.fusiondms.modaceitarcarga.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import br.com.fusiondms.modaceitarcarga.adapter.CargasAdapter
import br.com.fusiondms.modaceitarcarga.databinding.FragmentListarCargasBinding
import br.com.fusiondms.modaceitarcarga.viewmodel.CargasViewModel
import br.com.fusiondms.modcommon.bottomdialog.DialogAcao
import br.com.fusiondms.modcommon.setDefaultStatusBarColor
import br.com.fusiondms.modcommon.setStatusBarColor

class ListarCargasFragment : Fragment() {

    private var _binding: FragmentListarCargasBinding? = null
    private val binding get() = _binding!!

    private val cargasViewModel: CargasViewModel by activityViewModels()

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


        val adapter = CargasAdapter {
            motoristaTerceirizado(it)
        }
        binding.recyclerView.adapter = adapter
        adapter.submitList(cargasViewModel.sportsData)
    }

    private fun motoristaTerceirizado(romaneio: br.com.fusiondms.modmodel.Romaneio) {
        val dialog = DialogAcao(
            "Confirmar vínculo",
            "Você é um motorista terceirizado?",
            "Sim",
            "Não"
        )

        val iDialog = object : DialogAcao.IDialogAcaoListener {
            override fun onClickPositivo() {
                cargasViewModel.updateCurrentSport(romaneio)
                requireActivity().setStatusBarColor(romaneio.corIdentificador)
                binding.slidingPaneLayout.openPane()
                dialog.dismiss()
            }

            override fun onClickNegativo() {
                dialog.dismiss()
                findNavController().navigate(br.com.fusiondms.modnavegacao.R.id.action_listarCargasFragment_to_mapaFragment)
            }
        }

        dialog.setListener(iDialog)
        dialog.show(requireActivity().supportFragmentManager, DialogAcao.TAG)
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
        _binding = null
    }
}