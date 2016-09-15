import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.hardware.sensor.NXTLightSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.hardware.sensor.NXTSoundSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;
import lejos.hardware.Sound;
import java.util.Timer;

public class Tunnel{

	// Definer motorer
	public static NXTRegulatedMotor hoyre;
	public static NXTRegulatedMotor venstre;

	public static void main(String[] args) throws Exception{
		// Try catch (for feilmeldinger)
		try {
			//variabler
			float dist = 0;
			float press = 0;
			float farge = 0;
			float sound = 0;
			int retning = 0;
			int wait = 0;

			// Definer boks
			Brick legogo = BrickFinder.getDefault();
			EV3 ev3 = (EV3) BrickFinder.getLocal();

			// Definer skjerm
			TextLCD lcd = ev3.getTextLCD();

			// Definer knapper
			Keys keys = ev3.getKeys();

			// Definer porter
			Port s1 = legogo.getPort("S1");
			Port s2 = legogo.getPort("S2");
			Port s3 = legogo.getPort("S3");
			Port s4 = legogo.getPort("S4");

			// Definer ultra sensor
			EV3UltrasonicSensor sensorU = new EV3UltrasonicSensor(s4);
			SampleProvider leserU = sensorU.getDistanceMode();
			float[] dataU = new float[leserU.sampleSize()];

			// Definer lyssensor
			EV3ColorSensor sensorF = new EV3ColorSensor(s3);
			SampleProvider leserF = sensorF.getRGBMode();
			float[] dataF = new float[leserF.sampleSize()];

			// Definer lydsensor
			NXTSoundSensor sensorL = new NXTSoundSensor(s2);
			SampleProvider leserL = sensorL.getDBMode();
			float[] dataL = new float[leserL.sampleSize()];

			// Definer trykksensor
			EV3TouchSensor sensorT = new EV3TouchSensor(s1);
			SampleProvider leserT = sensorT.getTouchMode();
			float[] dataT = new float[leserT.sampleSize()];

			// Vent på knapp enter for å starte
			//lcd.drawString("Trykk enter starte", 0, 1);
			//keys.waitForAnyPress();

			// Definer motorer
			hoyre = Motor.C;
			venstre = Motor.B;

			// Sett fart
			Motor.A.setSpeed(100);
			hoyre.setSpeed(200); // VENSTRE
			venstre.setSpeed(200); // HØYRE
			Motor.D.setSpeed(100);

			// Start motorer
			//Motor.A.forward();
			hoyre.forward();
			venstre.forward();
			//Motor.D.forward();

			// Start hovedloop
			while (true){

				// Trykksensor ----------------------------
				// Les trykksensor
				leserT.fetchSample(dataT, 0);
				press = dataT[0];

				// Sjekk trykk
				if(press>0){

					// Stop motorer
					hoyre.stop(true);
					venstre.stop(true);

					// Stop hovedloop
					break;
				}

				// Lydsensor -------------------------------
				// Les lyd
				leserL.fetchSample(dataL, 0);
				sound = dataL[0];

				// Sjekk lyd
				if(sound > 0.85){
					// Stop motorer
					hoyre.stop(true);
					venstre.stop(true);

					// Vent
					Thread.sleep(5000);

					//Start motorer i rett retning
					if (retning==1){
						hoyre.forward();
						venstre.forward();
					} else {
						hoyre.backward();
						venstre.backward();
					}
				}

				// Sving/rettnings korreksjon --------------------
				leserU.fetchSample(dataU, 0);
				dist = dataU[0];
				if(dist != 0.055){
					if (retning == 1){
						if(dist < 0.055){
							hoyre.setSpeed(85);
							venstre.setSpeed(100);
						} else if(dist > 0.055){
							hoyre.setSpeed(100);
							venstre.setSpeed(85);
						}
					} else {
						if(dist < 0.055){
							hoyre.setSpeed(85);
							venstre.setSpeed(100);
						} else if(dist > 0.055){
							hoyre.setSpeed(100);
							venstre.setSpeed(85);
						}
					}
				} else {
					hoyre.setSpeed(100);
					venstre.setSpeed(100);
					}

				// Kjør fram/tilbake ------------------------------
				if (retning == 1){
					hoyre.forward();
					venstre.forward();
				} else {
					hoyre.backward();
					venstre.backward();
				}

				// Farge sensor -----------------------------------
				// Vent før ny lesing dersom nettop lest
				if (wait==1){
					wait = 0;
					Thread.sleep(500);
				}

				// Les farge
				leserF.fetchSample(dataF, 0);
				farge = dataF[0];

				//Sjekk farge
				if(farge < 0.15){
					retning = (retning==1) ? 2 : 1;
					// Sett vent variabel før ny lesing
					wait = 1;
				}

				// Skjerm ------------------------------------------
				// Hent data
				leserU.fetchSample(dataU, 0);
				leserF.fetchSample(dataF, 0);
				leserL.fetchSample(dataL, 0);
				leserT.fetchSample(dataT, 0);
				// Skriv data
				lcd.drawString("Dist: "+dataU[0],0,2);
				lcd.drawString("Farge: "+dataF[0],0,3);
				lcd.drawString("Lyd: "+dataL[0],0,4);
				lcd.drawString("Trykk: "+dataT[0],0,5);
			}
		} catch (Exception e){
			// Dersom exception, print den og vent 5 sec før programet stopper
			System.out.println("Error: "+e);
			Thread.sleep(5000);
		}
	}
}
