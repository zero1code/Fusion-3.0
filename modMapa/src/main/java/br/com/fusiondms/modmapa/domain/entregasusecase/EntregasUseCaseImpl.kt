package br.com.fusiondms.modmapa.domain.entregasusecase

import br.com.fusiondms.moddatabase.repository.entregas.EntregasRepository
import br.com.fusiondms.modmodel.Entrega
import javax.inject.Inject

class EntregasUseCaseImpl @Inject constructor(
    private val entregasRepository: EntregasRepository
) : EntregasUseCase {

    override suspend fun getListaEntrega(): List<Entrega> {
        return entregasRepository.getListaEntrega()
    }
}