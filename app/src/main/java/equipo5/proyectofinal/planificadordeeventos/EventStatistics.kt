package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import utilities.CustomCircleDrawable
import utilities.Graphics

/**
 * Actividad que muestra estadísticas visuales de un evento, como el presupuesto estimado y el gasto real.
 *
 * Recupera información de Firestore, calcula los gastos realizados con base en los proveedores seleccionados
 * para cada subtarea, y actualiza un gráfico de barras y un gráfico circular para mostrar una comparación
 * visual entre presupuesto y gasto real.
 */
class EventStatistics : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event_statistics)

        val totalEstimadoText = findViewById<TextView>(R.id.totalEstimadoText)
        val totalRealText = findViewById<TextView>(R.id.totalRealText)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()

        val totalBar = findViewById<View>(R.id.Total)
        val totalRBar = findViewById<View>(R.id.TotalR)
        val graph = findViewById<ConstraintLayout>(R.id.graph)

        val eventId = intent.getStringExtra("eventoId") ?: return

        db.collection("Eventos").document(eventId)
            .get()
            .addOnSuccessListener { document ->
                val totalEstimado = document.getDouble("presupuesto") ?: 0.0
                totalEstimadoText.text = "$%.2f".format(totalEstimado)

                calcularTotalReal(eventId) { totalReal ->
                    totalRealText.text = "$%.2f".format(totalReal)
                    actualizarGrafica(totalEstimado, totalReal, totalBar, totalRBar, graph)
                }
            }
            .addOnFailureListener { e ->
                Log.e("EventStatistics", "Error al obtener totalEstimado: ${e.message}")
            }

        val btnSalePoint = findViewById<Button>(R.id.btn_sale_point)
        btnSalePoint.setOnClickListener {
            val intent = Intent(this, SalePoint::class.java)
            intent.putExtra("eventoId", eventId)
            startActivity(intent)
        }
    }

    /**
     * Calcula el gasto real total de un evento sumando los precios de los proveedores seleccionados
     * en todas las subtareas.
     *
     * @param eventoId ID del evento.
     * @param callback Función que recibe el total calculado.
     */
    private fun calcularTotalReal(eventoId: String, callback: (Double) -> Unit) {
        var totalReal = 0.0

        db.collection("Eventos").document(eventoId).collection("Tareas")
            .get()
            .addOnSuccessListener { tareas ->
                val tareasList = tareas.documents
                var tareasProcesadas = 0

                if (tareasList.isEmpty()) {
                    callback(0.0)
                    return@addOnSuccessListener
                }

                for (tareaDoc in tareasList) {
                    val tareaId = tareaDoc.id
                    db.collection("Eventos").document(eventoId)
                        .collection("Tareas").document(tareaId)
                        .collection("Subtareas")
                        .get()
                        .addOnSuccessListener { subtareas ->
                            val subtareasList = subtareas.documents
                            var subtareasProcesadas = 0

                            if (subtareasList.isEmpty()) {
                                tareasProcesadas++
                                if (tareasProcesadas == tareasList.size) callback(totalReal)
                                return@addOnSuccessListener
                            }

                            for (subtareaDoc in subtareasList) {
                                val subtareaId = subtareaDoc.id
                                db.collection("Eventos").document(eventoId)
                                    .collection("Tareas").document(tareaId)
                                    .collection("Subtareas").document(subtareaId)
                                    .collection("Proveedor")
                                    .whereEqualTo("seleccionado", true)
                                    .get()
                                    .addOnSuccessListener { proveedores ->
                                        for (proveedorDoc in proveedores) {
                                            val precio = proveedorDoc.getDouble("precio") ?: 0.0
                                            totalReal += precio
                                        }

                                        subtareasProcesadas++
                                        if (subtareasProcesadas == subtareasList.size) {
                                            tareasProcesadas++
                                            if (tareasProcesadas == tareasList.size) {
                                                callback(totalReal)
                                            }
                                        }
                                    }
                            }
                        }
                }
            }
    }

    /**
     * Actualiza la gráfica de barras y el gráfico circular de presupuesto vs. gasto real.
     *
     * @param totalEstimado Presupuesto estimado del evento.
     * @param totalReal Gasto real calculado del evento.
     * @param totalBar Vista de barra para presupuesto.
     * @param totalRBar Vista de barra para gasto real.
     * @param graph Contenedor donde se dibuja el gráfico circular.
     */
    private fun actualizarGrafica(
        totalEstimado: Double,
        totalReal: Double,
        totalBar: View,
        totalRBar: View,
        graph: ConstraintLayout
    ) {
        val totalMax = maxOf(totalEstimado, totalReal, 1.0) // evitar división por cero en barras
        val porcentajeEstimadoBar = ((totalEstimado / totalMax) * 100).toInt()
        val porcentajeRealBar = ((totalReal / totalMax) * 100).toInt()

        val colorEstimado = ContextCompat.getColor(this, R.color.orange_graphic)
        val colorReal = ContextCompat.getColor(this, R.color.blue_graphic)

        setBarWidth(totalBar, porcentajeEstimadoBar, colorEstimado)
        setBarWidth(totalRBar, porcentajeRealBar, colorReal)

        val total = totalEstimado + totalReal
        val porcentajeEstimado = if (total > 0) ((totalEstimado / total) * 100).toFloat() else 0f
        val porcentajeReal = if (total > 0) ((totalReal / total) * 100).toFloat() else 0f

        val graficas = arrayListOf(
            Graphics(porcentajeEstimado, R.color.orange_graphic),
            Graphics(porcentajeReal, R.color.blue_graphic)
        )

        val fondo = CustomCircleDrawable(this, graficas)
        graph.background = fondo
    }

    /**
     * Configura el ancho y color de una barra de progreso proporcional a un porcentaje dado.
     *
     * @param bar Vista que representa la barra.
     * @param percentage Porcentaje de la barra respecto al máximo.
     * @param color Color a aplicar a la barra.
     */
    private fun setBarWidth(bar: View, percentage: Int, color: Int) {
        val params = bar.layoutParams
        params.width = maxOf(20, (300 * percentage / 100))
        bar.layoutParams = params
        bar.setBackgroundColor(color)
    }
}
