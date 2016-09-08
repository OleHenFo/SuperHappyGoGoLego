import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;

public class Golfbane{
	public static void main(String[] args) throws Exception{
		// Variabler
		int stop = 0;

		// Definer boks
		Brick legogo = BrickFinder.getDefault();
		EV3 ev3 = (EV3) BrickFinder.getLocal();

		// Definer skjerm
		TextLCD lcd = ev3.getTextLCD();

		// Definer knapper
		Keys keys = ev3.getKeys();

		// Port
		Port s4 = legogo.getPort("S4");

		// Definer sensor
		EV3UltrasonicSensor sensor = new EV3UltrasonicSensor(s4);
		SampleProvider leser = sensor.getDistanceMode();
		float[] data = new float[leser.sampleSize()];

		// Vent for knappetrykk for å starte
		lcd.drawString("Trykk for starte", 0, 1);
		keys.waitForAnyPress();

		// Hoved loop
		while (stop<=20){
			stop+=stop;

			// Hent data fra leser, legg i data plass 0
			leser.fetchSample(data, 0);

			// Skriv data fra plass 0
			System.out.println("Avstand: " + data[0]);

			// Sleep 1 sec
			Thread.sleep(1000);
		}
	}
}