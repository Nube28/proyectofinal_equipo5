package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Pantalla de registro de nuevos usuarios.
 * Permite a los usuarios crear una cuenta con nombre, correo y contraseña.
 * Después del registro, guarda el usuario en Firestore y redirige a la pantalla principal.
 */
class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        val et_name: EditText = findViewById(R.id.et_name)
        val et_email: EditText = findViewById(R.id.et_email)
        val et_password: EditText = findViewById(R.id.et_password)
        val et_repeat_password: EditText = findViewById(R.id.et_repeat_password)

        val btn_sign_in = findViewById(R.id.btn_sign_in) as Button

        btn_sign_in.setOnClickListener{
            if (et_name.text.isEmpty() && et_email.text.isEmpty() && et_password.text.isEmpty() && et_repeat_password.text.isEmpty()){
                Toast.makeText(this, "Todos los campos deben ser llenados", Toast.LENGTH_SHORT).show()
            } else {
                if (et_password.text.toString() == et_repeat_password.text.toString()){
                    signUp(et_name.text.toString(), et_email.text.toString(), et_password.text.toString())
                    val intent: Intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    /**
     * Registra un nuevo usuario en Firebase Authentication y lo guarda en la colección `Usuarios` de Firestore.
     *
     * @param name Nombre del usuario
     * @param email Correo electrónico
     * @param password Contraseña
     */
    fun signUp(name: String, email: String, password: String){
        val db = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    val user = auth.currentUser

                    val usuario = hashMapOf(
                        "nombre" to name,
                        "usuarioId" to user!!.uid
                    )

                    db.collection("Usuarios")
                        .add(usuario)
                        .addOnSuccessListener {
                            Log.d("INFO", "Usuario registrado en Firestore")

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.w("ERROR", "Error al guardar usuario en Firestore", e)
                            Toast.makeText(this, "Error al guardar usuario", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    Log.w("ERROR", "signUpWithEmail:failure", task.exception)
                    Toast.makeText(this, "El registro falló", Toast.LENGTH_SHORT).show()
                }
            }
    }
}