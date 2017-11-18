package synth;

import com.acid.Statics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Output implements Runnable {
    private static Thread thread = null;
    private static Synthesizer[] tracks;
    private AcidSequencer sequencer;


    public static double volume = 1D;
    public static double SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 2050;
    private float[] buffer = new float[BUFFER_SIZE];
    public static boolean running = false;
    private static Reverb reverb;
    private static Delay delay;
    private static boolean paused = false;
    private AudioDevice ad;

    public static double getVolume() {
        return volume;
    }

    static void setVolume(double value) {
        volume = value;
    }

    public static Delay getDelay() {
        return delay;
    }

    public static Reverb getReverb() {
        return reverb;
    }

    public Output() {
        ad = Gdx.audio.newAudioDevice((int) SAMPLE_RATE, false);
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


    public static boolean isPaused() {
        return paused;
    }

    public static void pause() {
        paused = true;
    }

    public static void resume() {
        paused = false;
    }

    public void run() {
        while (running) {
            if (paused) {
                try {
                    Thread.sleep(25L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            for (int i = 0; i < buffer.length; i += 2) {
                double left = 0.0D;
                double right = 0.0D;

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
                buffer[i] = (float) (left * volume);
                buffer[i + 1] = (float) (right * volume);
            }
            if (Statics.export) {
                if (Statics.exportFile!=null)Statics.exportFile.writeBytes(FloatArray2ByteArray(buffer), true);
            } else {
                if (ad == null) {
                    ad = Gdx.audio.newAudioDevice((int) SAMPLE_RATE, false);
                }
                ad.writeSamples(buffer, 0, BUFFER_SIZE);
            }
        }
        dispose();
    }


    public static byte[] FloatArray2ByteArray(float[] values) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        for (float value : values) {
            buffer.putFloat(value);
        }

        return buffer.array();
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
