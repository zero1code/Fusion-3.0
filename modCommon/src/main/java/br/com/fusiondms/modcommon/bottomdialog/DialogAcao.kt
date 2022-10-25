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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DialogAcao(
    private val titulo: String,
    private val mensagem: String,
    private val textoBotaoPositivo: String,
    private val textoBotaoNegativo: String,
    acao: (() -> Unit)? = null
) : BottomSheetDialogFragment() {

    private var _binding : DialogAcaoBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: IDialogAcaoListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAcaoBinding.inflate(inflater)
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

    fun setListener(listener: IDialogAcaoListener) {
        this.listener = listener
    }

    private fun iniciarListener() {
        binding.btnConfirmar.setOnClickListener {
            listener.onClickPositivo()
            dismiss()
        }

        binding.btnRecusar.setOnClickListener {
            listener.onClickNegativo()
            dismiss()
        }
    }


    interface IDialogAcaoListener {
        fun onClickPositivo()
        fun onClickNegativo()
    }

    companion object {
        const val TAG = "DialogAcao"
    }
}