package pendu.itf;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements ActionListener {

	private JMenuBar menuBar;
	private JMenu menuFile, menuAbout;
	private JMenuItem menuItemNew, menuItemScores, menuItemRules, menuItemQuit;

	private JPanel mainPanel;
	
	/**
	 * Constructeur par défaut
	 */
	public MainWindow() {
		this.setSize(1000, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		initMenuBar();
		mainPanel = new PanelHtml("start.html");
		//mainPanel = new PanelPartie();
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		this.setVisible(true);
	}

	/**
	 * Initialise la barre de menus.
	 */
	private void initMenuBar() {
		menuBar = new JMenuBar();
		menuFile = new JMenu("Fichier");
		menuAbout = new JMenu("À propos");
		menuItemNew = new JMenuItem("Nouvelle partie");
		menuItemScores = new JMenuItem("Scores");
		menuItemRules = new JMenuItem("Règles");
		menuItemQuit = new JMenuItem("Quitter");
		
		menuBar.add(menuFile);
		menuBar.add(menuAbout);
		
		menuFile.add(menuItemNew);
		menuFile.add(menuItemScores);
		menuFile.add(menuItemRules);
		menuFile.addSeparator();
		menuFile.add(menuItemQuit);
		
		// Shortcuts
		menuFile.setMnemonic('F');
		menuAbout.setMnemonic('o');
		menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 
				KeyEvent.CTRL_DOWN_MASK));
		menuItemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				KeyEvent.CTRL_DOWN_MASK));
		menuItemScores.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				KeyEvent.CTRL_DOWN_MASK));
		
		// Action Listeners
		menuItemNew.addActionListener(this);
		menuItemScores.addActionListener(this);
		menuItemRules.addActionListener(this);
		menuItemQuit.addActionListener(this);
		
		this.setJMenuBar(menuBar);
	}
	
	
	public static void main(String[] args) {
		new MainWindow();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(menuItemNew)) {
			newGame();
		}
		
		if (e.getSource().equals(menuItemScores)) {
			displayScores();
		}
		
		if (e.getSource().equals(menuItemRules)) {
			displayRules();
		}
		
		if (e.getSource().equals(menuItemQuit)) {
			System.exit(0);
		}
		//this.repaint();
	}
	
	public void newGame() {
		getContentPane().removeAll();
		mainPanel = new PanelPartie();
		this.add(mainPanel);
		getContentPane().repaint();
		getContentPane().revalidate();
	}

	/**
	 * Affiche les règles dans le mainPanel
	 */
	private void displayRules() {
		getContentPane().removeAll();
		mainPanel = new PanelHtml("help.html");
		this.add(mainPanel);
		getContentPane().repaint();
		getContentPane().revalidate();
	}

	/**
	 * Affiche les scores dans le mainPanel
	 */
	private void displayScores() {
		getContentPane().removeAll();
		mainPanel = new PanelScores();
		this.add(mainPanel);
		getContentPane().repaint();
		getContentPane().revalidate();
	}
}
