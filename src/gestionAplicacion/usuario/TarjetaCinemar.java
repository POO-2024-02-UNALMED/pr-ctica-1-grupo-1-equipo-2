package gestionAplicacion.usuario;

import java.io.Serializable;

/**
 * @author Alan David Racines Casierra
 * */
public class TarjetaCinemar implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//Atributos
	private double saldo;
	private Cliente dueno;
	
	
	//constructores
	public TarjetaCinemar(){}
	
	public TarjetaCinemar(int saldo,Cliente dueno) {

		this.saldo = saldo;
		this.dueno = dueno;
		
	}
	
	//metodos
	
	/**
	 * Description : Este método se encarga de ingresar el saldo dado a la tarjeta cinemar
	 * @param saldo : Este método recibe como parámetro el saldo a ingresar
	 * (De tipo double)
	 * @return <b>void</b> : No hay retorno
	 * */
	public void ingresarSaldo(double saldo) {this.saldo+=saldo;}
	
	
	/**
	*Description: se le descuenta a la tarjeta cinemar el monto pasado en el parametro
	*@param saldo : se le pasa el monto a ser descontado
	*(De tipo double)
	*@return <b>void</b> : No hay retorno
	*/
	public void hacerPago(double saldo) {
		this.saldo-=saldo;
	}

	//getters y setters
	public double getSaldo() {
		return saldo;
	}


	public Cliente getDueno() {
		return dueno;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}


	public void setDueno(Cliente dueno) {
		this.dueno = dueno;
	}	
	
	
}
