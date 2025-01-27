package gestionAplicacion.usuario;


import gestionAplicacion.servicios.Producto;
import gestionAplicacion.servicios.herencia.Servicio;

/**
 * @author Alan David Racines Casierra y Jeronimo Rua H
*<b>Description</b>: Esta es la interfaz en la cual se harán los pagos. Por el momento, se declaran
*los siguientes métodos:
*<ol>
 *<li> <b>verificarPago</b> : el método verificarPago, para verificar si el pago fue realizado o no.</li>
 * <li> <b>obtenerDetallesCompra</b> : el método obtenerDetallesCompra, para obtener los detalles de la compra realizada.</li>
*<li> <b>procesarPagoRealizado</b> : el método procesarPagoRealizado, 
*en el cuál se encuentran los procesos a realizar por cada clase que los implemente luego de confirmar la totalidad del pago.
*verificar que el pago fue realizado.</li>
*<li> <b>factura</b> : el método de factura, para mostrar en pantalla los detalles de la compra realizada. Estos varian dependiendo de las funcionalidades.</li>
*</ol>
*/
public interface IBuyable {
	
	void procesarPagoRealizado(Cliente cliente);
	default String obtenerDetallesCompra(Cliente cliente, double valor) {
		StringBuilder sb = new StringBuilder();
		sb.append("Detalles de la compra:\n");
		sb.append("Cliente: ").append(cliente.getNombre()).append("\n");
		sb.append("Documento: ").append(cliente.getDocumento()).append("\n");
		sb.append("Fecha de compra: ").append(java.time.LocalDate.now()).append("\n");
		sb.append("Valor total: ").append(valor).append("\n");
		return sb.toString();
	}
	String factura();
	static Boolean verificarPago(double precio) {
		if (precio == 0) {
			return true;
		}
		return false;
	}

	static Producto validarBono(String codigo, Servicio servicio) {
		Producto producto;
		for (int i = 0; i < servicio.getBonosCliente().size(); i++) {
			if (servicio.getBonosCliente().get(i).getCodigo().equals(codigo) && servicio.getBonosCliente().get(i).getTipoServicio().equalsIgnoreCase(servicio.getNombre())) {
				producto = servicio.getBonosCliente().get(i).getProducto();
				return producto;
			}
		}
		return null;
	}
}
