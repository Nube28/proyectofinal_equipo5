package equipo5.proyectofinal.planificadordeeventos

data class TaskItem(val id: String,
                    val name: String,
                    val cost: String,
                    var isChecked: Boolean = false,
                    var isExpanded: Boolean = false,
                    val subTasks: List<SubTaskItem>)
