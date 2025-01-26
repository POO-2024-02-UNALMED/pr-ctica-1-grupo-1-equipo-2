package baseDatos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import gestionAplicacion.SucursalCine;
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

public class Deserializador {
	// Este atributo es para definir la ruta al directoria temp que contiene las clases.
	private static File rutaTemp = new File ("src\\baseDatos\\temp\\sucursales");
	private static File rutaTempLinux = new File ("src/baseDatos/temp/sucursales");
	// Este método se encarga de cargar las listas de objetos que hay almacenados (serializados).
	
	/**
	 * Description : Este método se encarga de deserializar los atributos de instancia de sus correspondientes
	 * instancias de SucursalCine, para esto iteramos sobre los directorios que contienen la información
	 * de cada sucursal por separado, creamos una instancia de esa sucursal, iteramos sobre cada *.txt
	 * obteniendo así la información a deserializar, le asignamos sus atributos de instancia,
	 * solucionamos algunos errores de referencias a objetos (Para hacer correctamente las validaciones durante 
	 * la ejecución del programa) y repetimos este proceso por cada directorio/sucursal. <br>
	 * 
	 * Atributos que deserializa:
	 * <ol>
	 * <li>Lugar.</li>
	 * <li>Salas de cine.</li>
	 * <li>Peliculas.</li>
	 * <li>Bonos.</li>
	 * <li>Cantidad de tickets creados.</li>
	 * <li>Inventario.</li>
	 * <li>Servicios.</li>
	 * <li>Tarjetas cinemar.</li>
	 * </ol>
	 * */
	@SuppressWarnings("unchecked")
	public static void deserializar () {
		File [] dirs = rutaTempLinux.listFiles();
		FileInputStream fis;
		ObjectInputStream ois;
		
		//Iteramos sobre los directorios de nuestras distintas sucursales
		for (File dir : dirs) {
			
			SucursalCine sucursalCine = new SucursalCine();
				
			File [] docs = dir.listFiles();
			
			//Deserializador
			for (File file : docs) {
				
				if (file.getAbsolutePath().contains("lugar")) {
					try {
						fis = new FileInputStream(file);
						ois = new ObjectInputStream(fis);
						sucursalCine.setLugar((String) ois.readObject());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}  catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				} else if (file.getAbsolutePath().contains("salasDeCine")) {
					try {
						fis = new FileInputStream(file);
						ois = new ObjectInputStream(fis);
						sucursalCine.setSalasDeCine((ArrayList<SalaCine>) ois.readObject());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				} else if (file.getAbsolutePath().contains("tpeliculas")) {
					try {
						fis = new FileInputStream(file);
						ois = new ObjectInputStream(fis);
						sucursalCine.setCartelera((ArrayList<Pelicula>) ois.readObject());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				} else if (file.getAbsolutePath().contains("inventarioCine")) {
					try {
						fis = new FileInputStream(file);
						ois = new ObjectInputStream(fis);
						sucursalCine.setInventarioCine((ArrayList<Producto>) ois.readObject());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}  catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				} else if (file.getAbsolutePath().contains("servicios")) {
					try {
						fis = new FileInputStream(file);
						ois = new ObjectInputStream(fis);
						sucursalCine.setServicios((ArrayList<Servicio>) ois.readObject());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}  catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				} else if (file.getAbsolutePath().contains("bonos")) {
					try {
						fis = new FileInputStream(file);
						ois = new ObjectInputStream(fis);
						sucursalCine.setBonosCreados((ArrayList<Bono>) ois.readObject());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}  catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				} else if (file.getAbsolutePath().contains("tarjetasCinemar")) {
					try {
						fis = new FileInputStream(file);
						ois = new ObjectInputStream(fis);
						sucursalCine.setInventarioTarjetasCinemar((ArrayList<TarjetaCinemar>) ois.readObject());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}  catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				} else if (file.getAbsolutePath().contains("cantidadTicketsCreados")) {
					try {
						fis = new FileInputStream(file);
						ois = new ObjectInputStream(fis);
						sucursalCine.setCantidadTicketsCreados((int) ois.readObject());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}  catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				} 
			}
			
		}
	}
	
	//Creamos la ruta donde se encuentran los archivos .txt que guardan la información de los atributos estáticos
	private static File rutaTemp2 = new File ("src\\baseDatos\\temp\\staticAttributes");
	private static File rutaTempLinux2 = new File ("src/baseDatos/temp/staticAttributes");
	//Desearilzamos la información de los atributos estáticos
	/**
	 * Description: Este método se encarga de deserializar los atributos estáticos de la clase sucursal cine, 
	 * accede al directorio que contiene los atributos estáticos, itera sobre cada *.txt e ingresa la respectiva 
	 * información deserializada a la clase, además, corrige algunos errores en las referencias a objetos 
	 * previamente deserializados.<br>
	 * 
	 * Atributos que deserializa:
	 * <ol>
	 * <li>Clientes.</li>
	 * <li>Juegos.</li>
	 * <li>Fecha nuevo día.</li>
	 * <li>Métodos de pago disponibles.</li>
	 * <li>Fecha lógica de negocio.</li>
	 * <li>Fecha actual.</li>
	 * <li>Tickets Disponibles.</li>
	 * <li>Membresias.</li>
	 * </ol>
	 * */
	@SuppressWarnings("unchecked")
	public static void deserializarEstaticos () {
		//Definimos las variables que usaremos durante el proceso
		File [] docs = rutaTempLinux2.listFiles();
		FileInputStream fis;
		ObjectInputStream ois;

		for (File file : docs) {
			
			if (file.getAbsolutePath().contains("clientes")){
				try{
					fis = new FileInputStream(file);
					ois = new ObjectInputStream(fis);
					SucursalCine.setClientes((ArrayList<Cliente>) ois.readObject());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			} else if (file.getAbsolutePath().contains("juegos")) {
				try {
					fis = new FileInputStream(file);
					ois = new ObjectInputStream(fis);
					SucursalCine.setJuegos((ArrayList<Arkade>) ois.readObject());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			} else if (file.getAbsolutePath().contains("fechaNuevoDia")) {
				try {
					fis = new FileInputStream(file);
					ois = new ObjectInputStream(fis);
					SucursalCine.setFechaValidacionNuevoDiaDeTrabajo((LocalDate) ois.readObject());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			} else if (file.getAbsolutePath().contains("metodosDePagoDisponibles")) {
				try {
					fis = new FileInputStream(file);
					ois = new ObjectInputStream(fis);
					SucursalCine.setMetodosDePagoDisponibles((ArrayList<MetodoPago>) ois.readObject());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			} else if (file.getAbsolutePath().contains("fechaLogicaNegocio")) {
				try {
					fis = new FileInputStream(file);
					ois = new ObjectInputStream(fis);
					SucursalCine.setFechaRevisionLogicaDeNegocio((LocalDate) ois.readObject());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			} else if (file.getAbsolutePath().contains("fechaActual")) {
				try {
					fis = new FileInputStream(file);
					ois = new ObjectInputStream(fis);
					SucursalCine.setFechaActual((LocalDateTime) ois.readObject());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			} else if (file.getAbsolutePath().contains("ticketsDisponibles")) {
				try {
					fis = new FileInputStream(file);
					ois = new ObjectInputStream(fis);
					SucursalCine.setTicketsDisponibles((ArrayList<Ticket>) ois.readObject());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			} else if (file.getAbsolutePath().contains("membresias")) {
				try {
					fis = new FileInputStream(file);
					ois = new ObjectInputStream(fis);
					SucursalCine.setTiposDeMembresia((ArrayList<Membresia>) ois.readObject());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
				
		}
		
	}
	
	/**
	 * Description: Este método se encarga de reasignar las referencias correctamente de los atributos de instancia a cada
	 * sucursal en caso de que estos dependan de alguno de los atributos estáticos, que por orden de deserialización, 
	 * se deserializan antes. Todo esto con el fin de conservar la persistencia de datos.
	 * 
	 * <ol>
	 *	<li>Corrige las referencias en las películas a la sala de cine y sucursal donde será presentado.</li>
	 *	<li>Corrige la referencia en las salas de cine a la sucursal a la que pertenece.</li>
	 *	<li>Corrige la referencia de los prodcutos en inventario a la sucursal a la que pertenecen.</li>
	 *	<li>Corrige la referencia de los bonos en bonos creados al cliente al que pertenecen.</li>
	 *	<li>Limpia los tickets que tiene el cliente para luego asignarlos tras validar que no ha caducado
	 *y corregir sus referencias.</li>
	 *	<li>Corrige las referencias en los tickets a la sala de cine, película, cliente (También lo añade
	 * a su array de tickets) y sucursal, para hacer las validaciones de ingreso correctamente.</li>
	 * 	<li>Asigna a cada sucursal los tickets que pueden recibir algún descuento durante la fecha del programa.</li>
	 *  <li>Corrige las referencias en las membresias para que los clientes se les vuelvan a asignar con los nuevos espacios en memoria</li>
	 * </ol>
	 * 
	 * Motivo de corrección de referencias: Al deserializar cambia la referencia (espacio en memoria) del objeto deserializado
	 * si el objeto fue alterado durante la ejecución del programa (Cambia su estado en memoria) y lo guarda en la sucursaal; sin embargo, 
	 * todos los apuntadores a este objeto (En caso de tenerlos) están apuntando a su estado en memoria anterior, 
	 * si bien encuentran la información relacionada a este objeto (Debido al orden de deserialización), el objeto correctamente deserializado 
	 * se encuntra en un espacio en memoria nuevo y esto genera conflictos al realizar validaciones. 
	 * 
	 * Migrar a un sistema de validaciones basadas en ID's prodría ser una solución más óptima; sin embargo, por cuestiones de tiempo
	 * no podemos implementarlo.
	 * */
	public static void asignarReferenciasDeserializador() {
		ArrayList<Ticket> ticketsAEliminar = new ArrayList<Ticket>();
		
		////////////////////Asignación de referencias atributos de instancia
		
		for (SucursalCine sede : SucursalCine.getSucursalesCine()) {
			
			//Asigna las referencias de la sucursal a la que pertenece y la sala donde será presentada. 
			for (Pelicula pelicula : sede.getCartelera()) {
				pelicula.setSucursalCartelera(sede);
				pelicula.setSalaPresentacion( sede.obtenerSalaCinePorId(pelicula.getSalaPresentacion().getIdSalaCine()) );
				
			}
			
			//Asigna la sucursal donde está ubicada de forma correcta
			for (SalaCine salaDeCine : sede.getSalasDeCine()) {
				//Reasigna la referencia de la sucursal a la que pertenece (Debido a que acabamos de construir su nueva sucursal).
				//Settea como nulos su película y horario para que sean actualizados durante la ejecución del programa.
				salaDeCine.setUbicacionSede(sede);
				salaDeCine.setPeliculaEnPresentacion(null);
				salaDeCine.setHorarioPeliculaEnPresentacion(null);

			}
			
			//Asigna la sucursal a la que pertenece de forma correcta
			for (Producto producto : sede.getInventarioCine()) {
				//Reasigna la referencia de la sucursal a la que pertenece (Debido a que acabamos de construir su nueva sucursal).
				producto.setSucursalSede(sede);
			
			}
			
			//Asigna correctamente a los bonos sus dueños
			for (Bono bono : sede.getBonosCreados()) {
				//Reasigna al bono el cliente de quién es dueño
				bono.setCliente(Cliente.revisarDatosCliente(bono.getCliente().getDocumento()));
			}
				
		}
		
		////////////////////Asignación de referencias de atributos estáticos
		
		for (Cliente cliente : SucursalCine.getClientes()) {
			//Limpia los tickets que tienen, estos serán recuperados luego de verificar si caducaron o no
			cliente.getTickets().clear();
		}
		
		//Se itera sobre las membresias para actualizar los apuntadores a los clientes que han adquirido la membresia.
		for (Membresia membresia : SucursalCine.getTiposDeMembresia()) {
			ArrayList<Cliente> clienteTemp = new ArrayList<>();
			//Se obtiene los nuevos apuntadores para el arreglo de clientes en Membresia
			for (Cliente cliente : membresia.getClientes()) {
				clienteTemp.add(Cliente.revisarDatosCliente(cliente.getDocumento()));
			} membresia.setClientes(clienteTemp);
			//Una vez actualizado el arreglo de clientes, se actualizan los apuntadores de Membresia que tiene cada cliente.
			for (Cliente cliente : membresia.getClientes()) {
				cliente.setMembresia(membresia);
			}

		}
		
		for (Ticket ticket : SucursalCine.getTicketsDisponibles()) {
			//Verificamos si el ticket ha caducado
			if (ticket.getHorario().toLocalDate().isBefore(SucursalCine.getFechaActual().toLocalDate())) {
				ticketsAEliminar.add(ticket);
			} else {
				//Reasigna los atributos cliente, sucursal, película y sala de cine para realizar validaciones de uso correctamente.
				ticket.agregarTicketClienteSerializado();
				ticket.setSucursalCompra( SucursalCine.obtenerSucursalPorId(ticket.getPelicula().getSucursalCartelera().getIdSucursal()) );
				ticket.setPelicula( ticket.getSucursalCompra().obtenerPeliculaPorId( ticket.getPelicula().getIdPelicula() ));
				ticket.setSalaDeCine( ticket.getPelicula().getSalaPresentacion() );
			}
			
		}
		
		////////////////////Optimización luego de deserializar (Esto puede implementarse como un proceso previo a la serialización)
		
		//Eliminamos los tickets caducados de tickets disponibles (optimización)
		for (Ticket ticket : ticketsAEliminar) {
			SucursalCine.getTicketsDisponibles().remove(ticket);
		}
		
		
		////////////////////Asignación de referencias de atributos de instancia a partir de atributos estáticos
		
		for (SucursalCine sede : SucursalCine.getSucursalesCine()) {
			//Borramos los tickets con las referencias antiguas
			sede.getTicketsParaDescuento().clear();
			
			for (Ticket ticket : SucursalCine.getTicketsDisponibles()) {
				//Validamos si la sede es la misma y su horario de presentación es igual o posterior a la hora actual
				if (ticket.getSucursalCompra().equals(sede) && 
					ticket.getHorario().toLocalDate().isEqual(SucursalCine.getFechaActual().toLocalDate())) {
					sede.getTicketsParaDescuento().add(ticket);
				}
			}
		}
	
		
		
		
	}

	
	
}

