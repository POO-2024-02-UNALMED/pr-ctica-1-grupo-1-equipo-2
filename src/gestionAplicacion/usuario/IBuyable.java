package gestionAplicacion.usuario;


/**
 * @author Alan David Racines Casierra y Jeronimo Rua H
*<b>Description</b>: Esta es la interfaz en la cual se harán los pagos. Por el momento, se declaran
*los siguientes métodos:
*<ol>
 *     <li> <b>verificarPago</b> : el método verificarPago, para verificar si el pago fue realizado o no.</li>
*<li> <b>procesarPagoRealizado</b> : el método procesarPagoRealizado, 
*en el cuál se encuentran los procesos a realizar por cada clase que los implemente luego de confirmar la totalidad del pago.
*verificar que el pago fue realizado.</li>
*<li> <b>factura</b> : el método de factura, para mostrar en pantalla los detalles de la compra realizada. Estos varian dependiendo de las funcionalidades.</li>
*</ol>
*/
public interface IBuyable {
	
	void procesarPagoRealizado(Cliente cliente);
	String factura();
	static Boolean verificarPago(double precio) {
		if (precio == 0) {
			return true;
		}
		return false;
	}

}
