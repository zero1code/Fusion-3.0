package br.com.fusiondms.core.database.mapper

interface EntityMapper<Model, Entity> {
    fun mapEntityToModel(entity: Entity): Model
    fun mapModelToEntity(model: Model): Entity
}