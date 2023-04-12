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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.core.common.*
import br.com.fusiondms.core.common.bottomdialog.Dialog
import br.com.fusiondms.core.common.permissiondiaolog.PermissionRequestDialog
import br.com.fusiondms.core.common.snackbar.TipoMensagem
import br.com.fusiondms.core.common.snackbar.mensagemCurta
import br.com.fusiondms.core.common.snackbar.showMessage
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.model.parametros.Parametros
import br.com.fusiondms.core.servives.location.ForegroundLocationService
import br.com.fusiondms.feature.entregas.presentation.adapter.EntregasParentAdapter
import br.com.fusiondms.feature.entregas.databinding.ItemEntregaChildBinding
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
import br.com.fusiondms.feature.mapa.databinding.EntregasBottomSheetBinding
import br.com.fusiondms.feature.mapa.databinding.EntregasInfoGeraisBinding
import br.com.fusiondms.feature.mapa.databinding.FragmentMapaBinding
import br.com.fusiondms.feature.mapa.databinding.LayoutPesquisarBinding
import br.com.fusiondms.feature.mapa.dialogeventos.DialogEventos
import br.com.fusiondms.feature.mapa.dialogeventos.interfaces.IDialogEventos
import br.com.fusiondms.feature.preferences.presentation.ConfiguracoesActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

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

    private val adapterEntregas by lazy { EntregasParentAdapter() }

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var permissionRequestDialog: PermissionRequestDialog? = null

    private lateinit var dialogEventos: DialogEventos

    private var clientePosicao = -1
    private var chipSelecionadoId = -1
    private var chipSelecionado: Chip? = null

    private lateinit var parametros: Parametros

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
                setupMap()
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
//            entregaViewModel.getListaEntrega()
            _bindingSheet = bottomSheet
            _bindingSheetInfoGerais = bindingSheet.ltEntregasInfoGerais
            _bindingPesquisarEntregas = bindingSheet.ltPesquisarEntregas

            setupRecyclerViewEntregas()
            setupBottomSheetEntregas()
            setupPesquisaAvancada()
        }

        bindObservers()
        bindListeners()
        bindNavView()
    }

    private fun bindObservers() {
        entregaViewModel.getListaEntrega()
        lifecycleScope.launchWhenCreated {
            entregaViewModel.listaEntrega.collect { listaCliente ->
                adapterEntregas.atualizarLista(listaCliente)
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
                        binding.root.mensagemCurta("Entrega atualizada.")
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
    }

    private fun bindListeners() {
        adapterEntregas.onEntregaClickListener = { binding, entrega ->
            actionDetalheEntrega(binding, entrega)
        }

        adapterEntregas.onEntregaLongClickListener = { entrega, position ->
            clientePosicao = position
            setupDialogEvento(entrega)
        }

        binding.apply {
            fabLeftDrawer.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
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

    private fun setupRecyclerViewEntregas() {
        bindingSheet.rvEntregas.doOnPreDraw {
            startPostponedEnterTransition()
        }
        bindingSheet.rvEntregas.apply {
            if (!adapterEntregas.hasObservers()) adapterEntregas.setHasStableIds(true)
            adapter = adapterEntregas
        }
    }

    private fun setupBottomSheetEntregas() {
        val bottomSheetLastState = entregaViewModel.bottomSheetState.value
        bottomSheetBehavior = BottomSheetBehavior.from(bindingSheet.root)
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.fabEspera.visibility = View.VISIBLE
                binding.fabDirecao.visibility = View.VISIBLE
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                if (slideOffset > 0.50) {
                    binding.fabEspera.fabAnimation(2F - slideOffset * 2)
                    binding.fabDirecao.fabAnimation(2F - slideOffset * 2)
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

        binding.fabDirecao.visibility =
            if (bottomSheetLastState == BottomSheetBehavior.STATE_EXPANDED) View.INVISIBLE
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

    private fun setupPesquisaAvancada() {
        val pesquisarListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textoParaPesquisar = s.toString().trim()
                if (chipSelecionadoId == -1 && textoParaPesquisar.isNotBlank()) {
                    binding.root.showMessage(requireContext().getString(R.string.label_escolher_filtro_busca), TipoMensagem.ERROR)
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
                    setupTipoTeclado(chip.text.toString())

                    chipSelecionado = chip
                }
            }
            etPesquisar.addTextChangedListener(pesquisarListener)
        }
    }

    private fun setupTipoTeclado(tipoFiltro: String) {
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
        limparFiltroPesquisa()
        bindingSheetInfoGerais.root.visibility = View.INVISIBLE
        bindingSheet.ivPesquisar.circularRevealAnimation(bindingPesquisarEntregas.root)
    }

    private fun fecharLayoutPesquisa() {
        bindingSheetInfoGerais.root.visibility = View.VISIBLE
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

    private fun setupDialogEvento(entrega: Entrega) {
        val iDialogEvento = object : IDialogEventos {
            override fun onEventoClick(idEvento: Int) {
                if (idEvento < 9) {
                    if (idEvento == ENTREGA_DEVOLVIDA) {
                        setupDialogEntregaDevolvida(entrega)
                    } else {
                        entregaViewModel.updateStatusEntrega(entrega, idEvento)
                    }
                }
                binding.root.mensagemCurta(idEvento.toString())
            }
        }
        dialogEventos = DialogEventos(entrega, requireContext())
        dialogEventos.initListener(iDialogEvento)
        dialogEventos.show(requireActivity().supportFragmentManager, DialogEventos.TAG)
    }

    private fun setupDialogEntregaDevolvida(entrega: Entrega) {
        Dialog(
            requireContext().getString(R.string.label_devolucao_mercadoria),
            requireContext().getString(R.string.label_qual_tipo_devolucao),
            requireContext().getString(R.string.label_devolucao_parcial),
            requireContext().getString(R.string.label_devolucao_total),
            acaoPositiva = {
                entregaViewModel.updateStatusEntrega(entrega, ENTREGA_DEVOLVIDA_PARCIAL)
            },
            acaoNegativa = {
                entregaViewModel.updateStatusEntrega(entrega, ENTREGA_DEVOLVIDA_TOTAL)
            }
        ).show(requireActivity().supportFragmentManager, Dialog.TAG)
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
                setupMap()
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

    @SuppressLint("MissingPermission")
    private fun setupMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(br.com.fusiondms.feature.mapa.R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val loc = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(loc).title(getString(R.string.label_voce_esta_aqui)))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 18.0f))
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle(mMap)
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

    private fun startLocationService() {
        if (verificarPermissao()) {
            foregroundLocationService?.subscribeToLocationUpdates()
                ?: Log.d("ForegroundLocationService", "Service Not Bound")
        } else {
            solicitarPermissao()
        }
    }

    override fun onResume() {
        super.onResume()
        verificarPermissao()
    }

    override fun onStart() {
        super.onStart()
        if (verificarPermissao()) {
            val serviceIntent = Intent(requireActivity(), ForegroundLocationService::class.java)
            requireActivity().bindService(
                serviceIntent,
                foregroundOnlyServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    override fun onStop() {
        if (foregroundLocationServiceBound) {
            requireActivity().unbindService(foregroundOnlyServiceConnection)
            foregroundLocationServiceBound = false
        }
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

    companion object {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        @RequiresApi(Build.VERSION_CODES.Q)
        val BACKGROUND_LOCATION_PERMISSION = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }
}