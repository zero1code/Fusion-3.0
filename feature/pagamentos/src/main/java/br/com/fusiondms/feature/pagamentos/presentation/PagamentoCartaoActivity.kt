package br.com.fusiondms.feature.pagamentos.presentation

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import br.com.fusiondms.core.common.*
import br.com.fusiondms.core.common.R.*
import br.com.fusiondms.core.common.bottomdialog.Dialog
import br.com.fusiondms.core.common.snackbar.TipoMensagem
import br.com.fusiondms.core.common.snackbar.showMessage
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.feature.pagamentos.R
import br.com.fusiondms.feature.pagamentos.databinding.ActivityPagamentoCartaoBinding
import br.com.fusiondms.feature.pagamentos.utils.MoedaTextWatcher
import br.com.fusiondms.feature.pagamentos.utils.formatPrice
import br.com.fusiondms.feature.pagamentos.utils.valorPagamentoParaSalvar
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant

class PagamentoCartaoActivity : AppCompatActivity() {
    private var _binding: ActivityPagamentoCartaoBinding? = null
    private val binding get() = _binding!!

    private var valorPagamento = BigDecimal.ZERO
    private var progressCounter = 0
    private var isPagamentoConfirmado = false
    private var tipoPagamento = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPagamentoCartaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        valorPagamento =
            getSerializable(this, PagamentoMainActivity.VALOR_PAGAMENTO_KEY, BigDecimal::class.java)
        tipoPagamento = intent.getStringExtra(PagamentoMainActivity.TIPO_PAGAMENTO_KEY).toString()
        binding.tvTitulo.text = getString(string.label_pagamento_com, tipoPagamento)
        bindEtValor()

        dialogAvisoMaquinaDePagamento()
        timerConfirmarPagamento()
        bindListeners()
    }

    private fun bindListeners() {
        binding.btnPagar.setOnClickListener { isPagamentoConfirmado = true }
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
                        color.brand_green_success
                    )
                )
                ivCartao.setImageResource(drawable.ic_check)
                ivCartao.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                ivCartao.fadeInZoomInAnimation()
                tvStatus.text = Html.fromHtml(getString(string.label_pagamento_efetuado_obrigado), 0)

                delay(2000)

                cvQrcode.scaleXYAnimation()
                ivCartao.setImageDrawable(null)
                ivCartao.fadeOutAnimation()
                setStatusBarColor(
                    ContextCompat
                        .getColor(applicationContext, color.brand_green_success), true
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
                dataRecebimento = Instant.now().epochSecond,
                bandeira = if (tipoPagamento == "Cartão de Crédito") "MASTERCARD" else "VISA"
            )

            val resultIntent = Intent().apply {
                putExtra(PagamentoMainActivity.RECEBIMENTO_KEY, recebimento)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun dialogAvisoMaquinaDePagamento() {
        Dialog(
            getString(string.label_maquina_pagamento),
            getString(string.label_conectar_maquina_pagamento),
            getString(string.label_sim),
            getString(string.label_nao),
            acaoPositiva = {
                binding.btnPagar.showMessage(
                    "Máquina conectada",
                    TipoMensagem.SUCCESS,
                    true
                )
            },
            acaoNegativa = {}
        ).show(supportFragmentManager, Dialog.TAG)
    }
}