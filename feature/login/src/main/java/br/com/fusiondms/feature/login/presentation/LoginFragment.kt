package br.com.fusiondms.feature.login.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.core.model.jornadatrabalho.JornadaTrabalho
import br.com.fusiondms.feature.login.R
import br.com.fusiondms.feature.login.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindListeners()
    }

    private fun bindListeners() {
        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().navigate(br.com.fusiondms.core.navigation.R.id.action_loginFragment_to_jornadaTrabalhoActivity)
                activity?.finish()
            }
            btnEsqueciSenha.setOnClickListener {
                findNavController().navigate(br.com.fusiondms.core.navigation.R.id.action_loginFragment_to_esqueciSenhaFragment)
            }
            btnPrimeiroAcesso.setOnClickListener {
                findNavController().navigate(br.com.fusiondms.core.navigation.R.id.action_loginFragment_to_primeiroAcessoFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}