package br.com.fusiondms.core.common.permissiondiaolog

import  android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.common.databinding.PermissionRequestDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PermissionRequestDialog(
    private val iconeImagem: Int,
    private val titulo: String,
    private val mensagem: String,
    private val textoBotaoAceitar: String,
    private val textoBotaoConfiguracao: String,
    acaoAceitarPermissao: (() -> Unit)?,
    acaoConfiguracoes: (() -> Unit)?
) : BottomSheetDialogFragment() {

    private val acaoAceitarPermissaoListener = acaoAceitarPermissao
    private val acaoConfiguracoesListener = acaoConfiguracoes

    private var _binding: PermissionRequestDialogBinding? = null
    private val binding: PermissionRequestDialogBinding get() = _binding!!

    override fun getTheme(): Int = R.style.PermissionRequestDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PermissionRequestDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.let { d ->
            d.window?.let { w ->
                w.attributes.windowAnimations = R.style.Theme_Dialog_Animation
            }
        }
        val bottomSheet = view.parent as View
        val metrics = Resources.getSystem().displayMetrics

        val bottomSheetBehavior = BottomSheetBehavior.from<View>(bottomSheet)
        bottomSheetBehavior.apply {
            isDraggable = false
            this.state = BottomSheetBehavior.STATE_EXPANDED
            peekHeight = metrics.heightPixels
        }

        val coordinatorLayout = binding.bottomSheetLayout
        coordinatorLayout.minimumHeight = metrics.heightPixels

        binding.apply {
            ivIcone.setImageResource(iconeImagem)
            tvTitle.text = titulo
            tvMessage.text = mensagem
            btnAceitarPermissao.text = textoBotaoAceitar
            btnConfiguracoes.text = textoBotaoConfiguracao

            btnAceitarPermissao.setOnClickListener {
                acaoAceitarPermissaoListener?.invoke()
            }

            btnConfiguracoes.setOnClickListener {
                acaoConfiguracoesListener?.invoke()
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "PermissionRequestDialog"
    }
}