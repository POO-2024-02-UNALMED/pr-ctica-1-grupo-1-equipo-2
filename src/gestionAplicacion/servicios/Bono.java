package gestionAplicacion.servicios;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import gestionAplicacion.SucursalCine;
import gestionAplicacion.usuario.Cliente;
import iuMain.Administrador;

/**
 * @author Julián Bedoya Palacio
 * */

public class Bono implements Serializable{
	
	// Atributos
	private static final long serialVersionUID = 1L;
	private String codigo;
	private Producto producto;
	private String tipoServicio;
	private Cliente cliente;
	
	
	//Constructores
	public Bono(){}
	

	public Bono(String code, Producto producto, String tipoServicio, Cliente cliente) {
		this.codigo = code;
		this.producto = producto;
		this.tipoServicio = tipoServicio;
		this.cliente = cliente;
		cliente.getCineActual().getBonosCreados().add(this);
	}
	
	
	
	/**
	*Description: Este metodo se encarga primeramente de seleccionar los productos de tipo comida del inventario de la sucursal, luego genera un codigo aleatorio de 7 digitos para el bono
	*y ademas escoge de esos productos seleccionados uno de manera aleatoria para ser asociado al bono y lo descuenta de la cantidad de disponibles, finalmente imprime por pantalla el bono al usuario
	*@param sucursal :  se pasa el como parametro la sucursal a la cual se le solicita el inventario
	*@return <b>Bono</b> :  Se retorna el bono creado
	*/
	public static Bono generarBonoComidaJuegos(SucursalCine sucursal, Cliente cliente) {
	    ArrayList<Producto> productosComida = new ArrayList<>();
	    for (Producto producto : sucursal.getInventarioCine()) {
	        if (producto.getTipoProducto().equals("comida") & producto.getCantidad()>0) {
	            productosComida.add(producto);
	        }
	    }
	    
	    if (productosComida.isEmpty()) {

	        return null;
	    }

	    Random random = new Random();

	    
	    int numeroAleatorio = random.nextInt(productosComida.size());
	    String code = generarCodigoAleatorio(7);
	    Producto productoBono = new Producto(productosComida.get(numeroAleatorio).getNombre(), productosComida.get(numeroAleatorio).getTamaño(), 1, productosComida.get(numeroAleatorio).getPrecio(), "comida"); 
	    Bono bono = new Bono(code,productoBono,productosComida.get(numeroAleatorio).getTipoProducto(),cliente);
	    productosComida.get(numeroAleatorio).setCantidad(productosComida.get(numeroAleatorio).getCantidad()-1);
	    
	    String tipoProducto = "Comida";
	    
	    Administrador.mostrarBono(productosComida, numeroAleatorio, tipoProducto, code);
	    
	    return bono;
	}
	
	
	/**
	*Description: Este metodo se encarga en primer lugar de seleccionar los productos de tipo souvenir del inventario de la sucursal, luego genera un codigo aleatorio de 7 digitos para el bono
	*y ademas escoge de esos productos seleccionados uno de manera aleatoria para ser asociado al bono y lo descuenta de la cantidad de disponibles, finalmente imprime por pantalla el bono al usuario
	*@param sucursal :  se pasa el como parametro la sucursal a la cual se le solicita el inventario
	*@return <b>Bono</b> :  Se retorna el bono creado
	*/
	public static Bono generarBonoSouvenirJuegos(SucursalCine sucursal, Cliente cliente) {
		ArrayList<Producto> productosSouvenirs = new ArrayList<>();
	    for (Producto producto : sucursal.getInventarioCine()) {
	        if (producto.getTipoProducto().equals("souvenir")& producto.getCantidad()>0) {
	        	productosSouvenirs.add(producto);
	        }
	    }
	    
	    if (productosSouvenirs.isEmpty()) {
	    	return null;
	    }
	        

	    Random random = new Random();

	    
	    int numeroAleatorio = random.nextInt(productosSouvenirs.size());
	    String code = generarCodigoAleatorio(7);
	    Producto productoBono = new Producto(productosSouvenirs.get(numeroAleatorio).getNombre(), productosSouvenirs.get(numeroAleatorio).getTamaño(), 1, productosSouvenirs.get(numeroAleatorio).getPrecio(), "souvenir"); 
	    Bono bono = new Bono(code,productoBono,productosSouvenirs.get(numeroAleatorio).getTipoProducto(), cliente);
	    productosSouvenirs.get(numeroAleatorio).setCantidad(productosSouvenirs.get(numeroAleatorio).getCantidad()-1);
	    
	    String tipoProducto = "Souvenir";
	    
	    Administrador.mostrarBono(productosSouvenirs, numeroAleatorio, tipoProducto, code);
	    
	    return bono;
	}
	
	
	
	/**
	*Description: Este metodo se encarga de generar un codigo aleatorio para los bonos creados.
	*@param longitud :  se pasa el como parametro la longitud que se desea el codigo
	*@return <b>Bono</b> :  Se retorna el bono creado
	*/
	private static String generarCodigoAleatorio(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder codigo = new StringBuilder(longitud);

        for (int i = 0; i < longitud; i++) {
            int index = random.nextInt(caracteres.length());
            codigo.append(caracteres.charAt(index));
        }

        return codigo.toString();
    }
	
	
	
	//Getters y Setters
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getTipoServicio() {
		return tipoServicio;
	}

	public void setTipoServicio(String tipoServicio) {
		this.tipoServicio = tipoServicio;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

}

