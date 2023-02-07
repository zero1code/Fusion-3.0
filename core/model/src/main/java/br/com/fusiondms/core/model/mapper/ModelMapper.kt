package br.com.fusiondms.core.model.mapper

interface ModelMapper<Model, T : Any> {
    fun mapToEntity(model: Model) : T
    fun mapToDTO(model: Model) : T
}