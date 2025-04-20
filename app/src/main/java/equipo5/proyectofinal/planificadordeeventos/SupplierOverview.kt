package equipo5.proyectofinal.planificadordeeventos

// Clase de modelo para los proveedores

data class SupplierOverview(
    val Supplier_name: String,
    var isSelected: Boolean = false,
    val id: String = "",  // Para almacenar el ID
    val price: Double = 0.0
)