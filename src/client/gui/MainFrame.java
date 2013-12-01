package client.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import client.core.ClientCallBack;
import client.core.Messenger;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

public class MainFrame extends JFrame implements ClientCallBack {
	private Startup parent;
	private JTabbedPane tabbedPane;
	private PublicPanel publicPanel;
	private ConfigPanel configPanel;
	private Map<String, PrivatePanel> privatePanel;
	private Messenger m;
	private String name;
	private String host;
	private int port;

	public MainFrame(Startup parent, String username, String host, int port) {
		this.parent = parent;
		name = username;
		this.host = host;
		this.port = port;

		setIconImage(new ImageIcon(MainFrame.class.getResource("mainIcon.jpg"))
				.getImage());
		setTitle("JChat - " + host + ":" + port);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				MainFrame.this.parent.saveSetting();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				MainFrame.this.parent.saveSetting();
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
				int index = tabbedPane.getSelectedIndex();
				ChatPanel cp = (ChatPanel) tabbedPane.getTabComponentAt(index);
				cp.loadConfigPanel();
				tabbedPane.setTitleAt(index, cp.getName());
			}
		});
		tabbedPane.setBounds(10, 10, 774, 552);
		getContentPane().add(tabbedPane);

		privatePanel = new HashMap<>();
		configPanel = new ConfigPanel();
		publicPanel = new PublicPanel(null, configPanel, "public");
		tabbedPane.addTab("public", publicPanel);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnOption = new JMenu("option");
		menuBar.add(mnOption);
		
		JMenu mnHelp = new JMenu("help");
		menuBar.add(mnHelp);
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
		// TODO read setting
		setVisible(true);
	}

	@Override
	public void online(String[] names) {
		// TODO Auto-generated method stub

	}

	@Override
	public void offline(String[] names) {
		// TODO Auto-generated method stub

	}

	@Override
	public void talkPublic(String from, String content) {
		// TODO Auto-generated method stub
		publicPanel.appendMessage(from, content);
		if (tabbedPane.getSelectedComponent() != publicPanel)
			tabbedPane.setTitleAt(tabbedPane.indexOfComponent(publicPanel), "NEW MESSAGE!");
	}

	@Override
	public void talkPrivate(String from, String content) {
		PrivatePanel pp;
		if (!privatePanel.containsKey(from)) {
			pp = new PrivatePanel(m, configPanel, from);
			tabbedPane.addTab(from, pp);
		} else
			pp = privatePanel.get(from);
		pp.appendMessage(from, content);
		if (tabbedPane.getSelectedComponent() != pp)
			tabbedPane.setTitleAt(tabbedPane.indexOfTab(from), "NEW MESSAGE!");
	}

	@Override
	public void fileRequest(String from, String filename, long filesize) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fileResponse(String from, boolean accepted, int port) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fileReceive(String sender, int port) {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(String message) {
		JOptionPane.showMessageDialog(this, message, "ERROR",
				JOptionPane.ERROR_MESSAGE);
	}

	public Properties getSetting(Properties p) {
		p.setProperty("color", configPanel.getSelectedColor().toString());
		p.setProperty("font", configPanel.getSelectedFontName());
		p.setProperty("size", configPanel.getSelectedFontSize() + "");
		p.setProperty("bold", configPanel.isBold() + "");
		p.setProperty("italic", configPanel.isItalic() + "");
		p.setProperty("underline", configPanel.isUnderine() + "");
		return p;
	}
}
