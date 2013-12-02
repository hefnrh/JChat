package client.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import client.core.ClientCallBack;
import client.core.Messenger;

public class MainFrame extends JFrame implements ClientCallBack {
	private Startup parent;
	private JTabbedPane tabbedPane;
	private PublicPanel publicPanel;
	private ConfigPanel configPanel;
	private JCheckBoxMenuItem chckbxmntmMute;
	private JPopupMenu tabPop;
	private Map<String, PrivatePanel> privatePanel;
	private Messenger m;
	private String name;
	private String host;
	private int port;
	private File fileToReceive = null;
	private String filename;
	
	public MainFrame(Startup parent, String username, String host, int port) {
		this.parent = parent;
		name = username;
		this.host = host;
		this.port = port;

		setIconImage(new ImageIcon(MainFrame.class.getResource("mainIcon.jpg"))
				.getImage());
		setTitle("JChat - " + host + ":" + port + " - " + name);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				MainFrame.this.parent.saveSetting();
				m.logout();
				MainFrame.this.parent.setVisible(true);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				MainFrame.this.parent.saveSetting();
				m.logout();
				MainFrame.this.parent.setVisible(true);
			}
		});
		setResizable(false);
		getContentPane().setLayout(null);
		setSize(800, 620);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ChatPanel cp = (ChatPanel) tabbedPane.getSelectedComponent();
				int index = tabbedPane.getSelectedIndex();
				if (cp != null) {
					cp.loadConfigPanel();
					tabbedPane.setTitleAt(index, cp.getName());
				}
				if (e.getButton() == MouseEvent.BUTTON3)
					tabPop.show(tabbedPane, e.getX(), e.getY());
			}
		});
		tabbedPane.setBounds(10, 10, 774, 552);
		getContentPane().add(tabbedPane);

		privatePanel = new HashMap<>();
		configPanel = new ConfigPanel();
		publicPanel = new PublicPanel(this, null, configPanel, "public");
		tabbedPane.addTab("public", publicPanel);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnOption = new JMenu("option");
		menuBar.add(mnOption);

		chckbxmntmMute = new JCheckBoxMenuItem("mute");
		mnOption.add(chckbxmntmMute);

		JMenu mnHelp = new JMenu("help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("about");
		mntmAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MainFrame.this,
						"author: GuYifan\nhttps://github.com/hefnrh", "ABOUT",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mnHelp.add(mntmAbout);

		tabPop = new JPopupMenu();
		JMenuItem closeTab = new JMenuItem("close");
		closeTab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ChatPanel cp = (ChatPanel) tabbedPane.getSelectedComponent();
				if (cp == publicPanel) {
					JOptionPane.showMessageDialog(MainFrame.this,
							"can't close public channel");
					return;
				}
				int index = tabbedPane.getSelectedIndex() - 1;
				ChatPanel last = (ChatPanel) tabbedPane.getComponentAt(index);
				last.loadConfigPanel();
				tabbedPane.setSelectedComponent(last);
				tabbedPane.remove(cp);
				privatePanel.remove(cp.getName());
			}
		});
		tabPop.add(closeTab);
	}

	public void setMessenger(Messenger m) {
		publicPanel.setMessenger(m);
		for (ChatPanel cp : privatePanel.values())
			cp.setMessenger(m);
		this.m = m;
	}

	public boolean login() {
		// FIXME it's better to throw exception when m == null
		return m == null ? false : m.login(name, host, port);
	}

	public void init(Properties p) {
		// get color
		String value = p.getProperty("color", Color.BLACK.toString())
				.substring(14);
		int pos = value.indexOf("=") + 1;
		int r = Integer.parseInt(value.substring(pos, value.indexOf(",", pos)));
		pos = value.indexOf("=", pos) + 1;
		int g = Integer.parseInt(value.substring(pos, value.indexOf(",", pos)));
		pos = value.indexOf("=", pos) + 1;
		int b = Integer.parseInt(value.substring(pos, value.indexOf("]")));
		Color c = new Color(r, g, b);
		configPanel.setColor(c);
		// get font
		configPanel.setFontName(p.getProperty("font", Font.SANS_SERIF));
		configPanel.setFontSize(Integer.parseInt(p.getProperty("size", "12")));
		configPanel
				.setBold(Boolean.parseBoolean(p.getProperty("bold", "false")));
		configPanel.setItalic(Boolean.parseBoolean(p.getProperty("italic",
				"false")));
		configPanel.setUnderline(Boolean.parseBoolean(p.getProperty(
				"underline", "false")));
		// get main frame setting
		int x = Integer.parseInt(p.getProperty("mainX", "0"));
		int y = Integer.parseInt(p.getProperty("mainY", "0"));
		setLocation(x, y);

		publicPanel.loadConfigPanel();
	}

	@Override
	public void online(String[] names) {
		if (!isVisible()) {
			setVisible(true);
			parent.setVisible(false);
		}
		publicPanel.addUser(names);
	}

	@Override
	public void offline(String[] names) {
		publicPanel.removeUser(names);
	}

	@Override
	public void talkPublic(String from, String content) {
		publicPanel.appendMessage(from, content);
		if (tabbedPane.getSelectedComponent() != publicPanel)
			tabbedPane.setTitleAt(tabbedPane.indexOfComponent(publicPanel),
					"NEW MESSAGE!");
	}

	@Override
	public void talkPrivate(String from, String content) {
		PrivatePanel pp;
		if (!privatePanel.containsKey(from)) {
			pp = new PrivatePanel(this, m, configPanel, from);
			addPrivatePanel(pp);
		} else
			pp = privatePanel.get(from);
		pp.appendMessage(from, content);
		if (tabbedPane.getSelectedComponent() != pp)
			tabbedPane.setTitleAt(tabbedPane.indexOfTab(from), "NEW MESSAGE!");
	}

	@Override
	public void fileRequest(String from, String filename, long filesize) {
		String message;
		if (fileToReceive != null) {
			message = from + " wants to send you: " + filename + "("
					+ filesize + " KB), but you are receiving another file.";
			JOptionPane.showMessageDialog(this, message);
			m.fileRequestResponse(from, false);
			return;
		}
		message = from + " wants to send you: " + filename + "("
				+ filesize + " KB)";
		this.filename = filename;
		int res = JOptionPane.showConfirmDialog(this, message, "receive file?",
				JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION)
			m.fileRequestResponse(from, true);
		else
			m.fileRequestResponse(from, false);
	}

	@Override
	public void fileResponse(String from, boolean accepted, int port) {
		if (!accepted) {
			privatePanel.get(from).fileSendOver();
			JOptionPane.showMessageDialog(this, from + " rejected your request.");
			return;
		}
		m.sendFile(privatePanel.get(from).getFile(), port);
		privatePanel.get(from).fileSendOver();
		JOptionPane.showMessageDialog(this, "transmission over");
	}

	@Override
	public void fileReceive(String sender, int port) {
		JFileChooser jfc = new JFileChooser();
		jfc.setSelectedFile(new File(filename));
		int res = jfc.showSaveDialog(this);
		while (res != JFileChooser.APPROVE_OPTION) {
			JOptionPane.showMessageDialog(this, "you must choose a place to save file");
			res = jfc.showSaveDialog(this);
		}
		fileToReceive = jfc.getSelectedFile();
		m.receiveFile(fileToReceive, port);
		JOptionPane.showMessageDialog(this, "transmission over");
		fileToReceive = null;
	}

	@Override
	public void error(String message) {
		JOptionPane.showMessageDialog(parent, message, "ERROR",
				JOptionPane.ERROR_MESSAGE);
		if (!isVisible()) {
			this.dispose();
		}
	}

	public Properties getSetting(Properties p) {
		p.setProperty("color", configPanel.getSelectedColor().toString());
		p.setProperty("font", configPanel.getSelectedFontName());
		p.setProperty("size", configPanel.getSelectedFontSize() + "");
		p.setProperty("bold", configPanel.isBold() + "");
		p.setProperty("italic", configPanel.isItalic() + "");
		p.setProperty("underline", configPanel.isUnderline() + "");
		p.setProperty("mainX", getX() + "");
		p.setProperty("mainY", getY() + "");
		return p;
	}

	public void addPrivatePanel(PrivatePanel pp) {
		if (privatePanel.containsKey(pp.getName()))
			return;
		privatePanel.put(pp.getName(), pp);
		tabbedPane.addTab(pp.getName(), pp);
	}

	@Override
	public String getName() {
		return name;
	}
}
