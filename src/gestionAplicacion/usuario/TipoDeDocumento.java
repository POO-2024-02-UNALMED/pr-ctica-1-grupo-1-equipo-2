package gestionAplicacion.usuario;

/**
 * @author Alan David Racines Casierra
 * */
public enum TipoDeDocumento {
	
	CC("Cedula de ciudadania"), TI("Tarjeta de identidad"), CE("Cedula de extranjeria");
	
	private String nombre;
	private TipoDeDocumento(String nombre) {this.nombre = nombre;}
	private static TipoDeDocumento[] TiposDeDocumento= TipoDeDocumento.values();
	
	public static TipoDeDocumento[] getTiposDeDocumento() {
		return TiposDeDocumento;
	}
	public static void setTiposDeDocumento(TipoDeDocumento[] tiposDeDocumento) {
		TiposDeDocumento = tiposDeDocumento;
	}
	
	/**
	 * Description : Este método genera un String que se imprimirá en pantalla, con el fin de que el usuario
	 * pueda visualizar los distintos tipos de tipos de documento
	 * @return resultado : Retorna un string, que se imprimirá en pantalla como parte del menú de las funcionalidades.
	 * */
	public static String mostrarTiposDeDocumento() {
		String resultado = null;
		int i = 1;
		for (TipoDeDocumento TipoDeDocumento : TiposDeDocumento) {
			if (resultado == null) {
				resultado = i + ". "+ TipoDeDocumento.getNombre();
			}else {
				resultado = resultado + "\n" +  i + ". " + TipoDeDocumento.getNombre();
			}
			i++;
		}
		
		return resultado;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	
}
