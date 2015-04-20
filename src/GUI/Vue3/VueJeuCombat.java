package GUI.Vue3;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Controleur.ControleurFenetre;
import Controleur.ControleurCombat;
import GUI.Vue3.Plateau.*;
import GUI.Vue3.Joueur.*;
import Model.Joueur;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import sun.security.pkcs11.wrapper.Constants;

/**
 * 
 * Classe interface qui etend VueJeu et qui va gerer l'affichage lors du combat entre les deux joueurs
 * @author Warlot/Gasquez
 */
public class VueJeuCombat extends VueJeu {
    
	static private ConsolePanel console = new ConsolePanel();
	
	/**
	 * Constructeur
	 * Il va instancier les deux parties joueurs a droite et gauche, au centre le plateau, et en bas la console
	 * @param controleurParent controleur parent
	 * @param longueur longueur du plateau
	 * @param largeur largeur du plateau
	 * @param listJoueurs liste de tous les joueurs
	 * @param joueurActuel joueur actuellement actif
	 */
	public VueJeuCombat(ControleurFenetre controleurParent, int longueur, int largeur, List<Joueur> listJoueurs, Joueur joueurActuel){
		super(controleurParent, listJoueurs, joueurActuel);
		
		this.panelJoueur1 = new VueJoueurCombat(getControleur(), listJoueurs.get(0));
		this.panelJoueur2 = new VueJoueurCombat(getControleur(), listJoueurs.get(1));
		this.panelPlateau = new VuePlateauCombat(getControleur(), joueurActuel, longueur, largeur);
		
		console.setLayout(new BoxLayout(console, BoxLayout.Y_AXIS));
		
//		console.add(new JLabel("Console de jeu :"));
		
		getPanelPlateau().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				majDimension();
			}
		});

		panelOuestAdd(panelJoueur1);
		panelEstAdd(panelJoueur2);
		panelCenterAdd(panelPlateau);
		panelSudAdd(console);
		
		getControleur().observePersonnages();
		cacherJoueur();
		initPanelJoueurActuel();
		majDimension();
		
		getControleur().afficherPersonnages();
	}

	/**
	 * Ajoute du texte dans la console de jeu
	 * @param texte Texte a afficher
	 */
	public void majConsole(String texte){
		console.add(texte);
		getPanelSud().revalidate();
	}
	
	/**
	 * redimentionne le panel sud
	 */
	private void majDimension(){
		getPanelSud().setPreferredSize(new Dimension(
				(int) (getControleur().getControleurParent().getVue().getWidth() * 0.75),
				(int) (getControleur().getControleurParent().getVue().getHeight() * 0.20))
				);
	}
	
	/****************** GETTERS ******************/	
	@Override
	public ControleurCombat getControleur() {
		return (ControleurCombat) controleur;
	}
	public VueJeuCombat getMaVue(){
		return (VueJeuCombat) this;
	}

	@Override
	public VueJoueurCombat getPanelJoueur1() {
		return (VueJoueurCombat) panelJoueur1;
	}

	@Override
	public VueJoueurCombat getPanelJoueur2() {
		return (VueJoueurCombat) panelJoueur2;
	}

	@Override
	public VueJoueurCombat getPanelJoueurActuel() {
		return (VueJoueurCombat) panelJoueurActuel;
	}

	@Override
	public VuePlateauCombat getPanelPlateau() {
		return (VuePlateauCombat) panelPlateau;
	}
	public JPanel getPanelSud() {
		return console;
	}
	
	/****************** SETTERS ******************/
	private void setPanelSud(ConsolePanel panelSud) {
		this.console = panelSud;
	}
	
	@Override
	public void setControleur(ControleurFenetre controleurParent){
		this.controleur = new ControleurCombat(getMaVue(), controleurParent);
	}
}

/**
 * Classe qui represente la console de jeu
 */
class ConsolePanel extends JPanel {
    protected JTextArea textArea;
    protected JScrollPane scrollPane;
    ConsolePanel() {
        super();
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 0));
        panel.setMaximumSize(new Dimension(400, 200));
        panel.add(new JLabel("Console de jeu :"));
        add(panel);
        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        textArea.setEditable(false);
        add(scrollPane);
    }

    void add(String text) {
        textArea.append(text + Constants.NEWLINE);
    }
}
