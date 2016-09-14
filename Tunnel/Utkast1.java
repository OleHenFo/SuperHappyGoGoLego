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

public class Utkast1{
	public static void main(String[] args) throws Exception{
		//variabler
		boolean go = true;
		float dist = 0;
		float retning = 1;
		float press = 0;
		float farge = 0;
		float sound = 0;

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
		SampleProvider leserF = sensorF.getMode("RGB");
		float[] dataF = new float[leserF.sampleSize()];

		// Definer lydsensor
		NXTSoundSensor sensorL = new NXTSoundSensor(s3);
		SampleProvider leserL = sensorL.getDBMode();
		float[] dataL = new float[leserL.sampleSize()];

		// Definer trykksensor
		NXTTouchSensor sensorT = new NXTTouchSensor(s2);
		SampleProvider leserT = sensorT.getTouchMode();
		float[] dataT = new float[leserT.sampleSize()];

		// Vent på knapp enter for å starte
		lcd.drawString("Trykk enter starte", 0, 1);
		keys.waitForAnyPress();

		Motor.A.setSpeed(300);
		Motor.D.setSpeed(300);

		Motor.A.forward();
		Motor.D.forward();

		while (keys.readButtons()==0){

			//Lager valg for trykksensor
			leserT.fetchSample(dataT, 0);
			press = dataT[0];
			if(true){
				break;
			}

			//Lager valg for lydsensor
			leserL.fetchSample(dataL, 1);
			sound = dataL[0];
			if(sound > 50){
				Thread.sleep(5000);
			}

			//Lager valg for avstandssensor
			leserU.fetchSample(dataU, 0);
			dist = dataU[0];
			if(dist > 0.06 || dist < 0.04){
				//Leser avstand på nytt
				if(dist < 0.04 && retning == 1){
					Motor.B.setSpeed(50);
					Motor.C.setSpeed(100);
				}

				else if(dist < 0.04 && retning != 1){
					Motor.B.setSpeed(100);
					Motor.C.setSpeed(50);
				}
				else if(dist > 0.06 && retning == 1){
					Motor.B.setSpeed(100);
					Motor.C.setSpeed(50);
				}
				else if(dist > 0.06 && retning != 1){
					Motor.B.setSpeed(50);
					Motor.C.setSpeed(100);
				}
			}
			else if(retning == 1){
				Motor.B.setSpeed(300);
				Motor.C.setSpeed(300);

				Motor.B.forward();
				Motor.C.forward();
			}
			else{
				Motor.B.setSpeed(300);
				Motor.C.setSpeed(300);

				Motor.B.backward();
				Motor.C.backward();
			}

			//Lager valg for fargesensor
			leserF.fetchSample(dataF, 0);
			farge = dataF[0];
			if(farge == 0){
				retning = (retning == 1) ? 2:1;
			}
		}
	}
}