package br.com.fusiondms.core.services.location

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import br.com.fusiondms.core.datastore.chaves.DataStoreChaves
import br.com.fusiondms.core.datastore.repository.DataStoreRepository
import br.com.fusiondms.core.datastore.repository.DataStoreRepositoryImpl
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.math.round

@AndroidEntryPoint
class ForegroundLocationService : Service() {

    var dataStoreRepository: DataStoreRepository = DataStoreRepositoryImpl(this)

    private val locationInterval = TimeUnit.SECONDS.toMillis(20)
    private val locationFastestInterval = TimeUnit.SECONDS.toMillis(5)
    private val locationMaxWaitTime = TimeUnit.SECONDS.toMillis(30)

    private var configurationChange = false

    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()

    private lateinit var notificationManager: NotificationManager

    // Classe principal que vai receber as atualizações da localização.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // Responsável pelas requisições de localização, como tempo de resposta e prioridade
    private lateinit var locationRequest: LocationRequest

    // Callback com uma nova localização
    private lateinit var locationCallback: LocationCallback

    // Usada para salvar a ultima localização conhecida, pode ser salvo no banco de dados, mas nesse exemplo
    // está sendo armazenado no sharedPrefences uma única vez.
    private var currentLocation: Location? = null

    override fun onCreate() {
        Log.d(TAG, "onCreate()")


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            locationInterval)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(locationFastestInterval)
            .setMaxUpdateDelayMillis(locationMaxWaitTime)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                locationResult.lastLocation
                currentLocation = locationResult.lastLocation
                val latitude = currentLocation?.latitude.toString()
                val longitude = currentLocation?.longitude.toString()
                //AQUI PEGA A LOCALIZACAO
                val myCurrentLocation = currentLocation?.toText() ?: "Sem localização 2"
                val velocidade = currentLocation?.run {
                    if (hasSpeed()) (speed * 3.6)
                    else getSpeed(this)
                } ?: 0.0
                Log.d(TAG, "Velocidade: ${velocidade}")
                runBlocking { dataStoreRepository.putLocation(myCurrentLocation, latitude, longitude, velocidade.toInt()) }

                if (serviceRunningInForeground) {
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        generateNotification(currentLocation)
                    )
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")

        val cancelLocationTrackingFromNotification =
            intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)

        if (cancelLocationTrackingFromNotification) {
            unsubscribeToLocationUpdates()
            stopSelf()
        }
        // Falar para o sistema nao recriar o seviço quando for morto
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind()")

        // MainActivity esta no primeiro plano e o serviço em background é iniciado
//        stopForeground(STOP_FOREGROUND_REMOVE)
        serviceRunningInForeground = true
        notificationManager.notify(
            NOTIFICATION_ID,
            generateNotification(currentLocation)
        )
        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        Log.d(TAG, "onRebind()")

        // // MainActivity retorna  ao primeiro plano e o serviço em background é reiniciado
