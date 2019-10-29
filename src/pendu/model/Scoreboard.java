package pendu.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Classe qui contient la liste de tous les scores.
 */
@SuppressWarnings("serial")
public class Scoreboard implements Serializable {

	private static final String PATH = "src/pendu/rsc/scores/";
	private static final String FILE_NAME = "scores.data";
	private static final String ERROR_FILE_NOT_FOUND = "Erreur : Impossible de trouver le fichier de scores.";
	private static final String ERROR_FILE_IO = "Erreur d'entrée / sortie.";
	private static final String ERROR_FILE_EMPTY = "Erreur : Aucun score trouvé.";

	private ArrayList<Score> scores;
	
	private transient ObjectInputStream in = null;
	private transient ObjectOutputStream out = null;
	
	/**
	 * Méthode de test.
	 * @param args
	 */
	public static void main(String[] args) {
		Scoreboard sc = new Scoreboard();
		sc.addScoreToPlayer("Toto", 25);
		sc.addScoreToPlayer("SUPERFORT", 12000);
		sc.addScoreToPlayer("pastrèsfort", 5);
		sc.addScoreToPlayer("bibi", 10);
		System.out.println(sc.toHTML());
	}
	
	/**
	 * Instancie la classe à partir du fichier sérializé si présent, en crée un sinon.
	 */
	public Scoreboard() {
		/*
		if (!openFile()) {
			// Fichier non présent, on en crée un nouveau.
			System.out.println("Remise à 0 des scores.");
			scores = new ArrayList<Score>();
			//createNewFile();
			addScoreToPlayer("Foo", 250);
			writeFile();
		} else {
			try {
				//System.out.println(in.readObject());
				scores = (ArrayList<Score>) (in.readObject());
			} catch (ClassNotFoundException e) {
				System.err.println(ERROR_FILE_EMPTY);
				//e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Erreur lors de la lecture du fichier.");
				System.err.println(ERROR_FILE_IO);
				System.out.println("Création d'un fichier vide.");
				//e.printStackTrace();
			}
		}*/
		
		try {
			in = new ObjectInputStream(
					new BufferedInputStream(
							new FileInputStream(
									new File(PATH + FILE_NAME))));
			this.scores = ((Scoreboard) in.readObject()).scores;
			in.close();
		} catch (IOException | ClassNotFoundException | ClassCastException e) {
			System.out.println("Pas de sauvegarde trouvée, création d'un scoreboard vide.\n");
			scores = new ArrayList<Score>();
			writeFile();
		}
		
	}
	
	/**
	 * Ouvre le fichier serialized à l'emplacement PATH et renvoie true si réussi, false sinon.
	 * @return true si le fichier a bien été ouvert, false sinon.
	 */
	private boolean openFile() {
		boolean succeeded = true;
		try {
			System.out.println("Tentative d'ouverture du fichier à l'emplacement : " + PATH + FILE_NAME + "...");
			in = new ObjectInputStream(
					new BufferedInputStream(
							new FileInputStream(
									new File(PATH + FILE_NAME))));
			
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println(ERROR_FILE_NOT_FOUND);
			succeeded = false;
		} catch (IOException e) {
			System.err.println(ERROR_FILE_IO);
			succeeded = false;
		}
		if (succeeded) System.out.println("Ouverture du fichier OK.");
		return succeeded;
	}
	
	/**
	 * Sérialize le scoreboard dans le fichier indiqué par PATH
	 * @return true si création du fichier réussie, false sinon.
	 */
	private boolean writeFile() {
		boolean succeeded = true;
		try {
			System.out.println("Écriture du fichier à l'emplacement : " + PATH + FILE_NAME + "...");
			out = new ObjectOutputStream(
					new BufferedOutputStream(
							new FileOutputStream(PATH + FILE_NAME)));
			out.writeObject(this);
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println(ERROR_FILE_NOT_FOUND);
			succeeded = false;
		} catch (IOException e) {
			System.err.println(ERROR_FILE_IO);
			e.printStackTrace();
			succeeded = false;
		}
		if (succeeded) System.out.println("Écriture du fichier OK.");
		return succeeded;
	}
	
	/**
	 * Crée un nouveau fichier vide.
	 * @return true si création du fichier réussie, false sinon.
	 */
	private boolean createNewFile() {
		boolean succeeded = true;
		try {
			out = new ObjectOutputStream(
					new BufferedOutputStream(
							new FileOutputStream(
									new File(PATH + FILE_NAME))));
			
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println(ERROR_FILE_NOT_FOUND);
			succeeded = false;
		} catch (IOException e) {
			System.err.println(ERROR_FILE_IO);
			succeeded = false;
		}
		return succeeded;
	}
	
	/**
	 * Retourne l'index du joueur entré en paramètre si présent dans la collection scores.
	 * Retourne -1 si scores est vide ou null, ou si le joueur n'est pas présent.
	 * @param nickname le nom du joueur à chercher dans la collection.
	 * @return l'index du joueur si présent, -1 sinon.
	 */
	private int indexOfPlayer(String nickname) {
		if (scores != null && !scores.isEmpty()) {
			int index = 0;
			for (Score entry : scores) {
				if (entry.getNickName().equals(nickname)) {
					return index;
				}
				index++;
			}
		} 
		return -1;
	}
	
	/**
	 * Ajoute un nombre de points points au joueur nickname, si présent. Si non présent, ajoute une entrée.
	 * Incrémente aussi le nombre de mots trouvés par le joueur nickname de 1.
	 * Sérialize le fichier à chaque appel.
	 * @param nickname
	 * @param points
	 */
	public void addScoreToPlayer(String nickname, int points) {

		int index = indexOfPlayer(nickname);
		if (index >= 0) {
			// Entrée trouvée, on la modifie.
			Score newScore = new Score(nickname,
									   scores.get(index).getScore() + points,
									   scores.get(index).getNumberOfWordsFound()+1); 
			scores.set(index, newScore);
		} else {
			// On crée une nouvelle entrée si non présente :
			scores.add(new Score(nickname, points, 1));
		}
		Collections.sort(scores);
		writeFile();
	}
	
	/**
	 * Retourne une représentation du scoreboard sous forme d'une chaîne de caractères.
	 */
	public String toString() {
		int position = 0;
		String str = "**** Scores ****\n";
		if (!scores.isEmpty()) {
			for (Score entry : scores) {
				position++;
				str += position + ". " + entry.toString();
				str += "\n";
			}
		} else {
			str += "Scoreboard vide !";
		}
		return str;
	}
	
	/**
	 * Retourne une représentation du scoreboard sous forme d'une chaîne de caractères,
	 * stylisée avec des balises HTML.
	 * @return
	 */
	public String toHTML() {
		int position = 0;
		String str = "<h1>Scores</h1>\n";
		if (!scores.isEmpty()) {
			for (Score entry : scores) {
				position++;
				String strScore = position + ". " + entry.toString();
				if (position == 1) {
					str += "<h2>" + strScore + "</h2>\n";
				} else if (position == 2) {
					str += "<h3>" + strScore + "</h3>\n";
				} else if (position == 3) {
					str += "<h4>" + strScore + "</h4>\n";
				} else {
					str += "<p>" + strScore + "</p>\n";
				}
			}
		} else {
			str += "<h1>Scoreboard vide !</h1>";
		}
		return str;
	}
}
