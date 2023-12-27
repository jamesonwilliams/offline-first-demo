package org.nosemaj.cra.data.net

import com.squareup.moshi.JsonClass
import org.nosemaj.cra.data.AppointmentModel

@JsonClass(generateAdapter = true)
data class AppointmentListResponse(
    val info: Info,
    val results: List<AppointmentModel>
) {
    @JsonClass(generateAdapter = true)
    data class Info(
        val count: Int,
        val next: String?,
        val pages: Int,
        val prev: String?
    )
}