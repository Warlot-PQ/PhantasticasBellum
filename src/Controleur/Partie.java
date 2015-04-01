package Controleur;

import Model.*;
import Controleur.ControleurPlacement.coteJeu;
import Exception.ExceptionPersonnage;
import Exception.ExceptionParamJeu;
import GUI.Fenetre;

import java.util.*;

import Personnages.*;

/**
 * La classe Partie est le controleur du systeme, elle est le pivot etre le model et la vue
 * @author Scrat
 *
 */

public class Partie {
        /******************DELAI******************/
    
        private static final int DELAY = 4;
        
	/******************ATTRIBUT******************/
        
	/**
	 * Collection Personnage disponible lors de la selection des equipes
	 */
	private static List<Personnage> personnagesDisponibles = new ArrayList<Personnage>();
	
	/**
	 * Taille des equipes
	 */
	private int tailleEquipe = 1;
	
	/**
	 * Taille du plateau de jeu
	 */
	private int plateauHauteur = 3;
	private int plateauLargeur = 3;
	
	/**
	 * Collection de joueurs
	 */
	private List<Joueur> joueurs = new ArrayList<Joueur>();
	
	/**
	 * Iterateur sur la collection de joueurs
	 */
	private Iterator<Joueur> joueurIterateur;
	
	/**
	 * Joueur actuellement en jeu
	 */
	private Joueur joueurActuel = null;
        
	
	/******************CONSTRUCTEUR******************/
	/**
	 * Constructeur de la classe Partie
	 */
	public Partie(){
		new Fenetre(this);
		initJoueurs();
		initPersonnagesDisponibles();
		
		determinerOrdre();
	}
        
        /**
	 * Constructeur de la classe Partie
	 */
	public Partie(boolean creerFenetre, Joueur j1, Joueur j2) {
                if (creerFenetre) {
                    new Fenetre(this);
                }
                joueurs.add(j1);
                joueurs.add(j2);
                joueurIterateur = getJoueurs().iterator();
		//Place l'iterateur sur le premier joueur
		setJoueurActuel(joueurIterateur.next());
                
		initPersonnagesDisponibles();
                joueurSuivant();
                joueurSuivant();
	}
        
        /**
         * Clone la partie courante
         * @return une nouvelle partie
         */
        public Partie clone() {
            Partie clone = new Partie(false, null, null);
            
            clone.tailleEquipe = this.tailleEquipe;
            clone.plateauHauteur = this.plateauHauteur;
            clone.plateauLargeur = this.plateauLargeur;
                    
            int n = joueurs.size();
            boolean found_current_player = false;
            clone.joueurIterateur = clone.joueurs.iterator();

            for (int i = 0; i < Math.min(n, 2); i++) {
                Joueur j_clone = (Joueur) this.joueurs.get(i).clone();
                clone.joueurs.set(i, j_clone);
                // Verifier si on est sur le joueur courant
                if (joueurActuel == this.joueurs.get(i)) {
                    clone.joueurActuel = j_clone;
                    found_current_player = true;
                }
                // Avancer l'iterateur si le joueur courant n'a pas encore ete atteint
                if (!found_current_player) {
                    clone.joueurIterateur.next();
                }
            }
            return clone;
        }
	
	
	/******************INITIALISATION******************/
	/**
	 * Initialtion des joueurs du jeu
	 */
	private void initJoueurs(){
		getJoueurs().add(new Joueur("Joueur 1"));
		getJoueurs().add(new Joueur("Joueur 2"));
		joueurIterateur = getJoueurs().iterator();
		//Place l'iterateur sur le premier joueur
		setJoueurActuel(joueurIterateur.next());
	}
	
	/**
	 * Reinitialise le jeu
	 */
	public void reset() {
		//Fixe le premier joueur
		determinerOrdre();
		
		//Reset les equipes
		for(Joueur o : getJoueurs()){
			o.clearEquipe();
		}
		
		//Reset le placement sur le plateau (placement)
		ControleurPlacement.setCoteJeuChoisi(coteJeu.AUCUN);
	}
	
