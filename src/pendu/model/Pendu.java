package pendu.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import pendu.observer.Observer;

/**
 * Jeu du pendu sous forme de texte. Lors que le constructeur Pendu(boolean) est utlisé, le jeu se lance en
 * mode texte dans la console. Le jeu se termine lorsque le joueur choisit de ne pas recommencer la partie.
 * @author Jo
 *
 */
public class Pendu {
	
	private URL dictio = Pendu.class.getResource("/pendu/rsc/dictionnaire.txt");

	private String motATrouver;
	private char[] motCache; // le mot en cours de résolution. 
	private int triesLeft;
	private String suggestedLetters; // lettres déja proposées par le joueur.
	private boolean gameover;
	private boolean textMode; // si true, affiche des messages dans la console.
	private String playerName = "Player1";

	private final static String ACCENTS_A = "aàâä";
	private final static String ACCENTS_E = "eéèêë";
	private final static String ACCENTS_I = "iïî";
	private final static String ACCENTS_O = "oôö";
	private final static String ACCENTS_U = "uùûü";
	private final static String ACCENTS_C = "cç©";
	//private final static String SPECIAL_CHAR = "æœÿ' "; // non-présents dans le dictionnaire fourni.
	
	// game "messages"
	public final static int GAME_START = 0;
	public final static int GAME_INVALID_LETTER = 1;
	public final static int GAME_SUGGESTED_LETTER = 2;
	public final static int GAME_CORRECT_LETTER = 3;
	public final static int GAME_INCORRECT_LETTER = 4;
	public final static int GAME_OVER_WIN = 5;
	public final static int GAME_OVER_LOST = 6;
	private int lastMessage = GAME_START;

	private ArrayList<Observer> listObserver = new ArrayList<Observer>();
	
	//Scoreboard
	Scoreboard scores;
	
	public Pendu() {
		textMode = false;
		resetGame();
		updateObserver();
	}
	
	/**
	 * Constructeur qui lance la méthode de résolution du jeu en mode texte.
	 * En mode texte le jeu se joue avec la console. 
	 * Implique une boucle while() tant que le jeu n'est pas terminé.
	 * @param textMode si différent de null, alors le jeu se lance en mode texte.
	 */
	public Pendu(boolean textMode) {
		if (textMode) {
			this.textMode = true;
			resetGame();
			updateObserver();
			solveTextMode();
		}
	}

