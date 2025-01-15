package gestionAplicacion.servicios;

import java.io.Serializable;
import java.util.Random;
import gestionAplicacion.SucursalCine;
import gestionAplicacion.servicios.herencia.Servicio;

/**
 * @author Valentina Leon Beltran
 * */
public class Producto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//Atributos
	private String tipoProducto;
	private String genero;
	private String nombre;
	private String tamaño;
	private double precio;
	private int cantidad;
	//Atributos Valoraciones Comida
	private double valoracionComida;
	private int totalEncuestasDeValoracionRealizadasComida;
	private boolean strikeCambio;
	private SucursalCine sucursalSede;
	public void descontarPrecioDeBono() {
		
	}
	
	/**
	*Description: Este metodo se encarga de generar un codigo aleatorio para los bonos creados.
	*@param longitud :  se pasa el como parametro la longitud que se desea el codigo
	*@return <b>Bono</b> :  Se retorna el bono creado
	*/
	public String generarCodigoAleatorio(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder codigo = new StringBuilder(longitud);

        for (int i = 0; i < longitud; i++) {
            int index = random.nextInt(caracteres.length());
            codigo.append(caracteres.charAt(index));
        }

        return codigo.toString();
    }
	

	//Constructores
	public Producto() {}
	
	//Constructor para los pedidos de la orden
	public Producto(String nombre,String tamaño,int cantidad) {
		this.nombre = nombre;
		this.tamaño = tamaño;
		this.cantidad = cantidad;
	}
	
	public Producto(String nombre,String tamaño,int cantidad, double precio, String tipo) {
		this.nombre = nombre;
		this.tamaño = tamaño;
		this.cantidad = cantidad;
		this.precio = precio;
		this.tipoProducto = tipo;
	}
	
	//Constructor para el inventario
	public Producto(String nombre,String tamaño,String tipoProducto,double precio,int cantidad,String genero,SucursalCine sucursalCine) {
		this.genero= genero;
		this.nombre = nombre;
		this.precio = precio;
		this.tamaño = tamaño;
		this.tipoProducto = tipoProducto;
		this.cantidad = cantidad;
		this.valoracionComida= 4.0;
		this.sucursalSede = sucursalCine;
		this.totalEncuestasDeValoracionRealizadasComida = 25;
		sucursalCine.getInventarioCine().add(this);
		this.strikeCambio = false;
	}
	
	//Constructor para objetos de Membresia y productos de bonos.
	public Producto(String tipoProducto, String nombre, double precio, int cantidad) {
		this.tipoProducto = tipoProducto;
		this.nombre = nombre;
		this.precio = precio;
		this.cantidad = cantidad;
		this.valoracionComida= 4.0;
		this.totalEncuestasDeValoracionRealizadasComida = 25;
	} 
	
	
	/**
	// Description: Este metodo se encarga de revisar que un producto tenga unidades disponibles en el inventario, 
	 * ya que con esto se hace una evaluacion a si unaproducto es apta para calificar o no
	* */
	public boolean verificarInventarioProducto(SucursalCine sucursalCine) {
		
		if (sucursalCine.getInventarioCine().size()<=sucursalCine.getTiposDeMembresia().size()) {
			return false;
		}
		else {
			return true;
		}	
	}
     
	//Ligadura Estatica
	
		/**
		*Description: Me verifica si tiene un producto del mismo tipo del bono para poder hacer 
		*su efectivo descuento
		*@param servicio : Recibe un parametro de tipo servicio para poder ver la orden que 
		*se esta haciendo en el momento
		*@return <b>verificacion</b> :Retorna un boolean para poder verificar la condicion 
		*planteada en el administrador
		*/
		
		public boolean comprobarBonoEnOrden(Servicio servicio) {
			for(int i = 0; i < servicio.getOrden().size();i++) {
				if(servicio.getOrden().get(i).getNombre().equals(nombre) && servicio.getOrden().get(i).getTamaño().equals(tamaño) && servicio.getOrden().get(i).getPrecio() >0) {
					return true;
				}
			}
			return false;
			
		}

	public String getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(String tipoProducto) {
		this.tipoProducto = tipoProducto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTamaño() {
		return tamaño;
	}

	public void setTamaño(String tamaño) {
		this.tamaño = tamaño;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
