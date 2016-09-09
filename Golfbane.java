import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3UltrasonicSensor;
//import lejos.hardware.sensor.NXTTouchSensor;
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
import java.util.Random;

public class Golfbane{
	public static void main(String[] args) throws Exception{
		/*// Thread for å stoppe program (kjører parallelt med resten)
		new Thread("Stopper") {
			@Override
			public void run() {
				while (true){
					if (Button.ESCAPE.isDown() && Button.ENTER.isDown()){
						System.exit(0);
					}
				}
			}
		}.start();*/

		//variabler
		boolean go = true;
		float dist = 0;
		int dir = 1;
		Random rand = new Random();

		// Definer boks
		Brick legogo = BrickFinder.getDefault();
		EV3 ev3 = (EV3) BrickFinder.getLocal();

		// Definer skjerm
		TextLCD lcd = ev3.getTextLCD();

		// Definer knapper
		Keys keys = ev3.getKeys();

		// Port
		Port s4 = legogo.getPort("S4");
		//Port s1 = legogo.getPort("S1");
		//Port s2 = legogo.getPort("S2");

		// Definer ultra sensor
		EV3UltrasonicSensor sensorU = new EV3UltrasonicSensor(s4);
		SampleProvider leserU = sensorU.getDistanceMode();
		float[] dataU = new float[leserU.sampleSize()];

		/*// Definer trykksensor venstre
		NXTTouchSensor tsv = new NXTTouchSensor(s1);
		SampleProvider leserV = tsv.getTouchMode();
		float[] dataV = new float[leserV.sampleSize()];

		// Definer trykksensor venstre
		NXTTouchSensor tsh = new NXTTouchSensor(s2);
		SampleProvider leserH = tsh.getTouchMode();
		float[] dataH = new float[leserH.sampleSize()];*/

		// Vent på knapp enter for å starte
		lcd.drawString("Trykk enter starte", 0, 1);
		keys.waitForAnyPress();

		// Hoved loop
		lcd.drawString("Kjorer", 0,5);
		while (true){
			/*// Trykksensor kode
			leserV.fetchSample(dataV, 0);
			if (dataV[0] == 1){
				lcd.drawString("Venstre "+dataV[0], 0,5);
				Motor.B.backward();
				Motor.C.backward();
				Thread.sleep(500);
				Motor.B.forward();
				Motor.C.backward();
				Thread.sleep(500);
				go = true;
			}

			leserH.fetchSample(dataH, 0);
			if (dataH[0] == 1){
				lcd.drawString("Høyre "+dataH[0], 0,5);
				Motor.B.backward();
				Motor.C.backward();
				Thread.sleep(500);
				Motor.B.backward();
				Motor.C.forward();
				Thread.sleep(500);
				go = true;
			}*/

			// Hent data fra leser, legg i data plass 0
			leserU.fetchSample(dataU, 0);
			dist = dataU[0];

			// Sjekk om sensor distanse er > 0.2
			if (dist<0.2&!go){

				// Skriv tekst
				lcd.drawString("Svinger", 0,5);
				Motor.B.stop();
				Motor.C.stop();
				Sound.twoBeeps();

				while(dist<0.4){

					// Sving sakte
					Motor.B.setSpeed(200);
					Motor.C.setSpeed(200);

					// Sving venstre eller høyre, ut fra random variabel
					if (dir == 1){
						Motor.B.forward();
						Motor.C.backward();
					} else {
						Motor.B.backward();
						Motor.C.forward();
					}
					Thread.sleep(300);

					//Motor.B.stop();
					//Motor.C.stop();

					// Sjekk sensor
					leserU.fetchSample(dataU, 0);
					dist = dataU[0];
				}

				go=true;
			} else if (dist>0.2&&go){

				// Set random variabel
				dir = rand.nextInt(2);

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