package br.com.fusiondms.modnetwork.mapper

interface DtoMapper<Model, Dto> {
    fun mapDtoToModel(dto: Dto): Model
    fun mapModelToDto(model: Model): Dto
}