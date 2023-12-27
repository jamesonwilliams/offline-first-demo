package org.nosemaj.cra.data.net

import org.nosemaj.cra.data.AppointmentModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AppointmentService {
    @GET("appointment")
    suspend fun listAppointments(@Query("page") page: Int): Response<AppointmentListResponse>

    @GET("appointment/{appointmentId")
    suspend fun getAppointment(appointmentId: Int): Response<AppointmentModel>

    companion object {
        fun create(baseUrl: String = "https://cra.nosemaj.org/v1/"): AppointmentService {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(AppointmentService::class.java)
        }
    }
}