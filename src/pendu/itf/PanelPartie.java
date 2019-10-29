package pendu.itf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import pendu.model.Pendu;
import pendu.observer.Observer;

/**
 * Panel permettant de jouer une partie.
 * @author Jo
 *
 */
@SuppressWarnings("serial")
public class PanelPartie extends JPanel implements ActionListener, KeyListener {

	private static final String rscPathImg = "/pendu/rsc/img/";
	private JLabel labelHiddenWord, labelImage, labelTriesLeft;
	private Font fontLabelHiddenWord;
	private JButton[] letterButtons;
	private JPanel panelLeft, panelRight, panelLabels, panelButtons, panelImage;
	private LabelGameMsg labelGameMessage;
	private JButton btnRestart;
	
	private boolean gameover;
	private String playerName;
	
	private Pendu pendu;
	
	public PanelPartie() {
		gameover = false;
		playerName = JOptionPane.showInputDialog("Veuillez rentrer votre nom"); 
		
		// Layouts
		this.setLayout(new BorderLayout());
		
		// Composants
		initLabels();
		initLetterButtons();
		labelImage = new JLabel();
		setImage(0);
		
		// Panels
		panelImage = new JPanel();
		panelImage.add(labelImage);
		
		// Panel Left
		panelLeft = new JPanel();
		panelLeft.setLayout(new GridLayout(2, 2));
		panelLeft.add(panelLabels);
		panelLeft.add(panelButtons);
		
		// Panel Right
		panelRight = new JPanel();
		panelRight.setPreferredSize(new Dimension(400, 300));
		panelRight.add(panelImage);
		btnRestart = new JButton("Recommencer !");
		Dimension size = new Dimension(200, 35);
		btnRestart.setMinimumSize(size);
		btnRestart.setPreferredSize(size);
		btnRestart.setMaximumSize(size);
		btnRestart.setAlignmentX(CENTER_ALIGNMENT);
		btnRestart.addActionListener(this);
		btnRestart.setVisible(false);
		panelRight.add(btnRestart);

		// Debug
//		panelLeft.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
//		panelRight.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
//		labelImage.setBorder(BorderFactory.createLineBorder(Color.PINK, 2));
//		btnRestart.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(Color.red),
//                btnRestart.getBorder()));
//		panelButtons.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
//		panelLabels.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 2));
//		labelHiddenWord.setBorder(BorderFactory.createLineBorder(Color.PINK, 2));
//		labelGameMessage.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
//		labelTriesLeft.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
		
		this.setLayout(new BorderLayout());
		this.add(panelLeft, BorderLayout.CENTER);
		this.add(panelRight, BorderLayout.EAST);
		this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		this.setVisible(true);
		
		pendu = new Pendu();
		pendu.setPlayerName(playerName);
		
		// observer
		this.pendu.addObserver(new Observer() {
			public void update(boolean isGameOver, int triesLeft, char[] hiddenWord, int gameMsg, String lettersTried) {

				labelHiddenWord.setText(displayHiddenWord(hiddenWord));
				labelGameMessage.setMessage(gameMsg);
				if (isGameOver) {
					gameOver();
					if (triesLeft > 0) {
						labelTriesLeft.setText("Vous avez remporté " + pendu.computeScore() + " points.");
					} else {
						labelTriesLeft.setText("");
					}
				} else if (triesLeft >1) {
					labelTriesLeft.setText("Il vous reste " + triesLeft + " essais.");
				} else if (triesLeft == 1) { 
					labelTriesLeft.setText("Il vous reste 1 essai !");
				}
				
				disableLetterButtons(lettersTried);
				
				// Image
				setImage(Math.abs(triesLeft-7));
			}
		});
		pendu.updateObserver();

		letterButtons[0].requestFocusInWindow();
	}

	/**
	 * Désactive les boutons qui ont pour lettre les lettres contenues dans lettersTried.
	 * 
	 * @param lettersTried
	 */
	protected void disableLetterButtons(String lettersTried) {
		for (int i = 0 ; i < letterButtons.length ; i++) {
			if (lettersTried.contains(letterButtons[i].getText().toLowerCase())) {
				letterButtons[i].setEnabled(false);
			}
		}
	}

	/**
	 * Méthode qui réalise la liste d'actions à faire lorsque la partie est terminée. 
	 * Elle est appelée lorsque l'instance pendu renvoie l'état "isGameOver = true".
	 * Set gameover = true, calcule le score et stocke le nouveau score,
	 * change les labels et affiche un bouton pour recommencer une partie.
	 */
	private void gameOver() {
		gameover = true;
		if (labelHiddenWord.getText().contains("_")) {
			labelHiddenWord.setForeground(Color.RED);
			labelHiddenWord.setText(displayHiddenWord(pendu.getMotATrouver()));
		} else {
			labelHiddenWord.setForeground(Color.GREEN);
		}

		btnRestart.setVisible(true);
	}


