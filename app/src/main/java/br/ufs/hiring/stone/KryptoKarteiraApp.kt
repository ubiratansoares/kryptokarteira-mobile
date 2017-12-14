package br.ufs.hiring.stone

import android.app.Application
import br.ufs.hiring.stone.di.Injection
import com.github.salomonbrys.kodein.KodeinAware

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class KryptoKarteiraApp : Application(), KodeinAware {

    override val kodein by Injection(this).graph

}