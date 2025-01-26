package gestionAplicacion;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import gestionAplicacion.proyecciones.Pelicula;
import gestionAplicacion.proyecciones.SalaCine;
import gestionAplicacion.servicios.Arkade;
import gestionAplicacion.servicios.Bono;
import gestionAplicacion.servicios.Producto;
import gestionAplicacion.servicios.herencia.Servicio;
import gestionAplicacion.usuario.Cliente;
import gestionAplicacion.usuario.Membresia;
import gestionAplicacion.usuario.MetodoPago;
import gestionAplicacion.usuario.TarjetaCinemar;
import gestionAplicacion.usuario.Ticket;

/**
 * @author Todos los integrantes del equipo participaron en la construcción de esta clase
 * */
public class SucursalCine implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//Atributos estaticos serializables
	private static LocalDateTime fechaActual;
	private static LocalDate fechaValidacionNuevoDiaDeTrabajo;
	private static LocalDate fechaRevisionLogicaDeNegocio; 
	private static ArrayList<Cliente> clientes = new ArrayList<>();
	private static ArrayList<Arkade> juegos = new ArrayList<>();
	private static ArrayList<MetodoPago> metodosDePagoDisponibles = new ArrayList<>();
	private static ArrayList<Ticket> ticketsDisponibles = new ArrayList<>();
	private static ArrayList<Membresia> tiposDeMembresia = new ArrayList<>();

			
	//Atributos de instancia serializables
	private String lugar;
	private ArrayList<SalaCine> salasDeCine = new ArrayList<>();
	private ArrayList<Producto> inventarioCine = new ArrayList<>();
	private ArrayList<Pelicula> cartelera = new ArrayList<>();
	private ArrayList<Ticket> ticketsParaDescuento = new ArrayList<>();
	private ArrayList<Servicio> servicios = new ArrayList<>();
	private ArrayList<Bono> bonosCreados = new ArrayList<>();
	private ArrayList<TarjetaCinemar> InventarioTarjetasCinemar = new ArrayList<>();
	private int cantidadTicketsCreados;
	
	//Atributos variables
	private int idSucursal;
	private static int cantidadSucursales;
	private static final LocalTime FIN_HORARIO_LABORAL = LocalTime.of(23, 00);
	private static final LocalTime INICIO_HORARIO_LABORAL = LocalTime.of(10, 00);
	private static final Duration LIMPIEZA_SALA_DE_CINE = Duration.ofMinutes(30);
	private static ArrayList<SucursalCine> sucursalesCine = new ArrayList<>();
	
	//Methods
	
	/**
	 * Description : Este método se encarga de crear un string que se imprimirá en pantalla para visualizar las 
	 * sucursales de nuestra franquicia.
	 * @return <b>String</b> : Retorna un string con el lugar de nuestras distintas dependencias, con el fin de que el cliente 
	 * elija a cual de estas desea ingresar.
	 * */
	public static String mostrarSucursalCine(){
		
		StringBuilder resultado = new StringBuilder();
		int i = 1;
		
		for (SucursalCine sucursal : sucursalesCine) {
			
			resultado.append("\n" + i + ". Sucursal Cinemar en "  + sucursal.lugar);
			i++;
			
		}
		
		return resultado.toString();
		
	}
	
	/**
	 * Description : Este método se encarga de actualizar las salas de todas las sedes, para esto, iteramos sobre el ArrayList de las sedes,
	 * luego iteramos sobre el ArrayList de las salas de cine de cada sede.
	 * */
	public static void actualizarPeliculasSalasDeCine() {
		
		for (SucursalCine sede : sucursalesCine) {
			//Evaluamos si la sala de cine en cuestion necesita un cambio de película en presentación
			for (SalaCine salaDeCine : sede.salasDeCine) {
				//try en caso de que sea la primera vez que se realiza este proceso y el horarioPeliculaEnPresentacion sea nulo
				try {
					//Solo actualizamos las salas de cine que estrictamente deban ser actualizadas
					if ( !(salaDeCine.getHorarioPeliculaEnPresentacion().plus(salaDeCine.getPeliculaEnPresentacion().getDuracion().plus(LIMPIEZA_SALA_DE_CINE)).isAfter(fechaActual) ) ) {
						salaDeCine.actualizarPeliculasEnPresentacion();
					}
				}catch(NullPointerException e) {
					//Llegamos acá en caso de desearialización o primer inicio de programa
					salaDeCine.actualizarPeliculasEnPresentacion();
					
					
				}
			}
		}
	}
	
	/**
	 * Description : Este método se encarga de eliminar los horarios que ya no pueden ser presentados luego de una semana 
	 * o luego de la deserialización de todas las películas de cada sucursal (Elimina los horarios anteriores al día 
	 * de la fecha actual). 
	 * */
	private static void dropHorariosVencidos() {
		
		//Iteramos sobre las sucursales
		for (SucursalCine sede : sucursalesCine) { 

			//Iteramos sobre las películas en cartelera
			for (Pelicula pelicula : sede.cartelera) {
				
				//Iteramos sobre los horarios de esa película
				Iterator<LocalDateTime> horariosPelicula = pelicula.getHorarios().iterator();
				
				while (horariosPelicula.hasNext()) {
					LocalDateTime horario = (LocalDateTime) horariosPelicula.next();
					//Verificamos si el horarios es anterior a la fecha actual menos la duración
					if (horario.toLocalDate().isBefore(fechaActual.toLocalDate())) {
						//Eliminamos su referencia de la sala de cine virtual (Asientos y horario)
						pelicula.getAsientosVirtuales().remove(pelicula.getHorarios().indexOf(horario));
						horariosPelicula.remove();
					}
				}
			}
		}
		
	}
	
	/**
	 * Description : Este método se encarga de crear 20 horarios por cada película en cartelera de la sucursal de cine, 
	 * teniendo en cuenta los siguientes criterios: 
	 * <ol>
	 * <li>El horario en el que se presentará la película se encuentra entre el horario de apertura y cierre de nuestras 
	 * instalaciones.</li>
	 * <li>La hora a la que termina la película es menor a la hora de cierre. </li>
	 * <li>Al finalizar una película se tiene en cuenta el tiempo de limpieza de la sala de cine.</li>
	 * <li>La creación de horarios no exceda una semana (Para ejecutar correctamente la lógica semanal de nuestro cine).</li>
	 * <li>Si varias películas serán presentadas en una sala se presentarán de forma intercalada evitando colisiones.</li>
	 * </ol>
	 * */
	private void crearHorariosPeliculasPorSala() {
		
		ArrayList<Pelicula> peliculasDeSalaDeCine = new ArrayList<>();
		
		final LocalDate limiteCreacionHorariosPeliculas =  fechaActual.toLocalDate().plusWeeks(1);
		
		//Iteramos sobre las salas de cine de esta sucursal
		for(SalaCine salaDeCine : this.salasDeCine) {
			
			LocalDateTime horarioParaPresentar = fechaActual.withMinute(0).withSecond(0).withNano(0);
			
			//Buscamos las películas de la cartelera a las cuales les corresponde esta sala de cine
			for(Pelicula pelicula : this.cartelera) {
				if (salaDeCine.equals(pelicula.getSalaPresentacion())) {
					peliculasDeSalaDeCine.add(pelicula);
				}
			}
			
			//Creamos 20 horarios por convención
			for (int i = 1; i <= 20; i++) {
				
				//Verificamos que no se exceda la proyección semanal de películas
				if (!horarioParaPresentar.toLocalDate().isBefore(limiteCreacionHorariosPeliculas)) {
					break;
				}
				
				//Iteramos sobre las películas que comparten sala de cine
				for (Pelicula pelicula : peliculasDeSalaDeCine) {
					
					//1 y 2 verifican que se encuentre en el horario laboral, 3 y 4 que la duración no exceda el final del horario laboral y no pase al
					//siguiente día
					if (horarioParaPresentar.toLocalTime().isBefore(FIN_HORARIO_LABORAL) &&
							horarioParaPresentar.toLocalTime().isAfter(INICIO_HORARIO_LABORAL) &&
							horarioParaPresentar.plus(pelicula.getDuracion()).toLocalTime().isBefore(FIN_HORARIO_LABORAL) &&
							horarioParaPresentar.plus(pelicula.getDuracion()).toLocalDate().equals(horarioParaPresentar.toLocalDate()) ) {
						
						//Creamos el horario y nos preparamos para crear el siguiente horario disponible
						pelicula.crearSalaVirtual(horarioParaPresentar);
						horarioParaPresentar = horarioParaPresentar.plus(pelicula.getDuracion());
						horarioParaPresentar = horarioParaPresentar.plus(LIMPIEZA_SALA_DE_CINE);
						
					}else {
						
						//En caso de ejecutar el programa en la madrugada/mañana (Antes del inicio laboral) no se cumple esta condición
						//El problema de no verficar esto es que se crearían los horarios un día después a partir del horario actual.
						//En caso de que sea necesario pasar al día siguiente para crear los próximos horarios
						if (horarioParaPresentar.toLocalTime().isAfter(INICIO_HORARIO_LABORAL)) {
							horarioParaPresentar = horarioParaPresentar.plusDays(1);
						}
						
						//Verificamos que no se exceda la proyección semanal de películas
						if (!horarioParaPresentar.toLocalDate().isBefore(limiteCreacionHorariosPeliculas)) {
							break;
						}
							
						//Nos ubicamos en el inicio de la jornada laboral
						horarioParaPresentar = horarioParaPresentar.withHour(INICIO_HORARIO_LABORAL.getHour())
								.withMinute(INICIO_HORARIO_LABORAL.getMinute());
						
						//Creamos el horario y nos preparamos para crear el siguiente horario disponible
						pelicula.crearSalaVirtual(horarioParaPresentar);
						horarioParaPresentar = horarioParaPresentar.plus(pelicula.getDuracion());
						horarioParaPresentar = horarioParaPresentar.plus(LIMPIEZA_SALA_DE_CINE);
						
						}
						
				}
			}
			
			//Limpiamos las películas que se presentarán en la sala para continuar con la siguiente sala de cine
			peliculasDeSalaDeCine.clear();
			
		}
			
	}
		
	/**
	 * Description: Este método se encarga de distribuir las películas en cartelera en las distintas salas de cine 
	 * de la sucursal de cine que ejecuta este método, para esta distribución se tienen encuenta 3 casos posibles:
	 * <ol>
	 * <li>Hay menos películas que salas de cine o igual cantidad de ambas.</li>
	 * <li>Hay más películas que salas de cine, pero caben exactamente la misma cantidad de películas en cada sala.</li>
	 * <li>Hay más películas que salas de cine, pero al menos una sala de cine debe tener 1 película más que todas 
	 * las otras (Principio de Dirichlet o del palomar).</li>
	 * </ol>
	 * */
	private void distribuirPeliculasPorSala() {
		
		String[] formatos = {"2D", "3D", "4D"};
		
		ArrayList<SalaCine> grupoSalasPorFormato = new ArrayList<>();
		ArrayList<Pelicula> grupoPeliculasPorFormato = new ArrayList<>();

		int cantidadMaxPeliculasPorSala = 0;
		int indice = 0;
		int contador = 0;
		
		//Iteramos sobre los distintos formatos de películas disponibles
		for (String formato : formatos) {
			
			//Guardamos en un ArrayList las salas de cine que coinciden con el formato
			//Sobre el que estamos iterando
			for (SalaCine salaDeCine : this.salasDeCine) {
				if (salaDeCine.getTipoDeSala().equals(formato)) {
					grupoSalasPorFormato.add(salaDeCine);
				}
			}
			
			//Guardamos en un ArrayList las películas que coinciden con el formato
			//Sobre el que estamos iterando
			for (Pelicula pelicula : this.cartelera) {
				if(pelicula.getTipoDeFormato().equals(formato)) {
					grupoPeliculasPorFormato.add(pelicula);
				}
			}
			
			//Evaluamos esto con el fin de determinar si debemos distribuir de forma especial o no
			if (grupoPeliculasPorFormato.size() > grupoSalasPorFormato.size()) {
				
				//Hallamos el número máximo de películas que pueden presentarse en cada sala de cine
				//Distribución exacta o Principio del palomar
				cantidadMaxPeliculasPorSala = grupoPeliculasPorFormato.size() % grupoSalasPorFormato.size() == 0  ? grupoPeliculasPorFormato.size() / grupoSalasPorFormato.size() : grupoPeliculasPorFormato.size() / grupoSalasPorFormato.size() + 1;
				
				//Setteamos la sala de cine en presentación
				for (Pelicula pelicula : grupoPeliculasPorFormato) {
					
					pelicula.setSalaPresentacion(grupoSalasPorFormato.get(indice));
					pelicula.setNumeroSalaPresentacion(grupoSalasPorFormato.get(indice).getNumeroSala());
					contador++;
					
					//En caso de que el contador sea igual al número máximo de películas por sala, cambiamos de sala
					// Y reiniciamos el contador
					if (contador == cantidadMaxPeliculasPorSala) {
						contador = 0;
						indice++;
					}
					
				}
				
			}else {
				
				for (Pelicula pelicula : grupoPeliculasPorFormato) {
					
					pelicula.setSalaPresentacion(grupoSalasPorFormato.get(indice));
					pelicula.setNumeroSalaPresentacion(grupoSalasPorFormato.get(indice).getNumeroSala());
					indice++;
					
				}
				
			}
			
			//Reiniciamos las variables para el próximo formato
			indice = 0;
			contador = 0;
			grupoPeliculasPorFormato.clear();
			grupoSalasPorFormato.clear();
			
		}
		
	}
	
	/**
	 * Description: Este método se encarga de realizar los preparativos para ejecutar la lógica de la funcionalidad #3:
	 * <ol>
	 * <li>Renueva las cantidades disponibles de los productos en inventario</li>
	 * <li>Eliminar los horarios de la semana anterior.</li>
	 * <li>Distribución de películas en las salas de cine y la creación de sus horarios.</li>
	 * <li>Eliminar los tickets comprados de películas de la semana anterior.</li>
	 * </ol>
	 * */
	public static void logicaSemanalSistemaNegocio() {
		ticketsDisponibles.clear();
		
		ArrayList<Pelicula> peliculas2D = new ArrayList<Pelicula>();
		
		for (SucursalCine sede : sucursalesCine) {
			
			for (Producto producto : sede.inventarioCine) {
				if(producto.getTipoProducto().equalsIgnoreCase("comida") || producto.getTipoProducto().equalsIgnoreCase("souvenir")) {
					producto.setCantidad(200);
				}
			}
			
			for(Pelicula pelicula:sede.cartelera) {
				if(pelicula.getTipoDeFormato().equals("2D")){
					peliculas2D.add(pelicula);
				}
			}
			
			for (Pelicula pelicula : peliculas2D) {
				sede.logicaCalificacionPeliculas(pelicula);
			}
			
			sede.distribuirPeliculasPorSala();
			sede.crearHorariosPeliculasPorSala();
			
			
		}
		
		
		
		logicaSemanalProducto();
		
	}
	
	/**
	 * Description: Este método se encarga de ejecutar toda la lógica para realizar reservas de ticket por primera vez,
	 * se compone de 3 puntos principales:
	 * <ol>
	 * <li>Distribuir las películas en cartelera de cada sucursal de forma equitativa respecto a sus salas de cine.</li>
	 * <li>Una vez realizada la distribución, crear los horarios en los que se presentará cada película.</li>
	 * <li>Actualizar las películas cuyo horario se esta presentando en estos momentos.</li>
	 * <li>Establecer las fechas cuando se ejecutarán la lógica diaria y semanal del negocio.</li>
	 * </ol>
	 * */
	public static void logicaInicioSistemaReservarTicket() {
		
		fechaActual = LocalDateTime.now();
		for (SucursalCine sucursal: sucursalesCine) {
			
			sucursal.distribuirPeliculasPorSala();
			sucursal.crearHorariosPeliculasPorSala();
		}
		actualizarPeliculasSalasDeCine();
		fechaValidacionNuevoDiaDeTrabajo = fechaActual.toLocalDate().plusDays(1);
		fechaRevisionLogicaDeNegocio = fechaActual.toLocalDate().plusWeeks(1);
	}
	
	/**
	 * Description : Este método se encarga de evaluar la lógica diaria de la reserva de tickets, para esto evalua los siguientes criterios:
	 * <ol>
	 * <li>Añade los tickets de películas que serán presentadas el día de hoy al array de tickets para descuento y elimina los tickets
	 * caducados de los clientes y del array de tickets disponibles.</li>
	 * <li>Elimina los horarios de películas que ya no serán presentados.</li>
	 * </ol>
	 * */
	public static void logicaDiariaReservarTicket() {
		
		ArrayList<Ticket> ticketsAEliminar = new ArrayList<Ticket>();
		
		for (SucursalCine sede : sucursalesCine) {
			//Añadimos los tickets que podrán recibir descuentos a su array de tickets para descuento de su respectiva sucursal
			sede.ticketsParaDescuento.clear();
			for (Ticket ticket : ticketsDisponibles) {
				if (ticket.getSucursalCompra().equals(sede) &&
					ticket.getHorario().toLocalDate().isEqual(fechaActual.toLocalDate())) {
					sede.ticketsParaDescuento.add(ticket);
					
				}
				
				if (ticket.getHorario().plus(ticket.getPelicula().getDuracion()).isBefore(fechaActual)) {
					ticketsAEliminar.add(ticket);
				}
			}
		}
		
		//Eliminamos los horarios caducados
		dropHorariosVencidos();
		
		//Eliminamos los tickets caducados
		for (Ticket ticket : ticketsAEliminar) {
			SucursalCine.ticketsDisponibles.remove(ticket);
		}
		
		//Eliminamos los tickets caducados de los clientes
		for (Cliente cliente : clientes) {
			cliente.dropTicketsCaducados();
		}
		
	}
	
	/**
	 * Description : Este método se encarga de retornar la sucursal cuyo id coincida con el pasado como parámetro, 
	 * del array de sucursales cine.
	 * @param idSucursalCine : Este método recibe como parámetro el id de la sucursal (De tipo int).
	 * @return SucursalCine : Este método retorna la sucursal (De tipo SucursalCine) cuyo id coincide con el seleccionada, 
	 * con el fin de realizar las validaciones.
	 * */
	public static SucursalCine obtenerSucursalPorId(int idSucursalCine) {
		
		for (SucursalCine sede : sucursalesCine) {
			if (sede.idSucursal == idSucursalCine) {
				return sede;
			}
		}
		
		return null;
	}
	
	/**
	 * Description : Este método se encarga de retornar la película cuyo id coincida con el pasado como parámetro, 
	 * del array de peliculas en cartelera de la sucursal.
	 * @param idPeliculaCartelera : Este método recibe como parámetro el id de la película a buscar (De tipo int).
	 * @return Pelicula : Este método retorna la pelicula (De tipo Pelicula) cuyo id coincide con el seleccionada, 
	 * con el fin de realizar las validaciones.
	 * */
	public Pelicula obtenerPeliculaPorId(int idPeliculaCartelera) {
		
		for (Pelicula pelicula : this.cartelera) {
			if (pelicula.getIdPelicula() == idPeliculaCartelera) {
				return pelicula;
			}
		}
		
		return null;
	}
	
	/**
	 * Description : Este método se encarga de retornar la sala de cine cuyo id coincida con el pasado como parámetro, 
	 * del array de salas de cine de la sucursal.
	 * @param idSalaCineSucursal : Este método recibe como parámetro el id de la sala de cine a buscar (De tipo int).
	 * @return SalaCine : Este método retorna la sala de cine (De tipo SalaCine) cuyo id coincide con el seleccionada, 
	 * con el fin de realizar las validaciones.
	 * */
	public SalaCine obtenerSalaCinePorId(int idSalaCineSucursal) {
		
		for (SalaCine salaDeCine : this.salasDeCine) {
			if (salaDeCine.getIdSalaCine() == idSalaCineSucursal) {
				return salaDeCine;
			}
		}
		
		return null;
	}
	

	
	/**public void cambiarPeliculaSede(Pelicula pelicula){
		
	/** Description: Este metodo se encarga de seleccionar las sucursales del arrayList y con el uso de la funcion random de la libreria math,
	 * se selecciona una sucursal aleatoriamente, ya que esto nos permetira mas adelante el cambio de sucursal de una
	 * pelicula a otra
	 * */	 
		 
	private static SucursalCine seleccionarSucursalAleatoriamente(SucursalCine sucursalCine) {
		while(true) {
			int numeroAleatorio= (int)(Math.random()*10)%(sucursalesCine.size());
			SucursalCine sucursalSeleccionada=sucursalesCine.get(numeroAleatorio);
			if(sucursalCine.equals(sucursalSeleccionada)) {
				continue;
			}
			
			return sucursalSeleccionada;
		}
	    
	
	}
	
	
	
	
	
	   
	/** Description: Este metodo se encarga de remover las peliculas que fueron mal calificadas en dos sucursales, por lo
	 * tanto por temas de negocio decidimos eliminar esta pelicula por malas ventas, usando la funcion remove, quitandola
	 * de la cartelera principal de peliculas.
	 *
	 * */
	
	private void eliminarPeliculas(ArrayList<Pelicula> PeliculasEliminar) {
		
	   for(Pelicula pelicula:PeliculasEliminar) {
		   this.cartelera.remove(pelicula);
	   } 
	   
	   
	 
	}
	
	
	/** Description: Este metodo se encarga de analizar por semana que peliculas han sido bien o mal calificadas, evaluando
	 * las calificaciones de los clientes, si una pelicula es calificada por debajo de 3, la consideramos como mal calificada
	 * y la cambiamos de sede, y si la pelicula esta por encima de 3 esta catalogada como bien, ya en el caso en que la 
	 * pelicula este calificada como mayor a 4.5, la cambiamos de sede, ya que consideramos que es una muy buena pelicula, y 
	 * nos hara ganar mayor rentabilidad.Tambien se encarga de cambiar peliculas de sede, ya que en nuestra logica de negocio implementamos
	 * el sistema de calificaciones, entonces tenemos que estar constantemente pendientes de que peliculas han sido
	 * buenas o malas recibidas por los clientes, y cambiandolas de sede, esperamos que su calificacion mejore, si esto
	 * no se da, la pelicula es eliminada de la cartelera, ya que se considera como mala
	 * */
	
		
	private void logicaCalificacionPeliculas(Pelicula pelicula){	
		
		ArrayList <Pelicula> peliculasCalificadas = Pelicula.filtrarPorNombreDePelicula(pelicula.getNombre(), this.cartelera);
		double promedio =0;
		double calificacionReal=0;
		boolean verificacionCambio=true;
		for(Pelicula peliculas : peliculasCalificadas) {
			promedio = promedio + peliculas.getValoracion();
			verificacionCambio=peliculas.isStrikeCambio();
		}
		
		calificacionReal = promedio/peliculasCalificadas.size();
		
		if (calificacionReal<3) {
			if(verificacionCambio) {
				SucursalCine sucursal=seleccionarSucursalAleatoriamente(this);
				for (Pelicula pelicula1:peliculasCalificadas) {
					this.getCartelera().remove(pelicula1);
					if (pelicula1.getTipoDeFormato().equals("2D")){
						new Pelicula(pelicula1.getNombre(),(int)(pelicula1.getPrecio()*0.9),pelicula1.getGenero(),pelicula1.getDuracion(),pelicula1.getClasificacion(),pelicula1.getTipoDeFormato(),sucursal);
					}
				}
				
			}
			else {
				eliminarPeliculas(peliculasCalificadas);
			}			
		}
		else if (calificacionReal>4.5) {
			SucursalCine sucursal1=seleccionarSucursalAleatoriamente(this);
			for (Pelicula pelicula2:peliculasCalificadas) {
				if (pelicula2.getTipoDeFormato().equals("2D")){
					new Pelicula(pelicula2.getNombre(),(int)(pelicula2.getPrecio()*1.10),pelicula2.getGenero(),pelicula2.getDuracion(),pelicula2.getClasificacion(),pelicula2.getTipoDeFormato(),sucursal1);
					
				}
				
			}
			
						
		}
	}
	
	/**
	 * Description : Este método se encarga de revisar la validez de la membresia del cliente y,
	 * en caso de que este apunto de expirar, se le notificará con antelación (5 dias) para que pueda
	 * renovar su membresia. En caso de que se expire, se notifica y se desvincula del cliente.
	 * @param cliente : Se usa el cliente para obtener los datos de las membresias
	 * @return String : Se retorna el mensaje de advertencia en caso de que la membresia esta apunto de expirar o ya expiró.
	 */
	public static String notificarFechaLimiteMembresia(Cliente cliente) {
		
		String mensaje = "";
		//Se obtiene el objeto MetodoPago Puntos con apuntador puntos.
		if (cliente.getMembresia()!= null) {
			MetodoPago puntos = null;
			for (MetodoPago metodoPago : cliente.getMetodosDePago()) {
				if (metodoPago.getNombre().equals("Puntos")) {
					puntos = metodoPago;
					break;
				}
			}
			//Se verifica si la fecha actual esta pasada a la fecha limite de la membresia.
			if (!fechaActual.toLocalDate().isBefore(cliente.getFechaLimiteMembresia())) {
				//Se guardan la cantidad de puntos en el atributo de Cliente para no perder la acumulación.
				cliente.setPuntos(cliente.getPuntos()+(int)puntos.getLimiteMaximoPago());
				//Se obtiene el nombre de la membresia y se desvincula del cliente.
				String nombreMembresia = cliente.getMembresia().getNombre();
				cliente.getMembresia().getClientes().remove(cliente);
				cliente.setMembresia(null);
				//Se reinician sus métodos de pago en caso de perder la membresia.
				MetodoPago.asignarMetodosDePago(cliente);
				mensaje = "Su membresia ha expirado. Le invitamos a renovarla para no perder sus beneficios.";
				
				//Para volver a asignar la membresia expirada al stock de inventario, se valida con el nombre.
				for (SucursalCine sucursal : SucursalCine.getSucursalesCine()) {
					if (sucursal.getIdSucursal() == cliente.getOrigenMembresia()) {
						for (Producto productoMembresia : sucursal.getInventarioCine()) {
							if (productoMembresia.getNombre().equals(nombreMembresia)) {
								productoMembresia.setCantidad(productoMembresia.getCantidad()+1);
								break;
							}
						} break;
					}
				}
			//En caso de que falten 5 días o menos para que la membresía expire, se actualiza el mensaje con una advertencia.	
			} else if (fechaActual.toLocalDate().isAfter(cliente.getFechaLimiteMembresia().minusDays(6))
					&& fechaActual.toLocalDate().isBefore(cliente.getFechaLimiteMembresia())) {
				mensaje = "Estimado cliente, recuerde que le quedan " + 
					ChronoUnit.DAYS.between(fechaActual.toLocalDate(), cliente.getFechaLimiteMembresia()) + 
					" dia(s) para que caduzca su membresía.\nLo invitamos a actualizar su suscripción para poder disfrutar de sus beneficios.";
				
			}
		}
		return mensaje;
	}
	/** Description: Este metodo se encarga de revisar en el arrayList de peliculasDisponibles que pelicula ha tenido
	 * la peor calificacion, osea, la pelicula mas deficiente segun los gustos de los clientes, con esta pelicula vamos 
	 * a generar combos en recompensa a los clientes que nos dejaron sus reseñas
	 * */
	public Pelicula peorPelicula() {
		Pelicula peliculaPeorCalificada=null;
		boolean primeraComparacion=true;
		
		for(Pelicula peliculas : this.cartelera) {
			if(peliculas.seleccionarHorarioMasLejano()==null) {
				continue;
			}
			if(primeraComparacion) {
				peliculaPeorCalificada=peliculas;
				
			}
			if(peliculas.getValoracion()<peliculaPeorCalificada.getValoracion()) {
				peliculaPeorCalificada=peliculas;
			}
			
		}
		return peliculaPeorCalificada;

	}
	/** Description: Este metodo se encarga de revisar en el arrayList de peliculasDisponibles que pelicula ha tenido
	 * la mejor calificacion, osea, la pelicula mas eficiente segun los gustos de los clientes, con esta pelicula vamos 
	 * a generar combos en recompensa a los clientes que nos dejaron sus reseñas
	 * */
	public Pelicula mejorPelicula() {
		Pelicula peliculaMejorCalificada=null;
		boolean primeraComparacion=true;
		for(Pelicula peliculas : this.cartelera) {
			if(peliculas.seleccionarHorarioMasLejano()==null) {
				continue;
			}
			if(primeraComparacion) {
				peliculaMejorCalificada=peliculas;
				
			}
			if(peliculas.getValoracion()>peliculaMejorCalificada.getValoracion()) {
				peliculaMejorCalificada=peliculas;
			}
			
		}
		return peliculaMejorCalificada;

	}
	/** Description: Este metodo se encarga de revisar en el arrayList de inventario que producto ha tenido
	 * la peor calificacion, osea, el producto mas deficiente segun los gustos de los clientes, con este producto vamos 
	 * a generar combos en recompensa a los clientes que nos dejaron sus reseñas
	 * */
	
	public Producto peorProducto() {
		Producto productoPeorCalificado=null;
		boolean primeraComparacion=true;
		for(Producto producto : inventarioCine) {
			if(producto.getTipoProducto().equalsIgnoreCase("comida")||producto.getTipoProducto().equalsIgnoreCase("souvenir")) {
				if(primeraComparacion) {
					productoPeorCalificado=producto;
					
				}
				if(producto.getValoracionComida()<productoPeorCalificado.getValoracionComida()) {
					productoPeorCalificado=producto;
				}
			}
			
		}
		return productoPeorCalificado;

	}
	/** Description: Este metodo se encarga de revisar en el arrayList de inventario que producto ha tenido
	 * la mejor calificacion, osea, el producto mas eficiente segun los gustos de los clientes, con este producto vamos 
	 * a generar combos en recompensa a los clientes que nos dejaron sus reseñas
	 * */
	public Producto mejorProducto() {
		Producto productoMejorCalificado=null;
		boolean primeraComparacion=true;
		for(Producto producto : inventarioCine) {
			if(producto.getTipoProducto().equalsIgnoreCase("comida")||producto.getTipoProducto().equalsIgnoreCase("souvenir")) {
				if(primeraComparacion) {
					productoMejorCalificado=producto;
					
				}
				if(producto.getValoracionComida()>productoMejorCalificado.getValoracionComida()) {
					productoMejorCalificado=producto;
				}
		    }
		}
		return productoMejorCalificado;

	}
	/**
	 * Description: Este método se encarga de realizar la distribución de productos en los inventarios de los productos
	 * ccada semana
	 *  luego de haber efectuado el cambio de producto de sucursal propio de la funcionalidad 3. 
	 * */
	private static void logicaSemanalProducto() {
		for (SucursalCine sede : sucursalesCine) {
			for(Producto producto:sede.getInventarioCine()) {
				if(producto.getTipoProducto().equals("comida")){
					
					sede.logicaCalificacionProductos(producto);
					
				}
				else if(producto.getTipoProducto().equals("souvenir")){
					
					sede.logicaCalificacionProductos(producto);
					
				}
			}
		}
	}	
	/** Description: Este metodo se encarga de analizar por semana que productos han sido bien o mal calificadas, evaluando
	 * las calificaciones de los clientes, si un producto es calificado por debajo de 3, lo consideramos como mal calificado
	 * y lo cambiamos de sede, y si la valoracion del producto esta por encima de 3 esta catalogada como bien, ya en el caso en que el 
	 * bono este calificado como mayor a 4.5, lo cambiamos de sede, ya que consideramos que es un muy buen producto, y 
	 * nos hara ganar mayor rentabilidad.Tambien se encarga de cambiar productos de sede, ya que en nuestra logica de negocio implementamos
	 * el sistema de calificaciones, entonces tenemos que estar constantemente pendientes de que productos han sido
	 * bien o mal recibidos por los clientes, y cambiandolos de sede, esperamos que su calificacion mejore, si esto
	 * no se da, el producto es eliminado del inventario, ya que se considera como malo
	 * */
	private  void logicaCalificacionProductos(Producto producto){	
		
		ArrayList <Producto> productosCalificados = filtrarPorNombreDeProducto(producto.getNombre(), this.inventarioCine);
		
		
		boolean verificacionCambio=true;
		
			
			
		
		if (producto.getValoracionComida()<3) {
			if(verificacionCambio) {
				SucursalCine sucursal=seleccionarSucursalAleatoriamente(producto.getSucursalSede());
				for (Producto productos1:productosCalificados) {
					this.inventarioCine.remove(productos1);
					if (productos1.getTipoProducto().equals("comida")){
						new Producto(productos1.getNombre(),productos1.getTamaño(),productos1.getTipoProducto(),(productos1.getPrecio()*0.9),productos1.getCantidad(),productos1.getGenero(),sucursal);
						if (productos1.getTipoProducto().equals("souvenir")){
							new Producto(productos1.getNombre(),productos1.getTamaño(),productos1.getTipoProducto(),(productos1.getPrecio()*0.9),productos1.getCantidad(),productos1.getGenero(),sucursal);
					}
				   }
				}
			}
			else {
				eliminarProducto(productosCalificados);
			}			
		}
		else if (producto.getValoracionComida()>4.5) {
			SucursalCine sucursal1=seleccionarSucursalAleatoriamente(producto.getSucursalSede());
			for (Producto productos2:productosCalificados) {
				if (productos2.getTipoProducto().equals("comida")){
					new Producto(productos2.getNombre(),productos2.getTamaño(),productos2.getTipoProducto(),(productos2.getPrecio()*1.10),productos2.getCantidad(),productos2.getGenero(),sucursal1);
					if (productos2.getTipoProducto().equals("souvenir")){
						new Producto(productos2.getNombre(),productos2.getTamaño(),productos2.getTipoProducto(),(productos2.getPrecio()*1.10),productos2.getCantidad(),productos2.getGenero(),sucursal1);
						
					}
				}
				
			}
			
						
		}
	
		
	}
	/** Description: Este metodo se encarga de remover los productos que fueron mal calificadas en dos sucursales, por lo
	 * tanto por temas de negocio decidimos eliminar este producto por malas ventas, usando la funcion remove, quitandola
	 * de la cartelera principal de peliculas.
	 *
	 * */
	private void eliminarProducto(ArrayList<Producto> productosEliminar) {
		for(Producto producto:productosEliminar) {
			this.inventarioCine.remove(producto);
		}
	}

 
	/**
	 * Description : Este método se encarga de retornar los productos cuyo nombre coincide con el nombre del producto seleccionada por el cliente.
	 * @param nombreProducto : Este método recibe como parámetro el nombre del producto (De tipo String) con el cuál se realizará el filtrado.
	 * @param Inventario : Este método recibe como parámetro una lista (De tipo ArrayList<Producto>) que contiene 
	 * los productos previamente filtrados según los datos del cliente y su disponibilidad horaria.
	 * @return <b>ArrayList<Producto></b> : Este método retorna un ArrayList de los productos cuyo nombre coinciden con el nombre seleccionado 
	 * por el cliente.
	 * */	 
	private static ArrayList<Producto> filtrarPorNombreDeProducto(String nombreProducto, ArrayList<Producto> Inventario){
		ArrayList<Producto> productosEncontrados = new ArrayList<>();
		
		for (Producto producto : Inventario) {
			if (producto.getNombre().equals(nombreProducto)) {
				productosEncontrados.add(producto);
			}
		}
		
		return productosEncontrados;
	}
	//Constructor
	public SucursalCine() {
		sucursalesCine.add(this);
		cantidadSucursales++;
		this.idSucursal = cantidadSucursales;
		
	}
	
	public SucursalCine(String lugar) {
		this();
		this.cantidadTicketsCreados = 1;
		this.lugar = lugar;
	}
	
	//Getters and Setters
	public static LocalDateTime getFechaActual() {
		return fechaActual;
	}
	
	public static void setFechaActual(LocalDateTime fechaActual) {
		SucursalCine.fechaActual = fechaActual;
	}
	
	public ArrayList<Pelicula> getCartelera() {
		return cartelera;
	}
	
	public void setCartelera(ArrayList<Pelicula> cartelera) {
		this.cartelera = cartelera;
	}
	
	public String getLugar() {
		return lugar;
	}
	
	public void setLugar(String lugar) {
		this.lugar = lugar;
	}
	
	public ArrayList<SalaCine> getSalasDeCine() {
		return salasDeCine;
	}
	
	public void setSalasDeCine(ArrayList<SalaCine> salasDeCine) {
		this.salasDeCine = salasDeCine;
	}
	
	public static ArrayList<SucursalCine> getSucursalesCine() {
		return sucursalesCine;
	}
	
	public static void setSucursalesCine(ArrayList<SucursalCine> sucursalesCine) {
		SucursalCine.sucursalesCine = sucursalesCine;
	}
	
	public ArrayList<Bono> getBonosCreados() {
		return bonosCreados;
	}
	
	public void setBonosCreados(ArrayList<Bono> bonosCreados) {
		this.bonosCreados = bonosCreados;
	}

	public ArrayList<Producto> getInventarioCine() {
		return inventarioCine;
	}

	public void setInventarioCine(ArrayList<Producto> inventarioCine) {
		this.inventarioCine = inventarioCine;
	}

	public ArrayList<Servicio> getServicios() {
		return servicios;
	}

	public void setServicios(ArrayList<Servicio> servicios) {
		this.servicios = servicios;
	}

	public ArrayList<TarjetaCinemar> getInventarioTarjetasCinemar() {
		return InventarioTarjetasCinemar;
	}

	public void setInventarioTarjetasCinemar(ArrayList<TarjetaCinemar> inventarioTarjetasCinemar) {
		InventarioTarjetasCinemar = inventarioTarjetasCinemar;
	}

	public static ArrayList<Cliente> getClientes() {
		return clientes;
	}

	public static void setClientes(ArrayList<Cliente> clientes) {
		SucursalCine.clientes = clientes;
	}

	public static ArrayList<Arkade> getJuegos() {
		return juegos;
	}

	public static void setJuegos(ArrayList<Arkade> juegos) {
		SucursalCine.juegos = juegos;
	}

	public static ArrayList<MetodoPago> getMetodosDePagoDisponibles() {
		return metodosDePagoDisponibles;
	}

	public static void setMetodosDePagoDisponibles(ArrayList<MetodoPago> metodosDePagoDisponibles) {
		SucursalCine.metodosDePagoDisponibles = metodosDePagoDisponibles;
	}

	public static LocalDate getFechaValidacionNuevoDiaDeTrabajo() {
		return fechaValidacionNuevoDiaDeTrabajo;
	}

	public static void setFechaValidacionNuevoDiaDeTrabajo(LocalDate fechaValidacionNuevoDiaDeTrabajo) {
		SucursalCine.fechaValidacionNuevoDiaDeTrabajo = fechaValidacionNuevoDiaDeTrabajo;
	}

	public static LocalDate getFechaRevisionLogicaDeNegocio() {
		return fechaRevisionLogicaDeNegocio;
	}

	public static void setFechaRevisionLogicaDeNegocio(LocalDate fechaRevisionLogicaDeNegocio) {
		SucursalCine.fechaRevisionLogicaDeNegocio = fechaRevisionLogicaDeNegocio;
	}

	public int getCantidadTicketsCreados() {
		return cantidadTicketsCreados;
	}

	public void setCantidadTicketsCreados(int cantidadTicketsCreados) {
		this.cantidadTicketsCreados = cantidadTicketsCreados;
	}

	public static ArrayList<Ticket> getTicketsDisponibles() {
		return ticketsDisponibles;
	}

	public static void setTicketsDisponibles(ArrayList<Ticket> ticketsDisponibles) {
		SucursalCine.ticketsDisponibles = ticketsDisponibles;
	}

	public int getIdSucursal() {
		return idSucursal;
	}

	public void setIdSucursal(int idSucursal) {
		this.idSucursal = idSucursal;
	}

	public static LocalTime getFinHorarioLaboral() {
		return FIN_HORARIO_LABORAL;
	}

	public static LocalTime getInicioHorarioLaboral() {
		return INICIO_HORARIO_LABORAL;
	}

	public static ArrayList<Membresia> getTiposDeMembresia() {
		return tiposDeMembresia;
	}

	public static void setTiposDeMembresia(ArrayList<Membresia> tiposDeMembresia) {
		SucursalCine.tiposDeMembresia = tiposDeMembresia;
	}

	public ArrayList<Ticket> getTicketsParaDescuento() {
		return ticketsParaDescuento;
	}

	public void setTicketsParaDescuento(ArrayList<Ticket> ticketsParaDescuento) {
		this.ticketsParaDescuento = ticketsParaDescuento;
	}
	
	/*ToDo list
	0. Optimizar código en serializador y deserializador (Hecho).
	1. Realizar testeos.
	2. Crear método que avance el día cuando estemos fuera de la jornada laboral.
	6. Estudiar y crear el ejecutable.
	7. Realizar testeos grupales.
	8. Empezar documentación.
	
	

	*/
}
