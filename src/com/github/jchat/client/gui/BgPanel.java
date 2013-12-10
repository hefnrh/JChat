package com.github.jchat.client.gui;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BgPanel extends JPanel {
	private ImageIcon image;
	private float alpha;
	
	public BgPanel(ImageIcon image, float alpha) {
		this.image = image;
		setLayout(null);
		this.alpha = alpha;
	}
	public BgPanel() {
		this(null, 1);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image == null)
			return;
		Graphics g1 = g.create();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha);
		((Graphics2D)g).setComposite(ac);
		((Graphics2D)g).drawImage(image.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
		super.paintChildren(g1);
	}

	public void setImage(ImageIcon img) {
		image = img;
		repaint();
	}
	
	public String getImage() {
		if (image == null)
			return "";
		return image.toString();
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
		repaint();
	}
	
	public float getAlpha() {
		return alpha;
	}
}
