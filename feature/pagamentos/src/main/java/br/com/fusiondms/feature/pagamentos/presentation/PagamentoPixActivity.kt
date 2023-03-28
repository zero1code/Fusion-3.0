package br.com.fusiondms.feature.pagamentos.presentation

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import br.com.fusiondms.core.common.*
import br.com.fusiondms.core.common.R.color
import br.com.fusiondms.core.common.R.drawable
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.feature.pagamentos.R.*
import br.com.fusiondms.feature.pagamentos.databinding.ActivityPagamentoPixBinding
import br.com.fusiondms.feature.pagamentos.utils.valorPagamentoParaSalvar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

class PagamentoPixActivity : AppCompatActivity() {
    private var _binding: ActivityPagamentoPixBinding? = null
    private val binding get() = _binding!!

    private var progressCounter = 30
    private var valorPagamento = BigDecimal.ZERO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPagamentoPixBinding.inflate(layoutInflater)
        setContentView(binding.root)

        valorPagamento =
            getSerializable(this, PagamentoMainActivity.VALOR_PAGAMENTO_KEY, BigDecimal::class.java)
        binding.tvValor.text = valorPagamento.toMoedaLocal()

        gerarQrCode()
    }

    private fun gerarQrCode() {
        Timer("pix").schedule(timerTask {
            runOnUiThread {
                binding.apply {
                    ivQrcode.setImageResource(drawable.ic_qrcode_teste)
                    ivQrcode.fadeInZoomInAnimation()
                    tvStatus.text = getString(R.string.label_ler_qrcode)
                    progressCircular.setIndicatorColor(
                        ContextCompat.getColor(
                            applicationContext,
                            color.brand_celtic_blue
                        )
                    )
                    progressCircular.isIndeterminate = false
                    timerConfirmarPagamento()
                }
            }
        }, TimeUnit.SECONDS.toMillis(3))
    }

    private fun timerConfirmarPagamento() {
        lifecycleScope.launch {
            progressCounter++
            binding.progressCircular.setProgressCompat(progressCounter, true)
            if (progressCounter == 100) {
                progressCounter = 0
                binding.progressCircular.setProgressCompat(progressCounter, false)
                pagamentoConfirmado()
            } else {
                delay(100)
                timerConfirmarPagamento()
            }
        }
    }

    private fun pagamentoConfirmado() {
        lifecycleScope.launch {
            binding.apply {
                cvQrcode.setCardBackgroundColor(
                    ContextCompat.getColor(
                        applicationContext,
                        color.brand_green_success
                    )
                )
                ivQrcode.setImageResource(drawable.ic_check_big)
                ivQrcode.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                ivQrcode.fadeInZoomInAnimation()
                tvStatus.text = Html.fromHtml(getString(R.string.label_pagamento_efetuado_obrigado), 0)

                delay(2000)

                cvQrcode.scaleXYAnimation()
                ivQrcode.setImageDrawable(null)
                ivQrcode.fadeOutAnimation()
                setStatusBarColor(
                    ContextCompat
                        .getColor(applicationContext, color.brand_green_success), true
                )
                inserirRecebimento()
            }
        }
    }

    private fun inserirRecebimento() {
        val valorPagamento = valorPagamentoParaSalvar(binding.tvValor.text.toString())
        if (valorPagamento != "0") {
            val recebimento = Recebimento(
                valor = BigDecimal(valorPagamento),
                dataRecebimento = Instant.now().epochSecond,
                txid = "269Q80"
            )

            val resultIntent = Intent().apply {
                putExtra(PagamentoMainActivity.RECEBIMENTO_KEY, recebimento)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
            overridePendingTransition(anim.fade_in, anim.fade_out)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timer("pix").cancel()
    }
}