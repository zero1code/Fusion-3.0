package br.com.fusiondms.feature.mapa.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.text.TextWatcher
import android.transition.Fade
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.core.common.*
import br.com.fusiondms.core.common.bottomdialog.Dialog
import br.com.fusiondms.core.common.permissiondiaolog.PermissionRequestDialog
import br.com.fusiondms.core.common.snackbar.TipoMensagem
import br.com.fusiondms.core.common.snackbar.exibirMensagemSnack
import br.com.fusiondms.core.common.snackbar.mensagemCurta
import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.model.parametros.Parametros
import br.com.fusiondms.core.notification.EnumNotificacao
import br.com.fusiondms.core.notification.Notificacao
import br.com.fusiondms.core.services.location.ForegroundLocationService
import br.com.fusiondms.feature.entregas.databinding.ItemEntregaChildBinding
import br.com.fusiondms.feature.entregas.presentation.adapter.EntregasParentAdapter
import br.com.fusiondms.feature.entregas.presentation.viewmodel.EntregaViewModel
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_ADIADA
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_CHEGADA_CLIENTE
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_DEVOLVIDA
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_DEVOLVIDA_PARCIAL
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_DEVOLVIDA_TOTAL
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_INICIADA
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_PENDENTE
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_REALIZADA
import br.com.fusiondms.feature.mapa.R.*
import br.com.fusiondms.feature.mapa.bottomdialog.DialogRaioCliente
import br.com.fusiondms.feature.mapa.databinding.EntregasBottomSheetBinding
import br.com.fusiondms.feature.mapa.databinding.EntregasInfoGeraisBinding
import br.com.fusiondms.feature.mapa.databinding.FragmentMapaBinding
import br.com.fusiondms.feature.mapa.databinding.LayoutPesquisarBinding
import br.com.fusiondms.feature.mapa.dialogeventos.DialogEventos
import br.com.fusiondms.feature.mapa.dialogeventos.interfaces.IDialogEventos
import br.com.fusiondms.feature.mapa.presentation.viewmodel.MapaViewModel
import br.com.fusiondms.feature.preferences.presentation.ConfiguracoesActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MapaFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapaBinding? = null
    private var _bindingSheet: EntregasBottomSheetBinding? = null
    private var _bindingSheetInfoGerais: EntregasInfoGeraisBinding? = null
    private var _bindingPesquisarEntregas: LayoutPesquisarBinding? = null

    private val binding get() = _binding!!
    private val bindingSheet get() = _bindingSheet!!
    private val bindingSheetInfoGerais get() = _bindingSheetInfoGerais!!
    private val bindingPesquisarEntregas get() = _bindingPesquisarEntregas!!

    private val entregaViewModel: EntregaViewModel by activityViewModels()
    private val mapaViewModel: MapaViewModel by viewModels()
    private var listaCliente: List<Conteudo> = listOf()
    private var isNoRaioCliente = false

    private val adapterEntregas by lazy { EntregasParentAdapter() }

    private lateinit var mapaGoogle: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var permissionRequestDialog: PermissionRequestDialog? = null

    private lateinit var dialogEventos: DialogEventos

    private var clientePosicao = -1
    private var chipSelecionadoId = -1
    private var chipSelecionado: Chip? = null

    private lateinit var parametros: Parametros

    private lateinit var contador: Contador
    private var mostrarTempo = false
    private var isTempoEspera = true
    private var alturaMaximaSheetInfo = 229

    private var isMapaCarregado = false
    private val raioMarkerMap: MutableMap<Circle, Marker> = mutableMapOf()

    private var foregroundLocationServiceBound = false
    // Fornece a atualizações de localização para while-in-use recurso.
    private var foregroundLocationService: ForegroundLocationService? = null

    // Monitora a conexão com o while-in-use service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ForegroundLocationService.LocalBinder
            foregroundLocationService = binder.service
            foregroundLocationServiceBound = true
            startLocationService()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundLocationService = null
            foregroundLocationServiceBound = false
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                //Localização precisa aceita
                if (!isMapaCarregado) {
                    bindMap()
                }
                permissionRequestDialog?.dismiss()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissionRequestDialog = null
                    verificarPermissaoBackground()
                }
            }
            permission.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                //Localização aproximada acieta
            }
            else -> {
                //Permissão de localização negada
                permissionRequestDialog()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val locationBackgroundPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                //Localização background aceita
                binding.root.mensagemCurta(getString(R.string.label_permissao_localizacao_aceita), false)
                permissionRequestDialog?.dismiss()
                foregroundLocationService?.subscribeToLocationUpdates()
            }
            else -> {
                //Localização background negada
                permissionBackgroundRequestDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.close()
            } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED || bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                findNavController().navigateUp()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().getOrientacaoTela()
        _binding = FragmentMapaBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTransparentStatusBar()
        postponeEnterTransition()

        binding.apply {
            _bindingSheet = bottomSheet
            _bindingSheetInfoGerais = bindingSheet.ltEntregasInfoGerais
            _bindingPesquisarEntregas = bindingSheet.ltPesquisarEntregas

            bindRecyclerViewEntregas()
            bindBottomSheetEntregas()
            bindPesquisaAvancada()
        }

        bindObservers()
        bindListeners()
        bindNavView()
    }

    private fun bindObservers() {
        entregaViewModel.getListaEntrega()
        lifecycleScope.launchWhenCreated {
            entregaViewModel.listaEntrega.collect { result ->
                listaCliente = result
                mapaViewModel.setListaCliente(result)
                adapterEntregas.atualizarLista(result)
            }
        }

        lifecycleScope.launchWhenCreated {
            entregaViewModel.romaneioId.collect { cargaId ->
                bindingSheet.tvRomaneio.text = Html.fromHtml(getString(R.string.label_carga_id, cargaId), 0)
            }
        }

        lifecycleScope.launchWhenCreated {
            entregaViewModel.statusEntrega.collect { result ->
                when(result) {
                    is EntregaViewModel.EntregaResult.ErrorUpdate -> binding.root.mensagemCurta(result.message)
                    is EntregaViewModel.EntregaResult.SuccessUpdate -> {
                        binding.root.mensagemCurta("Entrega atualizada.", status = result.idEvento)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            entregaViewModel.parametros.collect {
                parametros = it
            }
        }

        lifecycleScope.launchWhenCreated {
            entregaViewModel.tempo.collect {
                if (mostrarTempo) {
                    binding.fabEspera.text = it
                }
                else encolherBotaoEspera()
            }
        }


        lifecycleScope.launch {
            mapaViewModel.localizacaoAtual.collect { localizacaoAtual ->
                if (!isNoRaioCliente) {
                    localizacaoAtual?.let {
                        if (listaCliente.isNotEmpty())
                            mapaViewModel.moverParaProximoCliente(localizacaoAtual.toLatLng(), listaCliente)
                        checarSeEstaDentroRaioCliente(localizacaoAtual, raioMarkerMap)?.let { marker ->
                            isNoRaioCliente = true
                            dialogEstaNoRaioCliente(
                                listaCliente[marker.tag.toString().toInt()] as Conteudo.CarouselEntrega
                            )
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            mapaViewModel.velocidadeAtual.collect { velocidade ->
                bindingSheet.tvTitulo.text = velocidade.toString()
            }
        }

        lifecycleScope.launchWhenCreated {
            mapaViewModel.notificacaoViagem.collect { notificacao ->
                Notificacao.distanciaProximoCliente(requireContext(), notificacao, EnumNotificacao.UPDATE_LOCAL_PROXIMO_CLIENTE)
            }
        }
    }

    private fun bindListeners() {
        adapterEntregas.onEntregaClickListener = { binding, entrega ->
            actionDetalheEntrega(binding, entrega)
        }

        adapterEntregas.onEntregaLongClickListener = { entrega, position ->
            clientePosicao = position
            bindDialogEvento(entrega)
        }

        binding.apply {
            fabLeftDrawer.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            fabEspera.setOnClickListener {
                expandirBotaoEspera()
            }
        }

        bindingSheetInfoGerais.apply {
            cvPendente.setOnClickListener {
                val ativo = checarStatusEntregaFiltro(cvPendenteIndicador)
                adapterEntregas.filtroPorStatus(
                    ENTREGA_PENDENTE, ENTREGA_CHEGADA_CLIENTE, ENTREGA_INICIADA,
                    adicionarFiltro = ativo
                )
            }

            cvRealizada.setOnClickListener {
                val ativo = checarStatusEntregaFiltro(cvRealizadaIndicador)
                adapterEntregas.filtroPorStatus(ENTREGA_REALIZADA, adicionarFiltro = ativo)
            }

            cvAdiada.setOnClickListener {
                val ativo = checarStatusEntregaFiltro(cvAdiadaIndicador)
                adapterEntregas.filtroPorStatus(ENTREGA_ADIADA, adicionarFiltro = ativo)
            }

            cvDevolvida.setOnClickListener {
                val ativo = checarStatusEntregaFiltro(cvDevolvidaIndicador)
                adapterEntregas.filtroPorStatus(
                    ENTREGA_DEVOLVIDA_PARCIAL,
                    adicionarFiltro = ativo
                )
            }
        }

        bindingSheet.apply {
            ivPesquisar.setOnClickListener { abrirLayoutPesquisa() }
        }

        bindingPesquisarEntregas.apply {
            ivFechar.setOnClickListener { fecharLayoutPesquisa() }
            etPesquisar.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            etPesquisar.setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED ||
                    bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
                ) bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun bindMarkerListener() {
        mapaGoogle.setOnMarkerClickListener { marker ->
            mapaGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 18.0f))
            marker.showInfoWindow()
            true
        }

        mapaGoogle.setOnInfoWindowClickListener { marker ->
            val position = marker.tag.toString().toInt()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bindingSheet.rvEntregas.smoothScrollToPosition(position + 1)
            adapterEntregas.destacarPosicao(position)
        }
    }

    private fun bindNavView() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                br.com.fusiondms.feature.mapa.R.id.menu_jornada_trabalho -> {
                    var launchIntent: Intent? = null
                    try {
                        launchIntent =
                            requireContext().packageManager.getLaunchIntentForPackage("br.com.fusiondms.jornadatrabalho")
                    } catch (ignored: Exception) {
                    }

                    if (launchIntent == null) {
                        startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=" + "br.com.fusiondms.jornadatrabalho")))
                    } else {
                        startActivity(launchIntent)
                    }
//                    findNavController().navigate(br.com.fusiondms.core.navigation.R.id.action_mapaFragment_to_jornadaTrabalhoActivity)
                }
                br.com.fusiondms.feature.mapa.R.id.menu_configuracoes -> {
                    startActivity(Intent(requireActivity(), ConfiguracoesActivity::class.java))
                }
            }
            binding.drawerLayout.close()
            return@setNavigationItemSelectedListener false
        }
    }

    private fun bindRecyclerViewEntregas() {
        bindingSheet.rvEntregas.doOnPreDraw {
            startPostponedEnterTransition()
        }
        bindingSheet.rvEntregas.apply {
            if (!adapterEntregas.hasObservers()) adapterEntregas.setHasStableIds(true)
            adapter = adapterEntregas
        }
    }

    private fun bindBottomSheetEntregas() {
        val bottomSheetLastState = entregaViewModel.bottomSheetState.value
        bottomSheetBehavior = BottomSheetBehavior.from(bindingSheet.root)
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.fabEspera.visibility = View.VISIBLE
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                if (slideOffset > 0.50) {
                    binding.fabEspera.fabAnimation(2F - slideOffset * 2)
                    bindingSheetInfoGerais.root.visibility = View.VISIBLE
                    bindingSheetInfoGerais.root.scaleY(alturaMaximaSheetInfo - (2F - 2F * slideOffset) * alturaMaximaSheetInfo)
                } else {
                    bindingSheetInfoGerais.root.visibility = View.GONE
                }
            }
        }

         bottomSheetBehavior.apply {
            isFitToContents = false
            expandedOffset = requireContext().getActionBarSize()
            peekHeight = resources.getDimension(R.dimen.bottom_sheet_peek_height).toInt()
            halfExpandedRatio = 0.5F
            state = bottomSheetLastState
            addBottomSheetCallback(bottomSheetCallback)
        }

        binding.fabEspera.visibility =
            if (bottomSheetLastState == BottomSheetBehavior.STATE_EXPANDED) View.INVISIBLE
            else View.VISIBLE

        bindingSheetInfoGerais.root.visibility =
            if (bottomSheetLastState == BottomSheetBehavior.STATE_COLLAPSED) View.GONE
            else View.VISIBLE
    }

    private fun actionDetalheEntrega(binding: ItemEntregaChildBinding, entrega: Entrega) {
        entregaViewModel.saveBottomSheetEntregasState(bottomSheetBehavior.state)
        entregaViewModel.setEntregaSelecionada(entrega)
        entregaViewModel.resetStatusEntrega()
        val directions = MapaFragmentDirections.actionMapaFragmentToDetalheEntregaFragment(entrega.idEntrega)
        val extras = FragmentNavigatorExtras(
//            binding.tvStatus to "status_${entrega.idEntrega}",
//            binding.tvOrdemEntrega to "ordem_entrega_${entrega.idEntrega}",
            binding.root to "card_${entrega.idEntrega}"
        )

        findNavController().navigate(directions, extras).apply {
            exitTransition = Fade(Fade.MODE_OUT)
        }
    }

    private fun checarStatusEntregaFiltro(cvIndicador: View) : Boolean {
        val isVisible = cvIndicador.isVisible
        cvIndicador.visibility = if (isVisible) View.INVISIBLE else View.VISIBLE

        return !isVisible
    }

    private fun bindPesquisaAvancada() {
        val pesquisarListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textoParaPesquisar = s.toString().trim()
                if (chipSelecionadoId == -1 && textoParaPesquisar.isNotBlank()) {
                    binding.root.exibirMensagemSnack(requireContext().getString(R.string.label_escolher_filtro_busca), TipoMensagem.ERROR)
                    bindingPesquisarEntregas.etPesquisar.setText("")
                } else {
                    val filtrarPor = chipSelecionado?.text.toString()
                    adapterEntregas.filtroAvancado(requireContext(),textoParaPesquisar, filtrarPor)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        bindingPesquisarEntregas.apply {
            chipGroupFiltros.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId == chipSelecionadoId) return@setOnCheckedChangeListener
                chipSelecionadoId = checkedId
                if (chipSelecionadoId != -1) {
                    val chip = group.findViewById<Chip>(chipSelecionadoId)
                    chip.isChecked = true
                    tilPesquisar.hint = chip.text.toString()
                    etPesquisar.setText("")
                    bindTipoTeclado(chip.text.toString())

                    chipSelecionado = chip
                }
            }
            etPesquisar.addTextChangedListener(pesquisarListener)
        }
    }

    private fun bindTipoTeclado(tipoFiltro: String) {
        val etPesquisar = bindingPesquisarEntregas.etPesquisar
        when (tipoFiltro) {
            requireContext().getString(R.string.label_nota_fiscal),
            requireContext().getString(R.string.label_codigo_cliente),
            requireContext().getString(R.string.label_cpf),
            requireContext().getString(R.string.label_cnpj) -> {
                etPesquisar.inputType = InputType.TYPE_CLASS_NUMBER
            }
            else -> {
                etPesquisar.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            }
        }
    }

    private fun abrirLayoutPesquisa() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bindingSheet.ivPesquisar.circularRevealAnimation(bindingPesquisarEntregas.root)
    }

    private fun fecharLayoutPesquisa() {
        limparFiltroPesquisa()
        bindingSheet.ivPesquisar.circularConcealAnimation(bindingPesquisarEntregas.root)
        bindingPesquisarEntregas.apply {
            requireContext().hideKeyboard(etPesquisar)
            etPesquisar.setText("")
            tilPesquisar.hint = requireContext().getString(R.string.label_escolha_filtro_abaixo)
            chipGroupFiltros.clearCheck()
            chipSelecionadoId = -1
        }
    }

    private fun limparFiltroPesquisa() {
        bindingSheetInfoGerais.apply {
            cvPendenteIndicador.visibility = View.INVISIBLE
            cvRealizadaIndicador.visibility = View.INVISIBLE
            cvAdiadaIndicador.visibility = View.INVISIBLE
            cvDevolvidaIndicador.visibility = View.INVISIBLE
            entregaViewModel.getListaEntrega()
        }
    }

    private fun bindDialogEvento(entrega: Entrega) {
        val iDialogEvento = object : IDialogEventos {
            override fun onEventoClick(idEvento: Int) {
                adicionarEventoEntrega(idEvento, entrega)
            }
        }
        dialogEventos = DialogEventos(entrega, requireContext())
        dialogEventos.initListener(iDialogEvento)
        dialogEventos.show(requireActivity().supportFragmentManager, DialogEventos.TAG)
    }

    private fun bindDialogEntregaDevolvida(entrega: Entrega) {
        Dialog(
            requireContext().getString(R.string.label_devolucao_mercadoria),
            requireContext().getString(R.string.label_qual_tipo_devolucao),
            requireContext().getString(R.string.label_devolucao_parcial),
            requireContext().getString(R.string.label_devolucao_total),
            acaoPositiva = {
                adicionarEventoEntrega(ENTREGA_DEVOLVIDA_PARCIAL, entrega)
            },
            acaoNegativa = {
                adicionarEventoEntrega(ENTREGA_DEVOLVIDA_TOTAL, entrega)
            }
        ).show(requireActivity().supportFragmentManager, Dialog.TAG)
    }

    private fun dialogEstaNoRaioCliente(cliente: Conteudo.CarouselEntrega) {
        val nomeCliente = cliente.entregasPorCliente.cliente
        val localCliente = cliente.entregasPorCliente.local
        val qtdEntregas = requireContext().resources.getQuantityString(
            R.plurals.label_quantidade_entregas,
            cliente.entregasPorCliente.entregas.size,
            cliente.entregasPorCliente.entregas.size

        )

        val mensagem = "$nomeCliente<br>$localCliente<br><b>$qtdEntregas</b>"

        DialogRaioCliente(
            getString(R.string.label_proximo_do_cliente),
            mensagem,
            getString(R.string.label_marcar_chegada_cliente),
            getString(R.string.label_silenciar),
            acaoPositiva = {
                isNoRaioCliente = true
            },
            acaoNegativa = {
                isNoRaioCliente = false
            }
        ).show(requireActivity().supportFragmentManager, DialogRaioCliente.TAG)
    }

    private fun adicionarEventoEntrega(idEvento: Int, entrega: Entrega) {
        when (idEvento) {
            ENTREGA_CHEGADA_CLIENTE -> {
                iniciarTempoEspera()
                entregaViewModel.updateStatusEntrega(entrega, idEvento)
            }
            ENTREGA_INICIADA -> {
                entregaViewModel.updateStatusEntrega(entrega, idEvento)
            }
            ENTREGA_REALIZADA -> {
                encolherBotaoEspera()
                finalizarTempoEspera()
                binding.lavConfetti.playAnimation()
                entregaViewModel.updateStatusEntrega(entrega, idEvento)
            }
            ENTREGA_DEVOLVIDA -> {
                bindDialogEntregaDevolvida(entrega)
            }
            ENTREGA_DEVOLVIDA_PARCIAL -> {
                entregaViewModel.updateStatusEntrega(entrega, idEvento)
            }
            ENTREGA_DEVOLVIDA_TOTAL -> {
                entregaViewModel.updateStatusEntrega(entrega, idEvento)
            }
        }
    }

    private fun iniciarTempoEspera() {
        binding.fabEspera.isActive(true)
        contador = Contador()
        contador.start()
        expandirBotaoEspera()
    }

    private fun finalizarTempoEspera() {
        binding.fabEspera.isActive(false)
        contador.onFinish()
        contador.cancel()
        mostrarTempo = false
    }

    private fun expandirBotaoEspera() {
        binding.fabEspera.extend()
        mostrarTempo = true
    }

    private fun encolherBotaoEspera() {
        binding.fabEspera.shrink()
        binding.fabEspera.text = null
    }

    private fun verificarPermissao(): Boolean {
        return when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                permissionRequestDialog?.dismiss()
                if (!isMapaCarregado) {
                    bindMap()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissionRequestDialog = null
                    verificarPermissaoBackground()
                } else {
                    true
                }
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                //Permissao negada mais de uma vez ir para as configurações
                permissionRequestDialog()
                false
            }

            else -> {
                permissionRequestDialog()
                false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun verificarPermissaoBackground(): Boolean {
        return when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                permissionRequestDialog?.dismiss()
                true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) -> {
                //Permissao negada mais de uma vez ir para as configurações
                permissionBackgroundRequestDialog()
                false
            }
            else -> {
                permissionBackgroundRequestDialog()
                false
            }
        }
    }

    private fun solicitarPermissao() {
        locationPermissionRequest.launch(LOCATION_PERMISSIONS)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun solicitarPermissaoBackground() {
        locationBackgroundPermissionRequest.launch(BACKGROUND_LOCATION_PERMISSION)
    }

    private fun permissionRequestDialog() {
        if (permissionRequestDialog == null) {
            permissionRequestDialog = PermissionRequestDialog(
                R.drawable.ic_location,
                getString(R.string.label_permissao_localizacao),
                getString(R.string.label_permissao_localizacao_mensagem),
                getString(R.string.label_aceitar_permissao),
                getString(R.string.label_ir_para_configuracoes),
                acaoAceitarPermissao = { solicitarPermissao() },
                acaoConfiguracoes = { actionConfiguracoes() }
            )
            permissionRequestDialog?.show(
                requireActivity().supportFragmentManager,
                PermissionRequestDialog.TAG
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun permissionBackgroundRequestDialog() {
        if (permissionRequestDialog == null) {
            permissionRequestDialog = PermissionRequestDialog(
                R.drawable.ic_location_all_time,
                getString(R.string.label_permissao_localizacao_background),
                getString(R.string.label_permissao_localizacao_background_mensagem),
                getString(R.string.label_aceitar_permissao),
                getString(R.string.label_ir_para_configuracoes),
                acaoAceitarPermissao = { solicitarPermissaoBackground() },
                acaoConfiguracoes = { actionConfiguracoes() }
            )
            permissionRequestDialog?.show(
                requireActivity().supportFragmentManager,
                PermissionRequestDialog.TAG
            )
        }
    }

    private fun actionConfiguracoes() {
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        requireActivity().startActivity(
            Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            ).apply {
                data = uri
            })
    }

    @SuppressLint("MissingPermission", "PotentialBehaviorOverride")
    private fun bindMap() {
            val mapFragment =
                childFragmentManager.findFragmentById(br.com.fusiondms.feature.mapa.R.id.map_fragment) as SupportMapFragment
            mapFragment.getMapAsync(this)

        if (::mapaGoogle.isInitialized) {
            if (!isMapaCarregado) {
                mapaGoogle.isMyLocationEnabled = true
                mapaGoogle.uiSettings.isCompassEnabled = false
                mapaGoogle.uiSettings.isMyLocationButtonEnabled = false

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.run {
                        val latLng = LatLng(latitude, longitude)
                        mapaGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f))
                    }
                }

                listaCliente.forEachIndexed { index, item ->
                    val cliente = item as Conteudo.CarouselEntrega

                    val loc = LatLng(
                        cliente.entregasPorCliente.latitude,
                        cliente.entregasPorCliente.longitude
                    )
                    val markerOptions = MarkerOptions().position(loc)

                    markerOptions.icon(layoutInflater.criarIconeLocalCliente(index))
                    val marker = mapaGoogle.addMarker(markerOptions)
                    marker?.tag = index
                    mapaGoogle.setInfoWindowAdapter(layoutInflater.bindInfoWindowLocalCliente(cliente))
                    mapaGoogle.addPolyline(requireContext().bindLinhaRotaEntrePontos(listaCliente))
                    val raio = mapaGoogle.addCircle(bindCirculoRaioCliente(cliente))
                    raioMarkerMap[raio] = marker!!
                }
                bindMarkerListener()
                isMapaCarregado = true
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapaGoogle = googleMap
        setMapStyle(mapaGoogle)
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    raw.map_style
                )
            )

            if (!success) {
                Log.e("TAG", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("TAG", "Can't find style. Error: ", e)
        }
    }

    private fun bindServicoLocalizacao() {
        if (verificarPermissao()) {
            val serviceIntent = Intent(requireActivity(), ForegroundLocationService::class.java)
            requireActivity().bindService(
                serviceIntent,
                foregroundOnlyServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    private fun startLocationService() {
        if (verificarPermissao()) {
            foregroundLocationService?.subscribeToLocationUpdates()
                ?: Log.d("ForegroundLocationService", "Service Not Bound")
        } else {
            solicitarPermissao()
        }
    }

    private fun stopServicoLocalizacao() {
        if (foregroundLocationServiceBound) {
            requireActivity().unbindService(foregroundOnlyServiceConnection)
            foregroundLocationServiceBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        verificarPermissao()
        entregaViewModel.getParametros()
    }

    override fun onStart() {
        super.onStart()
        bindServicoLocalizacao()
    }

    override fun onStop() {
        stopServicoLocalizacao()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        foregroundLocationService?.unsubscribeToLocationUpdates()
        entregaViewModel.resetViewModel()
        _binding = null
        _bindingSheet = null
        _bindingSheetInfoGerais = null
    }

    inner class Contador(timeToContinue: Long = 3540000L)
        : CountDownTimer(Long.MAX_VALUE, TimeUnit.SECONDS.toMillis(1)) {
        private val secondsInMillis = TimeUnit.SECONDS.toMillis(1)
        private val minutesInmillis = TimeUnit.MINUTES.toMillis(1)
        private val hoursInMillis = TimeUnit.HOURS.toMillis(1)
        private var elapsedTime = timeToContinue
        private var contadorTresSegundos = 0
        override fun onTick(millisUntilFinished: Long) {
            elapsedTime += secondsInMillis
            entregaViewModel.atualizarTempo(currentTime(), isTempoEspera)

            if (mostrarTempo && contadorTresSegundos++ > 3) {
                mostrarTempo = false
                contadorTresSegundos = 0
            }
        }

        override fun onFinish() {
            binding.root.exibirMensagemSnack("Tempo de espera finalizado: ${currentTime()}", TipoMensagem.NORMAL)
        }
        private fun currentTime() : String {
            val segundos = ((elapsedTime / secondsInMillis) % 60)
            val minutos = ((elapsedTime / minutesInmillis) % 60)
            val horas = ((elapsedTime / hoursInMillis) % 24)

            return if (elapsedTime >= TimeUnit.MINUTES.toMillis(60))
                String.format("%02d:%02d:%02d", horas, minutos, segundos)
            else String.format("%02d:%02d", minutos, segundos)
        }
    }

    companion object {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        @RequiresApi(Build.VERSION_CODES.Q)
        val BACKGROUND_LOCATION_PERMISSION = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }
}