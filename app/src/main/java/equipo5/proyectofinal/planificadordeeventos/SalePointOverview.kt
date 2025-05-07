package equipo5.proyectofinal.planificadordeeventos

/**
 * Representa un resumen de un proveedor (punto de venta) seleccionado para una subtarea específica.
 *
 * @param sale_point_name Nombre del proveedor o punto de venta.
 * @param sale_point_cost Costo asociado al proveedor.
 * @param subtaskId ID de la subtarea a la que está asociado este proveedor.
 */
data class SalePointOverview (var sale_point_name: String,
                              var sale_point_cost : Int,
                              var subtaskId: String)
