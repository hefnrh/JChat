package com.github.jchat.client.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import com.github.jchat.client.core.ClientCallBack;
import com.github.jchat.client.core.Messenger;
import com.github.jchat.client.gui.multimedia.SoundPlayer;

public class MainFrame extends JFrame implements ClientCallBack {
	private Startup parent;
	private BgPanel bgPanel;
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
	private volatile File fileToReceive = null;
	private long fileSize = 0;
	private ExecutorService pool = Executors.newCachedThreadPool();
	private ProgressDialog sendBar = null;
	private ProgressDialog recvBar = null;
	private VoiceDialog voiceDialog = null;
	private volatile boolean inVoiceChat = false;
	private JFileChooser bgChooser;

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

		UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
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
		bgPanel = new BgPanel();
		bgPanel.setBounds(0, 0, 800, 600);
		bgPanel.add(tabbedPane);
		getContentPane().add(bgPanel);

		privatePanel = new HashMap<>();
		configPanel = new ConfigPanel();
		publicPanel = new PublicPanel(this, null, configPanel, "public");
		tabbedPane.addTab("public", publicPanel);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnOption = new JMenu("option");
		menuBar.add(mnOption);

		JMenuItem bgSelect = new JMenuItem("background");
		bgSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectBackground();
			}
		});
		mnOption.add(bgSelect);

		JMenuItem alphaSel = new JMenuItem("alpha");
		alphaSel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				float alpha;
				do {
					try {
						alpha = Float.parseFloat(JOptionPane.showInputDialog(
								MainFrame.this, "Choose alpha between 0 and 1",
								bgPanel.getAlpha()));
						if (alpha < 0 || alpha > 1)
							throw new NumberFormatException();
					} catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(MainFrame.this,
								"invalid input", "ERROR",
								JOptionPane.ERROR_MESSAGE);
						continue;
					}
					break;
				} while (true);
				bgPanel.setAlpha(alpha);
			}
		});
		mnOption.add(alphaSel);

		chckbxmntmMute = new JCheckBoxMenuItem("mute");
		mnOption.add(chckbxmntmMute);

		JMenu mnHelp = new JMenu("help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("about");
		mntmAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JLabel label = new JLabel("<html><a href=\"\">author: GuYifan</a></html>");
				label.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							try {
								Desktop.getDesktop().browse(new URI("https://github.com/hefnrh"));
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				});
				label.setToolTipText("https://github.com/hefnrh");
				JOptionPane.showMessageDialog(MainFrame.this, label, "ABOUT",
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
		if (!Boolean.parseBoolean(p.getProperty("rememberSetting", "false"))) {
			publicPanel.loadConfigPanel();
			return;
		}
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
		// get mute setting
		chckbxmntmMute.setSelected(Boolean.parseBoolean(p.getProperty("mute",
				"false")));
		// get background
		String bg = p.getProperty("background", "");
		if (bg.length() > 0) {
			ImageIcon img = new ImageIcon(bg);
			bgPanel.setImage(img);
		}
		bgPanel.setAlpha(Float.parseFloat(p.getProperty("alpha", "1")));

		publicPanel.loadConfigPanel();
	}

	@Override
	public void online(String[] names) {
		if (!chckbxmntmMute.isSelected())
			playNotify();
		publicPanel.addUser(names);
		if (!isVisible()) {
			setVisible(true);
			parent.setVisible(false);
		}
	}

	@Override
	public void offline(String[] names) {
		if (!chckbxmntmMute.isSelected())
			playNotify();
		publicPanel.removeUser(names);
	}

	@Override
	public void talkPublic(String from, String content) {
		if (publicPanel.isBlack(from))
			return;
		publicPanel.appendMessage(from, content);
		if (tabbedPane.getSelectedComponent() != publicPanel)
			tabbedPane.setTitleAt(tabbedPane.indexOfComponent(publicPanel),
					"NEW MESSAGE!");
	}

	@Override
	public void talkPrivate(String from, String content) {
		if (publicPanel.isBlack(from))
			return;
		if (!chckbxmntmMute.isSelected())
			playNotify();
		PrivatePanel pp;
		if (!privatePanel.containsKey(from)) {
			pp = new PrivatePanel(name, m, configPanel, from);
			addPrivatePanel(pp);
		} else
			pp = privatePanel.get(from);
		pp.appendMessage(from, content);
		if (tabbedPane.getSelectedComponent() != pp)
			tabbedPane.setTitleAt(tabbedPane.indexOfTab(from), "NEW MESSAGE!");
	}

	@Override
	public void fileRequest(String from, String filename, long filesize) {
		if (publicPanel.isBlack(from)) {
			m.fileRequestResponse(from, false);
			return;
		}
		if (!chckbxmntmMute.isSelected())
			playNotify();
		String message;
		if (fileToReceive != null) {
			message = from + " wants to send you: " + filename + "("
					+ (filesize >> 10)
					+ " KB), but you are receiving another file.";
			JOptionPane.showMessageDialog(this, message);
			m.fileRequestResponse(from, false);
			return;
		}
		message = from + " wants to send you: " + filename + "("
				+ (filesize >> 10) + " KB)";
		fileSize = filesize;
		int res = JOptionPane.showConfirmDialog(this, message, "receive file?",
				JOptionPane.YES_NO_OPTION);
		if (res != JOptionPane.YES_OPTION) {
			m.fileRequestResponse(from, false);
			return;
		}
		JFileChooser jfc = new JFileChooser();
		jfc.setSelectedFile(new File(filename));
		res = jfc.showSaveDialog(this);
		if (res != JFileChooser.APPROVE_OPTION) {
			m.fileRequestResponse(from, false);
			return;
		}
		fileToReceive = jfc.getSelectedFile();
		m.fileRequestResponse(from, true);
	}

	@Override
	public void fileResponse(String from, boolean accepted, int port) {
		if (!chckbxmntmMute.isSelected())
			playNotify();
		if (!accepted) {
			privatePanel.get(from).fileSendOver();
			JOptionPane.showMessageDialog(this, from
					+ " rejected your request.");
			return;
		}
		if (sendBar == null)
			sendBar = new ProgressDialog(this);
		sendBar.setTitle("send: " + privatePanel.get(from).getFile().getName());
		sendBar.setVisible(true);
		m.sendFile(privatePanel.get(from).getFile(), port);
		privatePanel.get(from).fileSendOver();
		sendBar.dispose();
		JOptionPane.showMessageDialog(this, "transmission over");
	}

	@Override
	public void fileReceive(String sender, int port) {
		if (recvBar == null)
			recvBar = new ProgressDialog(this);
		recvBar.setTitle("receive: " + fileToReceive.getName());
		recvBar.setVisible(true);
		m.receiveFile(fileToReceive, port, fileSize);
		recvBar.dispose();
		JOptionPane.showMessageDialog(this, "transmission over");
		fileToReceive = null;
	}

	@Override
	public void error(String message) {
		if (!chckbxmntmMute.isSelected())
			playNotify();
		JOptionPane.showMessageDialog(parent, message, "ERROR",
				JOptionPane.ERROR_MESSAGE);
		if (!isVisible()) {
			this.dispose();
		}
	}

	@Override
	public void setSendProgress(long complete, long all) {
		sendBar.setProgress(complete, all);
	}

	public void setRecvProgress(long complete, long all) {
		recvBar.setProgress(complete, all);
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
		p.setProperty("mute", chckbxmntmMute.isSelected() + "");
		p.setProperty("background", bgPanel.getImage());
		p.setProperty("alpha", bgPanel.getAlpha() + "");
		return p;
	}

	public void addPrivatePanel(PrivatePanel pp) {
		if (privatePanel.containsKey(pp.getName()))
			return;
		privatePanel.put(pp.getName(), pp);
		tabbedPane.addTab(pp.getName(), pp);
	}

	@Override
	/**
	 * @return username
	 */
	public String getName() {
		return name;
	}

	private void playNotify() {
		try {
			pool.execute(new SoundPlayer(MainFrame.class
					.getResource("notify.wav")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void voiceRequest(String speaker) {
		if (publicPanel.isBlack(speaker)) {
			m.voiceRespond(speaker, false);
			return;
		}
		if (!chckbxmntmMute.isSelected())
			playNotify();
		if (inVoiceChat) {
			JOptionPane
					.showMessageDialog(
							this,
							speaker
									+ " wants to voice chat with you, but you are voice chatting, so rejected.");
			m.voiceRespond(speaker, false);
			return;
		}
		int res = JOptionPane.showConfirmDialog(this, speaker
				+ " wants to voice chat with you", "voice chat?",
				JOptionPane.YES_NO_OPTION);
		if (res != JOptionPane.YES_OPTION) {
			m.voiceRespond(speaker, false);
			return;
		}
		inVoiceChat = true;
		m.voiceRespond(speaker, true);
	}

	@Override
	public void voiceResponse(String listener, boolean accepted, int outPort,
			int inPort) {
		((PrivatePanel) privatePanel.get(listener)).setUnreqed();
		if (!accepted) {
			JOptionPane.showMessageDialog(this, listener
					+ " rejected your request");
			return;
		}
		inVoiceChat = true;
		if (voiceDialog == null)
			voiceDialog = new VoiceDialog(m, listener);
		else
			voiceDialog.setListener(listener);
		voiceDialog.setVisible(true);
		m.voiceChat(outPort, inPort);
		inVoiceChat = false;
		System.out.println("voice over");
	}

	@Override
	public void voiceRecv(String speaker, int outPort, int inPort) {
		if (voiceDialog == null)
			voiceDialog = new VoiceDialog(m, speaker);
		else
			voiceDialog.setListener(speaker);
		voiceDialog.setVisible(true);
		m.voiceChat(outPort, inPort);
		inVoiceChat = false;
		System.out.println("voice over");
	}

	public void voiceOver() {
		JOptionPane.showMessageDialog(this, "voice chat over");
		if (voiceDialog != null)
			voiceDialog.dispose();
	}

	private void selectBackground() {
		if (bgChooser == null) {
			bgChooser = new JFileChooser();
			bgChooser.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return ".jpg, .jpeg, .bmp, .png";
				}

				@Override
				public boolean accept(File f) {
					if (f.isDirectory())
						return true;
					String name = f.getName();
					int index = name.lastIndexOf(".");
					if (index < 0)
						return false;
					name = name.substring(index + 1);
					switch (name.toLowerCase()) {
					case "jpg":
					case "jpeg":
					case "png":
					case "bmp":
						return true;
					default:
						return false;
					}
				}
			});
		}
		int ret = bgChooser.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION)
			return;
		File bgImage = bgChooser.getSelectedFile();
		bgPanel.setImage(new ImageIcon(bgImage.getAbsolutePath()));
	}
}
