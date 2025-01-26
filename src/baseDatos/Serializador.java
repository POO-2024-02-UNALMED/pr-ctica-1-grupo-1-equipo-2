package baseDatos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import gestionAplicacion.SucursalCine;
import gestionAplicacion.proyecciones.SalaCine;
import gestionAplicacion.usuario.Ticket;

public class Serializador {
	// Este atributo es para definir la ruta al directoria temp que contiene las clases.
	private static File rutaTemp = new File ("src\\baseDatos\\temp\\sucursales");
	private static File rutaTempLinux = new File ("src/baseDatos/temp/sucursales");
	
	// Este método es el encargado de serializar las listas que están creadas en la clase SucursalCine.
	
	/**
	 * Description : Este método se encarga de serializar los atributos de instancia de sus
	 * instancias de SucursalCine. Primero, iteramos sobre los directorios de las sucursales,
	 * se borra el contenido de los .txt para evitar redundancia y una vez le damos formato,
	 * extraemos la información de los objetos que se esten ejecutando y se guardan dentro de
	 * los archivos en binarios.
	 * 
	 * Atributos que serializa:
	 * 1. Lugar.
	 * 2. Salas de cine.
	 * 3. Peliculas.
	 * 4. Bonos.
	 * 5. Cantidad de tickets creados.
	 * 6. Inventario.
	 * 7. Servicios.
	 * 8. Tarjetas cinemar.
	 * */
	public static void serializar(SucursalCine sucursalCine) {
		FileOutputStream fos;
		ObjectOutputStream oos;
		File [] dirs = rutaTempLinux.listFiles();
		PrintWriter pw;
		
		//Iteramos sobre los directorios de nuestras distintas sucursales
		for (File dir : dirs) {
			if (dir.getAbsolutePath().contains(sucursalCine.getLugar())) {
				
				File [] docs = dir.listFiles();
				
				// Este método for borra el contenido de los archivos al momento de guardar los objetos para
				// evitar que haya redundancia en los archivos y futuras complicaciones para buscar.
				
				for (File file : docs) {
					try {
						// Al crear este objeto PrintWriter y pasarle como parámetro, la ruta de cada
						// archivo borra lo que haya en ellos automáticamente.
						pw = new PrintWriter(file);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				//Serializa la informacion
				for (File file : docs) {
					//Se revisa la ruta que contenga el archivos con los objetos solicitados.
					if (file.getAbsolutePath().contains("lugar")) {
						try {
							//Se abren los flujos para hacer la escritura en los .txt
							fos = new FileOutputStream(file);
							oos = new ObjectOutputStream(fos);
							oos.writeObject(sucursalCine.getLugar());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else if (file.getAbsolutePath().contains("salasDeCine")) {
						try {
							fos = new FileOutputStream(file);
							oos = new ObjectOutputStream(fos);
							oos.writeObject(sucursalCine.getSalasDeCine());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else if (file.getAbsolutePath().contains("inventarioCine")) {
						try {
							fos = new FileOutputStream(file);
							oos = new ObjectOutputStream(fos);
							oos.writeObject(sucursalCine.getInventarioCine());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else if (file.getAbsolutePath().contains("tpeliculas")) {
						try {
							fos = new FileOutputStream(file);
							oos = new ObjectOutputStream(fos);
							oos.writeObject(sucursalCine.getCartelera());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else if (file.getAbsolutePath().contains("servicios")) {
						try {
							fos = new FileOutputStream(file);
							oos = new ObjectOutputStream(fos);
							oos.writeObject(sucursalCine.getServicios());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else if (file.getAbsolutePath().contains("bonos")) {
						try {
							fos = new FileOutputStream(file);
							oos = new ObjectOutputStream(fos);
							oos.writeObject(sucursalCine.getBonosCreados());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else if (file.getAbsolutePath().contains("tarjetasCinemar")) {
						try {
							fos = new FileOutputStream(file);
							oos = new ObjectOutputStream(fos);
							oos.writeObject(sucursalCine.getInventarioTarjetasCinemar());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else if (file.getAbsolutePath().contains("cantidadTicketsCreados")) {
						try {
							fos = new FileOutputStream(file);
							oos = new ObjectOutputStream(fos);
							oos.writeObject(sucursalCine.getCantidadTicketsCreados());
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				}
			}
		}
	}
	//Creamos la ruta donde se encuentran los archivos .txt que guardan la información de los atributos estáticos
	private static File rutaTemp2 = new File("src\\baseDatos\\temp\\staticAttributes");
	private static File rutaTempLinux2 = new File ("src/baseDatos/temp/staticAttributes");
	
	/**
	 * Description: Este método se encarga de serializar los atributos estáticos de la clase SucursalCine.
	 * Se realiza el mismo proceso que con los objetos de instancia, pero como son atributos de clase,
	 * estos son compartidos por cada sucursal, por lo que solo es necesario hacerlo una vez en un método
	 * aparte.
	 * 
	 * Atributos que serializa:
	 * 1. Clientes (Mantiene el registro de los clientes que han usado la sucursal).
	 * 2. Juegos.
	 * 3. Fecha nuevo día (Para ejecutar la lógica diaria del negocio).
	 * 4. Métodos de pago disponibles
	 * 5. Fecha lógica de negocio (Para ejecutar la lógica semanal del negocio).
	 * 6. Fecha actual (Mantiene la hora en la cual se está ejecutando el programa).
	 * 7. Tickets Disponibles (Renueva las referencias de sus atributos para hacer correctamente las validaciones).
	 * 8. Membresias.
	 * */
	public static void serializar() {
		
		//Creamos las variables que usaremos para este proceso
		File [] docs = rutaTempLinux2.listFiles();
		// Al crear este objeto PrintWriter y pasarle como parámetro, la ruta de cada
		// archivo borra lo que haya en ellos automáticamente.
		FileOutputStream fos;
		ObjectOutputStream oos;
		PrintWriter pw;
		
		//Limpiamos la información que había anteriormente en todos los txt's
		for (File file : docs) {
			try {
				pw = new PrintWriter(file);
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		//Serializamos la información
		for (File file : docs) {
			
			if (file.getAbsolutePath().contains("clientes")) {
				try {
					fos = new FileOutputStream(file);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(SucursalCine.getClientes());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else if (file.getAbsolutePath().contains("juegos")) {
				try {
					fos = new FileOutputStream(file);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(SucursalCine.getJuegos());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else if (file.getAbsolutePath().contains("fechaNuevoDia")) {
				try {
					fos = new FileOutputStream(file);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(SucursalCine.getFechaValidacionNuevoDiaDeTrabajo());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else if (file.getAbsolutePath().contains("metodosDePagoDisponibles")) {
				try {
					fos = new FileOutputStream(file);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(SucursalCine.getMetodosDePagoDisponibles());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (file.getAbsolutePath().contains("fechaLogicaNegocio")) {
				try {
					fos = new FileOutputStream(file);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(SucursalCine.getFechaRevisionLogicaDeNegocio());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else if (file.getAbsolutePath().contains("fechaActual")) {
				try {
					fos = new FileOutputStream(file);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(SucursalCine.getFechaActual());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (file.getAbsolutePath().contains("ticketsDisponibles")) {
				try {
					fos = new FileOutputStream(file);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(SucursalCine.getTicketsDisponibles());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else if (file.getAbsolutePath().contains("membresias")) {
				try {
					fos = new FileOutputStream(file);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(SucursalCine.getTiposDeMembresia());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}

	
}

