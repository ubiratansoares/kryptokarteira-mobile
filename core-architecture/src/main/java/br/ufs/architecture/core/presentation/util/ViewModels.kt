package br.ufs.architecture.core.presentation.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import android.view.View

/**
 *
 * Created by @ubiratanfsoares
 *
 */

typealias Screen = ViewModel

@Suppress("UNCHECKED_CAST")
inline fun <reified VM : Screen> FragmentActivity.screenProvider(
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
        crossinline provider: () -> VM) = lazy(mode) {

    val factory = object : ViewModelProvider.Factory {
        override fun <Model : Screen> create(klass: Class<Model>) = provider() as Model
    }

    ViewModelProviders.of(this, factory).get(VM::class.java)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified VM : Screen> View.screenProvider(
        mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
        crossinline provider: () -> VM) = lazy(mode) {

    val factory = object : ViewModelProvider.Factory {
        override fun <Model : Screen> create(klass: Class<Model>) = provider() as Model
    }

    ViewModelProviders.of(this.context as FragmentActivity, factory).get(VM::class.java)
}