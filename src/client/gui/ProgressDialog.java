package client.gui;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

public class ProgressDialog extends JDialog {
	private JProgressBar progressBar;
	public ProgressDialog(Frame parent) {
		super(parent);
		setSize(400, 100);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBounds(12, 12, 366, 46);
		getContentPane().add(progressBar);
	}
	
	public void setProgress(long complete, long all) {
		progressBar.setValue((int) (complete / all) * 100);
		progressBar.setString(complete + " / " + all);
	}
}
