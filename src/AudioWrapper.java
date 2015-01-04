package framework;

//XXX thx to LSP from the Minim comment section
import java.io.FileInputStream;
import java.io.InputStream;

import ddf.minim.AudioInput;
import ddf.minim.AudioMetaData;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;

import framework.EventLogger;

import ddf.minim.AudioListener;
import ddf.minim.AudioSource;
import ddf.minim.analysis.BeatDetect;

class Listener implements AudioListener {
	AudioWrapper myMain=null;
	BeatDetect beat;
	float kickSize, snareSize, hatSize;
	AudioSource as;
	long time=0;
	int sensitivity=300; // how many time to wait between beats in ms
	String onda= "";

	EventLogger logger;

	public Listener(EventLogger l) {
		super();
		logger = l;
		logger.flow("Listener init");
		// TODO Auto-generated constructor stub
	}

	public Listener(AudioWrapper myMain, AudioSource as, EventLogger l) {
		super();
		logger = l;
		logger.flow("Listener init");

		this.myMain=myMain;
		beat = new BeatDetect(as.bufferSize(), as.sampleRate());
		beat.setSensitivity(sensitivity);
		beat.detectMode(beat.SOUND_ENERGY);

		this.as=as;
		kickSize = snareSize = hatSize = 16;
		time = System.currentTimeMillis();
	}

	@Override
	public void samples(float[] samp) {
		for(int i=0;i<samp.length;i++){
			onda+=samp[i];
		}

		beat.detect(samp);
		if(System.currentTimeMillis() - time > sensitivity) {
			if(beat.isHat()) {
				logger.info("HAT");
			}
			if(beat.isOnset()) {
				logger.info("BEAT");
				time=System.currentTimeMillis();
			}
			if(beat.isKick()) {
				time=System.currentTimeMillis();

				logger.info("kick");
			}
			if(beat.isSnare()) {
				logger.info("SNARE");
			}
		}
	}

	@Override
	public void samples (float[] sampL, float[] sampR) {
		myMain.draw();
		String tmp = "";

		for(int i=0;i<sampL.length;i++){
			onda += sampL[i] + ",";
		}
		//logger.debug(onda);
		//logger.debug(System.currentTimeMillis() - time);

		//logger.debug("MEAN : " + mean(sampL));
		beat.detect(sampL);

		if(System.currentTimeMillis() - time > sensitivity){
			if(beat.isHat()) {
				logger.debug("HAT");
			}
			if(beat.isOnset()) {
				logger.debug("BEAT");
				onda += "beat";
				time = System.currentTimeMillis();
			}
			if(beat.isKick()) {
				time=System.currentTimeMillis();
				logger.debug("kick");
			}

			if(beat.isSnare()){
				logger.debug("SNARE");
			}
		}

	}
	public static float mean(float[] p) {
		float sum = 0; // sum of all the elements
		for (int i=0; i<p.length; i++) {
			sum += p[i];
		}
		return sum / p.length;
	}

}

public class AudioWrapper {
	private EventLogger logger;

	Minim minim;
	private static AudioPlayer player;
	private static AudioInput input;
	private AudioMetaData meta;
	private BeatDetect beat;
	private static boolean mic=false;

	Listener ml;
	String filename = "../resources/audio/bgm/bgm.mp3";

	public AudioWrapper (EventLogger l) {
		logger = l;
		logger.flow("AudioWrapper init.");

		init();
	}

	private void init() {
		Minim minim= new Minim(this);

		if(mic){
			input = minim.getLineIn(Minim.MONO);
			ml = new Listener(this, input, logger);
			input.addListener(ml);
		}
		else{
			player = minim.loadFile(filename);
			player.play();
			ml = new Listener(this, player, logger);
			player.addListener(ml);
		}
	}

	public void stop() {
		player.close();
		minim.stop();
	}

	public void draw() {// this function is never called
		//logger.debug("DRAW call");
	}

	public String sketchPath (String fileName) {
		logger.debug("sketchPath:" + fileName);
		return fileName;
	}

	public InputStream createInput(String fileName) {
		logger.debug("createInput:" + fileName);
		InputStream in2;
		try {
			in2 = new FileInputStream(fileName);
			logger.debug("InputStream: created! " + fileName);
			return in2;
		} catch (Exception ex) {
			logger.debug("Error Catch Triggered: " + ex);
			in2 = null;
		}
		return in2;
	}
}
