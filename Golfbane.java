import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.hardware.Button;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;

public class Golfbane{
	public static void main(String[] args) throws Exception{

		// Thread for å stoppe program (kjører parallelt med resten)
		new Thread("Stopper") {
			@Override
			public void run() {
				while (true){
					if (Button.ESCAPE.isDown() && Button.ENTER.isDown()){
						System.exit(0);
					}
				}
			}
      	}.start();

		//variabler
		boolean go = true;
		float dist = 0;
		float leftD = 0;
		float rightD = 0;

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

		// Definer motor
		// Venstre
		Motor.B.setSpeed(450);
		// Høyre
		Motor.C.setSpeed(450);

		// Vent på knapp enter for å starte
		lcd.drawString("Trykk enter starte", 0, 1);
		Button.ENTER.waitForPressAndRelease();

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

				// Sving litt venstre
				Motor.B.rotate(90);
				Motor.C.rotate(-90);
				while (Motor.A.isMoving()||Motor.B.isMoving()) Thread.yield();
				Thread.sleep(200);

				// Sjekk sensor
				leser.fetchSample(data, 0);
				dist = data[0];
				rightD=dist;

				// Sving høyre
				Motor.B.rotate(-180);
				Motor.C.rotate(180);
				while (Motor.A.isMoving()||Motor.B.isMoving()) Thread.yield();
				Thread.sleep(200);

				// Sjekk sensor
				leser.fetchSample(data, 0);
				dist = data[0];
				leftD=dist;

				// Tilbake til org. posisjon
				Motor.B.rotate(90);
				Motor.C.rotate(-90);
				Thread.sleep(200);

				// Velg retning med mest rom og sving
				if (leftD>rightD){
					Motor.B.rotate(180);
					Motor.C.rotate(-180);
				} else {
					Motor.B.rotate(-180);
					Motor.C.rotate(180);
				}
				while (Motor.A.isMoving()||Motor.B.isMoving()) Thread.yield();

				go=true;
			} else if (dist>0.2&&go){
				// Skriv tekst
				lcd.drawString("Kjorer", 0,5);

				// Kjør
				Motor.B.forward();
				Motor.C.forward();
				go=false;
			}
		}
	}
}