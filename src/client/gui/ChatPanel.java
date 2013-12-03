package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import client.core.Messenger;

public abstract class ChatPanel extends JPanel {

	protected String name;
	protected Messenger m;
	protected ConfigPanel configPanel;
	protected JTextPane readPane;
	protected JTextPane writePane;
	protected JComboBox<ImageIcon> faceBox;
	protected JButton btnClear;
	protected JButton btnSend;
	protected ImageIcon[] faces = null;

	private Pattern p1 = Pattern.compile(";");

	public ChatPanel(Messenger m, ConfigPanel configPanel, String name) {
		this.name = name;
		this.m = m;
		this.configPanel = configPanel;
		setLayout(null);
		setSize(774, 521);

		JLabel lblFace = new JLabel("face");
		lblFace.setBounds(492, 372, 38, 15);
		add(lblFace);

		faceBox = new JComboBox<>(loadFaces());
		faceBox.setMaximumRowCount(5);
		faceBox.setRenderer(new ListCellRenderer<ImageIcon>() {
			@Override
			public Component getListCellRendererComponent(
					JList<? extends ImageIcon> list, ImageIcon value,
					int index, boolean isSelected, boolean cellHasFocus) {
				return new JLabel(value);
			}
		});
		faceBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageIcon face = (ImageIcon) faceBox.getSelectedItem();
				writePane.insertIcon(face);
				writePane.grabFocus();
			}
		});
		faceBox.setBounds(529, 369, 58, 21);
		add(faceBox);

		writePane = new JTextPane();
		writePane.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
		writePane.setToolTipText("press enter to send");

		readPane = new JTextPane();
		readPane.setEditable(false);

		btnSend = new JButton("send");

		btnClear = new JButton("clear");
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				readPane.setText("");
			}
		});

	}

	public final void loadConfigPanel() {
		add(configPanel);
		configPanel.setLocation(10, 362);
		repaint();
	}

	@Override
	public final String getName() {
		return name;
	}

	public final void setMessenger(Messenger m) {
		this.m = m;
	}

	private ImageIcon[] loadFaces() {
		if (faces != null)
			return faces;
		String[] path = new String[12];
		for (int i = 0; i < path.length; ++i) {
			path[i] = "face/" + (i + 1) + ".jpg";
		}
		path[9] = "face/10.gif";
		faces = new ImageIcon[path.length];
		for (int i = 0; i < path.length; ++i) {
			faces[i] = new ImageIcon(ChatPanel.class.getResource(path[i]));
		}
		return faces;
	}

	protected final String getWriteText() {
		StringBuilder toSend = new StringBuilder();
		toSend.append("{color:");
		toSend.append(configPanel.getSelectedColor().toString().substring(14));
		toSend.append(";font:");
		toSend.append(configPanel.getSelectedFontName());
		toSend.append(";size:");
		toSend.append(configPanel.getSelectedFontSize());
		toSend.append(";bold:");
		toSend.append(configPanel.isBold());
		toSend.append(";italic:");
		toSend.append(configPanel.isItalic());
		toSend.append(";underline:");
		toSend.append(configPanel.isUnderline());
		toSend.append("}{");
		StyledDocument doc = writePane.getStyledDocument();
		StringBuilder content = new StringBuilder();
		boolean hasFace = false;
		// DFS
		Element root = doc.getRootElements()[0];
		Stack<Element> stack = new Stack<>();
		stack.push(root);
		while (!stack.isEmpty()) {
			root = stack.pop();
			if (root.isLeaf()) {
				switch (root.getName()) {
				case "content":
					int start = root.getStartOffset();
					try {
						content.append(doc.getText(start, root.getEndOffset()
								- start));
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					break;
				case "icon":
					String icon = StyleConstants.getIcon(root.getAttributes())
							.toString();
					icon = icon.substring(icon.lastIndexOf("/") + 1);
					toSend.append(icon);
					toSend.append(',');
					toSend.append(root.getStartOffset());
					toSend.append(';');
					hasFace = true;
					break;
				default:
					break;
				}
			} else {
				for (int i = root.getElementCount() - 1; i >= 0; --i)
					stack.push(root.getElement(i));
			}
		}
		toSend.append('}');
		String text = content.toString().trim();
		if (text.length() < 1 && !hasFace) {
			return null;
		}
		toSend.append(text);
		return toSend.toString();
	}

	public void appendMessage(String speaker, String content) {
		// get font setting
		String[] params = p1.split(content.substring(1, content.indexOf("}")));
		SimpleAttributeSet s = new SimpleAttributeSet();
		// add color
		int pos = params[0].indexOf("=") + 1;
		int r = Integer.parseInt(params[0].substring(pos,
				params[0].indexOf(",", pos)));
		pos = params[0].indexOf("=", pos) + 1;
		int g = Integer.parseInt(params[0].substring(pos,
				params[0].indexOf(",", pos)));
		pos = params[0].indexOf("=", pos) + 1;
		int b = Integer.parseInt(params[0].substring(pos,
				params[0].indexOf("]")));
		s.addAttribute(StyleConstants.Foreground, new Color(r, g, b));
		// add font
		s.addAttribute(StyleConstants.FontFamily,
				params[1].substring(params[1].indexOf(":") + 1));
		// add size
		s.addAttribute(StyleConstants.Size, Integer.parseInt(params[2]
				.substring(params[2].indexOf(":") + 1)));
		// add bold, italic, underline
		s.addAttribute(StyleConstants.Bold, Boolean.parseBoolean(params[3]
				.substring(params[3].indexOf(":") + 1)));
		s.addAttribute(StyleConstants.Italic, Boolean.parseBoolean(params[4]
				.substring(params[4].indexOf(":") + 1)));
		s.addAttribute(StyleConstants.Underline, Boolean.parseBoolean(params[5]
				.substring(params[5].indexOf(":") + 1)));
		// get image
		pos = content.indexOf("{", 1) + 1;
		params = p1.split(content.substring(pos, content.indexOf("}", pos)));
		ImageIcon[] face = null;
		int[] insertPos = null;
		if (params[0].length() > 0) {
			face = new ImageIcon[params.length];
			insertPos = new int[params.length];
			for (int i = 0; i < params.length; ++i) {
				pos = params[i].indexOf(",");
				face[i] = new ImageIcon(MainFrame.class.getResource("face/"
						+ params[i].substring(0, pos)));
				// an image[i] has i images before it
				insertPos[i] = Integer.parseInt(params[i].substring(pos + 1))
						- i;
			}
		}
		// get text
		pos = content.indexOf("}") + 1;
		pos = content.indexOf("}", pos) + 1;
		String text = "";
		if (pos < content.length())
			text = content.substring(pos);
		// insert into read pane
		StyledDocument doc = readPane.getStyledDocument();
		synchronized (this) {
			// append user name
			try {
				doc.insertString(doc.getLength(), speaker + ": ", s);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
			if (face == null) {
				try {
					doc.insertString(doc.getLength(), text + "\n\n", s);
				} catch (BadLocationException e) {
					JOptionPane.showMessageDialog(this,
							"error when display text");
				}
				readPane.setCaretPosition(doc.getLength());
				return;
			}
			int lastPos = 0;
			for (int i = 0, j = 0; j < insertPos.length;) {
				if (i < insertPos[j]) {
					++i;
					continue;
				}
				try {
					if (i > lastPos)
						doc.insertString(doc.getLength(),
								text.substring(lastPos, i), s);
					lastPos = i;
				} catch (BadLocationException e) {
					JOptionPane.showMessageDialog(this,
							"error when display text");
				} catch (Exception e) {
					e.printStackTrace();
				}
				readPane.setCaretPosition(readPane.getText().length());
				readPane.insertIcon(face[j]);
				++j;
			}
			if (lastPos < text.length()) {
				try {
					doc.insertString(doc.getLength(), text.substring(lastPos),
							s);
				} catch (BadLocationException e) {
					JOptionPane.showMessageDialog(this,
							"error when display text");
				}
			}
			try {
				doc.insertString(doc.getLength(), "\n\n", s);
			} catch (BadLocationException e) {
				JOptionPane.showMessageDialog(this, "error when display text");
			}
			readPane.setCaretPosition(doc.getLength());
		}
	}

	public void appendSystemMessage(String message) {
		StringBuilder sb = new StringBuilder("{color:");
		sb.append(Color.RED.toString());
		sb.append(";font:");
		sb.append(Font.DIALOG_INPUT);
		sb.append(";size:");
		sb.append(12);
		sb.append(";bold:true;italic:false;underline:false}{}");
		sb.append(message);
		appendMessage("system", sb.toString());
	}

	protected abstract void sendMessage();
}
