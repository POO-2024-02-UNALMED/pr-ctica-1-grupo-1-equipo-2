package gestionAplicacion.proyecciones;

import java.io.Serializable;

/**
 * @author Alan David Racines Casierra
 * */
public class Asiento implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String numeroAsiento;
	private boolean disponibilidad = true;
	
	public Asiento(int fila, int columna){
		this.numeroAsiento = (fila + 1) + "-" + (columna + 1);
	}
	public String getNumeroAsiento() {
		return numeroAsiento;
	}

	public void setNumeroAsiento(String numeroAsiento) {
		this.numeroAsiento = numeroAsiento;
	}

	public boolean isDisponibilidad() {
		return disponibilidad;
	}

	public void setDisponibilidad(boolean disponibilidad) {
		this.disponibilidad = disponibilidad;
	}
	
}
