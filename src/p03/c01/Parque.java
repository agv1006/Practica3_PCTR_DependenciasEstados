package src.p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;

public class Parque implements IParque{

	private final int personasTotales = 50;
	private int contadorPersonasTotales;
	private Hashtable<String, Integer> contadoresPersonasPuerta;
	
	
	public Parque() {
		contadorPersonasTotales = 0;
		contadoresPersonasPuerta = new Hashtable<String, Integer>();
	}


	@Override
	public synchronized void entrarAlParque(String puerta){	
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		// Comprobamos si podemos entrar en el parque (aforo)
		comprobarAntesDeEntrar();
				
		
		// Aumentamos el contador total y el individual
		contadorPersonasTotales++;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)+1);
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Entrada");
		
		// Comprobamos las invariantes
		checkInvariante();
		
		// Notificamos al resto de hilos
		notifyAll();	
	}
	
	@Override
	public synchronized void salirDelParque(String puerta){
		// Si no hay salidas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		// Comprobamos si podemos salir del parque (aforo)
		comprobarAntesDeSalir();
		
		// Decrementamos el contador total y el individual
		contadorPersonasTotales--;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)-1);
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Salida");		
		
		// Comprobamos las invariantes
		checkInvariante();
		
		// Notificamos al resto de hilos
		notifyAll();
	}
	
	
	private void imprimirInfo (String puerta, String movimiento){
		System.out.println(movimiento + " por puerta " + puerta);
		System.out.println("--> Personas en el parque " + contadorPersonasTotales); //+ " tiempo medio de estancia: "  + tmedio);
		
		// Iteramos por todas las puertas e imprimimos sus entradas
		for(String p: contadoresPersonasPuerta.keySet()){
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuerta.get(p));
		}
		System.out.println(" ");
	}
	
	
	private int sumarContadoresPuerta() {
		int sumaContadoresPuerta = 0;
			Enumeration<Integer> iterPuertas = contadoresPersonasPuerta.elements();
			while (iterPuertas.hasMoreElements()) {
				sumaContadoresPuerta += iterPuertas.nextElement();
			}
		return sumaContadoresPuerta;
	}
	
	
	protected void checkInvariante() {
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parte";
		assert contadorPersonasTotales <= personasTotales : "INV: No pueden haber más personas en el parque que lo que permite el aforo máximo";
		assert contadorPersonasTotales >= 0 : "INV: No puede haber un aforo negativo en el parque";
	}
	

	protected void comprobarAntesDeEntrar(){
		while (contadorPersonasTotales == personasTotales) {
			try {
				wait();
			} catch (InterruptedException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
	

	protected void comprobarAntesDeSalir(){
		while (contadorPersonasTotales == 0) {
			try {
				wait();
			} catch (InterruptedException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
}
