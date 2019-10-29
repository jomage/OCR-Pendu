package pendu.observer;

public interface Observer {
	// gameover, nombre de coups restants, mot caché, résultat de l'essai (trouvé, raté, lettre invalide)
	public void update(boolean isGameover, int trysLeft, char[] hiddenWord, int gameMsg, String lettersTried);
}
