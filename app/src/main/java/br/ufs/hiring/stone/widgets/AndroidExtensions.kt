package br.ufs.hiring.stone.widgets

import android.app.Activity
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import kotlin.reflect.KClass

/**
 *
 * Created by @ubiratanfsoares
 *
 */

fun Snackbar.colorForActionText(colorResource: Int): Snackbar {
    setActionTextColor(ContextCompat.getColor(this.context, colorResource))
    return this
}

fun Snackbar.action(actionText: Int, block: (Any) -> Unit): Snackbar {
    setAction(actionText, View.OnClickListener(block))
    return this
}

fun Activity.toast(messageResource: Int) {
    Toast.makeText(this, messageResource, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun <T : Any> Activity.launch(target: KClass<T>) {
    val intent = Intent(this, target.java)
    startActivity(intent)
}