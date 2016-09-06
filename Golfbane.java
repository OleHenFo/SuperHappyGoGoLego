import lejos.hardware.Button;
import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;

public class Golfbane{
	public static void main(String[] args){
		// Skjerm
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		// Port
		Port s4 = BrickFinder.getDefault().getPort("S4");
		// Definer sensor
		EV3UltrasonicSensor sensor = new EV3UltrasonicSensor(s4);
		SampleProvider reader = sensor.getDistanceMode();
		float[] data = new float[reader.sampleSize()];
		// Set fart
		Motor.B.setSpeed(450);
		Motor.C.setSpeed(450);

		while (true){
			Button.waitForAnyPress();
			reader.fetchSample(data, 0);
			System.out.println("Avstand: " + data[0]);
		}
	}
}