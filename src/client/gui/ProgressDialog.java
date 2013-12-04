package client.gui;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

public class ProgressDialog extends JDialog {
	private JProgressBar progressBar;
	public ProgressDialog(Frame parent) {
		super(parent);
		setSize(400, 100);
		setResizable(false);
		setTitle("transmitting file");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		progressBar.setBounds(12, 12, 366, 46);
		getContentPane().add(progressBar);
	}
	
	public void setProgress(long complete, long all) {
		progressBar.setString(complete + " / " + all);
		progressBar.setValue((int) (complete * 100 / all)); 
	}
}
