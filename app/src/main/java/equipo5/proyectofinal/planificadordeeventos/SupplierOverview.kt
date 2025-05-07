package equipo5.proyectofinal.planificadordeeventos

/**
 * Modelo que representa un proveedor disponible para una subtarea.
 *
 * @property Supplier_name Nombre del proveedor.
 * @property isSelected Marca si el proveedor fue seleccionado para la subtarea.
 * @property id Identificador único del proveedor.
 * @property price Precio ofrecido por el proveedor.
 */
data class SupplierOverview(
    val Supplier_name: String,
    var isSelected: Boolean = false,
    val id: String = "",
    val price: Double = 0.0
)