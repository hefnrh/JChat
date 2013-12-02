package client.gui.multimedia;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer implements Runnable {

	private AudioFormat format;
	private AudioInputStream ais;
	private SourceDataLine line;

	public SoundPlayer(URL fileToPlay) throws UnsupportedAudioFileException,
			IOException, LineUnavailableException {
		ais = AudioSystem.getAudioInputStream(fileToPlay);
		format = ais.getFormat();
		line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(
				SourceDataLine.class, format));
	}

	@Override
	synchronized public void run() {
		byte[] buffer = new byte[8192];
		int read;
		try {
			line.open(format);
			line.start();
			while ((read = ais.read(buffer)) >= 0)
				line.write(buffer, 0, read);
			line.drain();
			line.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		};
		
	}

}
