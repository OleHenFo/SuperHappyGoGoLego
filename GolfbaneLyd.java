import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.hardware.Button;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;
import lejos.hardware.Sound;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import static java.lang.System.out;

public class GolfbaneLyd{
	public static void main(String[] args) throws Exception{
		//variabler
		boolean go = true;
		float dist = 0;

		// Definer boks
		Brick legogo = BrickFinder.getDefault();
		EV3 ev3 = (EV3) BrickFinder.getLocal();

		// Definer skjerm
		TextLCD lcd = ev3.getTextLCD();

		// Definer knapper
		Keys keys = ev3.getKeys();

		// Port
		Port s4 = legogo.getPort("S4");
		Port s1 = legogo.getPort("S1");
		Port s2 = legogo.getPort("S2");

		// Definer ultra sensor
		EV3UltrasonicSensor sensor = new EV3UltrasonicSensor(s4);
		SampleProvider leser = sensor.getDistanceMode();
		float[] data = new float[leser.sampleSize()];

		// Definer motor
		// Venstre
		Motor.B.setSpeed(450);
		// Høyre
		Motor.C.setSpeed(450);

		// Vent på knapp enter for å starte
		lcd.drawString("Trykk enter starte", 0, 1);
		keys.waitForAnyPress();

		// Hoved loop
		lcd.drawString("Kjorer", 0,5);
		while (true){
			// Hent data fra leser, legg i data plass 0
			leser.fetchSample(data, 0);
			dist = data[0];

			// Sjekk om sensor distanse er > 0.2
			if (dist<0.2&!go){
				// Skriv tekst
				lcd.drawString("Svinger", 0,5);

				//R2D2 sound
				File myFile = new File("r2-d2");


				while(dist<0.4){
					Motor.B.setSpeed(200);
					Motor.C.setSpeed(200);
					// Sving litt venstre
					Motor.B.forward();
					Motor.C.backward();
					Thread.sleep(300);

					//Motor.B.stop();
					//Motor.C.stop();

					// Sjekk sensor
					leser.fetchSample(data, 0);
					dist = data[0];
				}

				go=true;
			} else if (dist>0.2&&go){
				// Venstre
				Motor.B.setSpeed(500);
				Motor.C.setSpeed(500);

				// Skriv tekst
				lcd.drawString("Kjorer", 0,5);

				Motor.B.setSpeed(450);
				Motor.C.setSpeed(450);
				// Kjør
				Motor.B.forward();
				Motor.C.forward();
				go=false;
			}
		}
	}
}