	/**
	 * Initialisation des Personnage disponible dans le jeu
	 */
	private void initPersonnagesDisponibles(){
                getPersonnagesDisponibles().clear();
		getPersonnagesDisponibles().add(new Magicien());
		getPersonnagesDisponibles().add(new Guerrier());
		getPersonnagesDisponibles().add(new Voleur());
		getPersonnagesDisponibles().add(new Cavalier());
	}
	
	
	/**
	 * Determination du joueur qui jouera en premier(=composera son equipe)
	 */
	private void determinerOrdre(){
		int step = 3;
		int min = 0;
		int max = step * getNbJoueurs();

		int nombreAlea = (int) (min + ( Math.random() * (max - min)));
		nombreAlea = nombreAlea / step;
		
		while(getJoueurActuel() != getJoueurs().get(nombreAlea)){
			joueurSuivant();
		}
	}
	
	
	/******************JOUEUR******************/
	/**
	 * Renvoie la collection de personnages du joueur actuel
	 * @return liste de Personnage
	 */
	public List<Personnage> listerEquipeJoueur(){
		List<Personnage> tousLesPersonnagesJoueur = new ArrayList<Personnage>();
		
		for(Personnage obj : getJoueurActuel().getEquipe().getMembres()){
			if (obj.estVivant() && obj.isPlace()){
				tousLesPersonnagesJoueur.add(obj);
			}
		}
		
		return tousLesPersonnagesJoueur;
	}
        
        /**
	 * Renvoie la collection de personnages des joueurs adverses
	 * @return liste de personnages
	 */
	public List<Personnage> listerEquipesAdverses(){
		List<Personnage> personnages = new ArrayList();
		
                for (Joueur j : getJoueurs()) {
                    if (j != getJoueurActuel()) {
                        for(Personnage pers : j.getEquipe().getMembres()) {
                            if (pers.estVivant() && pers.isPlace()){
                                personnages.add(pers);
                            }
                        }
                    }
                }
		
		return personnages;
	}
	
	/**
	 * Renvoie une collection contenant tous les personnages utilises par les joueurs
	 * @return collection de personnages
	 */
	public List<Personnage> listerEquipes(){
		List<Personnage> tousLesPersonnages = new ArrayList<Personnage>();
		
		for(Joueur o : getJoueurs()){
			for(Personnage obj : o.getEquipe().getMembres()){
				if (obj.estVivant() && obj.isPlace()){
					tousLesPersonnages.add(obj);
				}
			}
		}
		
		return tousLesPersonnages;
	}
		
	/**
	 * Ajoute le Personnage en parametre au joueur actuel
	 * @param personnage Personnage a ajouter au joueur actuel
	 */
	public void ajouterPersonnageJoueur(Personnage personnage){
		getJoueurActuel().ajouterMembre((Personnage) personnage.clone());
	}
	
	/**
	 * dit au joueur actuel que tous ses Personnage sont place
	 * @throws ExceptionPersonnage si il ne sont pas tous place
	 */
	public void personnagesTousPlaces() throws ExceptionPersonnage{
		joueurActuel.personnagesTousPlaces();
	}
	
	/**
	 * Passe au joueur suivant, disponible dans l'attibut joueurActuel
	 */
	public void joueurSuivant(){
		if (getJoueurIterateur().hasNext()) {
			setJoueurActuel(getJoueurIterateur().next());
		} else {
			setJoueurIterateur(getJoueurs().iterator());
			setJoueurActuel(getJoueurIterateur().next());
		}
	}
	
	public boolean isDeplacementEnCours(){
		return getJoueurActuel().IsdeplacementEnCours();
	}
	public boolean isAttaqueEnCours(){
		return getJoueurActuel().IsAttaqueEnCours();
	}
	public boolean isPassifEnCours(){
		return getJoueurActuel().IsPassifEnCours();
	}
	
	public boolean isCaseLibre(Position maPosition){
		for(Personnage o : listerEquipes()){
			if (o.getPosition().equals(maPosition)){
				return false;
			}
		}
		return true;
	}
        
        /**
         * Test si une case est bien sur le plateau
         * @param position Position de la case sur le plateau
         * @return vrai si la case est valide, faux sinon
         */
        public boolean isCaseValide(Position position) {
            return position.getY() >= 0 && position.getY() < getPlateauHauteur()
                    && position.getX() >= 0 && position.getX() < getPlateauLargeur();
        }
	
