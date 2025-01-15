package gestionAplicacion.usuario;

/**
*@author Alan David Racines Casierra
**/
public enum TipoDeDocumento {

CC(nombre:"cedula de ciudadania), TI(nombre:"Tarjeta de identidad"), CE(nombre:"cedula de extranjeria");

private String nombre;
private TipoDeDocumento(String nombre) {this.nombre = nombre;}

}
