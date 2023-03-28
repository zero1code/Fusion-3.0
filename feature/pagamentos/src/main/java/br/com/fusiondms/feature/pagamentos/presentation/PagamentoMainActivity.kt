package br.com.fusiondms.feature.pagamentos.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getFont
import br.com.fusiondms.core.common.*
import br.com.fusiondms.core.common.R.dimen
import br.com.fusiondms.core.common.R.font
import br.com.fusiondms.core.common.snackbar.TipoMensagem
import br.com.fusiondms.core.common.snackbar.showMessage
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.core.model.recebimento.TipoPagamento
import br.com.fusiondms.feature.pagamentos.databinding.ActivityPagamentoMainBinding
import br.com.fusiondms.feature.pagamentos.utils.getTipoErp
import br.com.fusiondms.feature.pagamentos.utils.toPagamentoFusion
import java.math.BigDecimal

class PagamentoMainActivity : AppCompatActivity() {

    private var _binding: ActivityPagamentoMainBinding? = null
    private val binding get() = _binding!!

    private var tipoPagamento: TipoPagamento? = null
    private var novoRecebimento: Recebimento? = null
    private var valorPagamento = BigDecimal.ZERO
    private var radioButtonSelecionado = ""

    private val pagamentoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val recebimento = getSerializable(data, RECEBIMENTO_KEY, Recebimento::class.java)
                confirmarPagamento(recebimento)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPagamentoMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this) {
            if (novoRecebimento == null) {
                finish()
            } else {
                inserirRecebimento(novoRecebimento!!)
            }
        }

        tipoPagamento = getSerializable(this, TIPO_PAGAMENTO_KEY, TipoPagamento::class.java)
        tipoPagamento?.let { bindTipoPagamento(it) } ?: tipoPagamentoNaoFoiEncontrado()
        valorPagamento = getSerializable(this, VALOR_PAGAMENTO_KEY, BigDecimal::class.java)

        bindListeners()
    }

    private fun bindListeners() {
        binding.rgFormaPagamento.setOnCheckedChangeListener { group, checkedId ->
            val radioButtonChecked = group.findViewById<RadioButton>(checkedId)
            val radioButtonValue = radioButtonChecked.text.toString()
            radioButtonSelecionado = radioButtonValue
        }

        binding.btnProximo.setOnClickListener { tipoPagamentoActivity() }

        binding.ivCompartilhar.setOnClickListener {
            binding.root.showMessage("Compartilhar", TipoMensagem.NORMAL)
        }
    }

    private fun tipoPagamentoNaoFoiEncontrado() {
        finish()
    }

    private fun bindTipoPagamento(pagamento: TipoPagamento) {
        val lsitaPagamentoErp = pagamento.formasPagamento.split(",")
        val pairPagamentoFusion = lsitaPagamentoErp.map { pagamentoErp ->
            pagamentoErp.toPagamentoFusion().descricao to pagamentoErp.toPagamentoFusion().icone
        }
        val pagamentoPadrao = pagamento.tipoFusion
        radioButtonSelecionado = pagamentoPadrao

        val fontFamily = getFont(this, font.my_poppins_font)

        val layoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.MATCH_PARENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        )
        val margin = resources.getDimension(dimen.dimen_medium).toInt()
        layoutParams.setMargins(0, margin, 0, margin)
        pairPagamentoFusion.forEachIndexed { index, formaPagamento ->
            val radioButton = RadioButton(this)
            radioButton.isChecked = formaPagamento.first == pagamentoPadrao
            radioButton.text = formaPagamento.first
            radioButton.id = index
            radioButton.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(dimen.medium_text)
            )
            radioButton.typeface = fontFamily
            radioButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, formaPagamento.second, 0)
            radioButton.layoutParams = layoutParams
            binding.rgFormaPagamento.addView(radioButton)
        }
    }

    private fun tipoPagamentoActivity() {
        val intent = when (radioButtonSelecionado.uppercase()) {
            "DINHEIRO" -> Intent(this, PagamentoDinheiroActivity::class.java)
            "PIX" -> Intent(this, PagamentoPixActivity::class.java)
            "CARTÃO DE DÉBITO" -> Intent(this, PagamentoCartaoActivity::class.java)
            "CARTÃO DE CRÉDITO" -> Intent(this, PagamentoCartaoActivity::class.java)
            else -> null
        }

        if (intent != null) {
            intent.putExtra(VALOR_PAGAMENTO_KEY, valorPagamento)
            intent.putExtra(TIPO_PAGAMENTO_KEY, radioButtonSelecionado)
            pagamentoLauncher.launch(intent)
        } else {
            binding.btnProximo.showMessage(
                getString(R.string.label_forma_pagamento_nao_encontrada),
                TipoMensagem.ERROR,
                true
            )
        }
    }

    private fun confirmarPagamento(recebimento: Recebimento?) {
        novoRecebimento = recebimento
        if (recebimento != null) {
            setStatusBarColor(
                ContextCompat
                    .getColor(applicationContext, R.color.brand_green_success), true
            )
            binding.apply {
                vfPagamento.displayedChild = 1
                tvFormaPagamento.text = Html.fromHtml(
                    getString(R.string.label_forma_pagamento, radioButtonSelecionado),
                    0
                )
                tvDataPagamento.text = Html.fromHtml(
                    getString(
                        R.string.label_data_pagamento,
                        converterDataParatextoLegivel(recebimento.dataRecebimento)
                    ), 0
                )
                tvValor.text = Html.fromHtml(
                    getString(R.string.label_valor_pagamento, recebimento.valor.toMoedaLocal()),
                    0
                )
            }
        } else {
            //ERRO, MUDAR O LAYOUT
        }
    }

    private fun inserirRecebimento(recebimento: Recebimento) {
        recebimento.run {
            tipo = radioButtonSelecionado.getTipoErp()
            formaPagamento = radioButtonSelecionado
            val resultIntent = Intent().apply {
                putExtra(RECEBIMENTO_KEY, recebimento)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        const val TIPO_PAGAMENTO_KEY = "tipo_pagamento"
        const val VALOR_PAGAMENTO_KEY = "valor_pagamento"
        const val RECEBIMENTO_KEY = "recebimento"
    }
}