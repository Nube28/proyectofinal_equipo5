package equipo5.proyectofinal.planificadordeeventos

/**
 * Modelo de datos que representa la vista general (overview) de una subtarea en la interfaz.
 *
 * @param tv_subtask_name Nombre visible de la subtarea.
 * @param tv_subtask_budget Presupuesto asignado a la subtarea.
 * @param tv_subtask_description Descripción detallada de la subtarea (opcional).
 * @param subtaskId Identificador único de la subtarea en Firestore.
 */
data class SubtaskOverview(
    val tv_subtask_name: String,
    val tv_subtask_budget: Int,
    val tv_subtask_description: String = "",
    val subtaskId: String
)
