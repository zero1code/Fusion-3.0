package br.com.fusiondms.modcommon.progressdialog

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.app.AlertDialog
import br.com.fusiondms.modcommon.R
import br.com.fusiondms.modcommon.databinding.ProgressLayoutBinding
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