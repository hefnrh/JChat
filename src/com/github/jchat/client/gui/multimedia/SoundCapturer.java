package com.github.jchat.client.gui.multimedia;

import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class SoundCapturer implements Runnable {
	private OutputStream os;
	private AudioFormat format;
	private TargetDataLine line;
	private boolean stop = false;

	public SoundCapturer(OutputStream os) throws LineUnavailableException {
		this.os = os;
		format = new AudioFormat(8000, 16, 2, true, true);
		line = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(
				TargetDataLine.class, format));
	}

	@Override
	public void run() {
		try {
			final int BUF_SIZE = 512;
			byte[] buf = new byte[BUF_SIZE];
			int read;
			line.open(format);
			line.start();
			while (!stop && (read = line.read(buf, 0, BUF_SIZE)) >= 0) {
				os.write(buf, 0, read);
			}
			line.stop();
			line.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		stop = true;
	}
}
