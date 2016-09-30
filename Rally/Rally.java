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
import java.util.*;

public class Rally{
	// Definer motorer
	public static NXTRegulatedMotor hoyre;
	public static NXTRegulatedMotor venstre;

	public static void main(String[] args) throws Exception{

		// Try catch (for feilmeldinger)
		try {
			//variabler
			float dist = 0;
			float fargeV = 0;
			float fargeH = 0;
			float mid = 0;
			boolean svinger = false;
			boolean wait = false;
			int svingV = 0;
			int svingH = 0;
			int sving = 0;
			int kryss = 0;
			int waitCount = 0;
			long time = 0;

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
			/*EV3UltrasonicSensor sensorU = new EV3UltrasonicSensor(s4);
			SampleProvider leserU = sensorU.getDistanceMode();
			float[] dataU = new float[leserU.sampleSize()];*/

			// Definer lyssensor Venstre
			EV3ColorSensor sensorFV = new EV3ColorSensor(s1);
			SampleProvider leserFV = sensorFV.getRGBMode();
			float[] dataFV = new float[leserFV.sampleSize()];

			// Definer lyssensor Høyre
			EV3ColorSensor sensorFH = new EV3ColorSensor(s2);
			SampleProvider leserFH = sensorFH.getRGBMode();
			float[] dataFH = new float[leserFH.sampleSize()];

			// Vent på trykk før kalibrering av svart
			lcd.drawString("Trykk for svart...", 0, 1);
			keys.waitForAnyPress();

			float svart = 0;
			// Loop 100 ganger og mål svart for å ungå feilmåling
			for (int i = 0; i<100; i++){
				leserFV.fetchSample(dataFV, 0);
				svart += dataFV[0] * 100;
			}
			svart = svart / 100 + 5; // Finn gjennomsnittsverdi for svart
			lcd.drawString("Svart: " + svart,0,2); // Skriv på skjerm, plass 2

			// Vent på trykk før kalibrering av hvit
			lcd.drawString("Trykk for hvit...", 0, 1);
			keys.waitForAnyPress();
			float hvit = 0;
			// Loop 100 ganger og mål hvit for å ungå feilmåling
			for (int i = 0; i<100; i++){
				leserFV.fetchSample(dataFV, 0);
				hvit += dataFV[0] * 100;
			}
			hvit = hvit / 100 + 5; // Finn gjennomsnittsverdi for hvit
			lcd.drawString("Hvit: " + hvit,0,3); // Skriv på skjerm, plass 3

			// Definer motorer
			hoyre = Motor.D;
			venstre = Motor.A;
			// Sett fart
			hoyre.setSpeed(300);
			venstre.setSpeed(300);

			// Vent på knapp før hovedloop starter
			lcd.drawString("Trykk for å starte", 0, 4);
			keys.waitForAnyPress();
			lcd.clear();
			Thread.sleep(1000);

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
				//leserU.fetchSample(dataU, 0);
				leserFV.fetchSample(dataFV, 0);
				leserFH.fetchSample(dataFH, 0);
				// Sett data i variabler (gang med 100 for å matche gjennomsnittsmålingene)
				//dist = dataU[0]*100;
				fargeV = dataFV[0]*100;
				fargeH = dataFH[0]*100;

				// Skriv data på skjerm
				//lcd.drawString("Dist: "+df.format(dist),0,3);
				//lcd.drawString("FargeV: "+df.format(fargeV),0,2);
				//lcd.drawString("FargeH: "+df.format(fargeH),0,3);

				// TO SENSOR KODE
				// Sjekk farge, set speed
				// Er fargen over eller under snittet mellom svart/hvit?
				mid = ((hvit+svart)/2);
				if (kryss == 0){
					if (fargeV<(hvit-mid)){			// Sving venstre
						if (!svinger && !wait){
							svingH=0;
							svingV++;
						}
						svinger = true;
						hoyre.setSpeed(320);
						venstre.setSpeed(20);
					} else if (fargeH<(hvit-mid)){	// Sving høyre
						if (!svinger && !wait){
							svingV=0;
							svingH++;
						}
						svinger = true;
						hoyre.setSpeed(20);
						venstre.setSpeed(320);
					} else {						// Kjør
						svinger=false;
						hoyre.setSpeed(330);
						venstre.setSpeed(330);
					}
				} else {							// BYTT SVINGPRIORITET ETTER KRYSS
					if (fargeH<(hvit-mid)){			// Sving høyre
						if (!svinger && !wait){
							svingV=0;
							svingH++;
						}
						svinger = true;
						hoyre.setSpeed(20);
						venstre.setSpeed(320);
					} else if (fargeV<(hvit-mid)){	// Sving venstre
						if (!svinger && !wait){
							svingH=0;
							svingV++;
						}
						svinger = true;
						hoyre.setSpeed(320);
						venstre.setSpeed(20);
					} else {						// Kjør
						svinger=false;
						hoyre.setSpeed(330);
						venstre.setSpeed(330);
					}
				}
				if (svingV>=5 || svingH>=5 && !wait){ // Øk total sving variabel dersom svingV/H > 4
					wait = true;
					svingV=0;
					svingH=0;
					sving++;
				}
				if (sving==5){	// Bytt kryss dersom sving > 5
					sving = 1;
					kryss = (kryss==0)?1:0;
				}
				if (wait){ // Dersom wait, tell til 12000 før ny svingteller
					waitCount++;
					if (waitCount>1200){
						waitCount = 0;
						wait = false;
					}
				}
				lcd.drawString("wait: "+wait,0,1);
				lcd.drawString("svingV: "+svingV,0,2);
				lcd.drawString("svingH: "+svingH,0,3);
				lcd.drawString("sving: "+sving,0,4);
				lcd.drawString("kryss: "+kryss,0,5);
			}
		} catch (Exception e){
			// Dersom exception, print den og vent 5 sec før programet stopper
			System.out.println("Error: "+e);
			Thread.sleep(5000);
		}
	}
}