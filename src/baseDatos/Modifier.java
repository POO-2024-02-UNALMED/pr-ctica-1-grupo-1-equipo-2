package baseDatos;

import gestionAplicacion.SucursalCine;

/**
 * Description: Esta clase se encarga de modificar instancias,
 * e atributos de clase cuando estos ya fueron modificados,
 * para esto aplicamos una logica muy parecida a la el inicio del
 * sistema (metodo en administrador) para leer
 * los objetos para su correcto funcionamiento, principalmente
 * para cuando están desincronizada la fecha actual, y los
 * horarios de las salas de cine.**/

public class Modifier {
    public static void main(String[] args) {
        //Deserializa las intancias de las clases
        Deserializador.deserializar();

        //Deserializa los atributos estaticos de las clases
        Deserializador.deserializarEstaticos();

        //Renueva referencias a los objetos deserializados
        Deserializador.asignarReferenciasDeserializador();

        //Este metodo actualizara fechas y horarios de las salas de cine
        SucursalCine.logicaInicioSistemaReservarTicket();

        //Serializa los atributos estaticos de las clases
        Serializador.serializar();

        //Serializa las instancias de las clases, para esto se recorre la lista de sucursales de cine.
        for (SucursalCine sede: SucursalCine.getSucursalesCine()){
            Serializador.serializar(sede);
        }

        /**
         * para mas informacion sobre el uso del serializador,
         * ve a la clase serializador, para poder entender la
         * implementacion del mismo**/
    }
}
