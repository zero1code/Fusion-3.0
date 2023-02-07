package br.com.fusiondms.core.common.progressdialog

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.common.databinding.ProgressLayoutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Activity.showProgressBar(titulo: String): AlertDialog {
    val builder = MaterialAlertDialogBuilder(this)
    val inflater = this.layoutInflater
    val binding = ProgressLayoutBinding.inflate(inflater)

    binding.tvTitulo.text = titulo

    builder.setView(binding.root)
    builder.setCancelable(true)
    builder.background = ColorDrawable(Color.TRANSPARENT)

    val dialog = builder.create()
    dialog.window!!.setWindowAnimations(R.style.Theme_Dialog_Animation)

    return dialog
}