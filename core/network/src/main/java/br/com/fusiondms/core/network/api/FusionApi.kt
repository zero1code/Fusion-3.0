package br.com.fusiondms.core.network.api

import br.com.fusiondms.core.network.model.RomaneioDto
import br.com.fusiondms.core.network.model.SincronizacaoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FusionApi {
    @GET("sincronizacao")
    suspend fun getSincronizacao(): Response<SincronizacaoDto>

    @POST("recusar_carga")
    suspend fun postRecusarCarga(@Body romaneioDto: RomaneioDto): Response<RomaneioDto>
}