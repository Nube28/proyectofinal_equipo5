package equipo5.proyectofinal.planificadordeeventos

/**
 * Modelo de datos que representa una subtarea dentro de una tarea de un evento.
 *
 * @param id Identificador único de la subtarea (document ID en Firestore).
 * @param name Nombre de la subtarea.
 * @param cost Presupuesto o costo asignado a la subtarea, en formato de texto.
 * @param isChecked Indica si la subtarea ha sido marcada como completada.
 * @param taskId ID de la tarea principal a la que pertenece esta subtarea.
 */
data class SubTaskItem(val id: String,
                       val name: String,
                       val cost: String,
                       var isChecked: Boolean = false,
                       val taskId: String)
