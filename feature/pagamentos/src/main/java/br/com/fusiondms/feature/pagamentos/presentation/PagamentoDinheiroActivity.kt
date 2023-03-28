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
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.feature.pagamentos.R.anim
import br.com.fusiondms.feature.pagamentos.databinding.ActivityPagamentoDinheiroBinding
import br.com.fusiondms.feature.pagamentos.utils.MoedaTextWatcher
import br.com.fusiondms.feature.pagamentos.utils.formatPrice
import br.com.fusiondms.feature.pagamentos.utils.valorPagamentoParaSalvar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant

class PagamentoDinheiroActivity : AppCompatActivity() {
    private var _binding: ActivityPagamentoDinheiroBinding? = null
    private val binding get() = _binding!!

    private var valorPagamento = BigDecimal.ZERO
    private var progressCounter = 0
    private var isPagamentoConfirmado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPagamentoDinheiroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        valorPagamento = getSerializable(this, PagamentoMainActivity.VALOR_PAGAMENTO_KEY, BigDecimal::class.java)
        bindEtValor()

        timerConfirmarPagamento()
        bindListeners()
    }

    private fun bindListeners() {
        binding.btnConfirmarPagamento.setOnClickListener { isPagamentoConfirmado = true }
    }

    private fun bindEtValor() {
        binding.apply {
            etValor.addTextChangedListener(MoedaTextWatcher(etValor))
            etValor.setText(formatPrice(valorPagamento.toString()))
        }
    }

    private fun timerConfirmarPagamento() {
        lifecycleScope.launch {
            progressCounter++
            binding.progressCircular.setProgressCompat(progressCounter, true)
            if (progressCounter == 100 || isPagamentoConfirmado) {
                progressCounter = 0
                binding.progressCircular.setProgressCompat(progressCounter, false)
                if (isPagamentoConfirmado) pagamentoConfirmado() else timerConfirmarPagamento()
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
                        R.color.brand_green_success
                    )
                )
                ivDinheiro.setImageResource(R.drawable.ic_check)
                ivDinheiro.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                ivDinheiro.fadeInZoomInAnimation()
                tvStatus.text = Html.fromHtml(getString(R.string.label_pagamento_efetuado_obrigado), 0)

                delay(2000)

                cvQrcode.scaleXYAnimation()
                ivDinheiro.setImageDrawable(null)
                ivDinheiro.fadeOutAnimation()
                setStatusBarColor(
                    ContextCompat
                        .getColor(applicationContext, R.color.brand_green_success), true
                )
                inserirRecebimento()
            }
        }
    }

    private fun inserirRecebimento() {
        val valorPagamento = valorPagamentoParaSalvar(binding.etValor.text.toString())
        if (valorPagamento != "0") {
            val recebimento = Recebimento(
                valor = BigDecimal(valorPagamento),
                dataRecebimento = Instant.now().epochSecond
            )

            val resultIntent = Intent().apply {
                putExtra(PagamentoMainActivity.RECEBIMENTO_KEY, recebimento)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
            overridePendingTransition(anim.fade_in, anim.fade_out)
        }
    }
}