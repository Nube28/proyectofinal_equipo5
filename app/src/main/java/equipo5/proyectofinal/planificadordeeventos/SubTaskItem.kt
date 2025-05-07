package equipo5.proyectofinal.planificadordeeventos

data class SubTaskItem(val id: String,
                       val name: String,
                       val cost: String,
                       var isChecked: Boolean = false,
                       val taskId: String)
