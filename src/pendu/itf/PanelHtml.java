package pendu.itf;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

/**
 * Classe qui ouvre la page entrée en paramètre du constructeur.
 * Cette classe va chercher dans le dossier "pendu/rsc/html" seulement !
 * @author Jo
 *
 */
@SuppressWarnings("serial")
public class PanelHtml extends JPanel {

	private static final String rscPathHtml = "/pendu/rsc/html/";
	
	private JEditorPane text = new JEditorPane();
	private String defaultPageName = "start.html";
	private URL pageURL;
	
	/**
	 * Constructeur par défaut, affiche start.html.
	 */
	public PanelHtml() {
		new PanelHtml(defaultPageName);
	}
	
	/**
	 * Constructeur avec paramètre, affiche name.
	 * name doit désigner une ressource existante dans pendu/rsc/html.
	 * @param name le nom de la ressource à afficher.
	 */
	public PanelHtml(String name) {
		pageURL = PanelHtml.class.getResource(
				rscPathHtml + name);

		if (pageURL != null) {
		    try {
		        text.setPage(pageURL);
		    } catch (IOException e) {
		        System.err.println("Attempted to read a bad URL: " + pageURL);
		    }

		    this.setLayout(new BorderLayout());
			this.add(text, BorderLayout.CENTER);
			
		} else {
		    System.err.println("Couldn't find file: " + name);
		    text.setText("Couldn't find file: " + name);
		}
		
		text.setEditable(false);
		this.setVisible(true);
	}
}
