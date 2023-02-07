package br.com.fusiondms.feature.jornadatrabalho.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.core.common.bottomdialog.Dialog
import br.com.fusiondms.core.common.hideFabAnimation
import br.com.fusiondms.core.common.hideKeyboard
import br.com.fusiondms.core.common.moveInAnimation
import br.com.fusiondms.core.common.progressAnimation
import br.com.fusiondms.core.common.snackbar.TipoMensagem
import br.com.fusiondms.core.common.snackbar.showMessage
import br.com.fusiondms.core.model.jornadatrabalho.Colaborador
import br.com.fusiondms.feature.jornadatrabalho.databinding.FragmentAdicionarColaboradorBinding
import br.com.fusiondms.feature.jornadatrabalho.presentation.viewmodel.JornadaTrabalhoViewModel
import br.com.fusiondms.core.common.R

class AdicionarColaboradorFragment : Fragment() {

    private var _binding: FragmentAdicionarColaboradorBinding? = null
    private val binding get() = _binding!!
    private val jornadaViewModel: JornadaTrabalhoViewModel by activityViewModels()

    private var radioButtonSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdicionarColaboradorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindObservers()
        bindListeners()
    }

    private fun bindObservers() {
        lifecycleScope.launchWhenStarted {
            jornadaViewModel.cadastrarColaborador.collect { result ->
                when(result) {
                    is JornadaTrabalhoViewModel.JornadaStatus.SuccessCadastro -> {
                        binding.root.showMessage("Colaborador cadastrado", TipoMensagem.SUCCESS)
                        findNavController().navigateUp()
                    }
                    is JornadaTrabalhoViewModel.JornadaStatus.ErroRegistro -> {
                        binding.root.showMessage(result.message, TipoMensagem.ERROR)
                        findNavController().navigateUp()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun bindListeners() {
        binding.apply {
            btnNext.setOnClickListener {
                if (checarCampo(vfFormulario.displayedChild)) {
                    vfFormulario.showNext()
                    linearProgressIndicator.progressAnimation(100 / vfFormulario.childCount)
                    if (vfFormulario.displayedChild == vfFormulario.childCount - 1) {
                        requireContext().hideKeyboard(it)
                        btnNext.hideFabAnimation(0F)
                    }
                }
            }

            rgFuncao.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId != -1 && !radioButtonSelected) {
                    radioButtonSelected = true
                    linearProgressIndicator.progressAnimation(100 - linearProgressIndicator.progress)
                    btnCadastrar.moveInAnimation()
                }
            }

            btnVoltar.setOnClickListener {
                findNavController().navigateUp()
            }

            btnCadastrar.setOnClickListener {
                confirmarFormulario()
            }
        }
    }

    private fun checarCampo(displayedChild: Int) : Boolean {
        when (displayedChild) {
            0 -> {
                if (binding.etMatricula.text.isEmpty()) {
                    binding.etMatricula.error = getString(R.string.label_digite_matricula)
                    return false
                }
            }
            1 -> {
                if (binding.etNome.text.isEmpty()) {
                    binding.etNome.error = getString(R.string.label_digite_nome)
                    return false
                }
            }
        }
        return true
    }

    private fun confirmarFormulario() {
        val funcao = if (binding.rgFuncao.checkedRadioButtonId == br.com.fusiondms.feature.jornadatrabalho.R.id.rb_motorista)
            getString(R.string.label_motorista) else getString(R.string.label_ajudante)
        val nome = binding.etNome.text.toString()
        val matricula = binding.etMatricula.text.toString()

        val mensagem = "${getString(R.string.label_matricula)}: <b>$matricula</b><br>" +
                "${getString(R.string.label_nome)}: <b>$nome</b><br>" +
                "${getString(R.string.label_funcao)}: <b>$funcao</b>"

        Dialog(
            getString(R.string.label_confirmar_dados),
            mensagem,
            getString(R.string.label_confirmar),
            getString(R.string.label_cancelar),
            acaoPositiva = {
                cadastrarColaborador(nome, matricula.toInt(), funcao)
            },
            acaoNegativa = {
                findNavController().navigateUp()
            }
        ).show(requireActivity().supportFragmentManager, Dialog.TAG)
    }

    private fun cadastrarColaborador(nome: String, matricula: Int, funcao: String) {
        val colaborador = Colaborador(matricula, nome, funcao)
        jornadaViewModel.inserirColaborador(colaborador)
    }

    override fun onDestroy() {
        super.onDestroy()
        jornadaViewModel.resetJornadaState()
        _binding = null
    }
}