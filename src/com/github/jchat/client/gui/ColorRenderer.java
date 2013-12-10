package com.github.jchat.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ColorRenderer implements ListCellRenderer<Color> {

	@Override
	public Component getListCellRendererComponent(JList<? extends Color> list,
			Color value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = new JLabel();
		label.setIcon(new MyIcon(value));
		return label;
	}
	
	static class MyIcon implements Icon {
		private Color color;
		public MyIcon(Color c) {
			color = c;
		}
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(color);
			g.fillRect(0, 0, 20, 20);
		}

		@Override
		public int getIconWidth() {
			return 20;
		}

		@Override
		public int getIconHeight() {
			return 20;
		}
		
	}
}
