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
import java.text.DecimalFormat;

public class Rally{

	// Definer motorer
	public static NXTRegulatedMotor hoyre;
	public static NXTRegulatedMotor venstre;

	public static void main(String[] args) throws Exception{
		// Try catch (for feilmeldinger)
		try {
			//variabler
			float dist = 0;
			float farge = 0;

			// Decimaltall formatering (for skjerm)
			DecimalFormat df = new DecimalFormat("#.###");

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
			EV3ColorSensor sensorF = new EV3ColorSensor(s1);
			SampleProvider leserF = sensorF.getRGBMode();
			float[] dataF = new float[leserF.sampleSize()];

			// Vent på trykk før kalibrering av svart
			lcd.drawString("Trykk for svart...", 0, 1);
			keys.waitForAnyPress();

			float svart = 0;
			// Loop 100 ganger og mål svart for å ungå feilmåling
			for (int i = 0; i<100; i++){
				leserF.fetchSample(dataF, 0);
				svart += dataF[0] * 100;
			}
			svart = svart / 100 + 5; // Finn gjennomsnittsverdi for svart
			lcd.drawString("Svart: " + svart,0,2); // Skriv på skjerm, plass 2

			// Vent på trykk før kalibrering av hvit
			lcd.drawString("Trykk for hvit...", 0, 1);
			keys.waitForAnyPress();
			float hvit = 0;
			// Loop 100 ganger og mål hvit for å ungå feilmåling
			for (int i = 0; i<100; i++){
				leserF.fetchSample(dataF, 0);
				hvit += dataF[0] * 100;
			}
			hvit = hvit / 100 + 5; // Finn gjennomsnittsverdi for hvit
			lcd.drawString("Hvit: " + hvit,0,3); // Skriv på skjerm, plass 3

			// Definer motorer
			hoyre = Motor.B;
			venstre = Motor.C;
			// Sett fart
			hoyre.setSpeed(100);
			venstre.setSpeed(100);

			// Vent på knapp før hovedloop starter
			lcd.drawString("Trykk for å starte", 0, 4);
			keys.waitForAnyPress();
			lcd.clear();

			// Start motorer
			hoyre.forward();
			venstre.forward();

			// Start hovedloop
			while (true){

				// Sjekk om trykket på knapp og avslutt
				if (keys.readButtons()>0){
					lcd.clear();
					hoyre.stop(true);
					venstre.stop(true);
					lcd.drawString("Snakkes!",0,4);
					Thread.sleep(2000);
					break;
				}

				// Hent data fra sensorer
				leserU.fetchSample(dataU, 0);
				leserF.fetchSample(dataF, 0);
				// Sett data i variabler (gang med 100 for å matche gjennomsnittsmålingene)
				dist = dataU[0]*100;
				farge = dataF[0]*100;

				// Skriv data på skjerm
				lcd.drawString("Dist: "+df.format(dist),0,3);
				lcd.drawString("Farge: "+df.format(farge),0,4);


				// Sjekk farge, set speed
				// Er fargen over eller under snittet mellom svart/hvit?
				if (farge>((svart+hvit)/2)){
					hoyre.setSpeed(200);
					venstre.setSpeed(40);
				} else if (farge<((svart+hvit)/2)){
					hoyre.setSpeed(40);
					venstre.setSpeed(200);
				}
			}
		} catch (Exception e){
			// Dersom exception, print den og vent 5 sec før programet stopper
			System.out.println("Error: "+e);
			Thread.sleep(5000);
		}
	}
}