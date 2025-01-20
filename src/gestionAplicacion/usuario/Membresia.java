package gestionAplicacion.usuario;
import java.util.ArrayList;
import java.io.Serializable;
import gestionAplicacion.SucursalCine;
import gestionAplicacion.servicios.Producto;

/**
 * @author Valentina Leon Beltran
 * */
public class Membresia implements IBuyable, Serializable{
	

	//Atrbutos
	private static final long serialVersionUID = 1L;
	private String nombre;
	private int categoria;
	private ArrayList<Cliente> clientes = new ArrayList<>();
	private double descuentoAsociado;
	private int valorSuscripcionMensual;
	private int duracionMembresiaDias;
	private int tipoMembresia;
	
	
	//Constructores
	public Membresia(String nombre, int categoria, int valorSuscripcionMensual, int duracionMembresiaDias) {
		this();
		this.nombre = nombre;
		this.categoria = categoria;
		this.valorSuscripcionMensual = valorSuscripcionMensual;
		this.duracionMembresiaDias = duracionMembresiaDias;
	}

	public Membresia(){
		SucursalCine.getTiposDeMembresia().add(this);
	}
	
	public Membresia(String nombre, int categoria, ArrayList<Cliente> clientes, double descuentoAsociado,
			int valorSuscripcionMensual, int duracionMembresiaDias, int tipo) {
		this.nombre = nombre;
		this.categoria = categoria;
		this.clientes = clientes;
		this.descuentoAsociado = descuentoAsociado;
		this.valorSuscripcionMensual = valorSuscripcionMensual;
		this.duracionMembresiaDias = duracionMembresiaDias;
		this.tipoMembresia = tipo;
		SucursalCine.getTiposDeMembresia().add(this);
	}
	
	/**
	*<b>Description</b>: Este método se encarga de verificar si el cliente tiene membresia activa
	*@param cliente : Se pide al cliente para revisar su atributo de tipo Membresia
	*@return <b>string</b> : Se retorna un texto personalizado indicando si tiene membresia
	*o no.
	*/
	public static String verificarMembresiaActual(Cliente cliente) {
		//Se crea las instancias
		String mensaje = null;
		Membresia membresiaActual = cliente.getMembresia();
		String nombreMembresiaActual = null;
		
		//Se actualiza el nombre de la membresia.
		if (membresiaActual == null) {
			mensaje = "Bienvenido, " + cliente.getNombre() 
			+".\nActualmente, no tiene membresia activa en el sistema.\nPor favor, seleccione la membresia que desea adquirir:\n";
		} else {
			nombreMembresiaActual = cliente.getMembresia().getNombre();
			mensaje = "Bienvenido, " + cliente.getNombre() 
			+".\nActualmente, su membresia es " + nombreMembresiaActual
			+ " de categoria " + cliente.getMembresia().getCategoria() + "\nPor favor, seleccione la membresia que desea adquirir/actualizar:\n";
		}
		return mensaje;
	}
		
