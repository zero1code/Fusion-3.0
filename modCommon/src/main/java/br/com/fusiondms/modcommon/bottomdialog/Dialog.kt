package br.com.fusiondms.modcommon.bottomdialog

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.fusiondms.modcommon.databinding.DialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class Dialog(
    private val titulo: String,
    private val mensagem: String,
    private val textoBotaoPositivo: String,
    private val textoBotaoNegativo: String,
    acaoPositiva: (() -> Unit)?,
    acaoNegativa: (() -> Unit)?
) : BottomSheetDialogFragment() {

    private var _binding : DialogBinding? = null
    private val binding get() = _binding!!
    private val acaoPositivaListener = acaoPositiva
    private val acaoNegativaListener = acaoNegativa

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)

        binding.apply {
            tvTitulo.text = titulo
            tvMensagem.text = mensagem
            btnConfirmar.text = textoBotaoPositivo
            btnRecusar.text = textoBotaoNegativo

            btnRecusar.visibility = if (acaoNegativaListener == null) View.GONE else View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        bindListeners()
    }
    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }

    private fun bindListeners() {
        binding.btnConfirmar.setOnClickListener {
            acaoPositivaListener?.invoke()
            dismiss()
        }

        binding.btnRecusar.setOnClickListener {
            acaoNegativaListener?.invoke()
            dismiss()
        }
    }

    companion object {
        const val TAG = "Dialog"
    }
}