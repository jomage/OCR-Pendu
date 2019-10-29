package pendu.itf;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

import pendu.model.Pendu;

/**
 * Label qui se charge d'afficher le bon message de pendu.java en fonction du game message envoyé.
 * Comprend sa propre police.
 * @author Jo
 *
 */
@SuppressWarnings("serial")
public class LabelGameMsg extends JLabel {
	
	private String start = "Choisissez une lettre pour commencer.";
	private String invalid = " Erreur : Lettre invalide.";
	private String alreadySuggested = "Erreur : Lettre déjà proposée.";
	private String correct = "Bravo ! Lettre trouvée.";
	private String incorrect = "Dommage ! Lettre non présente.";
	private String gameWon = "Bravo ! Vous avez gagné !";
	private String gameLost = "Dommage ! Vous avez perdu.";
	private String playerName = "";
	
	public LabelGameMsg(String playerName) {
		this.playerName = playerName;
		gameWon = "Bravo " + playerName + " ! Vous avez gagné !";
		gameLost = "Dommage " + playerName + " ! Vous avez perdu.";
		this.setForeground(Color.black);
		this.setFont(new Font("Arial", Font.BOLD, 15));
		this.setText("");
	}
	
	public void setMessage(int gameMsg) {
		String text = "";
		switch (gameMsg) {
			case Pendu.GAME_START: text = start;
			break;
			case Pendu.GAME_INVALID_LETTER: text = invalid;
			break;
			case Pendu.GAME_SUGGESTED_LETTER: text = alreadySuggested;
			break;
			case Pendu.GAME_CORRECT_LETTER: text = correct;
			break;
			case Pendu.GAME_INCORRECT_LETTER: text = incorrect;
			break;
			case Pendu.GAME_OVER_WIN: text = gameWon;
			break;
			case Pendu.GAME_OVER_LOST: text = gameLost;
			break;
		}
		
		this.setText(text);
	}
}
