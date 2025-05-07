package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

/**
 * Un subclase [Fragment] simple.
 * Representa el header con un boton que te lleva al perfil del usuario
 */
class HeaderProfile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_header_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnProfile: ImageView = view.findViewById(R.id.btn_profile)

        btnProfile.setOnClickListener {
            val intent = Intent(requireContext(), AccountConfig::class.java)
            startActivity(intent)
        }
    }
}