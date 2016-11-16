import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3UltrasonicSensor;
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

	//variabler
	private static int reloadSpeed = 400;
	private static int spinSpeed = 10;
	private static float dist;
	private static boolean ladd = false;
	private static int antSkudd = 0;

	private DecimalFormat df = new DecimalFormat("#.###");

	// Definer boks
	static Brick legogo = BrickFinder.getDefault();
	static EV3 ev3 = (EV3) BrickFinder.getLocal();
	/**
	* Spør om hva forskjellen mellom getDefault() og getLocal()
	*/

	// Definer porter
	static Port s1 = legogo.getPort("S1");

	// Definer ultra sensor
	static EV3UltrasonicSensor sensorU = new EV3UltrasonicSensor(s1);
	static SampleProvider leserU = sensorU.getDistanceMode();
	static float[] dataU = new float[leserU.sampleSize()];

	// Definer skjerm
	public static TextLCD lcd = ev3.getTextLCD();

	// Definer knapper
	public static Keys keys = ev3.getKeys();


	//Metode for å lade
	public static void lade(){
		lcd.clear();
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

		lcd.clear();
		lcd.drawString("Reloading: Complete",0,2);
		ladd = true;
	}

	//Metode for avfyring
	public static void ild(){
		lcd.clear();
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

		lcd.clear();
		lcd.drawString("TARGET DESTROYED",0,2);

		antSkudd++;

		ladd = false;
	}

	// Main metode
	public static void main(String[] args) throws Exception{

		// Sett fart
		a.setSpeed(reloadSpeed);
		b.setSpeed(reloadSpeed);
		spin.setSpeed(spinSpeed);

		// Vent på knapp før programmet fortsetter
		lcd.drawString("Trykk for å starte", 0, 2);
		keys.waitForAnyPress();
		lcd.clear();
		Thread.sleep(500);

		// Start hovedloop
		while(!false){

			//Er magasinet tomt, stopp hovedloopen
			if(antSkudd >= 7)break;

			//Dersom våpenet ikke er ladd, ladd våpenet
			if(!ladd){
				lade();
			}

			// Søk etter mål mens våpenet er ladd
			while(ladd){
				if (keys.readButtons()>0){
					break;
				}

				leserU.fetchSample(dataU, 0);
				dist = dataU[0];
				lcd.drawString("Searching...",0,2);
				lcd.drawString("UV: " + dist,0,4);
				lcd.drawString("Ammo: "+(7-antSkudd),0,5);
				if((dist<0.8)&&(dist>0.05)){
					ild();
				}
				spin.forward();
			}

			// Stop hovedloop dersom knapp trykkes
			if (keys.readButtons()>0){
				break;
			}
		}//end hovedloop

		//Kjøres før programmet avslutter
		lcd.clear();
		lcd.drawString("Programmet avsluttes", 0, 2);
		try{
			Thread.sleep(3000);
		}
		catch(Exception e){
		}
	}
}