	/**
	 * Sélectionne le mot dans le dictionnaire et initialise le mot à trouver avec des '_'.
	 */
	private void setMot() {
		/*
		FileInputStream fis = null;
	    try {
			int nbre = (int)(Math.random()*336529);
	        fis = new FileInputStream(new File(dictio.toURI()));
	        InputStreamReader streamReader = new InputStreamReader(fis, "UTF-8");
	        LineNumberReader reader = new LineNumberReader(streamReader);
	        while(reader.getLineNumber() < nbre-1) {
	        	reader.readLine();
			}
	        motATrouver = reader.readLine();
	        reader.close();
	    } catch (Exception ex) {
	    } finally {
	        try {
	            fis.close();
	        } catch (IOException ex) {
				ex.printStackTrace();
	        }
	    }*/
		try {
			int nbre = (int)(Math.random()*336529);
			LineNumberReader in = new LineNumberReader(new FileReader(new File(dictio.toURI())));
			while(in.getLineNumber() < nbre-1) {
				in.readLine();
			}
			motATrouver = in.readLine().toLowerCase();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		motCache = new char[motATrouver.length()];
		for(int i = 0 ; i < motATrouver.length() ; i++) {
			motCache[i] = '_';
		}
		// Affiche d'office les traits d'union des mots composés.
		revealLetter('-');
		// Affichage de certaines lettres si le mot est long :
		revealClues();
		System.out.println("MOT À TROUVER : " + motATrouver);
	}

	/**
	 * Révèle de façon aléatoire un nombre de lettres défini en fonction de la longueur du mot.
	 */
	private void revealClues() {
		if (motATrouver != null) {
			int numberOfClues = 0;
			if (motATrouver.length() >= 16) {
				numberOfClues = 4;
			} else if (motATrouver.length() >= 12) {
				numberOfClues = 3;
			} else if (motATrouver.length() >= 8) {
				numberOfClues = 2;
			} else if (motATrouver.length() >= 5) {
				numberOfClues = 1;
			}
			
			while (lettersFound() < numberOfClues) {
				int i = (int) (Math.random()*motATrouver.length());
				// On vérifie si la lettre actuelle ne révèle pas tout le mot. Si c'est le cas,
				// alors on stoppe cette fonction sans révéler la lettre incriminée.
				// Ce cas de figure est vérifié pour éviter que reavealClues() ne révèle tout le mot
				// et bloque ainsi la progression du jeu pour le joueur et ne crée un softlock.
				// Normalement pas nécessaire si le dictionnaire ne contient pas de mots du style "eeeeeeee".
				if (checkIfLetterWouldTerminateGame(motATrouver.charAt(i))) return;
				suggestedLetters += motATrouver.charAt(i);
				revealLetter(motATrouver.charAt(i));
			}
		}
	}

	/**
	 * Retourne true si la lettre c termine le jeu si elle est découverte, false sinon.
	 * @param c la lettre à vérifier.
	 * @return true si la lettre c termine le jeu si elle est découverte, false sinon.
	 */
	private boolean checkIfLetterWouldTerminateGame(char c) {
		boolean wouldTerminate = true;
		// Copie du mot caché.
		char [] copyMotCache = motCache.clone();
		// On get les index où la lettre c est présente
		ArrayList<Integer> indexes = checkLetter(c);
		// on remplace à ces index dans la copy du mot caché
		if (!indexes.isEmpty()) {
			for (int index : indexes) {
				copyMotCache[index] = c;
			}
		}
		// on check si le mot caché est entièrement révélé et on return.
		for (char chara : copyMotCache) {
			if (chara == '_') {
				wouldTerminate = false;
			}
		}

		return wouldTerminate;
	}
	
	/**
	 * Renvoie un tableau de int représentant les index de la lettre c, présente dans 
	 * motATrouver. Si non présente, renvoie un tableau vide. Prend en compte les accents (si c == a,
	 * check la présence de ACCENTS_A).
	 * @param c
	 * @return
	 */
	private ArrayList<Integer> checkLetter(char c) {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		if (contains(ACCENTS_A, c)) {
			indexes = checkLetters(ACCENTS_A);
		} else if (contains(ACCENTS_E, c)) {
			indexes = checkLetters(ACCENTS_E);
		} else if (contains(ACCENTS_I, c)) {
			indexes = checkLetters(ACCENTS_I);
		} else if (contains(ACCENTS_O, c)) {
			indexes = checkLetters(ACCENTS_O);
		} else if (contains(ACCENTS_U, c)) 	{
			indexes = checkLetters(ACCENTS_U);
		} else if (contains(ACCENTS_C, c)) {
			indexes = checkLetters(ACCENTS_C);
		} else {
			for (int i = 0 ; i < motATrouver.length() ; i++) {
				if (motATrouver.charAt(i) == c) {
					indexes.add(i);
				}
			}
		}
		return indexes;
	}
	
	/**
	 * Renvoie un tableau de int représentant les index de motATrouver, correspondant à chaque lettre
	 * de str.
	 * @param c
	 * @return
	 */
	private ArrayList<Integer> checkLetters(String str) {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for (int j = 0 ; j < str.length() ; j++) {
			for (int i = 0 ; i < motATrouver.length() ; i++) {
				if (motATrouver.charAt(i) == str.charAt(j)) {
					indexes.add(i);
				}
			}
		}
		return indexes;
	}
	
	/**
	 * Méthode qui lance la partie avec le mot motATrouver actuel.
	 * Lance le jeu avec la méthode d'entrée texte.
	 */
	private void solveTextMode() {
		Scanner input = new Scanner(System.in);
		boolean loop = true;
		print("Veuillez saisir votre nom :");
		playerName = input.next();
		do {
			while (!gameover) {
				printGameState();
				print("Veuillez saisir un caractère : ");
				tryLetter(input.next().charAt(0));
			}
			if (isGameWon()) {
				print("Partie terminée. Vous avez gagné !");
			} else {
				print("Partie terminée. Vous avez perdu :(");
			}
			print("Le mot était : " + motATrouver.toUpperCase());
			if (computeScore() > 0) {
				print("Vous avez remporté " + computeScore() + " points !");
			}
			print("Voulez-vous recommencer ? Y/N");
			char answer = input.next().charAt(0);
			if (answer == 'y' || answer == 'Y') {
				resetGame();
			} else {
				loop = false;
			}
		} while (loop);
		System.out.println("Merci d'avoir joué !");
		input.close();
		//debug();
	}

	/**
	 * Réinitialise les paramètres de jeu comme au début d'une partie.
	 * Repioche un mot dans le dictionnaire.
	 */
	private void resetGame() {
		suggestedLetters = "";
		setMot();
		triesLeft = 7;
		gameover = false;
		updateObserver();
	}

	/**
	 * Si textMode = true, affiche le string dans l'output standard System.out avec la méthode println.
	 * @param string
	 */
	private void print(String string) {
		if (textMode) {
			System.out.println(string);
		}
	}

	/**
	 * Utilise un tour pour tenter de trouver une lettre dans le mot.
	 * Si la partie est terminée (gameover = true), ne fais rien.
	 * la partie est terminée lorsque le mot est entièrement dévoilé (partie gagnée),
	 * ou lorsqu'il ne reste plus aucun essais (partie perdue).
	 * @param c le caractère à essayer dans le mot.
	 */
	public void tryLetter(Character c) {
		
		// prendre en compte les accents OK / charset TODO
		
		c = Character.toLowerCase(c);
		
		// On vérifie si c est un caractère valide.
		if (!Character.isLetter(Character.valueOf(c))) {
			print("===== Cette lettre n'est pas valide ! =====");
			lastMessage = GAME_INVALID_LETTER;
			return;
		}
		
		// On vérifie si la lettre a déjà été proposée. Si oui, on ne fais rien (return). 
		if (suggestedLetters.contains(c.toString())) {
			print("===== Cette lettre a déjà été proposée ! =====");
			lastMessage = GAME_SUGGESTED_LETTER;
			return;
		}
		
		if (revealLetter(c)) {
			print("===== Bravo ! Lettre trouvée ! =====");
			lastMessage = GAME_CORRECT_LETTER;
		} else {
			print("===== Raté. Lettre non présente. =====");
			lastMessage = GAME_INCORRECT_LETTER;
			// Lettre non trouvée. On retranche un essai.
			triesLeft--;
		}
		// Si c'était notre dernier essai ou si on a trouvé le mot complet, on a fini la partie.
		if (triesLeft == 0 || lettersRemaining() == 0) {
			gameover = true;
			if (isGameWon()) {
				lastMessage = GAME_OVER_WIN;
				addScore(playerName);
			} else if (gameover) {
				lastMessage = GAME_OVER_LOST;
			}
		}
		updateObserver();
		//printGameState();
	}
	
	/**
	 * Révèle la lettre dans le mot en cours si elle est présente. Ajoute la lettre c dans la liste
	 * des lettres proposées suggestedLetters.
	 * @return Retourne true si la lettre est présente (même si elle est déjà révelée),
	 * false si elle ne l'est pas.
	 * @param c le caractère à tester.
	 */
	private boolean revealLetter(Character c) {
		suggestedLetters += c;
		boolean isPresent = false;
		// Accents
		if (contains(ACCENTS_A, c)) {
			isPresent = revealLetters(ACCENTS_A);
		} else if (contains(ACCENTS_E, c)) {
			isPresent = revealLetters(ACCENTS_E);
		} else if (contains(ACCENTS_I, c)) {
			isPresent = revealLetters(ACCENTS_I);
		} else if (contains(ACCENTS_O, c)) {
			isPresent = revealLetters(ACCENTS_O);
		} else if (contains(ACCENTS_U, c)) {
			isPresent = revealLetters(ACCENTS_U);
		} else if (contains(ACCENTS_C, c)) {
			isPresent = revealLetters(ACCENTS_C);
		} else {
			for (int i = 0 ; i < motCache.length ; i++) {
				if (motATrouver.charAt(i) == c) {
					motCache[i] = c;
					isPresent = true;
				}
			}
		}
		return isPresent;
	}
	
	/**
	 * Méthode qui retourne vrai si chara est présent dans str.
	 * @param str la chaîne de caractères à vérifier.
	 * @param chara le caractère à rechercher.
	 * @return vrai si chara est présent dans str, false sinon.
	 */
	private boolean contains(String str, Character chara) {
		if (str == null) {
			return false;
		}
		
		for (int i = 0 ; i < str.length() ; i++) {
			if (str.charAt(i) == chara) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Révèle toutes les lettres présentes dans le string str. Retourne true si au moins une est présente.
	 * Ajoute str dans la liste des lettres proposées suggestedLetters.
	 * ! Cette méthode ne gère pas les accents ! Elle ne doit pas être utilisée pour vérifier une lettre seule.
	 * Dans ce cas, utiliser la méthode revealLetter(char).
	 * @param str les caractères à vérifier dans le mot à trouver.
	 * @return true si au moins une lettre de str est contenue dans le mot à trouver, false sinon.
	 */
	private boolean revealLetters(String str) {
		suggestedLetters += str;
		boolean isPresent = false;
		// Pour chaque lettre présente dans str
		for (int i = 0; i < str.length(); i++) {
			// On vérifie si elle est présente dans motATrouver et on révèle la lettre de motCache si c'est le cas.
			for (int j = 0 ; j < motCache.length ; j++) {
				if (motATrouver.charAt(j) == str.charAt(i)) {
					motCache[j] = str.charAt(i);
					isPresent = true;
				}
			}
		}
		return isPresent;
	}
	/**
	 * Si textMode = true, affiche dans la console l'état de la partie :
	 * si elle est en cours ou terminée, le nombre de coups restants,
	 * le mot à trouver et le mot en cours de résolution.
	 */
	private void printGameState() {
		print(" ----------------------------------");
		print("Essais restants : " + triesLeft);
		print("Mot en cours : " + String.valueOf(motCache));
		if (isGameWon()) {
			print("Partie terminée. Vous avez gagné !");
			lastMessage = GAME_OVER_WIN;
		} else if (gameover) {
			print("Partie terminée. Vous avez perdu :(");
			lastMessage = GAME_OVER_LOST;
		}
		//debug();
	}
	
/*	private void debug() {
		System.out.println("::gameover:" + gameover + "|lettersRemaining:" +lettersRemaining()
				+ "|trysleft:" + nbreCoupsRestants + "|lettresProposees:" + lettresProposees
				+ "|mot:" + String.valueOf(mot));
	}*/

	private boolean isGameWon() {
		return (gameover && !String.valueOf(motCache).contains("_"));
	}
	
	/**
	 * Retourne le nombre de lettres restantes à trouver. Si = 0, alors partie gagnée.
	 * @return
	 */
	private int lettersRemaining() {
		int number = 0;
		// On parcours mot[] et on compte les '_'.
		for (int i = 0 ; i < motCache.length ; i++) {
			 if (motCache[i] == '_') number++;
		}
		return number;
	}
	
	/**
	 * Retourne le nombre de lettres trouvées ou révélées dans le mot caché.
	 * @return le nombre de lettres trouvées ou révélées dans le mot caché.
	 */
	private int lettersFound() {
		int number = 0;
		// On parcours mot[] et on compte les '_'.
		for (int i = 0 ; i < motCache.length ; i++) {
			 if (motCache[i] != '_') number++;
		}
		return number;
	}
	
	/**
	 * Calcule le score de la partie. Les règles pour calculer le score sont les suivantes :
	 *  Mot trouvé sans erreur : 100 Pts
	 *	Mot trouvé avec 1 erreur : 50 Pts
	 *	Mot trouvé avec 2 erreurs : 35 Pts
	 *	Mot trouvé avec 3 erreurs : 25 Pts
	 *	Mot trouvé avec 4 erreurs : 15 Pts
	 *	Mot trouvé avec 5 erreurs : 10 Pts
	 *	Mot trouvé avec 6 erreurs : 5 Pts
	 */
	public int computeScore() {
		switch (getTriesLeft()) {
			case 7: return 100;
			case 6: return 50;
			case 5: return 35;
			case 4: return 25;
			case 3: return 15;
			case 2: return 10;
			case 1: return 5;
			default: return 0;
		}
	}

	/**
	 * Méthode qui ajoute le score en cours dans le scoreboard avec le pseudo nickname.
	 * S'exécute que si la partie en cours est terminée.
	 */
	private void addScore(String nickname) {
		if (gameover && computeScore() > 0) {
			scores = new Scoreboard();
			scores.addScoreToPlayer(nickname, computeScore());
		}
	}

	// Ajoute un observer à la liste
	public void addObserver(Observer obs) {
		this.listObserver.add(obs);
	}

	// Retire tous les observers de la liste
	public void delObservers() {
		this.listObserver = new ArrayList<Observer>();
	}
	
	// Avertit les observers que l'objet observable a changé et invoque la méthode update() de
	// chaque observer.
	public void updateObserver() {
		for (Observer observer : this.listObserver) {
			observer.update(gameover, triesLeft, motCache, lastMessage, suggestedLetters);
		}
	}

	public String getMotATrouver() {
		return motATrouver;
	}

	public int getTriesLeft() {
		return triesLeft;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
}
