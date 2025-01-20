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
