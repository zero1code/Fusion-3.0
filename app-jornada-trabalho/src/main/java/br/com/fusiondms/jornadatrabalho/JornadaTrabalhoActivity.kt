package br.com.fusiondms.jornadatrabalho

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import br.com.fusiondms.core.common.statusBarIconColor
import br.com.fusiondms.jornadatrabalho.databinding.ActivityJornadaTrabalhoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JornadaTrabalhoActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var _binding: ActivityJornadaTrabalhoBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityJornadaTrabalhoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        statusBarIconColor(this, Color.WHITE)
        bindNavigation()
    }

    private fun bindNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(br.com.fusiondms.jornadatrabalho.R.id.nav_host_fragment_jornada_trabalho) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

//        appBarConfiguration = AppBarConfiguration(
//            setOf(id.jornadaTrabalhoFragment, id.recibosFragment)
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}