	/**
	 * Transforme en String la séquence de char charSeq en ajoutant des espaces entre chaque lettre.
	 * Méthode utilisée pour styliser le mot caché pour labelHiddenWord.
	 * ex : transforme le char[] {A, B, C} en "A B C"
	 * @param charSeq le char[] à transformer.
	 * @return un string représentant charSeq avec des espaces entre chaque caractère.
	 * Retourne null si charSeq est null.
	 */
	private String displayHiddenWord(char[] charSeq) {
		
		if (charSeq == null) {
			return null;
		}
		
		String text = "";
		for (int i = 0 ; i < charSeq.length-1 ; i++) {
			text += charSeq[i] + " ";
		}
		text += charSeq[charSeq.length-1];
		return text;
	}
	
	/**
	 * Ajoute des espaces entre chaque lettre et renvoie le nouveau String formé.
	 * Méthode utilisée pour styliser le mot caché pour labelHiddenWord.
	 * ex : transforme "ABC" en "A B C"
	 * @param string le String à transformer.
	 * @return le nouveau String formé avec des espaces entre chaque caractère.
	 * Retourne null string est null.
	 */
	private String displayHiddenWord(String string) {
		
		if (string == null) {
			return null;
		}
		
		String text = "";
		for (int i = 0 ; i < string.length()-1 ; i++) {
			text += string.charAt(i) + " ";
		}
		text += string.charAt(string.length()-1);
		return text;
	}
	
	/**
	 * Affiche l'image indiquée en paramètre dans labelImage.
	 * Il y a 8 images. Si index est <0 ou >=8, ne fais rien.
	 * @param l'idex de 0 à 7 inclus de l'image du pendu à afficher dans labelImage.
	 */
	private void setImage(int index) {
		labelImage.removeAll();
		if (index >= 0 || index < 8) {
			URL imgURL = PanelHtml.class.getResource(rscPathImg + "pendu" + (index+1) + ".jpg");
			labelImage.setIcon(new ImageIcon(imgURL));
		}
		labelImage.revalidate();
	}

	private void initLabels() {
		// Hidden Word
		this.labelHiddenWord = new JLabel();
		fontLabelHiddenWord = new Font("Arial", Font.BOLD, 24);
		labelHiddenWord.setPreferredSize(new Dimension(100, 300));
		labelHiddenWord.setFont(fontLabelHiddenWord);
		labelHiddenWord.setForeground(Color.BLUE);
		labelHiddenWord.setHorizontalAlignment(SwingConstants.CENTER);
		labelHiddenWord.setAlignmentX(CENTER_ALIGNMENT);
		
		// Game Message
		labelGameMessage = new LabelGameMsg(playerName);
		labelGameMessage.setHorizontalAlignment(SwingConstants.CENTER);
		labelGameMessage.setAlignmentX(CENTER_ALIGNMENT);
		
		// Essais Restants
		labelTriesLeft = new JLabel();
		labelTriesLeft.setFont(new Font("Arial", Font.BOLD, 12));
		labelTriesLeft.setHorizontalAlignment(SwingConstants.CENTER);
		labelTriesLeft.setAlignmentX(CENTER_ALIGNMENT);
		
		// Panel Labels
		panelLabels = new JPanel();
		panelLabels.setLayout(new BoxLayout(panelLabels, BoxLayout.Y_AXIS));
//		panelLabels.setPreferredSize(new Dimension(300, 300));
		//panelLabels.add(Box.createHorizontalGlue());
		panelLabels.add(labelHiddenWord);
		panelLabels.add(labelGameMessage);
		panelLabels.add(labelTriesLeft);
	}

	private void initLetterButtons() {
		panelButtons = new JPanel();
		panelButtons.setLayout(new GridLayout(3, 10, 5, 5));
		panelButtons.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		panelButtons.setPreferredSize(new Dimension(300, 300));
		final String alphabet = "abcdefghijklmnopqrstuvwxyz";
		letterButtons = new JButton[alphabet.length()];
		for (int i = 0 ; i < alphabet.length() ; i++) {
			letterButtons[i] = new JButton(alphabet.substring(i, i+1).toUpperCase());
			letterButtons[i].setPreferredSize(new Dimension(20,11));
			letterButtons[i].addKeyListener(this);
			letterButtons[i].addActionListener(this);
			panelButtons.add(letterButtons[i]);
		}
	}
	
	// ACTION & KEY LISTENERS METHODS

	/**
	 * Gère les actions sur les boutons. Si la partie est terminée (gameover = true),
	 * les boutons de lettres ne font rien.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnRestart) {
			((MainWindow) SwingUtilities.windowForComponent(this)).newGame();
			return;
		}
		if (e.getSource().getClass() == JButton.class && !gameover) {
			String letter = ((JButton) e.getSource()).getText();
			pendu.tryLetter(letter.charAt(0));
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(Character.isAlphabetic(e.getKeyChar())) {
			pendu.tryLetter(e.getKeyChar());
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void keyReleased(KeyEvent e) {}   
}