	/**
	*<b>Description</b>: Este método se encarga de asignar los descuentos dependiendo de la
	*categoria de la membresia.
	*@param none : No se necesitan parametros.
	*@return <b>void</b> : No realiza retorno. El sistema asigna el correspondiente descuento
	*dependiendo de la categoria recorrida en el array.
	*/
	public static void asignarDescuento() {
		//Se realiza un ciclo y se toma la categoria de cada membresia para asignar el descuento con switch.
		for (Membresia membresia : SucursalCine.getTiposDeMembresia()) {
			//Se realiza el ciclo tomando la categoria de cada membresia.
	 		int categoria = membresia.getCategoria();
	 		double descuento = 0.05;
	 		descuento+=0.05 * categoria;
	 		switch (categoria) {
	 		
	 		//Por cada iteración del ciclo, se aumenta el número de la categoria y su descuento para usar el set.
	 		case 1: membresia.setDescuentoAsociado(descuento); break;
	 		case 2: membresia.setDescuentoAsociado(descuento); break;
	 		case 3: membresia.setDescuentoAsociado(descuento); break;
	 		case 4: membresia.setDescuentoAsociado(descuento); break;
	 		case 5: membresia.setDescuentoAsociado(descuento); break;
	 		}
		}	
 	}	
	
	
	/**
	*<b>Description</b>: Este método se encarga de mostrar las categorias de membresias disponibles que hayan en la sucursal actual.
	*Se realiza una búsqueda de los objetos de tipo Producto que sean de Membresia en el inventario de la sucursal. En caso
	*de que la cantidad de alguno de estos productos este en 0, se indica al cliente que la opción esta agotada.
	*Otra notación es que si el cliente ya posse una membresía y aún no esta en plazo de renovación, se omite su selección para tener
	*mejor control sobre nuestras unidades limitadas en el inventario de la sucursal de la compra.
	*@param clienteProceso : Se pide al cliente para verificar si posee membresía, lo cual modifica el resultado del método.
	*@param sucursalCineProceso : Se pide la sucursal actual para poder realizar la búsqueda de objetos de tipo Producto pertenecientes a Membresía.
	*@return <b>string</b> : Se retorna un texto mostrando el nombre de las categorias disponibles en la sucursal de la compra.
	*/
	public static String mostrarCategoria(Cliente clienteProceso, SucursalCine sucursalCineProceso) {
		String resultado = "\n";
		int i = 1;
		Membresia membresiaActual = clienteProceso.getMembresia();
		String nombreMembresiaActual = null; 
		
		//Se actualiza el nombre de la membresia.
		if (membresiaActual == null) {
			nombreMembresiaActual = "Sin membresia";
		} else {
			nombreMembresiaActual = clienteProceso.getMembresia().getNombre();}
		//Se recorre la lista de tipos de membresia.
		for (Producto membresia : sucursalCineProceso.getInventarioCine()) {
			//Se ignora los productos que no sean de tipo Membresia.
			if (!membresia.getTipoProducto().equalsIgnoreCase("Membresia")) {
				continue;
			}
				if (resultado == null) {
					if (membresia.getCantidad() == 0 && !nombreMembresiaActual.equals(membresia.getNombre())) {
						resultado = "Categoria " + i + ". " + membresia.getNombre() + " (AGOTADA)\n";
						i++;
						continue;
					}
					//Si el cliente ya tiene esta membresia y además, le faltan más de 5 dias para que expire, no se muestra en el menu.
					else if (nombreMembresiaActual.equalsIgnoreCase(membresia.getNombre()) && 
							(clienteProceso.getFechaLimiteMembresia().minusDays(6).isAfter(SucursalCine.getFechaActual().toLocalDate())
							&& clienteProceso.getFechaLimiteMembresia().isAfter(SucursalCine.getFechaActual().toLocalDate()))) {
						i++;
						continue;
					} else {
				resultado = "Categoria " + i + ". "+ membresia.getNombre() + ". Requisitos: " + (int) membresia.getPrecio() + " puntos."+"\n";}
			}else {
				if (membresia.getCantidad() == 0 && !nombreMembresiaActual.equals(membresia.getNombre())) {
					resultado = resultado + "Categoria " + i + ". " + membresia.getNombre() + " (AGOTADA)\n";
					i++;
					continue;
				}
				//Si el cliente ya tiene esta membresia y además, le faltan más de 5 dias para que expire, no se muestra en el menu.
				else if (nombreMembresiaActual.equalsIgnoreCase(membresia.getNombre()) && 
						(clienteProceso.getFechaLimiteMembresia().minusDays(6).isAfter(SucursalCine.getFechaActual().toLocalDate())
						&& clienteProceso.getFechaLimiteMembresia().isAfter(SucursalCine.getFechaActual().toLocalDate()))) {
					i++;
					continue;
				} else if (membresia.getNombre().equals("Challenger") || membresia.getNombre().equals("Radiante")) {
					if (membresia.getNombre().equals("Challenger")) {
					resultado = resultado + "Categoria " + i + ". " + membresia.getNombre() + ". Requisitos: " + (int) membresia.getPrecio() + " puntos y peliculas vistas: 10"+"\n";}
					else {
						resultado = resultado + "Categoria " + i + ". " + membresia.getNombre() + ". Requisitos: " + (int) membresia.getPrecio() + " puntos y peliculas vistas: 15"+"\n";
					}
				}else {
			resultado = resultado + "Categoria " + i + ". " + membresia.getNombre() + ". Requisitos: " + (int) membresia.getPrecio() + " puntos." +"\n";}
			}
			i++;
		}
		return resultado;
	}

