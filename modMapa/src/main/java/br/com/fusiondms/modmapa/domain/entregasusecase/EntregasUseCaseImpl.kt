package br.com.fusiondms.modmapa.domain

import br.com.fusiondms.moddatabase.repository.EntregasRepository
import br.com.fusiondms.modmodel.Entrega
import javax.inject.Inject

class EntregasUseCaseImpl @Inject constructor(
    private val entregasRepository: EntregasRepository
) : EntregasUseCase {

    override suspend fun getListaEntrega(): List<Entrega> {
        return entregasRepository.getListaEntrega()
    }
}