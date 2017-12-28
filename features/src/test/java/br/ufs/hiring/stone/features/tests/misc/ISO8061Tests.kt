package br.ufs.hiring.stone.features.tests.misc

import br.ufs.hiring.stone.features.util.ISO8601
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class ISO8061Tests {

    private val unzoned = "2017-12-26T03:12:31.821"
    private val unzonedMinusMillis = "2017-12-26T03:12:31"

    private val dateTime by lazy {
        val calendar = Calendar.getInstance(Locale.getDefault())

        calendar.set(
                2017,
                11,
                26,
                3,
                12,
                31
        )

        calendar.time
    }

    @Test fun `should obtain date from unzoned ISO8601 string`() {
        val expected = ISO8601.dateFromUnzonedString(unzoned)

        assertThat(expected)
                .hasYear(2017)
                .hasMonth(12)
                .hasDayOfMonth(26)
                .hasHourOfDay(3)
                .hasMinute(12)
                .hasSecond(31)
                .hasMillisecond(0)
    }


    @Test fun `should obtain unzoned ISO8601 string from date`() {
        val expected = ISO8601.unzonedStringFromDate(dateTime)
        assertThat(expected).isEqualTo(unzonedMinusMillis)
    }

    @Test fun `should obtain pretty format string from date`() {
        val expected = ISO8601.asBrazillianDateFormat(dateTime)
        assertThat(expected).isEqualTo("26/12/2017")
    }

}