	/**
	*<b>Description</b>: Este método verifica a que categorias puede acceder el cliente. Se revisa
	*si hay membresias disponibles y si el cliente tiene la cantidad de puntos e historial de peliculas como requisitos.
	*@param clienteProceso : Se pide al cliente para revisar su historial de peliculas para la 
	*verificación. Si tiene X peliculas vistas en el cine, tiene acceso a ciertas categorias.
	*@param categoriaSeleccionada : Se pide el número de la categoria que quiera adquirir.
	*@param sucursalCineProceso : Se pide la sucursal de cine para revisar la cantidad de membresias.
	*@return <b>boolean</b> : Se retorna un dato booleano que indica si el cliente puede 
	*adquirir la categoria de membresia seleccionada.
	*/
	public static boolean verificarRestriccionMembresia(Cliente clienteProceso, int categoriaSeleccionada, SucursalCine sucursalCineProceso) {
		Membresia membresiaProceso = Membresia.asignarMembresiaNueva(categoriaSeleccionada);
		boolean esValido = false;
		
		//Se obtiene los puntos que posea el cliente.
		double puntos = 0.0;
		for (MetodoPago metodoPago : clienteProceso.getMetodosDePago()) {
			if (metodoPago.getNombre().equals("Puntos")) {
				puntos = metodoPago.getLimiteMaximoPago();
				break;
			} else {
				puntos = clienteProceso.getPuntos();
			}
		}
		//Se obtiene la cantidad de la membresia que hayan en el cine.
		int membresiaStock = 0;
		for (Producto membresiaInventario : sucursalCineProceso.getInventarioCine()) {
			if (membresiaInventario.getNombre().equals(membresiaProceso.getNombre())) {
				membresiaStock = membresiaInventario.getCantidad();
				break;
			}
		}
		//Se realizan diferentes validaciones dependiendo si el cliente va a realizar una renovación o nueva subscripción.
		if (clienteProceso.getMembresia() == null || clienteProceso.getMembresia().getCategoria() != categoriaSeleccionada) {
		//Si la categoria es 4 o 5, se revisa si se cumple los requisitos.
			//En caso de no tener membresía.
			switch (categoriaSeleccionada) {
		
			case 1: esValido = (membresiaStock > 0) ? true : false; break;
			case 2: esValido = (membresiaStock > 0 && puntos >= 5000) ? true : false; break;
			case 3: esValido = (membresiaStock > 0 && puntos >= 10000) ? true : false; break;
			case 4: esValido = (membresiaStock > 0 && clienteProceso.getHistorialDePeliculas().size() >= 10 && puntos >= 15000) ? true : false; break;
			case 5: esValido = (membresiaStock > 0 && clienteProceso.getHistorialDePeliculas().size() >= 15 && puntos >= 20000) ? true : false; break;
			}
		//En caso de realizar la renovación de la misma membresia.
		} else if (clienteProceso.getMembresia() != null
				&& clienteProceso.getFechaLimiteMembresia().minusDays(6).isAfter(SucursalCine.getFechaActual().toLocalDate())
				&& clienteProceso.getMembresia().getCategoria() == categoriaSeleccionada) {
			switch (categoriaSeleccionada) {
			
			case 1: esValido = true; break;
			case 2: esValido = (puntos >= 5000) ? true : false; break;
			case 3: esValido = (puntos >= 10000) ? true : false; break;
			case 4: esValido = (clienteProceso.getHistorialDePeliculas().size() >= 10 && puntos >= 15000) ? true : false; break;
			case 5: esValido = (clienteProceso.getHistorialDePeliculas().size() >= 15 && puntos >= 20000) ? true : false; break;
			}
			
		}
		
		return esValido;
	}
	/**
	*<b>Description</b>: Este método asigna el tipo a la categoria de la membresia para
	*ser usado posteriormente en otras funcionalidades.
	*@param none : No se solicitan parametros.
	*@return <b>void</b> : No retorna ningún dato ya que solo actualiza el tipo 
	*de las membresias existentes.
	*/
	public static void asignarTipoMembresia () {
		//Se revisa la lista de Tipos de Membresia y se asigna el tipo
		for (Membresia membresia : SucursalCine.getTiposDeMembresia()) {
			if (membresia.getCategoria() > 0 && membresia.getCategoria() <= 3) {
				membresia.setTipoMembresia(1);
			} 
			else if (membresia.getCategoria() > 3 && membresia.getCategoria() <= 5){
				membresia.setTipoMembresia(2);
			}
		}
	}
	
