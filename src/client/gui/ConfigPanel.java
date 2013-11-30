package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;

public class ConfigPanel extends JPanel {

	private JComboBox<Color> colorBox;
	private JComboBox<String> fontBox;
	private JComboBox<Integer> sizeBox;
	private JToggleButton tglbtnB;
	private JToggleButton tglbtnI;
	private JToggleButton tglbtnU;
	
	public ConfigPanel() {
		setSize(472, 35);
		setLayout(null);
		JLabel lblColor = new JLabel("color");
		lblColor.setBounds(10, 10, 36, 15);
		add(lblColor);
		
		Color[] colors = { Color.BLACK, Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.DARK_GRAY,
				Color.ORANGE, Color.PINK, Color.YELLOW, Color.WHITE };
		colorBox = new JComboBox(colors);
		colorBox.setMaximumRowCount(5);
		colorBox.setRenderer(new ColorRenderer());
		colorBox.setBounds(46, 7, 42, 21);
		add(colorBox);
		
		JLabel lblFont = new JLabel("font");
		lblFont.setBounds(98, 10, 36, 15);
		add(lblFont);
		
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
		add(fontBox);
		
		tglbtnB = new JToggleButton("<html><b>B</b></html>");
		tglbtnB.setBounds(221, 6, 42, 23);
		add(tglbtnB);
		
		tglbtnI = new JToggleButton("<html><i>I</i></html>");
		tglbtnI.setBounds(273, 6, 42, 23);
		add(tglbtnI);
		
		tglbtnU = new JToggleButton("<html><u>U</u></html>");
		tglbtnU.setBounds(325, 6, 42, 23);
		add(tglbtnU);
		
		JLabel lblSize = new JLabel("size");
		lblSize.setBounds(377, 10, 36, 15);
		add(lblSize);
		
		Integer[] sizes = {4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24};
		sizeBox = new JComboBox(sizes);
		sizeBox.setMaximumRowCount(5);
		sizeBox.setBounds(410, 7, 52, 21);
		add(sizeBox);
	}
	
	public Color getSelectedColor() {
		return (Color) colorBox.getSelectedItem();
	}
	
	public String getSelectedFontName() {
		return (String) fontBox.getSelectedItem();
	}
	
	public int getSelectedFontSize() {
		return (int) sizeBox.getSelectedItem();
	}
	
	public boolean isBold() {
		return tglbtnB.isSelected();
	}
	
	public boolean isItalic() {
		return tglbtnI.isSelected();
	}
	
	public boolean isUnderine() {
		return tglbtnU.isSelected();
	}
}
