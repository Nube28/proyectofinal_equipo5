package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChoseEvent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chose_event)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()

        val headerProfileFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as? HeaderProfile

        headerProfileFragment?.view?.let { fragmentView ->
            val btnProfile: ImageView = fragmentView.findViewById(R.id.btn_profile)

            btnProfile.setOnClickListener {
                val intent = Intent(this, AccountConfig::class.java)
                startActivity(intent)
            }
        }
    }
}