	public List<String> lancerAttaque(){
		return getJoueurActuel().lancerAttaque();
	}
        
        /**
         * Applique un coup sur la partie courante
         * @param coup Le coup a appliquer
         */
        public void appliquerCoup(Coup coup) {
            //Selectionner le personnage qui joue le coup
            setPersonnageActif(coup.getAuteur());
            
            //Appliquer toutes les actions du coup
            for (Action a : coup.getActions()) {
                a.appliquer(this);
            }
            
            //Indique que ce Personnage a deja joue
            getPersonnageActif().setDejaJoue(true);
            
            //Decremente de 1 tour les effets de tous les Personnage
            getPersonnageActif().effetsTourSuivant();
            
            //Fixe l'etat passif
            setEtatTourPasser();
        }
	
	/****************** GETTERS ******************/
	/**
	 * Renvoi le nom de Personnage du joueur actuel
	 * @return nom du joueur
	 */
	public String getNomJoueur(){
		return getJoueurActuel().getNom();
	}
	
	/**
	 * Donne le Personnage actif du joueurActuel
	 * @return Personnage retourne le Personnage actif
	 */
	public Personnage getPersonnageActif(){
		return getJoueurActuel().getPersonnageActif();
	}
	
	public Sort getAttaqueActif(){
		return getJoueurActuel().getAttaqueActif();
	}
	
	public List<Personnage> getPersonnagesDisponibles() {
		return personnagesDisponibles;
	}
	public int getTailleEquipe() {
		return tailleEquipe;
	}
	public int getPlateauHauteur() {
		return plateauHauteur;
	}
	public int getPlateauLargeur() {
		return plateauLargeur;
	}
        /**
         * Retourne les position du plateau
         * @return une liste de positions
         */
        public List<Position> getToutesPositions() {
            List<Position> l = new ArrayList();
            for (int i = 0; i < getPlateauHauteur(); i++) {
                for (int j = 0; j < getPlateauLargeur(); j++) {
                    l.add(new Position(i, j));
                }
            }
            return l;
        }
        
        /**
         * Retourne tous les coups possibles pour le joueur actif
         * @return une liste de coups
         */
        public synchronized List<Coup> getTousCoups() {
            List<Coup> tousCoups = new ArrayList();
//            System.out.println(Thread.currentThread().getName()+": "+"Equipe courante = " + getJoueurActuel().getEquipe().toString());
            for (Personnage pf : getJoueurActuel().listerEquipe()) {
//                System.out.println("Coup de " + pf.toString());
                if (pf.estVivant() && !pf.isDejaJoue()) {
//                    System.out.println("OK");
                    tousCoups.addAll(getTousCoupsPersonnage(pf));
                }
            }
            return tousCoups;
        }
        
        /**
         * Retourne la liste des coups possibles par un Personnage
         * @param pf le Personnage
         * @return une liste de coups
         */
        public List<Coup> getTousCoupsPersonnage(Personnage pf) {
            List<Coup> coups = new ArrayList();
            // Coup nul
            coups.add(new Coup(pf, new ArrayList()));
            
            // Deplacements seuls
            List<Deplacement> deplacementsTheoriques = pf.getDeplacements();
            List<Deplacement> deplacementsVerifies = new ArrayList();
            for (Deplacement d : deplacementsTheoriques) {
//                System.out.println("Déplacement theorique = " + d.toString());
//                System.out.println("-> " + (isCaseValide(d.getDestination())?"valide":"pas valide") + " && " + (isCaseLibre(d.getDestination())?"libre":"pas libre"));
                if (isCaseValide(d.getDestination()) && isCaseLibre(d.getDestination())) {
                    deplacementsVerifies.add(d);
                    coups.add(new Coup(pf, d));
                }
            }
            
            // Attaques seules
            for (Sort sort : pf.getAttaques()) {
                List<Position> cibles = sort.getZone().getCasesAccessible(pf.getPosition());
                for (Personnage cible : listerEquipes()) {
                    if (cibles.contains(cible.getPosition())) {
                        coups.add(new Coup(pf, new Attaque(sort, cible)));
                    }
                }
            }
            
            return coups;
        }
        
