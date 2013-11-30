package client.gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import client.core.Messenger;

public abstract class ChatPanel extends JPanel {
	
	protected String name;
	protected Messenger m;
	protected ConfigPanel configPanel;
	protected ImageIcon[] faces;
	
	public ChatPanel(Messenger m, ConfigPanel configPanel, String name) {
		this.name = name;
		this.m = m;
		this.configPanel = configPanel;
		setLayout(null);
		setSize(743, 521);
		
		loadConfigPanel();
		
		JLabel lblFace = new JLabel("face");
		lblFace.setBounds(492, 372, 38, 15);
		add(lblFace);
		
		JComboBox<ImageIcon> faceBox = new JComboBox(loadFaces());
		faceBox.setMaximumRowCount(5);
		faceBox.setRenderer(new ListCellRenderer<ImageIcon>() {
			@Override
			public Component getListCellRendererComponent(
					JList<? extends ImageIcon> list, ImageIcon value,
					int index, boolean isSelected, boolean cellHasFocus) {
				return new JLabel(value);
			}
		});
		// TODO add listener
		faceBox.setBounds(529, 369, 58, 21);
		add(faceBox);
	}
	
	public final void loadConfigPanel() {
		add(configPanel);
		configPanel.setLocation(10, 362);
	}
	
	public final String getName() {
		return name;
	}
	
	public final void setMessenger(Messenger m) {
		this.m = m;
	}
	
	private ImageIcon[] loadFaces() {
		if (faces != null)
			return faces;
		// TODO add path
		String[] path = {};
		faces = new ImageIcon[path.length];
		for (int i = 0; i < path.length; ++i) {
			faces[i] = new ImageIcon(MainFrame.class.getResource(path[i]));
		}
		return faces;
	}
	
	public abstract void appendMessage(String speaker, String content);
}
