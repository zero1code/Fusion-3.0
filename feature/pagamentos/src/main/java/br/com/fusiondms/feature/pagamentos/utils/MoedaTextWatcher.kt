package br.com.fusiondms.feature.pagamentos.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class MoedaTextWatcher(private val editText: EditText) : TextWatcher {

    val editTextWeakReference = WeakReference(editText)
    val locale = Locale.getDefault()

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(editable: Editable?) {
        val editText = editTextWeakReference.get() ?: return
        editText.removeTextChangedListener(this)

        val parsed = parseToBigDecimal(editable.toString())
        val formatted = NumberFormat.getCurrencyInstance(locale).format(parsed)

        val replaceable = String.format("%s", getCurrencySymbol())
        val cleanString = formatted.replace(replaceable, replaceable)

        editText.setText(cleanString)
        editText.setSelection(cleanString.length)
        editText.addTextChangedListener(this)
    }

    private fun parseToBigDecimal(value: String) : BigDecimal {
        val replaceable = String.format("[%s,.\\s]", getCurrencySymbol())
        val cleanString = value.replace(replaceable.toRegex(), "")

        return try {
            BigDecimal(cleanString).setScale(2).divide(BigDecimal(100), RoundingMode.FLOOR)
        } catch (e: NumberFormatException) {
            BigDecimal(0)
        }
    }
}

fun formatPrice(price: String): String {
    //Ex - price = 2222
    //retorno = 2222.00
    val df = DecimalFormat("0.00")
    return df.format(java.lang.Double.valueOf(price)).toString()
}

fun valorPagamentoParaSalvar(valor: String) : String {

    //Ex - price = $ 5555555
    //return = 55555.55 para salvar no banco de dados
    val replaceable = String.format("[%s,.\\s]", getCurrencySymbol())
    val cleanString: String = valor.replace(replaceable.toRegex(), "")
    val stringBuilder = StringBuilder(cleanString.replace(" ".toRegex(), ""))

    return stringBuilder.insert(cleanString.length - 2, '.').toString()
}

fun getCurrencySymbol(): String? {
    return Objects.requireNonNull(
        NumberFormat.getCurrencyInstance(Locale.getDefault()).currency
    ).symbol
}