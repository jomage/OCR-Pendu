package pendu.model;

import java.io.Serializable;

/**
 * Classe qui représente une entrée dans le scoreboad.
 * Chaque score comprend une valeur (le nombre de points), un pseudo (l'auteur du score),
 * et un nombre de mots (le nombre de mots trouvés).
 * Note: this class has a natural ordering that is inconsistent with equals.
 * @author Jo
 */
@SuppressWarnings("serial")
public class Score implements Serializable, Comparable {
	
	private String nickName;
	private int score;
	private int numberOfWordsFound;
	
	public Score(String nickName, int score, int numberOfWordsFound) {
		this.nickName = nickName;
		this.score = score;
		this.numberOfWordsFound = numberOfWordsFound;
	}
	
	public String toString() {
		if (numberOfWordsFound == 1) {
			return nickName + " : " + score + " Pts (1 mot)";
		}
		return nickName + " : " + score + " Pts (" + numberOfWordsFound + " mots)";
	}
	
	// GETTERS & SETTERS

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getNumberOfWordsFound() {
		return numberOfWordsFound;
	}

	public void setNumberOfWordsFound(int numberOfWordsFound) {
		this.numberOfWordsFound = numberOfWordsFound;
	}

	@Override
	/**
	 * Du plus haut score au plus bas.
	 */
	public int compareTo(Object o) {
		return ((Score) o).getScore() - this.getScore();
	}
}
