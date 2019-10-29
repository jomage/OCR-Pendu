package pendu.itf;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;

import pendu.model.Scoreboard;

@SuppressWarnings("serial")
public class PanelScores extends JPanel {

	private JEditorPane text = new JEditorPane();
	private Scoreboard scores;
	
	public PanelScores() {
		String str = "<HTML><BODY style=\"font-family: arial; margin: 15px; text-align: center;\">\n";
		scores = new Scoreboard();
		str += scores.toHTML();
		str += "</BODY></HTML>";
		text.setEditorKit(new HTMLEditorKit()); 
		text.setText(str);
		text.setEditable(false);
		this.setLayout(new BorderLayout());
		this.add(text, BorderLayout.CENTER);
		this.setVisible(true);
	}
}
