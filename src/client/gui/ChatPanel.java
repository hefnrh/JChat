package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.Stack;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
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
	protected ImageIcon[] faces = null;

	private Pattern p = Pattern.compile(";");

	public ChatPanel(Messenger m, ConfigPanel configPanel, String name) {
		this.name = name;
		this.m = m;
		this.configPanel = configPanel;
		setLayout(null);
		setSize(774, 521);

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
		toSend.append(configPanel.isUnderine());
		toSend.append("}{");
		StyledDocument doc = writePane.getStyledDocument();
		StringBuilder content = new StringBuilder();
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
		toSend.append(content);
		return toSend.toString();
	}

	public void appendMessage(String speaker, String content) {
		// get font setting
		String[] params = p.split(content.substring(1, content.indexOf("}")));
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
		params = p.split(content.substring(pos, content.indexOf("}", pos)));
	}
}
