package gestionAplicacion.proyecciones;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import gestionAplicacion.SucursalCine;
import gestionAplicacion.usuario.Cliente;
import gestionAplicacion.usuario.Ticket;

/**
 * @author Andres Alejandro Rosero Toledo
 * */
public class SalaCine implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int idSalaCine;
	private static int cantidadSalasDeCineCreadas;
	private int numeroSala;
	private String tipoDeSala;
	private LocalDateTime horarioPeliculaEnPresentacion;
	private Asiento[][] asientos;
	private Pelicula peliculaEnPresentacion;
	private SucursalCine ubicacionSede;
	
	//Constructors
	
	//Este constructor se usa para ser llamado por el otro constructor para aumentar la cantidad de salas de
	//cine creadas y ese valor asociarselo al ID de la sala
	public SalaCine() {
		cantidadSalasDeCineCreadas++;
		this.idSalaCine = cantidadSalasDeCineCreadas;
	}
	
	//Con este constructor se llama al constructor vacio  y se inicializan los objetos creados 
	//de esta clase con sus atributos, ademas se agrega la instancia a las salas de cine de la sucursal pasada como parametro
	public SalaCine(int nSala, String tipoDeSala, SucursalCine ubicacionSede){
		this();
		this.numeroSala = nSala;
		this.tipoDeSala = tipoDeSala;
		this.ubicacionSede = ubicacionSede;
		ubicacionSede.getSalasDeCine().add(this);
		this.asientos = this.crearAsientosSalaDeCine();

	}

	//Methods
	/**
	 * Description : Este método se encarga de generar asientos para la sala de cine, facilitando el proceso de crear una sala de cine.
	 * @return Asiento[][] :  Este método retorna una matriz de asientos.
	 * */
	private Asiento[][] crearAsientosSalaDeCine() {
		Asiento[][] DistribucionAsientosSalaDeCine = new Asiento[8][8];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				DistribucionAsientosSalaDeCine[i][j] = new Asiento(i,j);
			}
		}
		return DistribucionAsientosSalaDeCine;
	}
	
	/**
	 * Description : Este método se encarga de generar un string que se imprimirá en pantalla para visualizar los
	 * asientos y su disponiblidad.
	 * @return <b>String</b> : Este método retorna un string que será impreso en pantalla para que el cliente 
	 * pueda interactuar con la funcionalidad.
	 * */
	public String mostrarAsientos() {
		StringBuilder resultado = new StringBuilder("\n");
		
		resultado.append("Asientos de Cine\n");
	    resultado.append("\n(Fila: distribución horizontal de asientos)\n(Columna: distribución vertical de asientos)\n(Número de asiento: Intersección fila y columna)\n");
	    resultado.append("  --------------------------------- \n              Pantalla\n");
	    resultado.append("    ");
	    
	    // Agregar números de columnas
	    for (int i = 0; i < this.asientos[0].length; i++) {
	        resultado.append(String.format("%-4d", i + 1));
	    }
	    resultado.append("\n");

	    // Mostrar asientos
	    for (int i = 0; i < this.asientos.length; i++) {
	        resultado.append(String.format("%-2d ", i + 1));
	        for (int j = 0; j < this.asientos[i].length; j++) {
	            resultado.append("[");
	            resultado.append(this.asientos[i][j].isDisponibilidad() ? "O" : "X");
	            resultado.append("] ");
	        }
	        resultado.append("\n");
	    }

	    return resultado.toString();
	}
	
	/**
	 * Description : Este método se encarga de modificar la disponiblidad de un asiento dada su posición,
	 * si su disponibilidad es verdadera la cambia a falsa, se usa para separar un asiento luego de ser comprado.
	 * @param numeroAsiento : Este método recibe como parámetro el numero del asiento seleccionado por el cliente
	 * (De tipo String) durante el proceso de la funcionalidad 1.
	 * */
	public void cambiarDisponibilidadAsientoLibre(String numeroAsiento) {
		for (Asiento[] asientos : this.asientos) {
			for (Asiento asiento : asientos) {
				if (asiento.getNumeroAsiento().equals(numeroAsiento)) {
					asiento.setDisponibilidad(false);
					break;
				}
			}
		}
	}
	
	/**
	 * Description : Este método se encarga de modificar la disponiblidad de un asiento dada su posición,
	 * si su disponibilidad es verdadera la cambia a falsa, se usa para cambiar la disponibilidad de un asiento
	 * la actualizar la sala con base en la información de la sala virtual (En el método actualizarPeliculaEnPresentacion()).
	 * @param fila : Índice de la fila del asiento que queremos modificar (De tipo int).
	 * @param columna : Índice de la columna del asiento que queremos modificar (De tipo int).
	 * */
	private void cambiarDisponibilidadAsientoLibreParaOcupado(int fila, int columna) {
		this.asientos[fila - 1][columna - 1].setDisponibilidad(false);	
	}
	
	/**
	 * Description : Este método se encarga de modificar la disponiblidad de un asiento dada su posición,
	 * si su disponibilidad es false la cambia a true, es especialmente útil para preparar la sala de cine
	 * para presentar una nueva película (En el método actualizarPeliculaEnPresentacion()).
	 * @param fila : Índice de la fila del asiento que queremos modificar (De tipo int).
	 * @param columna : Índice de la columna del asiento que queremos modificar(De tipo int).
	 * */
	private void cambiarDisponibilidadAsientoOcupadoParaLibre(int fila, int columna) {
		if (!this.asientos[fila - 1][columna - 1].isDisponibilidad()) {
			this.asientos[fila - 1][columna - 1].setDisponibilidad(true);
		}
	}
	
	/**
	 * Description : Este método se encarga de filtrar las salas de cine según si su película aún se encuentra en presentación,
	 * para esto verifica que el horario de la película en presentación más su duración no sea menor a la hora actual.
	 * @param sucursalCine : Este método recibe como parámetro la sede (De tipo SucursalCine) en donde se realiza la busqueda desde sus salas de cine
	 * @return  <b>ArrayList(SalaCine)</b> : Este método retorna las salas de cine, ( De tipo ArrayList(SalaCine) ),
	 * que aún tienen su película en presentación, con el fin de ser las únicas que serán mostradas en pantalla durante el proceso de la funcionalidad 1. 
	 * */
	public static ArrayList<SalaCine> filtrarSalasDeCine(SucursalCine sucursalCine) {
		ArrayList<SalaCine> salasDeCineDisponibles = new ArrayList<>();
		
		for ( SalaCine salaDeCine : sucursalCine.getSalasDeCine() ) {
			//Se usa try en caso de que en las salas de cine no se haya setteado una película en presentación, por lo tanto, 
			// ni tampoco un horario en presentacion en estos momentos y devolver un array vacío, para continuar con el proceso.
			try {
				if (salaDeCine.horarioPeliculaEnPresentacion.plus(salaDeCine.peliculaEnPresentacion.getDuracion()).isAfter(SucursalCine.getFechaActual())) {
					salasDeCineDisponibles.add(salaDeCine);
				}
			}catch(NullPointerException e) {
				continue;
			}
		}
		
		return salasDeCineDisponibles;
	}
	
	/**
	 * Description : Este método se encarga de generar un listado de la salas de cine con información relevante de estas,
	 * con el fin de que el usuario elija una de las opciones disponibles para ingresar.
	 * @param salasDeCine : Este método recibe como parámetro un listado de salas de cine disponibles ( De tipo ArrayList(SalaCine) ).
	 * @return <b>String</b>: Retorna un string con las salas de cine disponibles.
	 * */
	public static String mostrarSalaCine(ArrayList<SalaCine> salasDeCine) {
		
		StringBuilder resultado = new StringBuilder();
		int i = 1;
		
		for (SalaCine salaDeCine : salasDeCine) {
			resultado.append("\n" + i + ". Número sala de cine: " + salaDeCine.numeroSala 
			+ "; Formato de sala de cine : " + salaDeCine.tipoDeSala
			+ "; Película en presentación : " + salaDeCine.peliculaEnPresentacion.getNombre()
			+ "; Horario película : " + salaDeCine.horarioPeliculaEnPresentacion);
			i++;
		}
		
		return resultado.toString();
	}
	
}