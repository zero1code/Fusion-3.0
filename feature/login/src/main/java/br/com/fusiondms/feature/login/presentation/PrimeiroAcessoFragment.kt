package br.com.fusiondms.feature.login.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.core.common.*
import br.com.fusiondms.core.common.bottomdialog.Dialog
import br.com.fusiondms.feature.login.R
import br.com.fusiondms.feature.login.databinding.FragmentPrimeiroAcessoBinding

class PrimeiroAcessoFragment : Fragment() {

    private var _binding: FragmentPrimeiroAcessoBinding? = null
    private val binding get() = _binding!!

    private var radioButtonSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrimeiroAcessoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindObservers()
        bindListeners()
    }

    private fun bindObservers() {

    }

    private fun bindListeners() {
        binding.apply {
            btnNext.setOnClickListener {
                if (checarCampo(vfFormulario.displayedChild)) {
                    vfFormulario.showNext()
                    linearProgressIndicator.progressAnimation(100 / vfFormulario.childCount)
                    when(vfFormulario.displayedChild) {
                        2 -> requireContext().hideKeyboard(it)
                        3 -> btnNext.hideFabAnimation(0F)
                    }
                }
            }

            rgFuncao.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId != -1 && !radioButtonSelected) {
                    radioButtonSelected = true
                }
            }

//            btnVoltar.setOnClickListener {
//                findNavController().navigateUp()
//            }

//            btnTirarFoto.setOnClickListener {
//                val directions = PrimeiroAcessoFragmentDirections.actionPrimeiroAcessoFragmentToFaceDetectorFragment(
//                    ModoDeteccao.REGISTRAR_FACE.value)
//                findNavController().navigate(directions)
//            }

            btnCadastrar.setOnClickListener {
                confirmarFormulario()
            }
        }
    }

    private fun checarCampo(displayedChild: Int) : Boolean {
        when (displayedChild) {
            0 -> {
                if (binding.etEmpresaId.text.isEmpty()) {
                    binding.etEmpresaId.error = getString(br.com.fusiondms.core.common.R.string.label_digite_id_empresa)
                    return false
                }
            }
            1 -> {
                if (binding.etMatricula.text.isEmpty()) {
                    binding.etMatricula.error = getString(br.com.fusiondms.core.common.R.string.label_digite_matricula)
                    return false
                }
            }
        }
        return true
    }

    private fun confirmarFormulario() {
        val funcao = if (binding.rgFuncao.checkedRadioButtonId == R.id.rb_motorista)
            getString(br.com.fusiondms.core.common.R.string.label_motorista) else getString(br.com.fusiondms.core.common.R.string.label_ajudante)
        val empresaId = binding.etEmpresaId.text.toString()
        val matricula = binding.etMatricula.text.toString()

        val mensagem = "${getString(br.com.fusiondms.core.common.R.string.label_id_empresa)}: <b>$empresaId</b><br>" +
                "${getString(br.com.fusiondms.core.common.R.string.label_matricula)}: <b>$matricula</b><br>" +
                "${getString(br.com.fusiondms.core.common.R.string.label_funcao)}: <b>$funcao</b>"

        Dialog(
            getString(br.com.fusiondms.core.common.R.string.label_confirmar_dados),
            mensagem,
            getString(br.com.fusiondms.core.common.R.string.label_confirmar),
            getString(br.com.fusiondms.core.common.R.string.label_cancelar),
            acaoPositiva = {

            },
            acaoNegativa = {
                findNavController().navigateUp()
            }
        ).show(requireActivity().supportFragmentManager, Dialog.TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}