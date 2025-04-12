package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

open class DrawerBaseActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_base)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        actualizarOpcionesAdmin()

        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_Administrador -> {
                    startActivity(Intent(this, RegistroActivity::class.java))
                }
                R.id.nav_alumno -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.nav_docente -> {
                    startActivity(Intent(this, DocenteActivity::class.java))
                }
                R.id.nav_actividades -> {
                    startActivity(Intent(this, ActividadesActivity::class.java))
                }
                R.id.nav_noticias -> {
                    startActivity(Intent(this, NoticiasActivity::class.java))
                }
                R.id.nav_matricula -> {
                    startActivity(Intent(this, MatriculaActivity::class.java))
                }
                R.id.nav_gestion -> {
                    startActivity(Intent(this, GestionAcademicaActivity::class.java))
                }
                R.id.nav_cerrar_sesion_admin -> {
                    getSharedPreferences("UsuarioPrefs", MODE_PRIVATE)
                        .edit().putBoolean("esAdmin", false).apply()
                    Toast.makeText(this, "Sesión de administrador cerrada", Toast.LENGTH_SHORT).show()

                    actualizarOpcionesAdmin()

                    drawerLayout.closeDrawers()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Botones adicionales del contenido principal
        findViewById<Button>(R.id.btndocente).setOnClickListener {
            startActivity(Intent(this, DocenteInfoActivity::class.java))
        }
        findViewById<Button>(R.id.btnaulas).setOnClickListener {
            startActivity(Intent(this, AulasInfoActivity::class.java))
        }
        findViewById<Button>(R.id.btntalleres).setOnClickListener {
            startActivity(Intent(this, TalleresInfoActivity::class.java))
        }
        findViewById<Button>(R.id.btnareas).setOnClickListener {
            startActivity(Intent(this, AreasInfoActivity::class.java))
        }
    }

    // Este método actualiza visibilidad de las opciones del admin
    private fun actualizarOpcionesAdmin() {
        val sharedPref = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE)
        val esAdmin = sharedPref.getBoolean("esAdmin", false)

        val navMenu = navigationView.menu
        navMenu.findItem(R.id.nav_matricula)?.isVisible = esAdmin
        navMenu.findItem(R.id.nav_gestion)?.isVisible = esAdmin
        navMenu.findItem(R.id.nav_cerrar_sesion_admin)?.isVisible = esAdmin
    }

    override fun onResume() {
        super.onResume()
        actualizarOpcionesAdmin() // Asegura actualización al volver a esta pantalla
    }

    // Método para inflar contenido dinámicamente
    protected fun setContentViewToDrawer(contentView: View) {
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        contentFrame.removeAllViews()
        contentFrame.addView(contentView)
    }
}
