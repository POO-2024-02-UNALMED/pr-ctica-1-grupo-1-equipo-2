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
 * @author Jeronimo Rua Herrera
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
	
	
	// Metodos abstractos y ligadura Dinamica
	
	public abstract boolean descuentarPorCompra (MetodoPago metodo);
	
	public abstract ArrayList<Producto> actualizarInventario();
	
	/**
	*Description: Me muestra los bonos que tengo disponible para decidir si reclamo uno de esos
	*@param servicio : Recibe un parametro de tipo servicio para poder ver los 
	*@return <b>bono</b> :Retorna un string con los tipos de bonos que tengo disponible por reclamar
	*/
	public static String  mostrarBonos(Servicio servicio) {
		int n = 0;
		String bono = "\n ====== Tienes los siguientes bonos disponibles ======\n"+
						"\n0. No reclamar ningun bono.";
		for(int i = 0;i < servicio.getBonosCliente().size();i++) {
				n = i + 1;
				bono = bono + "\n" + n + ". " + servicio.getBonosCliente().get(i).getProducto().getNombre() + " " + servicio.getBonosCliente().get(i).getProducto().getTamaño() + " codigo: " + servicio.getBonosCliente().get(i).getCodigo();
		}
		return bono;
	}
	
	/**
	*Description: Me filtra los bonos dependiendo del servicio al cual se esta accediendo, asi separando 
	*los distintos tipos de bonos y solo mostrando los requeridos para este servicio
	*/
	public void actualizarBonos() {
		bonosCliente = new ArrayList<>();
		for(int i = 0;i < cliente.getCineActual().getBonosCreados().size();i++) {
			if (cliente.getCineActual().getBonosCreados().get(i).getTipoServicio().equalsIgnoreCase(nombre) && cliente.getCineActual().getBonosCreados().get(i).getCliente().equals(cliente)) {
				bonosCliente.add(cliente.getCineActual().getBonosCreados().get(i));
			}
		}
	}
	
	/**
	*Description: Me verifica si tiene un producto del mismo genero que un ticke que hallas comprado y ademas
	*que fecha de la pelicula sea la misma del dia de la compra
	*@param cine : Recibe un parametro de tipo sucursalCine para poder ver los tickes creados y hacer 
	*la comparacion
	*@return <b>orden</b> :Retorna el primer producto que coincida con la condicion para poder generarle
	*su respectivo descuento
	*/
	
	public Producto descuentarPorGenero (SucursalCine cine) {
		for (int i = 0;i < orden.size();i++) {
			for(int j = 0; j < cine.getTicketsParaDescuento().size(); j++) {
				if(orden.get(i).getGenero().equalsIgnoreCase(cine.getTicketsParaDescuento().get(j).getPelicula().getGenero()) && cliente.equals(cine.getTicketsParaDescuento().get(j).getDueno())){
					LocalDate fecha = SucursalCine.getFechaActual().toLocalDate();
					if (fecha.isEqual(cine.getTicketsParaDescuento().get(j).getHorario().toLocalDate()) && cine.getTicketsParaDescuento().get(j).isDescuento()) {
						cine.getTicketsParaDescuento().get(j).setDescuento(false);
						return orden.get(i);
					}
				}
			}
		}
		return null;
	}
	

	
	/**
	*Description: Hace una suma de todos los precios que tiene la orden para poder efectuar su pago
	*@return <b>total</b> :Retorna el total que tiene que pagar el cliente despues de sumar todos los productos
	*/
	
	public double calcularTotal() {
		double total = 0;
		for(int i = 0; i < orden.size();i++) {
			total = total + orden.get(i).getPrecio();
		}
		return total;
	}
	
	/**
	*Description: Agrega el producto que recive por parametro si en la orden no hay uno igual, 
	*llegando el caso de que halla uno igual aumenta su cantidad y precio
	*@param producto : Recibe un parametro de tipo producto para poder compararlo si ya hay uno igual,
	* o en caso contrario agregarlo
	*/
	
	public void agregarOrden (Producto producto) {
		if(0 < orden.size()) {
			for (int i = 0; i < orden.size(); i++) {
				if ((producto.getNombre().equals(orden.get(i).getNombre())) && (producto.getTamaño().equals(orden.get(i).getTamaño()))) {
					orden.get(i).setCantidad(orden.get(i).getCantidad() + producto.getCantidad());
					orden.get(i).setPrecio(orden.get(i).getPrecio() + producto.getPrecio() );
					break;
				}
				else if ((i+1) == orden.size()) {
					orden.add(producto);
					break;
				}
			}
		}
		else {
			orden.add(producto);
		}
	}
	
	/**
	*Description: Me busca el producto en la orden, para asi disminuir su precio
	*@param producto : Recibe un parametro de tipo producto para poder compararlo
	* con los productos de la orden y asi modificar el producto adecuado
	*/
	
	public void descontarProducto (Producto producto) {
		for(int i=0; i< orden.size(); i++) {
			if(orden.get(i).getNombre().equals(producto.getNombre()) && orden.get(i).getTamaño().equals(producto.getTamaño())) {
				orden.get(i).setPrecio(orden.get(i).getPrecio()-producto.getPrecio());
				break;
			}
		}
	}

	/**
	*Description: Me verifica si existe un bono asociado con el codigo para asi elminarme la referencia del cliente y 
	*generarme el producto del bono ademas de modificarme el inventario para asi reservar su producto
	*@param codigo : Recibe un parametro de tipo String el cual es el codigo que 
	*tiene asociado un bono si esta correctamente escrito
	*@param servicio : Recibe un parametro de tipo Servicio para poder ver los bonos existentes del cliente
	*@return <b>producto</b> :Retorna el primer producto del bono que tiene asociado
	*/
	
	public static Producto validarBono(String codigo , Servicio servicio){
		Producto producto;
		for (int i=0; i < servicio.getBonosCliente().size();i++) {
			if (servicio.getBonosCliente().get(i).getCodigo().equals(codigo) && servicio.getBonosCliente().get(i).getTipoServicio().equalsIgnoreCase(servicio.nombre)) {
				producto = servicio.getBonosCliente().get(i).getProducto();
				for (int j=0; j < servicio.getCliente().getCineActual().getBonosCreados().size();j++) {
					if (servicio.getCliente().getCineActual().getBonosCreados().get(j).getProducto().equals(producto) && servicio.getCliente().getCineActual().getBonosCreados().get(j).getCliente().equals(cliente)) {
						servicio.getCliente().getCineActual().getBonosCreados().remove(j);
					}
				}
				for (int j=0; j < servicio.getCliente().getBonos().size();j++) {
					if (servicio.getCliente().getBonos().get(j).getProducto().equals(producto)) {
						servicio.getCliente().getBonos().remove(j);
					}
				}
				return producto;
			}
		}
		return null;
	}
	
	/**
	*Description: Recorre el ArrayList y muestra todos los pedidos que 
	*ha hecho hasta el momento
	*@return <b>Pedido</b> :  Genera un pedido para que el usuario lo logre visualizar y 
	*tenga conocimiento de lo que ha pedido
	*/
	
	public String mostrarOrden() {
		String pedido = "";
		double total = 0;
		for(int i =0;i<orden.size();i++) {
			int n = i + 1;
			pedido = pedido + "\n" + n +" -- " +orden.get(i).getCantidad()+" " + orden.get(i).getNombre() + " " + orden.get(i).getTamaño() +
					" : $" + orden.get(i).getPrecio();
			total = total + orden.get(i).getPrecio();
		}
		
		pedido = pedido + "\n Total: $" + total;
		return pedido;
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
