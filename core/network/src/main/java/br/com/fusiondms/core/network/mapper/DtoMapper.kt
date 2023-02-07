package br.com.fusiondms.core.network.mapper

interface DtoMapper<Model, Dto> {
    fun mapDtoToModel(dto: Dto): Model
    fun mapModelToDto(model: Model): Dto
}