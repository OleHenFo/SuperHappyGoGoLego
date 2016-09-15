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

public class Tunnel{
	public static void main(String[] args) throws Exception{
		try {
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
			
			// Definer trykk
			EV3TouchSensor sensorT = new EV3TouchSensor(s1);
			SampleProvider leserT = sensorT.getTouchMode();
			float[] dataT = new float[leserT.sampleSize()];

			// Vent på knapp enter for å starte
			//lcd.drawString("Trykk enter starte", 0, 1);
			//keys.waitForAnyPress();

			// Hoved loop
			//lcd.drawString("Kjorer", 0,5);

			Motor.A.setSpeed(100);
			Motor.B.setSpeed(300);
			Motor.C.setSpeed(300);
			Motor.D.setSpeed(100);

			/*Motor.A.forward();
			Motor.B.forward();
			Motor.C.forward();
			Motor.D.forward();*/

			while (keys.readButtons()==0){

				leserU.fetchSample(dataU, 0);
				leserF.fetchSample(dataF, 0);
				leserL.fetchSample(dataL, 0);
				leserT.fetchSample(dataT, 0);
				lcd.drawString("Dist: "+dataU[0],0,2);
				lcd.drawString("Farge: "+dataF[0],0,3);
				lcd.drawString("Lyd: "+dataL[0],0,4);
				lcd.drawString("Trykk: "+dataT[0],0,5);

				Thread.sleep(10);
			}
		} catch (Exception e){
			System.out.println("Error: "+e);
			Thread.sleep(5000);
		}
	}
}
