package com.loan555.kisdapplication2.common

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun Context.showAlert(title: String?, message: String?, callback: () -> Unit = {}) {
    val dialogBuilder = AlertDialog.Builder(this)
    dialogBuilder.setTitle(title)
    dialogBuilder.setMessage(message)
    dialogBuilder.setPositiveButton("OK") { _, _ ->
        callback()
    }
    val dialog = dialogBuilder.create()
    dialog.show()
}

fun Context.showDialogConfirm(
    message: String,
    positiveBtnStr: String,
    negativeBtnStr: String,
    callbackBtnNeg: () -> Unit = {},
    callback: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(message)
    builder.setPositiveButton(positiveBtnStr) { dialog, _ ->
        callback()
        dialog.dismiss()
    }
    builder.setNegativeButton(negativeBtnStr) { dialog, _ ->
        callbackBtnNeg()
        dialog.dismiss()
    }
    builder.setCancelable(false)
    val alert: AlertDialog = builder.create()
    alert.show()
    val positive = alert.getButton(AlertDialog.BUTTON_POSITIVE)
    val negative = alert.getButton(AlertDialog.BUTTON_NEGATIVE)
    negative.isSingleLine = true
    positive.isSingleLine = true
}