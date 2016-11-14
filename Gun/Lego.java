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
	public static RegulatedMotor a = Motor.A;
	public static RegulatedMotor b = Motor.B;
	public static RegulatedMotor spin = Motor.C;

	private static int reloadSpeed = 400;
	private static int spinSpeed = 10;
	private static float dist;
	private static boolean ladd = false;

	private DecimalFormat df = new DecimalFormat("#.###");

	// Definer boks
	static Brick legogo = BrickFinder.getDefault();
	static EV3 ev3 = (EV3) BrickFinder.getLocal();

	// Definer porter
	static Port s1 = legogo.getPort("S1");
	Port s2 = legogo.getPort("S2");
	Port s3 = legogo.getPort("S3");
	Port s4 = legogo.getPort("S4");

	// Definer ultra sensor
	static EV3UltrasonicSensor sensorU = new EV3UltrasonicSensor(s1);
	static SampleProvider leserU = sensorU.getDistanceMode();
	static float[] dataU = new float[leserU.sampleSize()];

	// Definer skjerm
	public static TextLCD lcd = ev3.getTextLCD();

	// Definer knapper
	public static Keys keys = ev3.getKeys();

	// Definer motorer
	/*a = Motor.A;
	b = Motor.B;
	spin = Motor.C;*/

	//Klasse for å lade
	public static void lade(){

		lcd.drawString("Reloading",0,2);

		spin.stop(true);

		a.startSynchronization();
		a.rotate(360, true);
		b.rotate(360, true);
		a.endSynchronization();

		a.waitComplete();
		b.waitComplete();

		a.startSynchronization();
		a.rotate(-320, true);
		b.rotate(-320, true);
		a.endSynchronization();

		a.waitComplete();
		b.waitComplete();

		lcd.drawString("Reloading: Complete",0,2);
		lcd.clear();
		ladd = true;
	}

	public static void ild(){
		lcd.drawString("TARGET AQUIRED",0,2);

		spin.rotate(15, true);
		try{
			Thread.sleep(1500);
		}
		catch(Exception e){
		}

		a.startSynchronization();
		a.rotate(-40, true);
		b.rotate(-40, true);
		a.endSynchronization();

		a.waitComplete();
		b.waitComplete();

		lcd.drawString("TARGET DESTROYED",0,2);

		ladd = false;
	}

	public static void main(String[] args) throws Exception{


		//variabler


		// Decimaltall formatering (for skjerm)




		// Sett fart
		a.setSpeed(reloadSpeed);
		b.setSpeed(reloadSpeed);
		spin.setSpeed(spinSpeed);

		// Vent på knapp for hovedloop starter
		lcd.drawString("Trykk for å starte", 0, 2);
		keys.waitForAnyPress();
		lcd.clear();
		Thread.sleep(1000);



		// Start

		while(true){

			if(ladd == false){
				lade();
			}

			while(ladd == true){
				if (keys.readButtons()>0){
								break;
				}

				leserU.fetchSample(dataU, 0);
				dist = dataU[0];
				lcd.drawString("UV: " + dist,0,4);
				if((dist<0.8)&&(dist>0.05)){
					ild();
				}
				spin.forward();
			}
			/*if ((dist<1)&&(dist>0.05)){
				lcd.drawString("TARGET AQUIRED",0,2);
				spin.stop(true);

				a.startSynchronization();
				a.rotate(-40, true);
				b.rotate(-40, true);
				a.endSynchronization();

				a.waitComplete();
				b.waitComplete();

				a.startSynchronization();
				a.rotate(360, true);
				b.rotate(360, true);
				a.endSynchronization();

				a.waitComplete();
				b.waitComplete();

				a.startSynchronization();
				a.rotate(-320, true);
				b.rotate(-320, true);
				a.endSynchronization();

				a.waitComplete();
				b.waitComplete();

			} else {
				lcd.drawString("Searching.......",0,2);
				spin.forward();
			}*/
		}//end while(true)
	}
}
