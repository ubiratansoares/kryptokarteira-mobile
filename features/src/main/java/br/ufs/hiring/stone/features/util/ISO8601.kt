package br.ufs.hiring.stone.features.util

import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

object ISO8601 {

    private val iso8601Formatter by lazy { SimpleDateFormat(UNZONED_ISO8601, HERE) }
    private val prettyFormatter by lazy { SimpleDateFormat(BRAZILIAN_PRETTY_DATE, HERE) }

    fun dateFromUnzonedString(timestamp: String): Date {
        return iso8601Formatter.parse(timestamp)
    }

    fun unzonedStringFromDate(date: Date): String {
        return iso8601Formatter.format(date)
    }

    fun asBrazillianDateFormat(target: Date): String {
        return prettyFormatter.format(target)
    }

    private val UNZONED_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss"
    private val BRAZILIAN_PRETTY_DATE = "dd/MM/yyyy"
    private val HERE = Locale.getDefault()
}