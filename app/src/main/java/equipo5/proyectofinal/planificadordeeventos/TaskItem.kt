package equipo5.proyectofinal.planificadordeeventos

/**
 * Modelo que representa una tarea dentro del evento, con sus subtareas anidadas.
 *
 * @property id Identificador único de la tarea.
 * @property name Nombre descriptivo de la tarea.
 * @property cost Presupuesto asignado (como string).
 * @property isChecked Marca si la tarea fue completada.
 * @property isExpanded Define si la tarea está expandida en la vista.
 * @property subTasks Lista de subtareas asociadas a esta tarea.
 */
data class TaskItem(val id: String,
                    val name: String,
                    val cost: String,
                    var isChecked: Boolean = false,
                    var isExpanded: Boolean = false,
                    val subTasks: List<SubTaskItem>)
