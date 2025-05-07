package equipo5.proyectofinal.planificadordeeventos

/**
 * Modelo de datos que representa una vista general de un evento.
 *
 * @property event_iv_overview Identificador de la imagen del evento.
 * @property event_name_overview Nombre del evento.
 * @property event_budget_overview Presupuesto total asignado al evento.
 * @property event_category_overview Categoría o tipo del evento.
 * @property id Identificador único del evento en la base de datos.
 */
data class EventOverview(var event_iv_overview: Int,
                         var event_name_overview: String,
                         var event_budget_overview: String,
                         var event_category_overview: String,
                         val id: String)
