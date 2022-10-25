package br.com.fusiondms.modcommon.bottomdialog

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.fusiondms.modcommon.databinding.DialogAcaoBinding
import br.com.fusiondms.modcommon.databinding.DialogAvisoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DialogAviso(
    private val titulo: String,
    private val mensagem: String,
    private val textoBotaoConfirmacao: String
) : BottomSheetDialogFragment() {

    private var _binding : DialogAvisoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAvisoBinding.inflate(inflater)
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
            btnConfirmar.text = textoBotaoConfirmacao
        }
    }

    override fun onResume() {
        super.onResume()
        iniciarListener()
    }
    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismiss()
    }

    private fun iniciarListener() {
        binding.btnConfirmar.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "DialogAviso"
    }
}