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
	
	//Methods
	/**
	 * Description : Este método se encarga de verificar si se puede aplicar o no un descuento sobre el precio de la película
	 * según si la cantidad de tickets creados corresponde a un cuadrado perfecto.
	 * @return <b>double</b> : Retorna un double (De tipo double) que corresponde al precio del ticket en caso de aplicarse
	 * o no el descuento.
	 * */
	private double clienteSuertudo() {
		
		//Verificamos si al sacarle módulo a la raíz cuadrada a los tickets creados nos da un número sin decimales (Cuadrado perfecto)
		boolean verificacion = (Math.sqrt(sucursalCompra.getCantidadTicketsCreados()) % 1 == 0) ? true : false; 
		//Tomamos el precio de la película asociada al ticket
		double precio = this.pelicula.getPrecio();
		
		if(verificacion){
			if (this.pelicula.getTipoDeFormato().equals("3D") || this.pelicula.getTipoDeFormato().equals("4D") ) {
				precio = precio * 0.5;
			}else {
				precio = precio  * 0.2;
			}
		}
		return precio;
	}

	/**
	 * Description: Este método se encarga de generar el último paso del proceso de pago y será ejecutado por un ticket luego de ser verificado el pago: 
	 * <ol>
	 * <li>Se vuelven a settear los metodos de pago que el cliente tendrá disponibles.</li>
	 * <li>Se pasa la referencia del ticket al array de tickets del usuario.</li>
	 * <li>Se pasa la referencia del cliente al atributo dueño del ticket.</li>
	 * <li>Se aumenta la cantidad de tickets genereados en uno.</li>
	 * <li>Se crea una referencia de este ticket en el arraylist de los tickets creados en el cine.</li>
	 * <li>Se crea el código de descuento para los juegos y se asocian al cliente y a los códigos de descuentos generados en la clase Arkade.</li>
	 * <li>Creamos el id del ticket y aumentamos la cantidad de tickets creados (Lógica id).</li>
	 * </ol>
	 * @param cliente : Se pide como parámetro el cliente (De tipo Cliente) que realizó exitosamente el pago.
	 */
	@Override
	public void procesarPagoRealizado(Cliente cliente) {
		//Se reestablecen los métodos de pago disponibles del cliente
		MetodoPago.asignarMetodosDePago(cliente);
		
		//Se pasa la referencia del ticket al cliente que lo compró
		cliente.getTickets().add(this);
		this.setDueno(cliente);
		
		//Se aumenta la cantidad de tickets creados
		cliente.getCineActual().setCantidadTicketsCreados(cliente.getCineActual().getCantidadTicketsCreados() + 1);
		
		//Se crea un apuntador del ticket en el array de tickets disponibles y se evalua si aplica para algún descuento
		SucursalCine.getTicketsDisponibles().add(this);
		if (this.horario.toLocalDate().isEqual(SucursalCine.getFechaActual().toLocalDate())) {
			cliente.getCineActual().getTicketsParaDescuento().add(this);
		}
		
		//Proceso para funcionalidad 4
		String codigoArkade = this.generarCodigoTicket();
		//Arkade.getCodigosGenerados().add(codigoArkade);
		this.dueno.getCodigosDescuento().add(codigoArkade);
		
		//Lógica id
		cantidadTicketsCreados++;
		this.idTicket = cantidadTicketsCreados;
		
	}
	
	
	/**
	 * @Override
	 * Description: Este método se encarga de retornar un string que contiene toda la información del ticket en forma de factura.
	 * @return <b>String</b> : Este método retorna un String que representa la factura de compra con el fin de ser mostrada en pantalla
	 * luego de realizar una compra.
	 * */
	public String factura() {
		return	"========= Factura Ticket =========\n" +
				"Nombre dueño : " + this.dueno.getNombre() + "\n" +
				"Documento : " + this.dueno.getDocumento() + "\n" +
				"Pelicula : " + this.pelicula.getNombre() + "\n" +
				"Número de sala : " + this.salaDeCine.getNumeroSala() + "\n" +
				"Número de asiento : " + this.numeroAsiento + "\n" +
				"Fecha Presentación: " + this.horario.toLocalDate() + "\n" +
				"Hora Presentación: " + this.horario.toLocalTime() + "\n" + 
				"Valor ticket (IVA incluido): " + this.precio + "\n" + 
				"Fecha de compra: " + SucursalCine.getFechaActual().withNano(0) + "\n" +
				"Sucursal : " + this.sucursalCompra.getLugar();
				
	}
	
	/**
	 * Description : Este método se encarga de asignar el ticket a su respectivo dueño luego de la deserialización con el fin de asegurar
	 * la integridad de la persistencia de datos.
	 * */
	public void agregarTicketClienteSerializado() {
		Cliente cliente = Cliente.revisarDatosCliente(this.dueno.getDocumento());
		cliente.getTickets().add(this);
		this.dueno = cliente;
	}
	
	/**
	 * Description: Este metodo se encarga de generar un codigo de descuento que se le asocia al usuario dueño del ticket para que pueda redimirlo 
	 * en el Arkade posteriormente.
	 * @return <b>codigoTicket</b> : Este método retorna un String que contiene la información del codigo mas el genero de la pelicula asociada.
	 * */
	private String generarCodigoTicket() {
		String codigoTicket = this.getPelicula().getTipoDeFormato()
				+this.getDueno().getTipoDocumento()
				+this.getPelicula().getSalaPresentacion().getNumeroSala()
				+"-"
				+this.getPelicula().getGenero();

		return codigoTicket;
	}
	
	
	/**
	 * Description: Este metodo se encarga de encontar el genero de la pelicula asociada a un codigo que esta contenido dentro del string del mismo.
	 * @param cliente : Este método reckbe como parametro el codigo del cual se sacara un substring con el genero de la pelicula
	 * @return <b>String</b> : Este método retorna un String que contiene la información del genero de la pelicula del codigo.
	 * */
	public static String encontrarGeneroCodigoPelicula(String codigo) {
		int indiceGuion = codigo.indexOf("-");

        if (indiceGuion != -1 && indiceGuion != codigo.length() - 1) {

            return codigo.substring(indiceGuion + 1);
        } 
        else {

            return "";
        }
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