	/**
	*<b>Description</b>: Este método se encarga de asignar la nueva membresia con un apuntador
	*de Membresia que coincida con la opción seleccionada durante el proceso de compra.
	*@param membresia : Se pide una instancia de tipo de membresia para usarlo como apuntador.
	*@param categoriaMembresia : Se pide un entero que es la selección de la membresia.
	*@return <b>Membresia</b> : Se retorna un dato de tipo Membresia que contiene el apuntador de
	*tipo Membresia que coincide con la categoria deseada.
	*/
	public static Membresia asignarMembresiaNueva(int categoriaMembresia) {
		//Se crea una instancia de tipo Membresia null
		Membresia membresiaNueva = null;
		//Se busca las instancias de tipo Membresia en Tipos de Membresia y si la categoria coincide, la instancia anterior apunta a este resultado
		for (Membresia membresia2 : SucursalCine.getTiposDeMembresia()) {
			if (membresia2.getCategoria() == categoriaMembresia) {
					membresiaNueva = membresia2;
					break;
			}
		}
		return membresiaNueva;
	}
	/**
	*<b>Description</b>: Este método se encarga de añadir al inventario de cada sucursal de cine, 
	*los productos de tipo Membresia que se usarán para limitar las membresias que se puede adquirir en cada sucursal.
	*Por cada sucursal de cine en el lista, se crean los productos que corresponden a cada membresia con una cantidad limitada.
	*Esto se usa para tener un control sobre el número de membresia que se pueden adquirir en cada cine.
	*@param sucursalesCine : Se pide la lista que contiene las sucursales de cine creadas para acceder a su inventario y añadir los objetos
	*de tipo Producto pertenecientes a Membresía.
	*/
	public static void stockMembresia(ArrayList<SucursalCine> sucursalesCine) {
		int i = 50;
		int puntos = 0;
		//Se revisa la lista de las sucursales de cine creadas.
		for (SucursalCine sucursalCine : sucursalesCine) {
			//Por cada membresia, se crea un producto de este tipo y se añade al inventario de la sucursal.
			for (Membresia membresia : SucursalCine.getTiposDeMembresia()) {
					Producto membresiaSucursal = new Producto("Membresia", membresia.getNombre(), puntos, i);
					sucursalCine.getInventarioCine().add(membresiaSucursal);
					puntos+=5000;
					i-=10;
			}
			//Se reinicia el contador de cantidad y puntos cada vez que se itere a una nueva sucursal de la lista.	
			i = 50;
			puntos = 0;
		}
	}

	//Getters and Setters
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getDuracionMembresiaDias() {
		return duracionMembresiaDias;
	}
	
	public void setDuracionMembresiaDias(int duracionMembresiaDias) {
		this.duracionMembresiaDias = duracionMembresiaDias;
	}
	
