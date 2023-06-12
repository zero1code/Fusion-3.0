package br.com.fusiondms.feature.mapa.bottomdialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import br.com.fusiondms.core.common.snackbar.TipoMensagem
import br.com.fusiondms.core.common.snackbar.exibirMensagemSnack
import br.com.fusiondms.feature.mapa.R
import br.com.fusiondms.feature.mapa.databinding.DialogRaioClienteBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.concurrent.TimeUnit


class DialogRaioCliente(
    private val titulo: String,
    private val mensagem: String,
    private val textoBotaoPositivo: String,
    private val textoBotaoNegativo: String,
    private val acaoPositiva: (() -> Unit)?,
    private val acaoNegativa: (() -> Unit)?
) : BottomSheetDialogFragment() {

    private var _binding : DialogRaioClienteBinding? = null
    private val binding get() = _binding!!

    private var somNotificacao: MediaPlayer? = null
    private lateinit var contador: Contador

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRaioClienteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)


        binding.apply {
            startContador()
            playNotificationSong()
            tvTitulo.text = titulo
            tvMensagem.text = Html.fromHtml(mensagem, 0)
            btnConfirmar.text = textoBotaoPositivo
            btnRecusar.text = textoBotaoNegativo

            btnRecusar.visibility = if (acaoNegativa == null) View.GONE else View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        bindListeners()
    }
    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        stopNotificationSong()
        stopContador()
        dismiss()
    }

    private fun bindListeners() {
        binding.btnConfirmar.setOnClickListener {
            stopNotificationSong()
            acaoPositiva?.invoke()
            dismiss()
        }

        binding.btnRecusar.setOnClickListener {
            stopNotificationSong()
        }
    }

    private fun playNotificationSong() {
        somNotificacao = MediaPlayer.create(context, R.raw.notificacao)
        somNotificacao?.let {
            it.setVolume(2F, 2F)
            it.isLooping = true
            it.start()
        }
    }

    private fun stopNotificationSong() {
        somNotificacao?.stop()
        somNotificacao?.release()
        somNotificacao = null
    }

    private fun startContador() {
        contador = Contador()
        contador.start()
    }

    private fun stopContador() {
        contador.onFinish()
        contador.cancel()
    }

    inner class Contador()
        : CountDownTimer(TimeUnit.SECONDS.toMillis(30),
        TimeUnit.SECONDS.toMillis(1)
    ) {

        override fun onTick(millisUntilFinished: Long) {
            val segundos = millisUntilFinished / 1000
            binding.tvContador.text = String.format("%02d", segundos)
        }

        override fun onFinish() {
            stopNotificationSong()
            acaoPositiva?.invoke()
            dismiss()
        }
    }

    companion object {
        const val TAG = "DialogRaioCliente"
    }
}