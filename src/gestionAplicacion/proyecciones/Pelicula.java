package gestionAplicacion.proyecciones;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.Duration;
import gestionAplicacion.SucursalCine;
import gestionAplicacion.usuario.Cliente;
import java.io.Serializable;

/**
 * @author Julián Bedoya Palacio
 * */
public class Pelicula implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int idPelicula;
	private static int cantidadPeliculasGeneradas;
	private String nombre;
	private int precio;
	private String genero;
	private Duration duracion;
	private String clasificacion;
	private ArrayList<LocalDateTime> horarios = new ArrayList<>();
	private ArrayList<int[][]> asientosVirtuales = new ArrayList<>();
	private String tipoDeFormato;
	private SalaCine salaPresentacion;
	private int numeroSalaPresentacion;
	private double valoracion;
	private int totalEncuestasDeValoracionRealizadas;
	private SucursalCine sucursalCartelera;
	private boolean strikeCambio;
	
	// Constructor
	public Pelicula(){
		cantidadPeliculasGeneradas++;
		this.idPelicula = cantidadPeliculasGeneradas;
		this.valoracion = 4.0;
		this.totalEncuestasDeValoracionRealizadas = 25;
		this.strikeCambio = true;
	}

	public Pelicula(String nombre, int precio, String genero, Duration duracion, String clasificacion,
			String tipoDeFormato, SucursalCine sucursalCine) {
		this();
		this.nombre = nombre;
		this.precio = precio;
		this.genero = genero;
		this.duracion = duracion;
		this.clasificacion = clasificacion;
		this.tipoDeFormato = tipoDeFormato;
		
		sucursalCine.getCartelera().add(this);
		this.crearPelicula(sucursalCine);
		this.sucursalCartelera = sucursalCine;
		
	}
	
	public Pelicula(String nombre, int precio, String genero, Duration duracion, String clasificacion,
			String tipoDeFormato) {
		this();
		this.nombre = nombre;
		this.precio = precio;
		this.genero = genero;
		this.duracion = duracion;
		this.clasificacion = clasificacion;
		this.tipoDeFormato = tipoDeFormato;

	}

	//Methods
	/**
	 * Description : Este método se encarga de crear una matriz que representa la sala virtual posteriormente esta se añade al array 
	 * de asientos virtuales de la película y se añade el horario al array de horarios.
	 * @param fecha : Este método recibe una fecha (De tipo LocalDateTime) para crear la salaDeCineVirtual.
	 * */
	public void crearSalaVirtual(LocalDateTime fecha) {
		int[][] nuevaSalaVirtual = new int[8][8];
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				nuevaSalaVirtual[i][j] = 0;
			}
		}
		
		this.horarios.add(fecha);
		this.asientosVirtuales.add(nuevaSalaVirtual);
		
	}
	
	/**
	 * Description : Este método se encarga de filtar las películas en cartelera con los siguientes criterios:
	 * <ol>  
	 * <li>Su categoría es menor o igual a la edad del cliente.</li>
	 * <li>La película tiene al menos 1 horario en el cuál será presentada o se encuentra en presentación y no supera el
	 * límite de tiempo para comprar un ticket de una película que se encuentra en presentación (15 minutos).</li>  
	 * </ol>
	 * Todo esto con el fin de mostrar en pantalla, posteriormente, el array de las películas que cumplan estos criterios. 
	 * @param clienteProceso : Este método recibe como parámetro un cliente (De tipo cliente), que realizará el proceso de reserva de ticket.
	 * @param sucursalCine : Este método recibe como parámetro la sede (De tipo SucursalCine), para acceder a la cartelera de esta misma.
	 * @return <b>ArrayList(String) </b> : Retorna una lista con las peliculas filtradas por el criterio anterior.
	 * */
	public static ArrayList<Pelicula> filtrarCarteleraPorCliente(Cliente clienteProceso, SucursalCine sucursalCine){
		ArrayList<Pelicula> carteleraPersonalizada = new ArrayList<Pelicula>();
		
		for (Pelicula pelicula : sucursalCine.getCartelera()) {
			if (pelicula.filtrarHorariosPelicula().size() > 0 || pelicula.IsPeliculaEnPresentacion(sucursalCine) ) {
				if ((Integer.parseInt(pelicula.getClasificacion())) <= clienteProceso.getEdad()) {
					carteleraPersonalizada.add(pelicula);
				}
			}
		}
		
		return carteleraPersonalizada;
		
	}
	
	/**
	 * Description : Este método genera una lista filtrada según el nombre de las películas disponibles sin repetición.
	 * @param filtroPeliculasPorCliente : Este método recibe como parámetro las peliculas ( De tipo ArrayList(Pelicula) ) 
	 * resultantes de realizar el filtro por cliente (Edad y disponibilidad horaria).
	 * @return <b>ArrayList(String)</b> : Retorna una lista de nombres de las películas distintos entre sí.
	 * */
	public static ArrayList<String> filtrarNombrePeliculas(ArrayList<Pelicula> filtroPeliculasPorCliente){
		ArrayList<String> filtroNombrePeliculas = new ArrayList<>();
		
		for (Pelicula pelicula : filtroPeliculasPorCliente){
			if (!filtroNombrePeliculas.contains(pelicula.getNombre())) {
				filtroNombrePeliculas.add(pelicula.getNombre());
			}
		}
		
		return filtroNombrePeliculas;
	}
	
	/**
	 * Description : Este método genera una lista filtrada según el nombre de las películas que coinciden con determinado género, sin repetición.
	 * @param filtroPeliculasPorCliente : Este método recibe como parámetro las peliculas ( De tipo ArrayList(Pelicula) ) resultantes de realizar 
	 * el filtro por cliente (Edad y disponibilidad horaria).
	 * @param genero : Este método recibe como parámetro el género (De tipo String) más visualizado por el cliente.
	 * @return <b>ArrayList(String)</b> : Retorna una lista de nombres de las películas distintos entre sí, cuyo género es igual.
	 * */
	public static ArrayList<String> filtrarPorGenero(ArrayList<Pelicula> filtroPeliculasPorCliente, String genero){
		ArrayList<String> filtroNombrePeliculas = new ArrayList<>();
		for (Pelicula pelicula : filtroPeliculasPorCliente){
			if (pelicula.getGenero().equals(genero)) {
				if (!filtroNombrePeliculas.contains(pelicula.getNombre())) {
					filtroNombrePeliculas.add(pelicula.getNombre());
				}
			}
		}
		return filtroNombrePeliculas;
	}
	

	 
	// Getters and Setters
	public ArrayList<LocalDateTime> getHorarios() {
		return horarios;
	}

	public void setHorarios(ArrayList<LocalDateTime> horarios) {
		this.horarios = horarios;
	}
	
	public ArrayList<int[][]> getAsientosVirtuales() {
		return asientosVirtuales;
	}

	public void setAsientosVirtuales(ArrayList<int[][]> asientos) {
		this.asientosVirtuales = asientos;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public Duration getDuracion() {
		return duracion;
	}

	public void setDuracion(Duration duracion) {
		this.duracion = duracion;
	}

	public String getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}

	public String getTipoDeFormato() {
		return tipoDeFormato;
	}

	public void setTipoDeFormato(String tipoDeFormato) {
		this.tipoDeFormato = tipoDeFormato;
	}

	public SalaCine getSalaPresentacion() {
		return salaPresentacion;
	}

	public void setSalaPresentacion(SalaCine salaPresentacion) {
		this.salaPresentacion = salaPresentacion;
	}

	public int getIdPelicula() {
		return idPelicula;
	}

	public void setIdPelicula(int idPelicula) {
		this.idPelicula = idPelicula;
	}
	
	public double getValoracion() {
		return valoracion;
	}

	public void setValoracion(double valoracion) {
		this.valoracion = valoracion;
	}

	public int getTotalEncuestasDeValoracionRealizadas() {
		return totalEncuestasDeValoracionRealizadas;
	}

	public void setTotalEncuestasDeValoracionRealizadas(int totalEncuestasDeValoracionRealizadas) {
		this.totalEncuestasDeValoracionRealizadas = totalEncuestasDeValoracionRealizadas;
	}

	public SucursalCine getSucursalCartelera() {
		return sucursalCartelera;
	}

	public void setSucursalCartelera(SucursalCine sucursalCartelera) {
		this.sucursalCartelera = sucursalCartelera;
	}

	public boolean isStrikeCambio() {
		return strikeCambio;
	}

	public void setStrikeCambio(boolean strikeCambio) {
		this.strikeCambio = strikeCambio;
	}

	public int getNumeroSalaPresentacion() {
		return numeroSalaPresentacion;
	}

	public void setNumeroSalaPresentacion(int numeroSalaPresentacion) {
		this.numeroSalaPresentacion = numeroSalaPresentacion;
	}

}
