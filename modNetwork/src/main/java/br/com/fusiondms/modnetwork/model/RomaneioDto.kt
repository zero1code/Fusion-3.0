package br.com.fusiondms.modnetwork.model

import br.com.fusiondms.modmodel.romaneio.Romaneio
import br.com.fusiondms.modnetwork.mapper.DtoMapper
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class RomaneioDto(
    @SerializedName("id_romaneio")
    val idRomaneio: Int = 0
) : DtoMapper<Romaneio, RomaneioDto> {
    override fun mapDtoToModel(dto: RomaneioDto): Romaneio {
        val json = Gson().toJson(dto)
        return Gson().fromJson(json, Romaneio::class.java)
    }

    override fun mapModelToDto(model: Romaneio): RomaneioDto {
        val json = Gson().toJson(model)
        return Gson().fromJson(json, RomaneioDto::class.java)
    }
}