package com.github.jchat.client.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListRenderer implements ListCellRenderer<PublicPanel.User> {
	
	public final Color BLACK = Color.RED;
	public final Color NORMAL = Color.BLACK;
	@Override
	public Component getListCellRendererComponent(JList<? extends PublicPanel.User> list,
			PublicPanel.User value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = new JLabel(value.name);
		if (value.isBlack())
			label.setForeground(BLACK);
		else
			label.setForeground(NORMAL);
		if (isSelected) {
			label.setOpaque(true);
		} else
			label.setOpaque(false);
		return label;
	}

}
