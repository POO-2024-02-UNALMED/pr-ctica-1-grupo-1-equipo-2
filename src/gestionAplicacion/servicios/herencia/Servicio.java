package gestionAplicacion.servicios.herencia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.io.Serializable;
import gestionAplicacion.SucursalCine;
import gestionAplicacion.servicios.Bono;
import gestionAplicacion.servicios.Producto;
import gestionAplicacion.usuario.Cliente;
import gestionAplicacion.usuario.IBuyable;
import gestionAplicacion.usuario.MetodoPago;

/**
 * @author Jerónimo Rúa Herrera
 * */
public abstract class Servicio implements IBuyable, Serializable{

	private static final long serialVersionUID = 1L;
	
	protected String nombre;
	protected static Cliente cliente;
	protected ArrayList<Producto> inventario = new ArrayList<>();
	protected ArrayList<Producto> orden = new ArrayList<>();
	protected ArrayList<Bono> bonosCliente = new ArrayList<>();
	protected double valorPedido;
	
	
	public Servicio(){}
	
	public Servicio(String nombre) {
		this.nombre = nombre;
	}
	
	/**
	*Description: Muestra todos los productos disponibles que hay en el inventario 
	*@return <b>Productos</b> :  Muestra todos los productos disponibles, 
	*para generar la seleccion del producto deseado
	*/
	
	
	public String mostrarInventario() {
		String productos = "\n----------Productos disponibles----------\n\n0. Ningun producto";
		int r;
		if(0 == inventario.size()) {
			productos = "\nNO HAY PRODUCTOS DISPONIBLES :(\n";
		}
		for(int i=0;i<inventario.size();i++) {
			r = i + 1;
			if (inventario.get(i).getCantidad()==0) {
				productos = productos + "\n" + r +". "+ inventario.get(i).getNombre() + " " + inventario.get(i).getTamaño() + " $" + inventario.get(i).getPrecio() + " --> NO HAY EN EL MOMENTO DE ESTE PRODUCTO";
			}
			else {
				productos = productos + "\n" + r +". "+ inventario.get(i).getNombre() + " " + inventario.get(i).getTamaño() + " $" + inventario.get(i).getPrecio();
			}
		}
		return productos;
	}
	
	/**
	*Description: Genera un pedido segun los parametros del usuario y 
	*se descuenta la cantidad del producto que se genero
	*@param indice : Sirve para ubicar el producto en el inventario segun lo que el cliente escogio
	*@param cantidad :  Sirve para saver cuantos de esos productos se van a descontar del inventario y 
	*cuantos productos de estos se cobraran al momento de pagar
	*@return <b>producto</b> : Genera el producto para agregarlo al arrayList de Orden y poder validar su compra
	*/
	
	public Producto hacerPedido (int indice, int cantidad) {
		if (inventario.get(indice).getCantidad() >= cantidad) {
			inventario.get(indice).setCantidad(inventario.get(indice).getCantidad()-cantidad);
			Producto producto = new Producto(inventario.get(indice).getNombre(),inventario.get(indice).getTamaño(),cantidad);
			producto.setPrecio(inventario.get(indice).getPrecio()*cantidad);
			producto.setGenero(inventario.get(indice).getGenero());
			return producto;
		}
		else {
			return null;
		}
		
	}
	
	

	/** Description: Este metodo se encarga de seleccionar las sucursales del arrayList y con el uso de la funcion random de la libreria math,
	 * se selecciona una sucursal aleatoriamente, ya que esto nos permetira mas adelante el cambio de sucursal de un producto a otra
	 * 
	 * */	
	public  SucursalCine seleccionarSucursalAleatoriamente(SucursalCine sucursalCine) {
		while(true) {
			int numeroAleatorio= (int)(Math.random()*10)%(SucursalCine.getSucursalesCine().size());
			SucursalCine sucursalSeleccionada=SucursalCine.getSucursalesCine().get(numeroAleatorio);
			if(sucursalCine.equals(sucursalSeleccionada)) {
				continue;
			}
			
			return sucursalSeleccionada;
		}
	    
	
	}
	

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public ArrayList<Producto> getInventario() {
		return inventario;
	}
	
	public void setInventario(ArrayList<Producto> inventario) {
		this.inventario = inventario;
	}

	public ArrayList<Producto> getOrden() {
		return orden;
	}

	public void setOrden(ArrayList<Producto> orden) {
		this.orden = orden;
	}

	public double getValorPedido() {
		return valorPedido;
	}

	public void setValorPedido(double valorPedido) {
		this.valorPedido = valorPedido;
	}

	public ArrayList<Bono> getBonosCliente() {
		return bonosCliente;
	}

	public void setBonosCliente(ArrayList<Bono> bonosCliente) {
		this.bonosCliente = bonosCliente;
	}

	
	
}
