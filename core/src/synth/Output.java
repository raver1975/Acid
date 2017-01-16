package synth;

import com.acid.Statics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;

public class Output implements Runnable {
	private static Thread thread = null;
	public static Synthesizer[] tracks;
	private AcidSequencer sequencer;
	private static double left;
	private static double right;


	private static double volume = 1D;
	public static double SAMPLE_RATE = 44100;
	// public static final double SAMPLE_RATE = 2050;
	public static final int BUFFER_SIZE = 2050;
	float[] buffer1 = new float[BUFFER_SIZE];
	public static boolean running = false;
	private static boolean pause = false;
	private static boolean paused = false;
	private static Reverb reverb;
	private static Delay delay;

	private AudioDevice ad;



	public static double getVolume() {
		return volume;
	}

	public static void setVolume(double value) {
		volume = value;
	}

	public static Delay getDelay() {
		return delay;
	}

	public static Reverb getReverb() {
		return reverb;
	}

	public Output() {
		ad= Gdx.audio.newAudioDevice((int) SAMPLE_RATE, false);
		tracks = new Synthesizer[Statics.drumsOn ? 2 : 1];
		BasslineSynthesizer tb = new BasslineSynthesizer();
		tracks[0] = tb;
		RhythmSynthesizer tr = new RhythmSynthesizer();
		if (Statics.drumsOn)
			tracks[1] = tr;

		delay = new Delay();
		reverb = new Reverb();

		this.sequencer = new AcidSequencer(tb, tr, this);

		thread = new Thread(this);
		thread.setPriority(10);

	}

	public void start() {
		running = true;
		thread.start();
	}

	public void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}



	public static void unlock() {
		pause = false;
		paused = false;
	}

	public static void lock() {
		pause = true;
		while (!paused)
			;
	}

	public static boolean isPaused() {
		return paused;
	}

	public static void pause() {
		pause = true;
	}

	public static void resume() {
		unlock();
	}

	public void run() {
		while (running) {
			// Long time=System.currentTimeMillis();
			if (pause) {
				paused = true;
				try {
					Thread.sleep(25L);
				} catch (InterruptedException e) {
				}
				continue;
			}
			for (int i = 0; i < buffer1.length; i += 2) {
				left = 0.0D;
				right = 0.0D;

				this.sequencer.tick();
				if (Statics.drumsOn) {
					double[] tmp = null;
					tmp = tracks[1].stereoOutput();

					delay.input(tmp[2]);
					reverb.input(tmp[3]);

					left += tmp[0];
					right += tmp[1];
				}
				if (Statics.synthOn) {
					double[] tmp = null;
					tmp = tracks[0].stereoOutput();

					delay.input(tmp[2]);
					reverb.input(tmp[3]);

					left += tmp[0];
					right += tmp[1];
				}

				double[] del = delay.output();
				left += del[0];
				right += del[1];

				double[] rev = reverb.process();
				left += rev[0];
				right += rev[1];

				if (left > 1.0D)
					left = 1.0D;
				else if (left < -1.0D)
					left = -1.0D;
				if (right > 1.0D)
					right = 1.0D;
				else if (right < -1.0D) {
					right = -1.0D;
				}
				buffer1[i] = (float) (left * volume);
				buffer1[i + 1] = (float) (right * volume);
			}
			if (ad!=null)ad.writeSamples(buffer1, 0, BUFFER_SIZE);
		else{
			ad= Gdx.audio.newAudioDevice((int) SAMPLE_RATE, false);
				System.out.println(ad);
		}
		}
		dispose();
	}

	public void dispose() {
		running = false;
		ad.dispose();

	}

	public Synthesizer getTrack(int i) {
		return tracks[i];
	}

	public AcidSequencer getSequencer() {
		return this.sequencer;
	}

}
