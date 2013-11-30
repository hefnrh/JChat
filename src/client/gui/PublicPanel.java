package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;

import client.core.Messenger;

public class PublicPanel extends JPanel {
	private JComboBox<Color> colorBox;
	private JComboBox<String> fontBox;
	private JToggleButton tglbtnB;
	private JToggleButton tglbtnI;
	private JToggleButton tglbtnU;
	private JComboBox<Integer> sizeBox;
	private JList<String> list;
	private DefaultListModel<String> onlineModel;
	private ImageIcon[] faces;
	private Messenger m;
	private JTextPane textPane;
	private JTextPane textPane_1;
	public PublicPanel(Messenger m) {
		this.m = m;
		setLayout(null);
		setSize(753, 531);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 579, 342);
		add(scrollPane);
		
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 362, 579, 35);
		add(panel);
		panel.setLayout(null);
		
		JLabel lblColor = new JLabel("color");
		lblColor.setBounds(10, 10, 36, 15);
		panel.add(lblColor);
		
		Color[] colors = { Color.BLACK, Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.DARK_GRAY,
				Color.ORANGE, Color.PINK, Color.YELLOW, Color.WHITE };
		colorBox = new JComboBox(colors);
		colorBox.setMaximumRowCount(5);
		colorBox.setRenderer(new ColorRenderer());
		colorBox.setBounds(46, 7, 42, 21);
		panel.add(colorBox);
		
		JLabel lblFont = new JLabel("font");
		lblFont.setBounds(98, 10, 36, 15);
		panel.add(lblFont);
		
		String[] fonts = {Font.MONOSPACED, Font.SANS_SERIF, Font.SERIF, "BankGothic Md BT", "Comic Sans MS",
				"Gabriola", "RussellSquare", "Segoe Script"};
		fontBox = new JComboBox(fonts);
		fontBox.setMaximumRowCount(5);
		fontBox.setRenderer(new ListCellRenderer<String>() {
			@Override
			public Component getListCellRendererComponent(
					JList<? extends String> list, String value, int index,
					boolean isSelected, boolean cellHasFocus) {
				JLabel label = new JLabel(value);
				label.setFont(new Font(value, Font.PLAIN, 12));
				return label;
			}
		});
		fontBox.setBounds(134, 7, 77, 21);
		panel.add(fontBox);
		
		tglbtnB = new JToggleButton("<html><strong>B</strong></html>");
		tglbtnB.setBounds(221, 6, 42, 23);
		panel.add(tglbtnB);
		
		tglbtnI = new JToggleButton("<html><i>I</i></html>");
		tglbtnI.setBounds(273, 6, 42, 23);
		panel.add(tglbtnI);
		
		tglbtnU = new JToggleButton("<html><u>U</u></html>");
		tglbtnU.setBounds(325, 6, 42, 23);
		panel.add(tglbtnU);
		
		JLabel lblSize = new JLabel("size");
		lblSize.setBounds(377, 10, 36, 15);
		panel.add(lblSize);
		
		Integer[] sizes = {4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24};
		sizeBox = new JComboBox(sizes);
		sizeBox.setMaximumRowCount(5);
		sizeBox.setBounds(410, 7, 52, 21);
		panel.add(sizeBox);
		
		JLabel lblFace = new JLabel("face");
		lblFace.setBounds(472, 10, 29, 15);
		panel.add(lblFace);
		
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
		faceBox.setBounds(511, 7, 58, 21);
		panel.add(faceBox);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 407, 579, 81);
		add(scrollPane_1);
		
		textPane_1 = new JTextPane();
		scrollPane_1.setViewportView(textPane_1);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(598, 10, 145, 511);
		add(scrollPane_2);
		
		onlineModel = new DefaultListModel<>();
		list = new JList();
		list.setModel(onlineModel);
		scrollPane_2.setViewportView(list);
		
		JButton btnSend = new JButton("send");
		btnSend.setBounds(393, 498, 93, 23);
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		add(btnSend);
		
		JButton btnClear = new JButton("clear");
		btnClear.setBounds(496, 498, 93, 23);
		add(btnClear);
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
	
	private void sendMessage() {
		
	}
}
