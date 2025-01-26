package baseDatos;

import gestionAplicacion.SucursalCine;

public class Modifier {
    public static void main(String[] args) {
        //Deserializa
        Deserializador.deserializar();
        Deserializador.deserializarEstaticos();
        //Renueva referencias a los objetos deserializados
        Deserializador.asignarReferenciasDeserializador();

        //modificar
        SucursalCine.logicaInicioSistemaReservarTicket();
        Serializador.serializar();
        for (SucursalCine sede: SucursalCine.getSucursalesCine()){
            Serializador.serializar(sede);
        }
    }
}