	public int getCategoria() {
		return categoria;
	}

	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}

	public int getTipoMembresia() {
		return tipoMembresia;
	}

	public void setTipoMembresia(int tipo) {
		this.tipoMembresia = tipo;
	}

	public double getDescuentoAsociado() {
		return descuentoAsociado;
	}

	public void setDescuentoAsociado(double descuentoAsociado) {
		this.descuentoAsociado = descuentoAsociado;
	}

	public int getValorSuscripcionMensual() {
		return valorSuscripcionMensual;
	}

	public void setValorSuscripcionMensual(int valorSuscripcionMensual) {
		this.valorSuscripcionMensual = valorSuscripcionMensual;
	}
	
	public ArrayList<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(ArrayList<Cliente> clientes) {
		this.clientes = clientes;
	}
	
	
	//Métodos implementados por la interfaz.
	
	/**
	 * Description: Este método se encarga de realizar el proceso de asignación de membresía una vez completado el pago. Para ello, se realizan distintos pasos dependiendo del caso:
	 * <ol>
	 * <li>Se verifica si el cliente ya tiene una membresía asociada</li>
	 * <li>En caso de que no tuviera una membresía ya asociada, se asigna su atributo de membresía, el lugar donde fue comprada para restar la cantidad del producto en el inventario de la sucursal y la fecha de caducidad.</li>
	 * <li>En caso de que se vaya a actualizar la membresía, primero se devuelve la membresía a reemplazar al stock de inventario donde fue adquirida,
	 * luego se realiza los pasos de paso anterior pero la fecha de caducidad será mayor ya se basa en la fecha limite de la membresía anterior</li>
	 * <li>Por ultimo, si se va a renovar la misma membresía, solo se añaden más dias a la fecha de caducidad y se actualiza en el arreglo de tipo Cliente en Membresía</li>
	 * </ol>
	 * Una vez se realiza algunos de los procesos anteriores, se procede a actualizar los métodos de pago del cliente y se añade su referencia al arreglo de tipo Cliente en Membresía.
	 * @param cliente : Se pide como parámetro el cliente (De tipo Cliente) que realizó exitosamente el pago.
	 */
	@Override
	public void procesarPagoRealizado(Cliente cliente) {
		//Se asigna la referencia de la membresia adquirida en el cliente y se actualizan sus métodos de pago.
		boolean isPrimerMembresia = true;
		//Si el cliente no tiene membresia al momento de la compra, se le asigna y se cambia el boolean.
		if (cliente.getMembresia()==null) {
			cliente.setMembresia(this);
			cliente.setOrigenMembresia(cliente.getCineActual().getIdSucursal());
			cliente.setFechaLimiteMembresia(SucursalCine.getFechaActual().toLocalDate().plusDays(this.duracionMembresiaDias));
			isPrimerMembresia = false;
			
		//Si el cliente va a cambiar a otra membresia, se vuelve el stock a la sucursal original y luego se asigna la sucursal donde realizó el pago.
		} else if (!cliente.getMembresia().getNombre().equals(this.getNombre())) {
			for (SucursalCine sucursal : SucursalCine.getSucursalesCine()) {
				if (sucursal.getIdSucursal() == cliente.getOrigenMembresia()) {
					for (Producto productoMembresia : sucursal.getInventarioCine()) {
						if (productoMembresia.getNombre().equals(cliente.getMembresia().getNombre())) {
							productoMembresia.setCantidad(productoMembresia.getCantidad()+1);
							break;
						}
					} break;
				}
			}
			cliente.setMembresia(this);
			cliente.setOrigenMembresia(cliente.getCineActual().getIdSucursal());
			cliente.setFechaLimiteMembresia(cliente.getFechaLimiteMembresia().plusDays(this.duracionMembresiaDias));
			isPrimerMembresia = false;
		
		//En caso de tener una membresia se puede renovar y no se resta el stock de su inventario por lo que ya esta asignada.
		}else {
			cliente.setFechaLimiteMembresia(cliente.getFechaLimiteMembresia().plusDays(this.duracionMembresiaDias));
			cliente.getMembresia().getClientes().remove(cliente);
		}
		
		//Se va al inventario del cine para restar la cantidad de membresias si el cliente no esta renovando.
		if (this.getNombre().equals(cliente.getMembresia().getNombre()) && isPrimerMembresia == false)
		for (Producto membresiaStock : cliente.getCineActual().getInventarioCine()) {
			if (membresiaStock.getNombre().equals(this.getNombre())) {
				membresiaStock.setCantidad(membresiaStock.getCantidad() - 1);
				break;
			}
		}
		//Al adquirir la membresia, se crea y asigna un método de pago único que permite acumular puntos canjeables con compras en el cine.
		MetodoPago.asignarMetodosDePago(cliente);
		
		//Se pasa la referencia de la membresia al cliente que lo compró y se agrega este último al array de clientes en Membresia
		this.getClientes().add(cliente);
		
	}
		



	/**
	 * Description: Este método se encarga de crear una factura personalizada con la información de compra del cliente.
	 * Este método es ejecutado por la membresía que fue comprada exitosamente.
	 * @return String : Se retorna un String que contiene los datos de la compra.
	 */
	@Override
	public String factura() {
		return  "Membresia: " + this.getNombre()+ "\n" +
				"Categoria: " + this.getCategoria() + "\n" +
				"Tipo: " + this.getTipoMembresia() + "\n" +
				"Precio de compra: " + this.getValorSuscripcionMensual() + "\n================================";
	}
	
	
}