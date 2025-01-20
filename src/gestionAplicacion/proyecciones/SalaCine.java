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
	
	/**
	 * Description: Este método se encarga de verificar si una persona tiene al menos un ticket registrado en su array que cumpla los 
	 * siguientes criterios para ingresar a la sala de cine:
	 * <ol>
	 * <li> La película asociada al ticket coincide con la pelicula en presentacion de la sala de cine.</li>
	 * <li> La fecha actual es anterior a la fecha en que finaliza la película.</li>
	 * <li>La sala de cine asociada al ticket es la misma que la sala de cine que ejecuta este método.</li>
	 * </ol>
	 * @param cliente : Este método solicita al cliente (De tipo cliente) que va a ingresar a la SalaDeCine.
	 * @return <b>boolean</b> : Este método se encarga de retornar un boolean que será el resultado del proceso de verificación de entrada 
	 * a la sala de Cine.
	 * */
	public boolean verificarTicket(Cliente cliente) {
		
		boolean verificacionIngresoASala = false;
		boolean verificacionPelicula = false;
		boolean verificacionSalaCine = false;
		boolean verificacionHorario = false;
		Ticket ticketVerificado = null;
		
		//Verificamos si el atributo película de alguno de los tickets que tiene el cliente coinicide con la película en presentación
		//Verificamos si el atributo salaDeCine de ticket tiene asociado esta sala de cine
		//Verificamos si la fecha de actual no excede a la fecha en la que se presentaba la película más la duración de la misma
		for (Ticket ticket : cliente.getTickets()) {
			
			verificacionSalaCine = ticket.getSalaDeCine().equals(this);
			
			verificacionPelicula = ticket.getPelicula().equals(this.peliculaEnPresentacion);
			
			verificacionHorario = ticket.getHorario().equals(this.horarioPeliculaEnPresentacion) &
			SucursalCine.getFechaActual().isBefore(this.horarioPeliculaEnPresentacion.plus( this.peliculaEnPresentacion.getDuracion() ) ); 

			verificacionIngresoASala = verificacionPelicula & verificacionHorario & verificacionSalaCine;
			
			//En caso de encontrarlo rompemos el ciclo
			if (verificacionIngresoASala) {
				ticketVerificado = ticket;
				break;
			}
		}
		
		//Eliminamos la referencia del ticket verificado, en caso de que la verificación sea correcta, del array de tickets del cliente
		//Añadimos la película vista al historial de películas del cliente
		//En caso de que sea la primera vez que ve la película, la añadimos al array de películas para calificar
		if (verificacionIngresoASala) {
			
			if (!cliente.getHistorialDePeliculas().contains(ticketVerificado.getPelicula())) {
				cliente.getPeliculasDisponiblesParaCalificar().add(ticketVerificado.getPelicula());
			}

			cliente.getHistorialDePeliculas().add(ticketVerificado.getPelicula());
			
			cliente.getTickets().remove(ticketVerificado);
			
	    }
		
		//Retornamos el resultado de la verificación
		return verificacionIngresoASala;
	}
	
	/**
	 * Description: Este método se encarga actualizar la película en presentación, según los siguientes criterios: 
	 * <ol>
	 * <li>La sala de cine en que se presentará alguna de las películas en cartelera de la sucursal de cine 
	 * coincide con alguna de las salas de cine de esta. </li>
	 * <li>Revisamos si esa película tiene algún horario cercano o igual a la fecha actual durante el cuál estará o esta siendo presentada.</li>
	 * </ol>
	 * una vez hecho esto y cumpla con los dos criterios anteriores, limpiamos los asientos de la sala de cine, cambiando su disponibilidad a libre, y
	 * por último actualizamos la información de la disponibilidad de los asientos, tomando como referencia la información de los asientos virtuales 
	 * que coincidieron en fecha y hora de la película en presentación, además modificamos el atributo horario pelicula en presentación
	 * y pelicula en presentación de la sala de cine.
	 * */
	public void actualizarPeliculasEnPresentacion() {
		
		Pelicula peliculaPresentacion = null;
		LocalDateTime horarioPresentacion = null;
		
		LocalDateTime horarioMasCercanoAlActual = null;
		boolean firstTimeComparacionHorario = true;
		
		boolean firstTimePosiblePeliculaPresentacionEncontrada = true;
		
		//Actualizamos la película
		for (Pelicula pelicula : ubicacionSede.getCartelera()) {
			
			//Verificamos si la película tiene el mismo número de sala y tipo de formato que la sala de cine que ejecuta el método
			if ( pelicula.getSalaPresentacion().equals(this) ) {
				
				firstTimeComparacionHorario = true;

				//Para evitar que haga la comparación y le settee una película con un horario que no le corresponde
				if (pelicula.filtrarHorariosPeliculaParaSalaCine().size() == 0) {
					continue;
				}
				
				for (LocalDateTime horario : pelicula.filtrarHorariosPeliculaParaSalaCine()) {
					//Si es la primera vez que se realiza la comparación los setteamos como el valor más cercano al actual
					if (firstTimeComparacionHorario) {
						horarioMasCercanoAlActual = horario;
						firstTimeComparacionHorario = false;
					}
					
					//Si el horario es después del más cercano al actual y además no es después a la hora actual
					if ( (horario.isAfter(horarioMasCercanoAlActual)) && !(horario.isAfter(SucursalCine.getFechaActual())) ) {
						//Lo setteamos como el más cercano al actual
						horarioMasCercanoAlActual = horario;
					}
					
				}
				
				//Este try es para dos casos: 
				//1. No hay películas en presentación en el horario actual o la película no tenía horarios disponibles (Error en el if).
				//2. Es la primera vez que se realiza este proceso (Serialización o arranque del programa fuera de la jornada laboral) (Error en el else if).
				try {
					//Si el horarioMasCercanoAlActual es anterior o igual a la fecha actual, y es la primera vez que realizamos el proceso.
					//O si el horarioMasCercanoAlActual es anterior o igual a la fecha actual y este es posterior a al horario más cercano encontrado
					// previmente de una película cuya sala coincida también. (De esta forma no importa el orden en el que las 
					// películas sean analizadas y se garantiza que siempre estará la película más reciente).
					if ( !(horarioMasCercanoAlActual.isAfter(SucursalCine.getFechaActual()) ) && firstTimePosiblePeliculaPresentacionEncontrada ) {
						horarioPresentacion = horarioMasCercanoAlActual;
						peliculaPresentacion = pelicula;
						firstTimePosiblePeliculaPresentacionEncontrada = false;
					
					}else if( !(horarioMasCercanoAlActual.isAfter(SucursalCine.getFechaActual()) ) && 
							( horarioMasCercanoAlActual.isAfter(horarioPresentacion))  ){
						horarioPresentacion = horarioMasCercanoAlActual;
						peliculaPresentacion = pelicula;
					}
				}catch (NullPointerException e) {
					continue;
				}
			}
		} 
		
		//Ejecutamos esta operación en caso de que se haya encontrado un cambio para la película en presentación
		if (peliculaPresentacion != null) {
			this.setPeliculaEnPresentacion(peliculaPresentacion);
			this.setHorarioPeliculaEnPresentacion(horarioPresentacion);
			
			//Actualizamos los asientos de la sala de cine
			for (int i = 0; i < this.asientos.length; i++) {
		        for (int j = 0; j < this.asientos[i].length; j++) {
		        	//Preparamos los asientos para ser actualizados cambiando su disponibilidad a libre
		        	this.cambiarDisponibilidadAsientoOcupadoParaLibre(i+1, j+1);
		        	//Actualizamos el asiento según la información de la sala de cine virtual
		        	if (!this.peliculaEnPresentacion.isDisponibilidadAsientoSalaVirtual(horarioPresentacion, i+1, j+1)) {
		            this.cambiarDisponibilidadAsientoLibreParaOcupado(i+1, j+1);
		        	}
		        }
		    }
			
		}
		
	}
	
	/**
	 * Description : Este método se encarga de retornar la disponibilidad de un asiento dada su fila y su columna.
	 * @param fila : Este método recibe la fila del asiento a consultar (De tipo int).
	 * @param columna : Este método recibe la columna del asiento a consultar (De tipo int).
	 * @return <b>boolean</b> : Este método retorna la disponibilidad del asiento consultado.
	 * */
	public boolean isDisponibilidadAsiento(int fila, int columna) {
		return this.asientos[fila - 1][columna - 1].isDisponibilidad();
		
	}
	
	/**
	 * Description : Este método se encarga de generar un string, que se imprimirá en pantalla, para visualizar los
	 * asientos con su respectivo número de asiento.
	 * @return <b>String</b> : Este método retorna un string que será impreso en pantalla para que el cliente 
	 * pueda visualizar de mejor forma el proceso de entrada a la sala de cine.
	 * */
	public String mostrarAsientosParaPantalla() {
		StringBuilder resultado = new StringBuilder("\n");
	    resultado.append("  -------------------------------------------------------------- \n                           Pantalla\n");
	    resultado.append("    ");
	    resultado.append("\n");
	    resultado.append("                       Asientos de Sala\n");
	    
	    // Mostrar asientos
	    for (int i = 0; i < this.asientos.length; i++) {
	    	resultado.append("         ");
	        for (int j = 0; j < this.asientos[i].length; j++) {
	            resultado.append("[");
	            resultado.append(this.asientos[i][j].getNumeroAsiento());
	            resultado.append("] ");
	        }
	        resultado.append("\n");
	    }

	    return resultado.toString();
	}
	
	
	
	/**
	 * Description : Este método se encarga de generar un string que se imprimirá en pantalla para visualizar los
	 * la pantalla de la sala de cine con un pequeño mensaje, además se llama al método mostrarAsientosParaPantalla.
	 * @return <b>String</b> : Este método retorna un string que será impreso en pantalla para que el cliente 
	 * pueda visualizar de mejor forma el proceso de entrada a la sala de cine.
	 * */
	public String mostrarPantallaSalaCine () {
		StringBuilder resultado = new StringBuilder("  -------------------------------------------------------------- ");
		
		for (int i = 0; i < 6; i++) {
			resultado.append("\n" + " |                      					|");
		}
		
		resultado.append("\n |             Programación Orientada a objetos			|");
		
		for (int i = 0; i < 6; i++) {
			resultado.append("\n |                      					|");
		}
		
		resultado.append(this.mostrarAsientosParaPantalla());
		
		return resultado.toString();
	}
	
	/**
	 * Description : Este método se encarga de revisar si una sala de cine tendrá durante ese día más películas en presentación.
	 * @return <b>boolean</b> : retorna el estado de la validación.
	 * */
	public boolean tieneHorariosPresentacionHoy() {
		
		boolean isHorarioEncontrado = false;		
		
		for (Pelicula pelicula : ubicacionSede.getCartelera()) {
			if (pelicula.getSalaPresentacion().equals(this)) {
				for (LocalDateTime horario : pelicula.filtrarHorariosPeliculaParaSalaCine()) {
					
					if (horario.plus(pelicula.getDuracion()).isAfter(SucursalCine.getFechaActual())) {
						isHorarioEncontrado = true;
						break;
					}
				}
				
				if (isHorarioEncontrado) {
					break;
				}
			}
		}
		
		return isHorarioEncontrado;
		
	}
	
	// Getters and Setters
	public Pelicula getPeliculaEnPresentacion() {
		return peliculaEnPresentacion;
	}

	public void setPeliculaEnPresentacion(Pelicula peliculaEnPresentacion) {
		this.peliculaEnPresentacion = peliculaEnPresentacion;
	}
	
	public LocalDateTime getHorarioPeliculaEnPresentacion() {
		return horarioPeliculaEnPresentacion;
	}
	
	public void setHorarioPeliculaEnPresentacion(LocalDateTime horarioPeliculaEnPresentacion) {
		this.horarioPeliculaEnPresentacion = horarioPeliculaEnPresentacion;
	}
	
	public int getNumeroSala() {
		return numeroSala;
	}

	public void setNumeroSala(int numeroSala) {
		this.numeroSala = numeroSala;
	}

	public String getTipoDeSala() {
		return tipoDeSala;
	}

	public void setTipoDeSala(String tipoDeSala) {
		this.tipoDeSala = tipoDeSala;
	}
	
	public Asiento[][] getAsientos() {
		return asientos;
	}

	public void setAsientos(Asiento[][] asientos) {
		this.asientos = asientos;
	}

	public SucursalCine getUbicacionSede() {
		return ubicacionSede;
	}

	public void setUbicacionSede(SucursalCine ubicacionSede) {
		this.ubicacionSede = ubicacionSede;
	}

	public int getIdSalaCine() {
		return idSalaCine;
	}

	public void setIdSalaCine(int idSalaCine) {
		this.idSalaCine = idSalaCine;
	}

	public static int getCantidadSalasDeCineCreadas() {
		return cantidadSalasDeCineCreadas;
	}

	public static void setCantidadSalasDeCineCreadas(int cantidadSalasDeCineCreadas) {
		SalaCine.cantidadSalasDeCineCreadas = cantidadSalasDeCineCreadas;
	}

}