//        stopForeground(STOP_FOREGROUND_REMOVE)
        serviceRunningInForeground = true
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind()")

        // MainActivity (client) deixa o primeiro plano, então um serviço é necessário para inicar um background service
        // para manter o 'while-in-use'.
        // NOTE: Se o método for chamado devido a uma alteração da MainActivity (Rotação da tela por exemplo)
        // Não irá fazer nada.

        val locationForegroundEnabled = runBlocking {
             dataStoreRepository.getBoolean(DataStoreChaves.KEY_FOREGROUND_ATIVADO) ?: false
        }

        if (!configurationChange && locationForegroundEnabled) {
            Log.d(TAG, "Start foreground service")
            val notification = generateNotification(currentLocation)
            startForeground(NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        }

        // Garante que o onRebind() é chamado se a MainActivity (client) retornar
        return true
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    @SuppressLint("MissingPermission")
    fun subscribeToLocationUpdates() {
        Log.d(TAG, "subscribeToLocationUpdates()")
        runBlocking { dataStoreRepository.putBoolean(DataStoreChaves.KEY_FOREGROUND_ATIVADO, true) }

        // A vinculação a este serviço não aciona noStartCommand(). Isso é necessário para
        // garantir que este Serviço possa ser promovido a um serviço em background, ou seja, o serviço precisa
        // ser oficialmente iniciado (o que fazemos aqui).
        startService(Intent(applicationContext, ForegroundLocationService::class.java))

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

        } catch (unlikely: SecurityException) {
            runBlocking { dataStoreRepository.putBoolean(DataStoreChaves.KEY_FOREGROUND_ATIVADO, false) }
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

     fun unsubscribeToLocationUpdates() {
        Log.d(TAG, "unsubscribeToLocationUpdates()")

        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Location Callback removed.")
                    stopSelf()
                } else {
                    Log.d(TAG, "Failed to remove Location Callback.")
                }
            }

            runBlocking { dataStoreRepository.putBoolean(DataStoreChaves.KEY_FOREGROUND_ATIVADO, false) }

        } catch (unlikely: SecurityException) {
            runBlocking { dataStoreRepository.putBoolean(DataStoreChaves.KEY_FOREGROUND_ATIVADO, true) }
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    /*
     * Gerar uma BIG_TEXT_STYLE Notification que representa a mais recente location.
     */
    private fun generateNotification(location: Location?): Notification {
        Log.d(TAG, "generateNotification()")

        val mainNotificationText = "Durante a execução do aplicativo."  //location?.toText() ?: "Sem localização"
        val titleText = "Localização ativada"

        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_LOW
        )
        // Desabilita a vibração para o canal de notificação
        notificationChannel.vibrationPattern = null

        notificationManager.createNotificationChannel(notificationChannel)

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)

        val notificationCompatBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
        val bitmap = BitmapFactory.decodeResource(resources, br.com.fusiondms.core.common.R.drawable.ic_logo)
        return notificationCompatBuilder
//            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(mainNotificationText)
            .setSmallIcon(br.com.fusiondms.core.common.R.drawable.ic_notificacao_app)
            .setLargeIcon(bitmap)
            .setColorized(true)
            .setColor(ContextCompat.getColor(applicationContext, br.com.fusiondms.core.common.R.color.brand_green))
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun getSpeed(localizacaoAtual: Location): Double {
        try {
            var velocidade = 0.0
            runBlocking {
                val strLocalizacao = dataStoreRepository.getLastLocation(DataStoreChaves.KEY_CURRENT_LOCATION)
                strLocalizacao?.toLatLng()?.let { localizacao ->
                    val localizacaoAnterior = Location("Fusion")
                    localizacaoAnterior.latitude = localizacao.latitude
                    localizacaoAnterior.longitude = localizacao.longitude
                    localizacaoAnterior.time = (Instant.now().epochSecond - 10)

                    val tempoDecorrido = (localizacaoAtual.time - localizacaoAnterior.time) / 1000
                    val distancia = localizacaoAnterior.distanceTo(localizacaoAtual)

                    velocidade = (distancia / tempoDecorrido) * 3.6
                }
            }
            return velocidade
        } catch (e: Exception) {
            e.printStackTrace()
            return 0.0
        }
    }

    fun String.toLatLng() : LatLng {
        val latlng = this.split(", ")
        val latitudeAtual = latlng[0].toDouble()
        val longitudeAtual = latlng[1].toDouble()

        return LatLng(latitudeAtual, longitudeAtual)
    }

    /**
     * Classe usada para o cliente Binder. Desde que este servico rode no mesmo processo
     * não precisamos nos preocupar com IPC.
     */
    inner class LocalBinder() : Binder() {
        val service: ForegroundLocationService
            get() = this@ForegroundLocationService
    }

    companion object {
        private const val TAG = "ForegroundLocationService"

        private const val PACKAGE_NAME = "br.com.fusiondms.core.services.location"

        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"

        private const val NOTIFICATION_ID = 1

        private const val NOTIFICATION_CHANNEL_ID = "localizacao"
    }

    private fun Location?.toText(): String {
        return if (this != null) {
            "$latitude, $longitude"
        } else {
            "Unknown location"
        }
    }
}