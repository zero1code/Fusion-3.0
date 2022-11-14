package br.com.fusiondms.modmapa.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.modcommon.R.*
import br.com.fusiondms.modcommon.fabAnimation
import br.com.fusiondms.modcommon.getActionBarSize
import br.com.fusiondms.modcommon.getOrientacaoTela
import br.com.fusiondms.modcommon.permissiondiaolog.PermissionRequestDialog
import br.com.fusiondms.modcommon.setTransparentStatusBar
import br.com.fusiondms.modcommon.snackbar.mensagemCurta
import br.com.fusiondms.modmapa.adapter.EntregasParentAdapter
import br.com.fusiondms.modmapa.R
import br.com.fusiondms.modmapa.databinding.EntregasBottomSheetBinding
import br.com.fusiondms.modmapa.databinding.EntregasInfoGeraisBinding
import br.com.fusiondms.modmapa.databinding.FragmentMapaBinding
import br.com.fusiondms.modmapa.presentation.viewmodel.MapaViewModel
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapaFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapaBinding? = null
    private var _bindingSheet: EntregasBottomSheetBinding? = null
    private var _bindingSheetInfoGerais: EntregasInfoGeraisBinding? = null

    private val binding get() = _binding!!
    private val bindingSheet get() = _bindingSheet!!
    private val bindingSheetInfoGerais get() = _bindingSheetInfoGerais!!

    private val mapaViewModel: MapaViewModel by viewModels()

    private val adapter by lazy { EntregasParentAdapter() }

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from<View>(bindingSheet.root) }
    private var permissionRequestDialog: PermissionRequestDialog? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                //Localização precisa aceita
                binding.root.mensagemCurta("Permissão de localização concedida", false)
                setupMap()
                permissionRequestDialog?.dismiss()
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

        binding.apply {
            _bindingSheet = bottomSheet
            _bindingSheetInfoGerais = bindingSheet.ltEntregasInfoGerais
            fabLeftDrawer.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            bindingSheet.rvEntregas.adapter = adapter
            setupBottomSheetEntregas()
        }

        bindObservers()
        bindListeners()
    }

    private fun bindObservers() {
        lifecycleScope.launchWhenCreated {
            mapaViewModel.listaEntrega.collect() { listaCliente ->
                adapter.submitList(listaCliente)
            }
        }

        lifecycleScope.launchWhenCreated {
            mapaViewModel.cargaId.collect { cargaId ->
                bindingSheet.tvRomaneio.text = "Romaneio: $cargaId"
            }
        }
    }

    private fun bindListeners() {
        adapter.onClienteClickListener = { cliente ->
            binding.root.mensagemCurta(cliente, false)
        }

        adapter.onEntregaClickListener = { entrega ->
            binding.root.mensagemCurta(entrega.idEntrega.toString(), true)
        }

        bindingSheetInfoGerais.apply {
            cvPendente.setOnClickListener {
                val ativo = checarStatusEntregaFiltro(cvPendenteIndicador)
            }

            cvRealizada.setOnClickListener {
                val ativo = checarStatusEntregaFiltro(cvRealizadaIndicador)
            }

            cvAdiada.setOnClickListener {
                val ativo = checarStatusEntregaFiltro(cvAdiadaIndicador)
            }

            cvDevolvida.setOnClickListener {
                val ativo = checarStatusEntregaFiltro(cvDevolvidaIndicador)
            }
        }
    }

    private fun setupBottomSheetEntregas() {
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

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
            peekHeight = resources.getDimension(dimen.bottom_sheet_peek_height).toInt()
            halfExpandedRatio = 0.5F
            this.state = BottomSheetBehavior.STATE_COLLAPSED
            addBottomSheetCallback(bottomSheetCallback)
        }
    }

    private fun verificarPermissao() {
        when {
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
            }

            else -> {
                permissionRequestDialog()
            }
        }
    }

    private fun solicitarPermissao() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun setupMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val loc = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(loc).title("Você esta aqui."))
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
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e("TAG", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("TAG", "Can't find style. Error: ", e)
        }
    }

    private fun permissionRequestDialog() {
        if (permissionRequestDialog == null) {
            permissionRequestDialog = PermissionRequestDialog(
                drawable.ic_location,
                "Permissão de Localização",
                "Para continuar, precisamos que permita que o Fusion tenha acesso a localização do dispositivo.",
                "Aceitar permissão",
                "Ir para Configurações",
                acaoAceitarPermissao = { solicitarPermissao() },
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

    private fun checarStatusEntregaFiltro(cvIndicador: View) : Boolean {
        val isVisible = cvIndicador.isVisible
        cvIndicador.visibility = if (isVisible) View.INVISIBLE else View.VISIBLE

        return isVisible
    }

    override fun onResume() {
        super.onResume()
        verificarPermissao()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _bindingSheet = null
        _bindingSheetInfoGerais = null
    }
}