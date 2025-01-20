package gestionAplicacion.usuario;
import java.util.ArrayList;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Iterator;

import gestionAplicacion.SucursalCine;
import gestionAplicacion.proyecciones.Pelicula;
import gestionAplicacion.servicios.Bono;
import gestionAplicacion.servicios.Producto;
//import gestionAplicacion.servicios.Servicio;

/**
* @author Todos los integrantes del equipo participaron en la construcción de esta clase
 * */
public class Cliente implements Serializable{

  private static final long serialVersionUID = 1L;
  
  //Atributos
  private String nombre;
  private int edad;
  private long documento;
  private TipoDeDocumento tipoDocumento;
  private SucursalCine cineActual;

  //Atributos para funcionalidad 1 
  private ArrayList<Ticket> tickets = new ArrayList<>();
  private ArrayList<Pelicula> historialDePeliculas = new ArrayList<>();

  //Atributos para funcionalidad 3
  private ArrayList<Pelicula> peliculasDisponiblesParaCalificar = new arrayList<>();
  private ArrayList<Producto> productosDisponiblesParaCalificar = new arrayList<>();

  //Atributos para funcionalidad 4 y 2 
  private TarjetaCinemar cuenta;
  private ArrayList<String> codigosDescuento = new ArrayList<>();
  private ArrayList<String> codigosBonos = new ArrayList<>();
  private ArrayList<Bono> bonos = new ArrayList<>();
  private ArrayList<Producto> historialDePedidos = new ArrayList<>();

  //Atributos para funcionalidad 5 
  private Membresia membresia;
  private LocalDate fechaLimiteMembresia;
  private int puntos;
  private int origenMembresia;
  private ArrayList<MetodoPago> metodosDePago = new ArrayList<>();

  //Constructores 
}
