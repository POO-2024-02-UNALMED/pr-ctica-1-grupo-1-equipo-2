package gestionAplicacion.usuario;

import java.io.Serializable;
import java.time.LocalDateTime;

import gestionAplicacion.SucursalCine;
import gestionAplicacion.proyecciones.Pelicula;
import gestionAplicacion.proyecciones.SalaCine;

/**
 * @author Julián Bedoya Palacio 
 * */
public class Ticket implements IBuyable, Serializable{
	
	private static final long serialVersionUID = 1L;
	private static int cantidadTicketsCreados;
	
	private int idTicket;
	private Cliente dueno;
	private SalaCine salaDeCine;
	private Pelicula pelicula;
	private LocalDateTime horario;
	private String numeroAsiento;
	private double precio;
	private SucursalCine sucursalCompra;
	private boolean descuento;

	//Constructors
	public Ticket(Pelicula pelicula, LocalDateTime horario, String numeroAsiento, SucursalCine sucursalDondeFueComprado) {
		this.descuento = true;
		this.pelicula = pelicula;
		this.numeroAsiento = numeroAsiento;
		this.horario = horario;
		this.sucursalCompra = sucursalDondeFueComprado;
		this.precio = this.clienteSuertudo();
		this.salaDeCine = pelicula.getSalaPresentacion();
	}
	
	
	//Getters and Setters
	public Cliente getDueno() {
		return dueno;
	}

	public void setDueno(Cliente dueno) {
		this.dueno = dueno;
	}

	public SalaCine getSalaDeCine() {
		return salaDeCine;
	}

	public void setSalaDeCine(SalaCine salaDeCine) {
		this.salaDeCine = salaDeCine;
	}

	public double getPrecio() {
		return precio;
	}
	
	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public Pelicula getPelicula() {
		return pelicula;
	}

	public void setPelicula(Pelicula pelicula) {
		this.pelicula = pelicula;
	}

	public LocalDateTime getHorario() {
		return horario;
	}

	public void setHorario(LocalDateTime horario) {
		this.horario = horario;
	}

	public int getIdTicket() {
		return idTicket;
	}

	public void setIdTicket(int idTicket) {
		this.idTicket = idTicket;
	}

	public String getNumeroAsiento() {
		return numeroAsiento;
	}

	public void setNumeroAsiento(String numeroAsiento) {
		this.numeroAsiento = numeroAsiento;
	}

	public boolean isDescuento() {
		return descuento;
	}

	public void setDescuento(boolean descuento) {
		this.descuento = descuento;
	}

	public SucursalCine getSucursalCompra() {
		return sucursalCompra;
	}

	public void setSucursalCompra(SucursalCine sucursalCompra) {
		this.sucursalCompra = sucursalCompra;
	}

	public static int getCantidadTicketsCreados() {
		return cantidadTicketsCreados;
	}

	public static void setCantidadTicketsCreados(int cantidadTicketsCreados) {
		Ticket.cantidadTicketsCreados = cantidadTicketsCreados;
	}
	
	
}