	public List<Joueur> getJoueurs() {
		return joueurs;
	}
	public int getNbJoueurs(){
		return getJoueurs().size();
	}
	public Joueur getJoueurActuel() {
		return joueurActuel;
	}
	private Iterator<Joueur> getJoueurIterateur() {
		return joueurIterateur;
	}

	public void signifierVictoire(Joueur joueurGagnant) {
		joueurGagnant.signifierVictoire();
		
	}
        
        /**
         * Teste si la partie courante est terminee
         * @return Vrai si la partie est terminee, faux sinon
         */
        public boolean estTerminee() {
            for (Joueur j : joueurs) {
                if (j.estBattu()) { return true; }
            }
            return false;
        }
	
	/****************** SETTERS ******************/
	/**
	 * Fixe le Personnage actif du joueurActuel
	 * @param o Personnage a mettre a actif
	 */
	public void setPersonnageActif(Personnage o){
		getJoueurActuel().setPersonnageActif(o);
	}
	
	public void setAttaqueActif(Sort o){
		getJoueurActuel().setAttaqueActif(o);
	}
	
	public void setEtatTourPasser() {
		getJoueurActuel().setEtatTourPasser();
	}

	public void setEtatTourDeplacer() {
		getJoueurActuel().setEtatTourDeplacer();
	}
	
	public void setEtatTourAttaquer() {
		getJoueurActuel().setEtatTourAttaquer();
	}
	
	public boolean setPositionPersonnage(Position maPosition){
		if(!isCaseLibre(maPosition)){
			return false;
		}
		getJoueurActuel().getEquipe().setPositionPersonnage(maPosition);
		return true;
	}
	
	/**
	 * Attaque avec le sort passe en parametre sur la case dont la position est passee en parametre, ne fait rien si la case est vide
	 * @param maPosition position a attaquer
	 * @param monAttaque sort selectionne
	 */
	public void setPersonnagesAttaques(Position maPosition, Sort monAttaque){
		//Parcours de tous les Personnage en jeu
		for(Personnage o : listerEquipes()){
			if (o.getPosition().equals(maPosition)){
				getJoueurActuel().addPersonnageAttaque(o);
				break;
			}
		}
	}
	
	/**
	 * Fixe les tailles d'equipes et de plateau, leve une exception si erreur (taille negative, plateau trop petit)
	 * @param tailleEquipe taille de l'equipe
	 * @param taillePlateauLongueur longueur du plateau
	 * @param taillePlateauLargeur largeur du plateau
	 * @throws ExceptionParamJeu exception taille negative ou plateau trop petit
	 */
	public void setTailleEquipePlateau(int tailleEquipe, int taillePlateauHauteur, int taillePlateauLargeur) throws ExceptionParamJeu{
		if (tailleEquipe <= 0 || taillePlateauHauteur <= 0 || taillePlateauLargeur <= 0){
			throw new ExceptionParamJeu(ExceptionParamJeu.error.NEGATIVE);
		}
		if ((taillePlateauHauteur * taillePlateauLargeur) < (tailleEquipe * 2)){
			throw new ExceptionParamJeu(ExceptionParamJeu.error.PLATEAU);
		}
		setTailleEquipe(tailleEquipe);
		setPlateauHauteur(taillePlateauHauteur);
		setPlateauLargeur(taillePlateauLargeur);
	}
	
	
	/******************PRIVATE SETTERS******************/
	private void setPersonnagesDisponibles(List<Personnage> personnagesDisponible) {
		personnagesDisponibles = personnagesDisponible;
	}
	private void setTailleEquipe(int tailleEquipe) {
		this.tailleEquipe = tailleEquipe;
	}
	private void setPlateauHauteur(int plateauHauteur) {
		this.plateauHauteur = plateauHauteur;
	}
	private void setPlateauLargeur(int plateauLargeur) {
		this.plateauLargeur = plateauLargeur;
	}
	private void setJoueurs(List<Joueur> joueurs) {
		this.joueurs = joueurs;
	}
	private void setJoueurIterateur(Iterator<Joueur> joueurIterateur) {
		this.joueurIterateur = joueurIterateur;
	}
	private void setJoueurActuel(Joueur joueurActuel) {
		this.joueurActuel = joueurActuel;
	}
}