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
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class Lego{
	// Definer motorer
	public static RegulatedMotor a;
	public static RegulatedMotor b;
	public static RegulatedMotor spin;

	public static void main(String[] args) throws Exception{


		//variabler
		int reloadSpeed = 400;
		int spinSpeed = 10;
		float dist;

		// Decimaltall formatering (for skjerm)
		DecimalFormat df = new DecimalFormat("#.###");

		// Definer boks
		Brick legogo = BrickFinder.getDefault();
		EV3 ev3 = (EV3) BrickFinder.getLocal();

		// Definer porter
		Port s1 = legogo.getPort("S1");
		Port s2 = legogo.getPort("S2");
		Port s3 = legogo.getPort("S3");
		Port s4 = legogo.getPort("S4");

		// Definer ultra sensor
		EV3UltrasonicSensor sensorU = new EV3UltrasonicSensor(s1);
		SampleProvider leserU = sensorU.getDistanceMode();
		float[] dataU = new float[leserU.sampleSize()];

		// Definer skjerm
		TextLCD lcd = ev3.getTextLCD();

		// Definer knapper
		Keys keys = ev3.getKeys();

		// Definer motorer
		a = Motor.A;
		b = Motor.B;
		spin = Motor.C;

		// Sett fart
		a.setSpeed(reloadSpeed);
		b.setSpeed(reloadSpeed);
		spin.setSpeed(spinSpeed);

		// Vent på knapp før hovedloop starter
		lcd.drawString("Trykk for å starte", 0, 2);
		keys.waitForAnyPress();
		lcd.clear();
		Thread.sleep(1000);

		// Start
		while (!false){
			leserU.fetchSample(dataU, 0);
			dist = dataU[0];
			lcd.drawString("UV: " + dist,0,4);
			if (keys.readButtons()>0){
				break;
			}
			if ((dist<1)&&(dist>0.05)){
				lcd.drawString("TARGET AQUIRED",0,2);
				spin.stop(true);

				a.startSynchronization();
				a.rotate(360, true);
				b.rotate(360, true);
				a.endSynchronization();

				a.waitComplete();
				b.waitComplete();

				a.startSynchronization();
				a.rotate(-360, true);
				b.rotate(-360, true);
				a.endSynchronization();

				a.waitComplete();
				b.waitComplete();

			} else {
				lcd.drawString("Searching.......",0,2);
				spin.forward();
			}
		}
	}
}