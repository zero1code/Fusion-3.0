package br.com.fusiondms.modjornadatrabalho.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import br.com.fusiondms.modcommon.getActionBarSize
import br.com.fusiondms.modcommon.getStatusbarHeight
import br.com.fusiondms.modcommon.statusBarIconColor
import br.com.fusiondms.modjornadatrabalho.R
import br.com.fusiondms.modjornadatrabalho.databinding.ActivityJornadaTrabalhoBinding
import br.com.fusiondms.modnavegacao.R.id
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

        bindNavigation()
    }

    private fun bindNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_jornada_trabalho) as NavHostFragment
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