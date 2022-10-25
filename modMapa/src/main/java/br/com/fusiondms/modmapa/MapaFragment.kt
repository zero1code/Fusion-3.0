package br.com.fusiondms.modmapa

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.fusiondms.modcommon.R.dimen
import br.com.fusiondms.modcommon.fabAnimation
import br.com.fusiondms.modcommon.getActionBarSize
import br.com.fusiondms.modcommon.getOrientacaoTela
import br.com.fusiondms.modcommon.setTransparentStatusBar
import br.com.fusiondms.modcommon.snackbar.mensagemCurta
import br.com.fusiondms.modentrega.adapter.EntregasParentAdapter
import br.com.fusiondms.modmapa.databinding.EntregasBottomSheetBinding
import br.com.fusiondms.modmapa.databinding.EntregasInfoGeraisBinding
import br.com.fusiondms.modmapa.databinding.FragmentMapaBinding
import br.com.fusiondms.modmodel.Entrega
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

class MapaFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapaBinding? = null
    private var _bindingSheet: EntregasBottomSheetBinding? = null
    private var _bindingSheetInfoGerais: EntregasInfoGeraisBinding? = null

    private val binding get() = _binding!!
    private val bindingSheet get() = _bindingSheet!!
    private val bindingSheetInfoGerais get() = _bindingSheetInfoGerais!!

    private val adapter by lazy { EntregasParentAdapter() }

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from<View>(bindingSheet.root) }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                //Localização precisa aceita
//                binding.root.mensagemCurta("Permissão de localização precisa concedida", false)
                setupMap()
            }
            permission.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                //Localização aproximada acieta
//                binding.root.mensagemCurta("Permissão de localização aproximada concedida", false)
            }
            else -> {
                //Permissão de localização negada
//                binding.root.mensagemCurta("Permissão de localização negada", true)
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
        verificarPermissao()

        binding.apply {
            _bindingSheet = bottomSheet
            _bindingSheetInfoGerais = bindingSheet.ltEntregasInfoGerais
            fabLeftDrawer.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            bindingSheet.rvEntregas.adapter = adapter
            setupBottomSheetEntregas()
        }

        bindListeners()

    }

    private fun bindListeners() {
        adapter.onClienteClickListener = {cliente ->
            binding.root.mensagemCurta(cliente, false)
        }

        adapter.onEntregaClickListener = {entrega ->
            binding.root.mensagemCurta(entrega, true)
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
            expandedOffset = getActionBarSize(requireContext())
            peekHeight = resources.getDimension(dimen.bottom_sheet_peek_height).toInt()
            halfExpandedRatio = 0.5F
            this.state = BottomSheetBehavior.STATE_COLLAPSED
            addBottomSheetCallback(bottomSheetCallback)
        }

        val listaEntregas = arrayListOf(
            Entrega(),
            Entrega(),
            Entrega(),
            Entrega(),
            Entrega(),
            Entrega()
        )
        adapter.submitList(listaEntregas)
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
//                binding.root.mensagemCurta("Permissão de localização concedida", false)
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
                binding.root.mensagemCurta("Para continuar, aceite a permissão de localização", true)
                solicitarPermissao()
            }

            else -> {
                solicitarPermissao()
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
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _bindingSheet = null
        _bindingSheetInfoGerais = null
    }
}