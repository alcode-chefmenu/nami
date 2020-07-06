package com.example.nami

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badge
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import com.example.nami.adapter.SectionsAdapter
import com.example.nami.models.sections.SectionsResponse
import com.example.nami.models.user.UserResponse
import com.example.nami.presenters.SectionsPresenter
import com.example.nami.presenters.SectionsUI
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SectionsUI {
    //Declaracion presentador de secciones
    private val presenter = SectionsPresenter(this, this)
    //Declaracion layout de las pestañas
    var tabLayout: TabLayout? = null
    //Declaracion del contenido de las pestañas
    var viewPager: ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //solicitud del primer servicio (secciones)
        presenter.actionSections()
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
    }
    //Funcion cuando responde el servicio
    override fun showSection(data: SectionsResponse, userData: UserResponse) {
        //Se corre en el hilo principal
        runOnUiThread {
            //por cada seccion se genera una pestaña
            toolbar3.title = "${userData.user!!.lastname}"
            for (section in data.sections!!) {
                tabLayout!!.addTab(tabLayout!!.newTab().setText(section.name))
            }
            tabLayout?.tabGravity = TabLayout.GRAVITY_FILL

            //Se llama el adaptador de las secciones
            val adapter = SectionsAdapter(
                this,
                supportFragmentManager,
                tabLayout!!.tabCount,
                data.behaviors!!.toTypedArray(),
                data.sections!!
            )
            //Se asigna el adaptador
            viewPager!!.adapter = adapter
            viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
            tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager!!.currentItem = tab.position
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

            //Se creal el sidemenu
            drawer {
                this.closeOnClick = true
                this.toolbar = toolbar3
                accountHeader {
                    profile("${userData.user!!.name}", "${userData.user!!.role!!.name}") {
                        //icon = "http://some.site/samantha.png"
                    }
                    this.alternativeSwitching=false
                }
                primaryItem("Inicio")
                primaryItem("Recursos") {
                    enabled=false
                    badge {
                        cornersDp = 0
                        text = ">"
                        colorPressed = 0xFFCC99FF
                    }
                }
                primaryItem("Zonas") {
                    enabled=false
                    badge {
                        cornersDp = 0
                        text = ">"
                        colorPressed = 0xFFCC99FF
                    }
                }
                primaryItem("Mis gancias") {
                    enabled=false
                    badge {
                        cornersDp = 0
                        text = ">"
                        colorPressed = 0xFFCC99FF
                    }
                }
                primaryItem("Cerrar sesión") {
                    onClick { _ ->
                        presenter.actionLogOut()
                        true
                    }
                }

                footer {
                    primaryItem("Ayuda") {
                        onClick { _ ->
                            val intent = Intent(this@MainActivity, helpPage::class.java)
                            startActivity(intent)
                            false

                        }
                    }
                }
            }

        }
    }


    override fun showError(error: String) {
        runOnUiThread {
            if (error.contains("Error al autenticar el token")) {
                presenter.actionLogOut()
            } else {
                Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun exit() {
        finish()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
