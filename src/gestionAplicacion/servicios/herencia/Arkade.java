package gestionAplicacion.servicios;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import gestionAplicacion.SucursalCine;
import gestionAplicacion.usuario.*;

/**
 * @author Andres Alejandro Rosero Toledo
 * */
public class Arkade implements Serializable{

	//Atributos
	private static final long serialVersionUID = 1L;
	private static final double puntuacionMaxima = 10.0;
	
	private String nombreServicio;
	private double valorServicio;
	private String generoServicio;
	
	//Constructores
	
	public Arkade(){SucursalCine.getJuegos().add(this);}
	
	public Arkade(String nombreServicio, double valorServicio, String generoServicio) {
		super();
		this.nombreServicio = nombreServicio;
		this.valorServicio = valorServicio;
		this.generoServicio = generoServicio;
		SucursalCine.getJuegos().add(this);
	}

	//metodos
	
	/**
	*Description: Se verifica si almenos hay alguna tarjeta disponible en el array de tarjetas en inventario.
	*@return <b>boolean</b> :  retorna true o false si hay o no tarjetas en inventario.
	*/
	
	public static boolean verificarTarjetasEnInventario(SucursalCine cine) {
		boolean value= false;
		if (cine.getInventarioTarjetasCinemar().size()>0) {
			value = true;
		}
		return value;
	}
	
	/**
	*Description: Toma la primera tarjeta cinemar disponible y le asocia el cliente, se le cambia el estado, y se le asigna saldo 0,
	*ademas, al Cliente se le asocia la tajeta cinemar y se elimina esa tarjeta del ArrayList de tarjetas en inventario
	*@param cliente :  se pasa el cliente a asociar la tarjeta Cinemar.
	
	*/
	public static void asociarTarjetaCliente(Cliente cliente) {
		cliente.getCineActual().getInventarioTarjetasCinemar().get(0).setDueno(cliente);
		cliente.getCineActual().getInventarioTarjetasCinemar().get(0).setSaldo(0);
		cliente.setCuenta(cliente.getCineActual().getInventarioTarjetasCinemar().get(0));
		cliente.getCineActual().getInventarioTarjetasCinemar().remove(0);
	}
	
	
	/**
	*Description: Este metodo se encarga de mostrar los diferentes juegos por pantalla
	*@return <b>String</b> :  Se retorna un string con los juegos a mostrar al usuario
	*/
	public static String mostrarJuegos(){
		String juegos = null;
		int i = 1;
		ArrayList<Double> precios = new ArrayList<>();
		
		precios.addAll(Arrays.asList(15000.0, 20000.0, 10000.0, 30000.0, 7500.0));
		
		for (Arkade juego : SucursalCine.getJuegos()) {
			if (juegos == null) {
				if (juego.getValorServicio()==precios.get(i-1)) {
					juegos = i+". "+juego.nombreServicio+"--"+juego.generoServicio+"--"+juego.valorServicio+".\n";
				}
				else {
					juegos = i+". "+juego.nombreServicio+"--"+juego.generoServicio+"--"+juego.valorServicio+"--> Precio anterior: "+precios.get(i-1)+".\n";
				}

				i++;
			}
			else {
				if (juego.getValorServicio()==precios.get(i-1)) {
					juegos += i+". "+juego.nombreServicio+"--"+juego.generoServicio+"--"+juego.valorServicio+".\n";
				}
				else {
					juegos += i+". "+juego.nombreServicio+"--"+juego.generoServicio+"--"+juego.valorServicio+" --> Precio anterior: "+precios.get(i-1)+".\n";
				}

				i++;
			}
		}
		juegos+= "6. Volver al inicio\n7. Salir y Guardar\n";
		return "¿Cual juego desea jugar?\n"+juegos;
	}
	
	/**
	*Description: Este metodo se encarga de aplicar un descuento del 20% al valor de los juegos con un genero pasado en el parametro
	*@param genero :  se pasa el como parametro el genero de el juego a aplicar el descuento
	*@return <b>void</b> :  No hay retorno
	*/
	public static void AplicarDescuentoJuegos(String genero) {
		for (Arkade juego : SucursalCine.getJuegos()) {
			if (juego.getGeneroServicio().equals(genero))
			juego.setValorServicio(juego.getValorServicio()-(juego.getValorServicio()*20/100));
		}
	}
	
	
	/**
	*Description: Este metodo se encarga de restablecer el valor del precio de todos los juegos
	*@return <b>void</b> :  No hay retorno
	*/
	public static void reestablecerPrecioJuegos() {
		SucursalCine.getJuegos().get(0).setValorServicio(15000);
		SucursalCine.getJuegos().get(1).setValorServicio(20000);
		SucursalCine.getJuegos().get(2).setValorServicio(10000);
		SucursalCine.getJuegos().get(3).setValorServicio(30000);
		SucursalCine.getJuegos().get(4).setValorServicio(7500);
	}
	

	
	
	
	
	//getters y setters

	public String getNombreServicio() {
		return nombreServicio;
	}


	public double getValorServicio() {
		return valorServicio;
	}
	

	public static double getPuntuacionMaxima() {
		return puntuacionMaxima;
	}

	public String getGeneroServicio() {
		return generoServicio;
	}

	public void setNombreServicio(String nombreServicio) {
		this.nombreServicio = nombreServicio;
	}


	public void setValorServicio(double valorServicio) {
		this.valorServicio = valorServicio;
	}


	public void setGeneroServicio(String generoServicio) {
		this.generoServicio = generoServicio;
	}


	
}
