package iuMain;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import gestionAplicacion.SucursalCine;
import gestionAplicacion.proyecciones.*;
import gestionAplicacion.servicios.*;
import gestionAplicacion.servicios.herencia.Servicio;
import gestionAplicacion.usuario.*;
import baseDatos.Deserializador;
import baseDatos.Serializador;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Administrador {
	
	static Scanner sc = new Scanner(System.in);
	
	static long readLong() {return sc.nextLong();}
	
	static String readLn () {
		sc.nextLine();
		return sc.nextLine();
	}
	
	public static void main(String[] args) {
		
		System.out.println("CINEMAR\n");
		
		//MAIN
		inicioDelSistema();
		
		System.out.println("Iniciar sesión");
		Cliente clienteProceso = iniciarSesion();

		System.out.println("\nIngresar a una de nuestras sedes");
		clienteProceso.setCineActual(ingresarASucursal());
		
		System.out.println("\nHola " + clienteProceso.getNombre() + " Bienvenido a Cinemar");
		inicio(clienteProceso);

		salirDelSistema();
		
	}
	      
	/**
	 * Description : Este método se encarga de iniciar el programa mostrando las opciones de las funcionalidades en
	 * un menú.
	 * @param clienteProceso : Se usa a un objeto de tipo Cliente para que pueda ser usado en las funcionalidades
	 */
	private static void inicio(Cliente clienteProceso) {
		int opcion = 0;
		
		//Método de avanzar días
		avanzarDia(clienteProceso);
		
		//Avance de tiempo, se ejecuta cada vez que regresamos al menú inicial
		avanzarTiempo();
		
		//Cada vez que se va al inicio, se verifica si ya ha pasado el dia para revisar la validez de la membresia.
		logicaMembresia(clienteProceso);
		
		do {
			
			try {
				
				opcion = 0;
				System.out.println("\n¿Qué operacion desea realizar?");
				System.out.println("1. Ingresar a sistema de proyecciones de películas");
				System.out.println("2. Ingresar a los servicios de compra"); 
				System.out.println("3. Hacer calificación");
				System.out.println("4. Ingresar a la zona de juegos");
				System.out.println("5. Adquirir o actualizar membresia");
				System.out.println("6. Guardar y Salir");
				opcion = Integer.parseInt(sc.nextLine());
				
			}catch(NumberFormatException e) {
				System.out.println("Error, debe ingresar un único dato numérico entre los disponibles");
			}
			
		}while(!(opcion > 0 & opcion <= 7));
		
		
		switch (opcion) {
			case 1: ingresarASistemaDeProyecciones(clienteProceso); break;
			case 2: compras(clienteProceso); inicio(clienteProceso); break;
			case 3: calificacion(clienteProceso);inicio(clienteProceso); break;
			case 4: ingresoZonaJuegos(clienteProceso); inicio(clienteProceso); break;
			case 5: adquirirMembresia(clienteProceso); inicio(clienteProceso); break;
			case 6: salirDelSistema(); break;
			default: System.out.println("Opción invalida"); inicio(clienteProceso);
		  }
	
	}
	
	/**
	 * Description : Este método se encarga de ejecutar la lógica de aranque del programa, deserializa toda la información
	 * primero deserializa los atributos de instancia y luego deserializa los atributos estáticos, y corrige la referencia
	 * a la que apuntan los objetos deserializados, para que las funcionalidades que realizan validaciones a partir de 
	 * referencias puedan realizarlas de forma segura.
	 * */
	private static void inicioDelSistema() {
		
		//Deserializa
		Deserializador.deserializar();
		Deserializador.deserializarEstaticos();
		//Renueva referencias a los objetos deserializados
		Deserializador.asignarReferenciasDeserializador();
		
		
	}
	
	/**
	 * Description: Este método se encarga de serializar toda la información del programa y salir del sistema.
	 * */
	private static void salirDelSistema() {
		//Serialización
		
		//Atributos estáticos
		Serializador.serializar();
		
		//Atributos de instancia
		for (SucursalCine sede : SucursalCine.getSucursalesCine()) {
			Serializador.serializar(sede);
		}
		
		//Fin del programa
		System.out.println("¡Adios, vuelva pronto!");
		System.exit(0);
		
	}
	
	/**
	 * @Override
	 * Description : Este método se encarga de avanzar la hora y ejecutar la lógica de negocio en 3 plazos:
	 * 
	 * 1. Durante la jornada laboral: Actualiza las salas de cine, ubicando las películas en presentación en sus respectivas salas.
	 * 
	 * 2. Diariamente: Mejorar documentación
	 * (Limpia el array de tickets generados, con el fin de tener únicamente aquellos tickets que pueden usarse para generar descuentos
	 * y verifica la fecha de expedición de las memebresías de cada uno de los clientes).
	 * 
	 * 3. Semanalmente: Mejorar documentación
	 * (Cambia las películas de sucursal según su rendimiento, distribuye de nuevo las películas en sus salas de cine y crea los horarios de presentación
	 * semanal).
	 * 
	 * */
	private static void avanzarTiempo() {
		
		//Avanza lo hora 20 segundos
		SucursalCine.setFechaActual(SucursalCine.getFechaActual().plusSeconds(20)); 
		relojDigital(SucursalCine.getFechaActual());
		
		//Esta como after o equal debido a que en caso de serializar y desearilizar un día o más después podamos ejecutar esta lógica
		if(!SucursalCine.getFechaActual().toLocalDate().isBefore(SucursalCine.getFechaRevisionLogicaDeNegocio())) {
			//Lógica a evaluar cada semana
			
			SucursalCine.setFechaRevisionLogicaDeNegocio(SucursalCine.getFechaActual().toLocalDate().plusWeeks(1)); 
			
			//Ejecutamos la lógica semanal
			SucursalCine.logicaSemanalSistemaNegocio();
			
		}
			
		//Esta como after o equal debido a que en caso de serializar y desearilizar un día o más después podamos ejecutar esta lógica
		if (!SucursalCine.getFechaActual().toLocalDate().isBefore(SucursalCine.getFechaValidacionNuevoDiaDeTrabajo())) {
			//Lógica a evaluar cada día
			
			SucursalCine.setFechaValidacionNuevoDiaDeTrabajo(SucursalCine.getFechaActual().toLocalDate().plusDays(1));
			
			SucursalCine.logicaDiariaReservarTicket();		
		}
		
		if (SucursalCine.getFechaActual().toLocalTime().isBefore(SucursalCine.getFinHorarioLaboral()) 
				&& SucursalCine.getFechaActual().toLocalTime().isAfter(SucursalCine.getInicioHorarioLaboral()) ) {
			//Lógica durante la jornada laboral
			SucursalCine.actualizarPeliculasSalasDeCine();
			
		}
		
	}
	/**
	 * Description : Este método se encarga de ejecutar el proceso de revisar y notificar la validez de la membresía de cliente
	 * con respecto a su fecha de caducidad. Si la membresia esta por expirar en 5 días o menos, se arroja un mensaje en pantalla.
	 * @param clienteProceso : Se pide un objeto de tipo Cliente para obtener los datos necesarios en la ejecución de la lógica.
	 */
	private static void logicaMembresia(Cliente clienteProceso) {
		System.out.println(SucursalCine.notificarFechaLimiteMembresia(clienteProceso));
	}
	
	/**
	 * Description : Este método se encarga de avanzar de día en el programa cuando no hayan horarios.
	 * En caso de que falte un día para cumplir la semana laboral, se avanzará de día automáticamente.
	 * @param clienteProceso : Se pide un cliente para poder acceder a la sucursal de Cine
	 * y poder evaluar los horarios de sus salas de Cine.
	 */
	private static void avanzarDia(Cliente clienteProceso) {
		
		//Se crean variables para obtener la sucursal actual y una booleano que indica si hay horarios.
		SucursalCine sucursalActual = clienteProceso.getCineActual();
		Boolean hayHorarios = false;
		
		//Se revisa todas las salas de cine para ver si tienes horarios. De no ser el caso, se cambia el booleano a false.
		for (SalaCine salaCine : sucursalActual.getSalasDeCine()) {
			
			if (salaCine.tieneHorariosPresentacionHoy()) {
				hayHorarios = true;
				break;
			}
		}
		//El avance de dia se realiza automáticamente en caso de que no hayan horarios y falte 1 día para cumplir la semana de trabajo.
		if (!hayHorarios && SucursalCine.getFechaRevisionLogicaDeNegocio().minusDays(1).equals(SucursalCine.getFechaActual().toLocalDate())) {
			System.out.println("Hemos detectado que han concluido todas las presentaciones semanales, por lo tanto,\n"
					+ "Se ejecutará la lógica semanal del sistema de negocio y se pasará al dia siguiente de forma automática.\n"
					+ "Gracias por su compresión\n");
			SucursalCine.setFechaActual(SucursalCine.getFechaActual().plusDays(1).withHour(SucursalCine.getInicioHorarioLaboral().getHour()).withMinute(SucursalCine.getInicioHorarioLaboral().getMinute()).withSecond(0).withNano(0));
			
		//El avance de día se preguntará al usuario cuando ya no haya más peliculas por presentar.	
		} else if (!hayHorarios) {
			int opcionMenu = 0;
			try {
			System.out.println("Ya no hay más presentaciones de películas el día de hoy. ¿Desea avanzar al siguiente dia?\n1. Si.\n2. No.");
			opcionMenu = Integer.parseInt(sc.nextLine());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} switch (opcionMenu) {
				case 1: SucursalCine.setFechaActual(SucursalCine.getFechaActual().plusDays(1).withHour(SucursalCine.getInicioHorarioLaboral().getHour()).withMinute(SucursalCine.getInicioHorarioLaboral().getMinute()).withSecond(0).withNano(0)); break;
				case 2: break;
			}
		}
	}

	/**
	 * Description : Este método se encarga de iniciar sesión, para esto se le pregunta al cliente el tipo de documento y el número de documento,
	 * en caso de que se encuentre registrado, se verfica su nombre y se retorna ese cliente, en caso de que no, se crea un nuevo cliente, solicitando
	 * su nombre y edad, luego se asignan sus métodos de pago y se retorna este nuevo cliente.
	 * @return <b>Cliente</b> : Este método retorna el cliente que realizó este proceso de forma exitosa.
	 * */
	private static Cliente iniciarSesion() {
		//Pedimos el tipo de documento al usuario
		TipoDeDocumento documentoCliente = null;
		boolean casoValidoConfirmacion = false;
		boolean casoValido = false;
		int opcionMenu;
		do{
			opcionMenu = 0;
			try {
				System.out.println("Seleccione el tipo de documento:\n"+ TipoDeDocumento.mostrarTiposDeDocumento());
				opcionMenu = Integer.parseInt(sc.nextLine());
			}catch(NumberFormatException e){
				System.out.println("Error, debes ingresar un dato numérico");
				continue;
			}
				
			switch (opcionMenu) {
				case 1: documentoCliente = TipoDeDocumento.CC; casoValido=true; break;
				case 2: documentoCliente = TipoDeDocumento.TI; casoValido=true; break;
				case 3: documentoCliente = TipoDeDocumento.CE; casoValido=true; break;
				default: System.out.println("Opción invalida");
			}
				
		}while(!casoValido);	
				
		//Obtenemos al cliente que hará el proceso de interactuar con las funcionalidades
		Cliente clienteProceso = null;
		
		//Se pide al usuario su número de documento
		long numeroDocumentoCliente = 0;
		casoValido = false;
		casoValidoConfirmacion = false;
		do {
			do {
				try {
					System.out.print("\nIngrese el número de documento: ");
					numeroDocumentoCliente = Long.parseLong(sc.nextLine());
				}catch(NumberFormatException e) {
					System.out.println("Error, debes ingresar datos numéricos correspondientes a tu número de documento");
					continue;
				}
				
				//Confirmamos si es un dato correcto
				do {
					opcionMenu = 0;
					try {
						System.out.println("Tu número de documento es: " + numeroDocumentoCliente + " \n1. Correcto \n2. Cambiar número de documento");
						opcionMenu = Integer.parseInt(sc.nextLine());
					}catch(NumberFormatException e) {
						System.out.println("Error, debes ingresar un único dato numérico entre los disponibles");
					}
				}while(!(opcionMenu == 1 || opcionMenu == 2));
				
				switch(opcionMenu) {
					case 1: casoValidoConfirmacion = true; break;
					case 2: casoValidoConfirmacion = false; break;
					default: casoValidoConfirmacion = false; System.out.println("Opción invalida");
				}
				
				}while(!casoValidoConfirmacion);
				
				//Se verficia si el cliente existe
				clienteProceso = Cliente.revisarDatosCliente(numeroDocumentoCliente);
				
				//En caso de que no exista, lo creamos
				if (clienteProceso == null) {
					System.out.println("Hemos detectado que es la primera vez que visita nuestro cine, " +
					"Por políticas de seguridad de nuestra compañia," + 
					"le solicitamos que amablemente responda las siguientes preguntas para completar su registro");
					
					//Pedimos la edad del cliente
					int edadCliente = 0;
					casoValidoConfirmacion = false;
					do{
						try {
							System.out.println("( Edad mínima para hacer uso de nuestras instalaciones: 5 )" + "\n"
							+ "( Edad máxima para hacer uso de nuestras instalaciones : 100 )");
							System.out.print("Ingrese su edad: ");
							edadCliente = Integer.parseInt(sc.nextLine());
						}catch (NumberFormatException e) {
							System.out.println("Error, debes ingresar datos numéricos correspondientes a tu edad");
							continue;
						}
						
						//Verificamos si la edad seleccionada por el cliente es acorde a su número de documento
						if ( (documentoCliente.equals(TipoDeDocumento.CC) && edadCliente < 18) || 
						   ( (documentoCliente.equals(TipoDeDocumento.TI) && (edadCliente > 18 || edadCliente < 5) ) ) ||
						   ( (documentoCliente.equals(TipoDeDocumento.CE) && edadCliente < 5) ) ||
						   ( (edadCliente > 100) ) ){
							System.out.println("Error, debes ingresar una edad válida o una apropiada para un documento tipo: " + documentoCliente.getNombre());
							continue;
						}
						//Confirmamos si la edad ingresada es correcta
						do {
							opcionMenu = 0;
							try {
								System.out.println("Tu edad es: " + edadCliente + " \n 1. Correcto \n 2. Cambiar edad");
								opcionMenu = Integer.parseInt(sc.nextLine());
							}catch(NumberFormatException e) {
								System.out.println("Error, debes ingresar un único dato numérico");
							}
							
						}while(!(opcionMenu == 1 || opcionMenu == 2));
						
						switch(opcionMenu) {
							case 1: casoValidoConfirmacion = true; break;
							case 2: casoValidoConfirmacion = false; break;
							default: casoValidoConfirmacion = false; System.out.println("Opción invalida");
						}
					}while(!casoValidoConfirmacion);
					
					//Pedimos el nombre del cliente
					String nombreCliente = null;
					casoValido = false;
					do {
						System.out.println("Ingrese su nombre: ");
						nombreCliente = stringMayuscula(sc.nextLine()); 
						
						//Confirmamos si el nombre ingresado es correcto
						do {
							opcionMenu = 0;
							try {
								System.out.println("Su nombre es: " + nombreCliente + "\n1. Correcto \n2. Cambiar nombre");
								opcionMenu = Integer.parseInt(sc.nextLine());
							}catch(NumberFormatException e) {
								System.out.println("Error, debe ingresar un único dato numérico");
							}
						}while(!(opcionMenu == 1 || opcionMenu == 2));
						
						switch(opcionMenu) {
							case 1: casoValidoConfirmacion = true; break;
							case 2: casoValidoConfirmacion = false; break;
							default: casoValidoConfirmacion = false; System.out.println("Opción invalida");
						}
						
					}while(!casoValidoConfirmacion);
					
					//Creamos un nuevo cliente con la información dada
					clienteProceso = new Cliente(nombreCliente,edadCliente,numeroDocumentoCliente,documentoCliente);
					clienteProceso.setMetodosDePago(MetodoPago.asignarMetodosDePago(clienteProceso));
					casoValido = true;
					System.out.println("\nEstos son sus datos de registro: " 
					+ "\nNombre: " + clienteProceso.getNombre() 
					+ "\nIdentificacion: "+ clienteProceso.getDocumento() 
					+ "\nEdad: " + clienteProceso.getEdad() + "\n");
				}
				//En caso de que el cliente exista
				else {
					do {
						opcionMenu = 0;
						try {
							System.out.println("¿Eres " + clienteProceso.getNombre() + "?\n1. SI\n2. NO");
							opcionMenu = Integer.parseInt(sc.nextLine());
						}catch(NumberFormatException e) {
							System.out.println("Error, debes ingresar un único dato numérico");
						}
						
						switch(opcionMenu) {
							case 1: 
								System.out.println("\nEstos son sus datos de registro: " + 
								"\nNombre: " + clienteProceso.getNombre() 
								+ "\nIdentificacion: "+ clienteProceso.getDocumento() 
								+ "\nEdad: " + clienteProceso.getEdad() + "\n");
								casoValido = true;
								casoValidoConfirmacion = true;
								break;
							case 2:
								System.out.println("Verifica el numero de documento\n");
								casoValidoConfirmacion = true;
								casoValido = false;
								break;
							default: 
								System.out.println("Digite una opción valida"); 
								casoValidoConfirmacion = false;
						}
					}while(!casoValidoConfirmacion);
				}
		}while(!casoValido);	
	
		return clienteProceso;
		
	}
	
	/**
	 * Description: Este metodo se encarga de convertir las primeras letras de las palabras de un String en mayuscula
	 * @param input : Es el string que el usuario proporciona
	 * @return String : retorna el String convertido.
	 */
	private static String stringMayuscula(String input) {
        StringBuilder result = new StringBuilder();
        String[] words = input.split("\\s+");
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
	
	/**
	 * Description : Este método se encarga de realizar el proceso de selección de alguna de nuestras dependencias según los datos 
	 * ingresados por el cliente, para esto, mostramos en pantalla nuestras distintas sedes, el cliente elige una de estas y una vez confirmemos
	 * su elección, retornamos la sucursal de nuestra cine a la cual desea acceder.
	 * @return <b>SucursalCine</b> : Este método se encarga de retornar la sucursal de nuestro cine (De tipo SucursalCine) a la cuál el cliente
	 * intenta acceder, con el fin de que el proceso de las funcionalidades ocurra en el contexto de alguna de nuestras sucursales.
	 * */
	private static SucursalCine ingresarASucursal() {
		
		boolean casoValido = false;
		int opcionMenu = 0;
		
		SucursalCine sucursalCineProceso = null;
		
		do {
			do {
				opcionMenu = 0;
				try {
					System.out.println("A continuación se presentará la ubicación de nuestras distintas sedes\n" + 
				    "Por favor elige a cuál de las siguientes deseas acceder: " + SucursalCine.mostrarSucursalCine());
					opcionMenu = Integer.parseInt(sc.nextLine());
				}catch(NumberFormatException e) {
					System.out.println("Error, debes ingresar un único dato numérico entre los disponibles");
				}
			}while(!(opcionMenu > 0 & opcionMenu < Integer.valueOf(SucursalCine.getSucursalesCine().size()) + 1 ) );
			
			sucursalCineProceso = SucursalCine.getSucursalesCine().get(opcionMenu - 1);
			
			do {
				opcionMenu = 0;
				try {
					System.out.println("Usted ha seleccionado nuestra sede ubicada en " + sucursalCineProceso.getLugar() +
					" ¿Es esto correcto?\n1. Correcto\n2. Cambiar sucursal");
					opcionMenu = Integer.parseInt(sc.nextLine());
				}catch(NumberFormatException e) {
					System.out.println("Error, debes ingresar un único dato numérico entre los disponibles");
				}
			}while(!(opcionMenu == 1 || opcionMenu == 2));
			
			casoValido = (opcionMenu == 1) ? true : false;
			
		}while(!casoValido);
			
		return sucursalCineProceso;
	}

	/**
	 * Description : Este método se encarga de cambiar la sucursal en la cual se encuentra el cliente
	 * @param clienteProceso : Este método recibe al cliente (De tipo cliente) que desea cambiar de sucursal
	 * */
	private static void cambiarSucursalCine(Cliente clienteProceso) {
		
		System.out.println("\n==============================");
		System.out.println("Sistema de cambio de sucursal");
		
		int opcionMenu;
		do {
			opcionMenu = 0;
			
			try {
				System.out.println("\n¿Desea cambiar de " + clienteProceso.getCineActual().getLugar() + " a otra de nuestras sucursales? \n1. Si\n2. No");
				opcionMenu = Integer.parseInt(sc.nextLine());
			}catch(NumberFormatException e) {
				System.out.println("Error, debe ingresar un único dato numérico entre las opciones disponibles");
			}
			
		}while(!(opcionMenu == 1 || opcionMenu == 2));
		
		if (opcionMenu == 1) {
			clienteProceso.setCineActual(ingresarASucursal());
		}else {
			System.out.println("\nRegresando al menú principal...");
		}
	}
	
//--------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//Bloque funcionalidad 1
	
	
	// _____  _   _   _   _    _____   _    ____    _   _       __       _       _   _____       __       _____  	    __
	//|  __| | | | | | \ | |  / ____| | |  / __ \  | \ | |     /  \     | |     | | |  __ \     /  \     |  __ \ 	   /  |
	//| |__  | | | | |  \| | | |	  | | | |  | | |  \| |    /    \    | |     | | | |  | |   /    \    | |  | |	  /   |
	//|  __| | | | | |	  \| | |      | | | |  | | |    \|   /  __  \   | |     | | | |  | |  /  __  \   | |  | |	  \/| |
	//| |    | |_| | | |\  | | \____  | | | |__| | | |\  |  / ______ \  | |___  | | | |__| | / ______ \  | |__| |	   _| |_
	//|_|     \___/  |_| \_|  \_____| |_|  \____/  |_| \_| /_/      \_\ |_____| |_| |_____/ /_/      \_\ |_____/ 	  |_____| 
	
	/**
	 * Description: Este método se encarga mostrar en pantalla los procesos de la funcionalidad 1, para que el cliente elija uno de ellos, 
	 * una vez termine su interacción, el cliente regresará a este mismo menú, en caso de que quiera regresar al menú principal termina el
	 * ciclo y se ejecuta el menú inicial.
	 * @param clienteProceso : Este método recibe como parámetro el cliente (De tipo cliente) que realizará algún proceso
	 * del sistema de proyeciones.
	 * */
	private static void ingresarASistemaDeProyecciones(Cliente clienteProceso) {
		
		int opcionMenu;
		boolean opcionValida = false;
		do {
			opcionMenu = 0;
			try {
				System.out.println("\nBienvenido al sistema de proyecciones de películas\n"
						+ "Estos son nuestros servicios disponibles:\n"
						+ "1. Reservar ticket de película\n"
						+ "2. Ingresar a sala de cine\n"
						+ "3. Ingresar a sala de espera\n"
						+ "4. Volver al menú principal\n");
				System.out.print("Elige una de las opciones disponibles para continuar con el proceso: ");
				opcionMenu = Integer.parseInt(sc.nextLine());
			}catch(NumberFormatException e) {
				System.out.println("Error, debes ingresar un único dato númerico entre los disponibilidad");
				continue;
			}
			
			switch(opcionMenu) {
				case 1: reservarTicket(clienteProceso); break;
				case 2: ingresarSalaCine(clienteProceso); break;
				case 3: ingresarSalaDeEspera(clienteProceso); break;
				case 4: System.out.println("\nRegresando al menú principal..."); opcionValida = true; break;
				default: System.out.println("Digite una única opción entre las disponibles"); 
			}
		}while(!opcionValida);
		
		Administrador.inicio(clienteProceso);
		
	}
	
	/**
	 * Description: Este método se encarga de realizar el proceso de reserva de ticket de la funcionalidad 1.
	 * Para llevar su cometido, se muestran las películas en cartelera de la franquicia a la que accedió previamente, 
	 * el cliente selecciona una de estas, luego se busca si la película seleccionada se encuentra en presentación en alguna de las salas de cine 
	 * de la sucursal y no lleve más de 15 minutos en presentación:
	 * <ol>
	 * <li>En caso de que sí, se le pregunta al cliente si quiere comprar la película en ese horario, dada una respuesta positiva, con la sala de cine
	 * previamente encontrada realizamos el proceso de reserva del ticket (Mostramos los asientos de la sala de cine, 
	 * le pedimos al cliente que seleccione el asiento, se valida su disponibilidad, se realiza el proceso de pago y se asigna el ticket al cliente)
	 * (Para este proceso cuenta con un tiempo límite de 20 minutos).</li>
	 * 
	 * <li>En caso de que haya decidido no comprar en ese horario o directamente la película no estaba en presentación, mostramos los horarios 
	 * de la película, el usuario selecciona uno de ellos y realizamos el proceso de reserva del ticket (Mostramos los asientos de la sala de 
	 * cine virtual asociada al horario previamente seleccionado, el cliente selecciona el asiento deseado, se valida su disponibilidad, 
	 * se realiza el proceso de pago y se asigna el ticket al cliente).</li>
	 * </ol>
	 * @param clienteProceso : Este método recibe como parámetro el cliente (De tipo cliente) que desea realizar la reserva de un ticket.
	 * */
	private static void reservarTicket(Cliente clienteProceso) {
		
		boolean finalizarProcesoReservaTicket = false;
		do {
			
			System.out.println("\nSistema de Reserva de ticket para película");
			
			//Elección menu inicial
			boolean casoValidoIniciarFuncionalidad = false;
			int opcionMenu = 0;
			do {			
				try {
					System.out.println("¿Desea ingresar o volver?" +"\n1. Ingresar" + "\n2. Volver al menú anterior");
					opcionMenu = Integer.parseInt(sc.nextLine());
				}catch(NumberFormatException e) {
					System.out.println("Error, debes ingresar un dato numérico");
					continue;
				}
				
				switch (opcionMenu) {
					case 1: casoValidoIniciarFuncionalidad = true; break;
					case 2: casoValidoIniciarFuncionalidad = true; break;
					default: System.out.println("Opción invalida");
				}
				
			}while(!casoValidoIniciarFuncionalidad);
			
			//En caso de que el cliente elija volver al menú principal
			if (opcionMenu == 2) {
				break;
			}
			
			//Funcionalidad reserva de ticket
			
			//Avance de tiempo para ejecutar los filtros de películas correctamente
			avanzarTiempo();
			
			//Mostramos una cartelera personalizada de acuerdo a la edad del cliente, si la película tiene horarios disponibles o se encuentra en presentación
			ArrayList<Pelicula> carteleraPersonalizadaProceso = Pelicula.filtrarCarteleraPorCliente(clienteProceso, clienteProceso.getCineActual());
			
			//Verificamos si el cliente tiene acceso para al menos una película
			if (carteleraPersonalizadaProceso.size() == 0) {
				System.out.println("No hay películas disponibles para reservar (Redireccionando al menú principal...)\n");
				SucursalCine.setFechaActual(SucursalCine.getFechaActual().withHour(SucursalCine.getFinHorarioLaboral().getHour()).withMinute(0));
				avanzarDia(clienteProceso);
				break;
			}
			
			//Tomamos los nombres de las películas para mostrarlos en pantalla
			ArrayList<String> nombresPeliculasCarteleraPersonalizadaProceso = Pelicula.filtrarNombrePeliculas(carteleraPersonalizadaProceso);
			
			//Tomamos los nombres de las películas cuyo género coincide con el género más visto por el cliente con el fin de realizar
			//el proceso de recomendación de películas en caso de que el cliente tenga membresía.
			ArrayList<String> peliculasRecomendadas = Pelicula.filtrarPorGenero(carteleraPersonalizadaProceso, clienteProceso.generoMasVisto());
			
			//Seleccionamos una película
			boolean volverAlMenu = false;
			boolean casoValidoSeleccionPelicula = false;
			boolean casoValido = false;
			boolean casoValidoConfirmacion = false;
			Pelicula peliculaProceso = null;
			String nombrePelicula = null;
			
			do {
				//Mostramos los nombres de las películas en cartelera y le pedimos al usuario elegir una de estas
				do {
					
					opcionMenu = 0;
					System.out.println("\nHola " + clienteProceso.getNombre() + ", bienvenido al sistema de reserva de ticket\n"
					+ "================================================================\n"
					+ "Este es el listado de los nombres de las películas en cartelera:"
					+ Pelicula.showNombrePeliculas(nombresPeliculasCarteleraPersonalizadaProceso, clienteProceso, peliculasRecomendadas) + "\n"
					+ ( Integer.valueOf(nombresPeliculasCarteleraPersonalizadaProceso.size()) + 1 ) + ". Regresar al menú de sistema de proyecciones");
					
					try {
						System.out.print("\nElige una de las películas disponibles para ver más información: " );
						opcionMenu = Integer.parseInt(sc.nextLine());
					}catch (NumberFormatException e) {
						System.out.println("\nError, debes ingresar un único dato numérico");
					}
					
					if ((opcionMenu > 0 && opcionMenu <= nombresPeliculasCarteleraPersonalizadaProceso.size())) {
						//Obtenemos el nombre de la película seleccionada por el cliente
						nombrePelicula = nombresPeliculasCarteleraPersonalizadaProceso.get(opcionMenu - 1);
						casoValido = true;
					}else if (opcionMenu == Integer.valueOf(nombresPeliculasCarteleraPersonalizadaProceso.size()) + 1) {
						//Volvemos al menú del sistema de proyecciones
						casoValido = true;
						volverAlMenu = true;
					}else {
						System.out.println("\nOpción inválida");
					}
					
				}while(!casoValido);
				
				//Cerramos el bucle de la lógica de selección de película
				if (volverAlMenu) {
					break;
				}
				
				//Buscamos las películas que coinciden con el nombre de película seleccionado con el cliente
				ArrayList<Pelicula> peliculasProceso = Pelicula.filtrarPorNombreDePelicula(nombrePelicula, carteleraPersonalizadaProceso);
				
				//Mostramos información del nombre de la película seleccionada
				System.out.println("\nInformación película seleccionada -> \nNombre: " + peliculasProceso.get(0).getNombre() 
				+ "; Género: " + peliculasProceso.get(0).getGenero()
				+ ", Duración: " + peliculasProceso.get(0).getDuracion().toMinutes() + " Minutos.\n" 
				+"\n========================================================================================");
				
				//Mostramos en pantalla los formatos del nombre de la película seleccionada
				casoValido = false;
				boolean casoVolverASeleccionarPelicula = false;
				do {
					
					opcionMenu = 0;
					System.out.println("\nEste es el listado de los formatos de la película:\n" 
					+ Pelicula.showTiposFormatoPeliculaSeleccionada(peliculasProceso)+ "\n"
					+ ( Integer.valueOf(peliculasProceso.size()) + 1 ) + ". Seleccionar otra película");
							
					try {
						System.out.print("\nElige uno de los formatos disponibles: " );
						opcionMenu = Integer.parseInt(sc.nextLine());
					}catch (NumberFormatException e) {
						System.out.println("\nError, debes ingresar un único dato numérico");
					}
					
					if ( opcionMenu > 0 && opcionMenu <= Integer.valueOf(peliculasProceso.size()) ) {
						//Seleccionamos la película con el formato seleccionado por el cliente
						peliculaProceso = peliculasProceso.get(opcionMenu - 1);
						casoValido = true;
					}else if (opcionMenu == Integer.valueOf(peliculasProceso.size()) + 1) {
						//Volver al menú anterior
						casoVolverASeleccionarPelicula = true;
						casoValido = true;
					}else {
						System.out.println("\nOpción invalida");
					}
					
				}while( !casoValido );
				
				//En caso de que el cliente haya elegido regresar y cambiar película
				if ( casoVolverASeleccionarPelicula ) {
					continue;
				}
				
				//Confirmamos la elección del cliente
				do {
					
					opcionMenu = 0;
					try {
						System.out.println("\nHas elegido la película " + peliculaProceso.getNombre() 
						+ " en formato " + peliculaProceso.getTipoDeFormato()
						+ "\n1. Correcto \n2. Cambiar Pelicula");
						opcionMenu = Integer.parseInt(sc.nextLine());
					}catch(NumberFormatException e) {
						System.out.println("Error, debes ingresar un único dato numérico");
					}
					
					switch(opcionMenu) {
						case 1: casoValidoSeleccionPelicula = true; casoValidoConfirmacion = true; break;
						case 2: casoValidoSeleccionPelicula = false; casoValidoConfirmacion = true; break;
						default : System.out.println("Opción Invalida"); casoValidoConfirmacion = false; 
					}
					
				}while(!casoValidoConfirmacion);
					
			}while (!casoValidoSeleccionPelicula);
			
			//Rompemos la lógica de reserva de ticket para regresar al menú principal, en caso de que el cliente lo haya solicitado en el proceso de
			// seleccionar la película
			if (volverAlMenu) {
				break;
			}
			
			//Creamos el espacio en memoria para almacenar la información dada por el cliente luego de solicitarla
			SalaCine salaDeCineProceso = null;
			String numeroAsientoProceso = null;
			LocalDateTime horarioProceso = null;
			
			//Avance de tiempo para realizar los filtros de horarios correctamente
			avanzarTiempo();
			
			//Filtramos los primeros 7 horarios con asientos disponibles desde la fecha actual
			ArrayList<LocalDateTime> horariosPeliculaProceso = peliculaProceso.filtrarHorariosPelicula();
			boolean disponibilidadHoraria = horariosPeliculaProceso.size() > 0;
			boolean realizarReservaDePeliculaEnPresentacion = false;
			
			//Mostramos este menú en caso de que la película se encuentre en presentación en alguna sala de cine y 
			//además la película no lleva más de 15 minutos en presentación
			if (peliculaProceso.IsPeliculaEnPresentacion(clienteProceso.getCineActual())) {
				
				//Se busca en que sala se encuentra la película en presentación
				salaDeCineProceso = peliculaProceso.whereIsPeliculaEnPresentacion(clienteProceso.getCineActual());
				casoValidoConfirmacion = false;
				
				//Preguntamos si desea ver la película a la hora de esta presentación o en un horario diferente en caso de tener más horarios disponibles
				do {
					
					opcionMenu = 0;
					try {
						System.out.println("\nHemos detectado que la película seleccionada se encuentra en presentación. \ninicio de proyección: " 
						+ salaDeCineProceso.getHorarioPeliculaEnPresentacion() + "\n¿Desea reservar un ticket para este horario? " 
						+" (Hora actual: " + SucursalCine.getFechaActual().withNano(0) + ")\n1. Comprar en este horario" 
						+ disponibilidadHorariaFuncionalidad1(horariosPeliculaProceso));
						opcionMenu = Integer.parseInt(sc.nextLine());
					}catch(NumberFormatException e){
						System.out.println("Error, debes ingresar un único dato númerico entre los disponibles");
					}
					
					//En caso de que la película tenga horarios disponibles, se usa un menú de 3 opciones, en caso de que no, uno de 2 opciones
					if (disponibilidadHoraria) {
						
						switch(opcionMenu) {
							case 1: casoValidoConfirmacion = true;
								//Se piden los datos de reserva de ticket en la sala de cine en cuestión
								
								//El cliente elige el asiento de la sala de cine que tiene la película seleccionada en presentación
								numeroAsientoProceso = seleccionarAsiento(salaDeCineProceso, peliculaProceso);
								//Revisamos que el asiento haya sido seleccionado con éxito
								if (numeroAsientoProceso == null) {
									volverAlMenu = true;
									System.out.println("\nNo se ha podido seleccionar correctamente el asiento, serás redireccionado al menú reserva de ticket");
								}
								
								//Obtenemos el horario de la película seleccionada
								horarioProceso = salaDeCineProceso.getHorarioPeliculaEnPresentacion();
								
								realizarReservaDePeliculaEnPresentacion = true;
								
								break;
									
							case 2: casoValidoConfirmacion = true; 							
									//Se piden los datos de reserva de ticket de la película en otro horario
									
									//El cliente elige el horario de la película seleccionada 
									horarioProceso = seleccionarHorarioPelicula(clienteProceso, peliculaProceso, horariosPeliculaProceso);
									if (horarioProceso == null) {
										volverAlMenu = true;
										System.out.println("\nNo se ha podido seleccionar correctamente el horario, serás redireccionado al menú del sistema de proyecciones");
									}
									
									//El cliente elige el asiento de la película seleccionada
									numeroAsientoProceso = seleccionarAsiento(clienteProceso, horarioProceso, peliculaProceso);
									//Revisamos que el asiento haya sido seleccionado con éxito
									if (numeroAsientoProceso == null) {
										volverAlMenu = true;
										System.out.println("\nNo se ha podido seleccionar correctamente el asiento, serás redireccionado al menú del sistema de proyecciones");
									}
									
									break;
									
							case 3: casoValidoConfirmacion = true; volverAlMenu = true; break;
							
							default: casoValidoConfirmacion = false; System.out.println("Digite un número válido");
						}
						
					}else {
						switch(opcionMenu) {
							case 1: casoValidoConfirmacion = true;
									//Se piden los datos de reserva de ticket en la sala de cine en cuestión
									
									//El cliente elige el asiento de la sala de cine que tiene la película seleccionada en presentación
									numeroAsientoProceso = seleccionarAsiento(salaDeCineProceso, peliculaProceso);
									//Revisamos que el asiento haya sido seleccionado con éxito
									if (numeroAsientoProceso == null) {
										volverAlMenu = true;
										System.out.println("\nNo se ha podido seleccionar correctamente el asiento, serás redireccionado al menú del sistema de proyecciones");
									}
									
									//Obtenemos el horario de la película seleccionada
									horarioProceso = salaDeCineProceso.getHorarioPeliculaEnPresentacion();
									
									realizarReservaDePeliculaEnPresentacion = true;
									
									break;
									
							case 2: casoValidoConfirmacion = true; volverAlMenu = true; break;
							
							default: casoValidoConfirmacion = false; System.out.println("Digite un número válido");
						}
					}
				}while(!(casoValidoConfirmacion));
				
			}else {
				
				//Se verifica que tenga horarios disponibles
				if(disponibilidadHoraria) {
					//Compra película en otro horario
					
					//El cliente elige el horario de la película seleccionada 
					horarioProceso = seleccionarHorarioPelicula(clienteProceso, peliculaProceso, horariosPeliculaProceso);
					if (horarioProceso == null) {
						volverAlMenu = true;
						System.out.println("\nNo se ha podido seleccionar correctamente el horario, serás redireccionado al menú del sistema de proyecciones");
					}
					
					//El cliente elige el asiento de la película seleccionada
					numeroAsientoProceso = seleccionarAsiento(clienteProceso, horarioProceso, peliculaProceso);
					//Revisamos que el asiento haya sido seleccionado con éxito
					if (numeroAsientoProceso == null) {
						volverAlMenu = true;
						System.out.println("\nNo se ha podido seleccionar correctamente el asiento, serás redireccionado al menú del sistema de proyecciones");
					}
					
				}else {
					System.out.println("La película seleccionada se encuentra únicamente en presentación o no tiene asientos disponibles." + 
					"\n(Serás redireccionado al menú inicial de este proceso...)");
					continue;
				}
				
			}
			
			//Rompemos la lógica de la reserva de ticket y regresamos al menú principal
			// para llegar aquí hay 2 caminos: 
			//1. El cliente estaba seleccionando un asiento y durante el proceso, el hilo modifico la sala virtual o 
			//sala presencial, en dónde se estaba haciendo este proceso
			// 2. El cliente eligió regresar al menú del sistema de proyecciones
			if (volverAlMenu) {
				break;
			}
			
			//Se genera el último mensaje con posibilidad de regresar al menú principal en caso de que no se quiera reservar la película
			boolean casoContinuarProcesoPago = false;
			casoValidoConfirmacion = false;
			do {
				opcionMenu = 0;
				try {
					System.out.println("\nVamos a empezar con el proceso de pago\n1. Continuar\n2. Volver al menú del sistema de proyecciones");
					opcionMenu = Integer.parseInt(sc.nextLine());
				}catch(NumberFormatException e) {
					System.out.println("Error, debes ingresar un único dato numérico entre los disponibles");
				}
				
				switch(opcionMenu) {
					case 1: casoContinuarProcesoPago = true; casoValidoConfirmacion = true; break;
					case 2: casoValidoConfirmacion = true; break;
					default : System.out.println("Opción Invalida"); casoValidoConfirmacion = false; 
				}
				
			}while(!casoValidoConfirmacion);
			
			//Verificar integridad datos seleccionados, en caso de que el cliente tarde mucho en ingresar la confirmación de continuar con el proceso
			if ( realizarReservaDePeliculaEnPresentacion ) {
				
				//Revisamos si el horario de la película en presentación ha sido modificado, comparando que el nombre sea el mismo
				// y el horario de presentación más la duración de la película no exceda a la fecha actual
				if ( !verificarIntegridadHorarioSeleccionado(salaDeCineProceso, peliculaProceso) ) {
					System.out.println("\nEl tiempo límite de compra de una película en presentación ha sido excedido (20 minutos a partir del inicio de la proyección)." 
					+ "\nLe solicitamos esperar a la siguiente presentación, serás redirigido al menú del sistema de proyecciones. ");
					break;
				}

				
			}else {
				
				//Revisamos si el horario de la película seleccionado desde la sala de cine virtual ha sido modificado, verificando
				// si el horario se encuentra en el array de horarios de la película
				if ( !verificarIntegridadHorarioSeleccionado(peliculaProceso, horarioProceso)) {
					System.out.println("\nEl horario seleccionado ha sido actualizado, actualmente se encuentra en presentación,"
					+ "\nserás redirigido al menú del sistema de proyecciones.");
					break;
				}
				
			}
			
			//Creamos el apuntador del ticket
			Ticket ticketProceso = null;
			
			if (casoContinuarProcesoPago) {
				
				//Creamos el ticket con su respectivo precio e informamos al cliente en caso de recibir un descuento
				ticketProceso = new Ticket(peliculaProceso, horarioProceso, numeroAsientoProceso, clienteProceso.getCineActual());
				//Mostramos un mensaje en pantalla en caso de recibir el descuento
				if ( ticketProceso.getPrecio() != peliculaProceso.getPrecio() ) {
					if (peliculaProceso.getTipoDeFormato().equals("3D") || peliculaProceso.getTipoDeFormato().equals("4D") ) {
						System.out.println("Felicidades, por ser nuestro cliente número " + clienteProceso.getCineActual().getCantidadTicketsCreados() 
						+ " has recibido un descuento del 50% por la compra de tu ticket\n"
						+ "(Precio anterior :" + peliculaProceso.getPrecio() + " -> Precio actual: " + ticketProceso.getPrecio() + " )");
					}else {
						System.out.println("Felicidades, por ser nuestro cliente número: " + clienteProceso.getCineActual().getCantidadTicketsCreados() 
						+ " has recibido un descuento del 80% por la compra de tu ticket\n"
						+ "(Precio anterior :" + peliculaProceso.getPrecio() + " -> Precio actual: " + ticketProceso.getPrecio() + " )");
					}
				}
				
			}else {
				//Volvemos al menú del sistema de proyecciones
				//Rompemos la lógica de la reserva de ticket
				break;
				
			}
			
			//Iniciamos el proceso de pago
			System.out.println("\n		Proceso de pago");
			System.out.println("=====================================================");
			
			boolean pagoRealizado = false;
			casoValido = false;
			casoValidoConfirmacion = false;
			
			MetodoPago metodoPagoProceso = null;
			double precioTicketProceso = ticketProceso.getPrecio();
			double precioAcumuladoTicketProceso = 0;
			
			//Selccionar el método de pago para realizar el pago y realizar el pago
			do {
				do {
					opcionMenu = 0;
					try {
						System.out.println("\nEl valor a pagar por el ticket es: " + precioTicketProceso
						+ "\nEste es el listado de los métodos de pago disponibles:\n" 
						+ MetodoPago.mostrarMetodosDePago(clienteProceso));
						System.out.print("\nElige una de las opciones disponibles para realizar el pago: " );
						opcionMenu = Integer.parseInt(sc.nextLine());
					}catch(NumberFormatException e) {
						System.out.println("\nError, debe ingresar un único dato númerico entre los disponibles");
					}
					
					if (opcionMenu > 0 & opcionMenu <= clienteProceso.getMetodosDePago().size()) {
						//Se selecciona el método de pago
						metodoPagoProceso = clienteProceso.getMetodosDePago().get(opcionMenu - 1);
						casoValido = true;
						
					}else {
						System.out.println("\nSeleccione un método de pago entre los disponibles");
						
					}
					
				}while( !casoValido );
				
				do {
					opcionMenu = 0;
					try {
						System.out.println("\nEl método de pago escogido es: " + metodoPagoProceso.getNombre() 
						+ " ( Precio anterior: " + precioTicketProceso + " -> Precio actual: " + precioTicketProceso * (1 - metodoPagoProceso.getDescuentoAsociado()) + " )"
						+ "\n1. Correcto\n2. Cambiar Método de pago");
						opcionMenu = Integer.parseInt(sc.nextLine());
					}catch(NumberFormatException e) {
						System.out.println("Error, debes ingresar un único dato numérico entre los disponibles");
					}
					
					switch(opcionMenu) {
					case 1: casoValidoConfirmacion = true; break;
					case 2: casoValidoConfirmacion = true; break;
					default: System.out.println("Opcion Invalida"); casoValidoConfirmacion = false;
					}
					
				}while(!casoValidoConfirmacion);
				
				if (opcionMenu == 2 || opcionMenu == 0) {
					continue;
				}
				
				//Verificar integridad datos seleccionados, en caso de que el cliente tarde mucho tiempo en seleccionar el método de pago y confirmarlo
				if ( realizarReservaDePeliculaEnPresentacion ) {
					
					//Revisamos si el horario de la película en presentación ha sido modificado, comparando que el nombre sea el mismo
					// y el horario de presentación más la duración de la película no exceda a la fecha actual
					if ( !verificarIntegridadHorarioSeleccionado(salaDeCineProceso, peliculaProceso) ) {
						System.out.println("\nEl proceso de compra no se ha llevado a cabo, debido a que ha excedido el tiempo límite " + 
						"de compra de una película en presentación\n(20 minutos a partir del inicio de la proyección).\nTerminando proceso de compra...");
						break;
					}

					
				}else {
					
					//Revisamos si el horario de la película seleccionado desde la sala de cine virtual ha sido modificado, verificando
					// si el horario se encuentra en el array de horarios de la película
					if ( !verificarIntegridadHorarioSeleccionado(peliculaProceso, horarioProceso)) {
						System.out.println("\nEl proceso de compra no se ha llevado a cabo, debido a que el horario seleccionado ha sido actualizado,"
						+ "\nactualmente se encuentra en presentación.\nTerminando proceso de compra...");
						break;
					}
					
				}
				
				//Realizamos el pago y sumamos el precio acumulado para mostrar el valor real del ticket
				precioAcumuladoTicketProceso = precioAcumuladoTicketProceso + precioTicketProceso * (1 - metodoPagoProceso.getDescuentoAsociado());
				precioTicketProceso = metodoPagoProceso.realizarPago(precioTicketProceso, clienteProceso);
				
				//Ponemos un delay en pantalla
				System.out.println("\nEstamos procesando su pago, por favor espere...\n");
				try {
					Thread.sleep(3000);
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			
				//Realizamos el pago, según si el cliente decidió comprar un asiento de una película en presentación o en otro horario distinto
				if ( realizarReservaDePeliculaEnPresentacion ) {
					
					//Verificamos si el pago fue cubierto en su totalidad
					if (precioTicketProceso == 0) {
						
						System.out.println("Pago realizado, La compra de su ticket fue exitosa\n");
						
						//Setteamos el precio del ticket
						ticketProceso.setPrecio(precioAcumuladoTicketProceso);
						
						//Realizamos el proceso correspondiente luego de ser verificado
						ticketProceso.procesarPagoRealizado(clienteProceso);
						salaDeCineProceso.cambiarDisponibilidadAsientoLibre(numeroAsientoProceso);
						
						//Generamos la fila y la columna a partir del número de asiento seleccionado para modificar su disponibilidad
						//Nota : Esto es para preservar la persistencia, en caso de reservar un ticket de una película en presentación,
						//también debemos añadir este cambio a la sala virtual, ya que luego de deserializar se ponen falsos todas las
						//disponibilidades de asientos y renovamos los valores con los asientos virtuales de la película que debe estar 
						//en presentación en ese momento del tiempo, es por esto que debemos actualizar también la matriz de asientos
						//en película, ya que sin esto, se pierde este dato de la compra y permitiría comprar varios tickets para el
						//mismo asiento.
						int filaProceso = Character.getNumericValue(numeroAsientoProceso.charAt(0));
						int columnaProceso = Character.getNumericValue(numeroAsientoProceso.charAt(2));
						peliculaProceso.modificarSalaVirtual( salaDeCineProceso.getHorarioPeliculaEnPresentacion(), filaProceso, columnaProceso );
						
						System.out.println( ticketProceso.factura() );
						pagoRealizado = true;
						
					}else {
						
						//Repetimos el proceso hasta validar el pago
						System.out.println("Tiene un saldo pendiente de : " + precioTicketProceso);
						
					}
					
				}else {
					
					//Verificamos si el pago fue cubierto en su totalidad
					if (precioTicketProceso == 0) {
						
						System.out.println("Pago realizado, La compra de su ticket fue exitosa\n");
						
						//Setteamos el precio del ticket
						ticketProceso.setPrecio(precioAcumuladoTicketProceso);
						
						//Realizamos el proceso correspondiente a realizar el pago
						ticketProceso.procesarPagoRealizado(clienteProceso);
						
						//Generamos la fila y la columna a partir del número de asiento seleccionado para modificar su disponibilidad
						int filaProceso = Character.getNumericValue(numeroAsientoProceso.charAt(0));
						int columnaProceso = Character.getNumericValue(numeroAsientoProceso.charAt(2));
						peliculaProceso.modificarSalaVirtual(horarioProceso, filaProceso, columnaProceso);
						
						System.out.println( ticketProceso.factura() );
						pagoRealizado = true;
						
					}else {
						
						//Repetimos el proceso hasta validar el pago
						System.out.println("Tiene un saldo pendiente de : " + precioTicketProceso);
						
					}
				
				}
			
			}while(!pagoRealizado);
			
			System.out.println("\nFin del proceso reserva de ticket");
			System.out.println("(Redireccionando al menú del sistema de proyecciones...)");
			try {
				Thread.sleep(3000);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			finalizarProcesoReservaTicket = true;
			
		}while(!finalizarProcesoReservaTicket);
			
	}

	/**
	 * Description : Este método se encarga de seleccionar un horario a partir de la pelicula seleccionada por el cliente, para realizar este proceso,
	 * Se muestra en pantalla los de horarios de la película seleccionada, para que el cliente pueda seleccionar uno de estos.
	 * @param clienteProceso : Este método recibe como parámetro un cliente (De tipo cliente), con el fin de que, 
	 * dado el caso, el cliente quiera regresar al menú del sistema de proyeccciones, pueda hacerlo.
	 * @param peliculaProceso : Este método recibe como parámetro una película (De tipo película) obtenido durante el proceso de la reserva de ticket,
	 * para que de esta podamos obtener su array de horarios y su correspondiente sala virtual obtenida del array de asientos virtuales.
	 * @param horariosPeliculaProceso : Este método recibe como parámetro los horarios disponibles de la película (De tipo ArrayList<LocalDateTime>)
	 * obtenido durante el proceso de la reserva de ticket.
	 * @return <b>LocalDateTime</b> : Este método retorna el horario seleccionado por el cliente, para continuar con el proceso de la reserva de ticket.
	 * */
	private static LocalDateTime seleccionarHorarioPelicula(Cliente clienteProceso, Pelicula peliculaProceso, ArrayList<LocalDateTime> horariosPeliculaProceso) {
		
		boolean casoValido = false;
		boolean casoValidoEleccionHorario = false;
		boolean casoValidoConfirmacion = false;
		int opcionMenu;
		
		LocalDateTime horarioProceso = null;
		
		do {
			System.out.println("\n		Selección de horario");
			System.out.println("=====================================================");
			//Mostramos en pantalla los horarios disponibles
			do {
				opcionMenu = 0;
				System.out.println("\nLos horarios de la película " + peliculaProceso.getNombre() 
				+ " son:\n" + peliculaProceso.mostrarHorarioPelicula(horariosPeliculaProceso));
				
				try {
					System.out.print("\nElige un horario entre los disponibles: ");
					opcionMenu = Integer.parseInt(sc.nextLine());
				} catch(NumberFormatException e) {
					System.out.println("\nError, debes ingresar un único dato numérico");
					continue;
				}
				
				if( opcionMenu > 0 && opcionMenu <= Integer.valueOf(horariosPeliculaProceso.size()) ) {
					horarioProceso = horariosPeliculaProceso.get(opcionMenu - 1);
					casoValidoEleccionHorario = true;
				}else {
					System.out.println("\nOpción inválida");
				}
				
			}while(!casoValidoEleccionHorario);
			
			//Confirmamos el horario seleccionado
			do {
				opcionMenu = 0;
				try {
					System.out.println("\nElegiste la película el día: " + horarioProceso.getDayOfWeek() +  " fecha: "
					+ horarioProceso.toLocalDate() + ", A las: " + horarioProceso.toLocalTime() + "\n1. Correcto \n2. Cambiar horario");
					opcionMenu = Integer.parseInt(sc.nextLine());
				} catch(NumberFormatException e) {
					System.out.println("Error, debes ingresar un único dato numérico");
				}
				
				switch(opcionMenu) {
					case 1: casoValidoConfirmacion = true; casoValido = true; break;
					case 2: casoValidoConfirmacion = true; casoValido = false; break;
					default : System.out.println("Opción invalida"); casoValidoConfirmacion = false; break;
				}
				
			}while(!casoValidoConfirmacion);
			
			//Avance de tiempo para verificar la integridad del horario seleccionado
			avanzarTiempo();
			
			//Revisar integridad del horario seleccionado en caso de que el cliente tarde mucho tiempo en confirmar su elección
			if (!horarioProceso.isAfter(SucursalCine.getFechaActual())) {
				System.out.println("\nEl horario seleccionado ha sido actualizado, actualmente se encuentra en presentación");
				horarioProceso = null;
			}
			
		}while(!casoValido);
		
		return horarioProceso;
	}
	
	/**
	 * Description: Este método se encarga de seleccionar el número de asiento del cliente para ver una película en un horario previamente seleccionado
	 * por el cliente, para hacer esto, se muestra en pantalla los asientos de la sala de cine virtual, con su respectiva disponibilidad,
	 * tras esto, el cliente elige la fila, luego la columna, se valida si el asiento en cuestión 
	 * se encuentra disponible y una vez cumplida la verificación, retornamos el número del asiento seleccionado.
	 * @param clienteProceso : Este método recibe como parámetro el cliente (De tipo Cliente) seleccionado en el proceso de login.
	 * @param horarioProceso : Este método recibe como parámetro el horario (De tipo LocalDateTime) seleccionado durante el proceso de reserva de ticket.
	 * @param horarioProceso : Este método recibe como parámetro la película (De tipo Pelicula) seleccionada durante el proceso de reserva de ticket.
	 * @return <b>String</b> : Este método retorna un String que corresponde al número de asiento seleccionado por el cliente.
	 * */
	private static String seleccionarAsiento(Cliente clienteProceso, LocalDateTime horarioProceso, Pelicula peliculaProceso) {
		
		boolean casoSeleccionExpirada = false;
		boolean casoValido = false;
		boolean casoValidoConfirmacion = false;
		int opcionMenu;
		
		String numeroAsientoProceso = null;
		int filaProceso = 0;
		int columnaProceso = 0;
		
		System.out.println("\n		Selección de asiento");
		System.out.println("=====================================================");
		
		//Elegimos el asiento
		do {
			System.out.println("\nEsta es la distribución de asientos, con su disponibilidad \nactual, de la película en el horario seleccionado" 
		    + "\n X : Ocupado\n O : Disponible\n" + peliculaProceso.mostrarAsientosSalaVirtual(horarioProceso) );
			
			//Elegimos la fila del asiento
			do {
				try {
					System.out.print("\nDigite la fila de su asiento deseado: ");
					filaProceso = Integer.parseInt(sc.nextLine());
				} catch(NumberFormatException e) {
					System.out.println("\nError, debe ingresar un dato numérico correspondiente a alguna de las filas disponibles");
					continue;
				}
				
				//Avance de tiempo para verificar si el horario aún se encuentra disponible en la sala de cine virtual (No ha sido actualizado)
				avanzarTiempo();
				
				//Verificamos si el horario aún se encuentra disponible
				try {
					
					if(!(filaProceso > 0 & filaProceso <= Integer.valueOf(peliculaProceso.getAsientosVirtuales().get(peliculaProceso.getHorarios().indexOf(horarioProceso)).length))){
						System.out.println("\nLa fila seleccionada no se encuentra disponible, le sugerimos que eliga una entre las disponibles");
						continue;
					}
					
				}catch (IndexOutOfBoundsException e) {
					casoSeleccionExpirada = true;
					System.out.println("\nEl horario seleccionado ha sido actualizado, actualmente se encuentra en presentación");
					break;
				}
				
				do {
					opcionMenu = 0;
					try {
						System.out.println("\nLa fila seleccionada es: " + filaProceso + "\n1. Correcto \n2. Cambiar fila");
						opcionMenu = Integer.parseInt(sc.nextLine()); 
					}catch (NumberFormatException e) {
						System.out.println("\nError, debe ingresar un único dato numérico entre los disponibles");
						continue;
					}
					
				}while(!(opcionMenu == 1 || opcionMenu == 2));
				
				casoValidoConfirmacion = (opcionMenu == 1) ? true : false;
				
			}while(!(casoValidoConfirmacion));
			
			//Rompemos la lógica de la selección de asientos
			if (casoSeleccionExpirada) {
				break;
			}