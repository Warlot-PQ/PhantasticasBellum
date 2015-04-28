package GuDuWa;

import java.util.Iterator;
import java.util.List;

import Controleur.Partie;
import IA.*;
import Model.Action;
import Model.Attaque;
import Model.Coup;
import Model.Personnage;
import Model.Personnage.creatureType;

public class IAGuduwa extends AbstractIA {
	FacteurPuissance monFacteurPuissance;
	private int alpha = 50;
	private int beta = -50;
	private int profondeur = 2;
	
	public IAGuduwa(String nom) {
		super(nom);
	}

	@Override
	public Coup getCoup(Partie p) {

		monFacteurPuissance = FacteurPuissance.getInstance(p);
		
		alphaBeta(p.clone(), this.beta, this.alpha, true, this.profondeur);
		
		return getCoupMemorise();
	}
	
	private int alphaBeta(Partie model, int alpha, int beta, boolean noeudMax, int profondeur) {
		boolean partieFini = model.estTerminee();
		
		if (profondeur == 0) {
			return heuristique_plateau(model);
		} else if (partieFini) {
			//Si la partie est termin�e
			boolean partieGagne = model.listerEquipesAdverses().isEmpty();
			boolean partiePerdu = model.listerEquipes().isEmpty();
			
			if (partieGagne) {
				//Termin�e et gagn�e => retourner la valeur maximum
				return this.alpha;
			} else if (partiePerdu) {
				//Termin�e et perdu => retourner la valeur minimum
				return this.beta;
			} else {
				//Termin�e et match nul => retourner la valeur moyenne
				return (this.beta + this.alpha) / 2;
			}
		} else {
			//Profondeur non atteinte et partie non termin�e
			Personnage personnageChoisi;	//Non utilis� pour le moment
			List<Coup> listeCoup;
			int alphaCourant;
			int betaCourant;
			
			if (noeudMax) {
				//A moi de jouer
				
				/*
				 * Pas de filtrage sur les personnages pour le moment
				 */ 
				//Choisie un personnage parmis ceux disponible 
				List<Personnage> mesPerso = model.listerEquipeJoueur();
				personnageChoisi = choix_personnage(mesPerso);
				
				//R�cup�re toutes les actions possibles du personnage selectionn�
				listeCoup = model.getTousCoupsPersonnage(personnageChoisi);
				//listeCoup = model.getTousCoups();
				
				//Ordonne et elague la liste de coup
				listeCoup = (List<Coup>) elaguage_coup(listeCoup);

				for(Coup coupJoue : listeCoup) {
					Partie modelClone = model.clone();
					
					//Applique l'action
					modelClone.appliquerCoup(coupJoue);
					modelClone.tourSuivant();
					
					//Noeud suivant
					alphaCourant = alphaBeta(modelClone, alpha, beta, !noeudMax, profondeur - 1);

					if (alphaCourant > alpha) {
						//Si un meilleur coups est trouv�
						alpha = alphaCourant;
						//Sauvegarde le coup si on est au premier niveau de profondeur
						if (profondeur == this.profondeur) {
							memoriseCoup(coupJoue);
						}
					}
					//Coupure beta
					if (alpha >= beta) {
                    	return alpha;
                    }
				}
				
				return alpha;
			} else {
				//A l'adversaire de jouer
				
				//R�cup�re toutes les actions possibles des personnages adverses
				listeCoup = model.getTousCoups();
				
				//Ordonne et elague la liste de coup
				listeCoup = elaguage_coup(listeCoup);
				for(Coup coupJoue : listeCoup) {
					Partie modelClone = model.clone();
					
					//Applique l'action
					modelClone.appliquerCoup(coupJoue);
					modelClone.tourSuivant();
					
					//Noeud suivant
					betaCourant = alphaBeta(modelClone, alpha, beta, !noeudMax, profondeur - 1);

					if (betaCourant < beta) {
						//Si pire coups trouv�
						beta = betaCourant;
					}
					//Coupure alpha
					if (beta <= alpha) {
	                	return beta;
					}
				}
				
				return beta;
			}
		}
	}
	
	/**
	 * Calcul l'heuristique de la partie (l'�value) pass� en param�tre et retourne la valeur calcul�
	 * @param maPartie partie � �valuer
	 * @return valeur du plateau
	 */
	private int heuristique_plateau(Partie maPartie) {

		return new HeuristiquePlateau(maPartie).calculHeuristique();
	}
	
	/**
	 * Elague les coups non necessaire
	 * @param listeCoup liste � �laguer
	 * @return liste �lagu�
	 */
	private List<Coup> elaguage_coup(List<Coup> listeCoup){
		Iterator<Coup> listeCoupIterator = listeCoup.iterator();
		
		while(listeCoupIterator.hasNext()) {
			Coup monCoup = listeCoupIterator.next();
			
			//Supprime les actions vides (passer son tour)
			if (monCoup.getActions().isEmpty()) {
				listeCoupIterator.remove();
			}
			// On parcours toutes les actions de notre coups
			for(Action a : monCoup.getActions()){
				// On regarde si l'action est une attaque
				if(a instanceof Attaque){
					// Si la cible de l'attaque est un personnage de la même équipe
					if(((Attaque) a).getCible().getProprio() == monCoup.getAuteur().getProprio()){
						listeCoupIterator.remove();
					} else
					// Si la cible de l'attaque est un personnage de type different que celle de l'attaque
					if(((Attaque) a).getSort().getTypeCible() != creatureType.TOUT &&
							((Attaque) a).getCible().getType() != ((Attaque) a).getSort().getTypeCible()){
						listeCoupIterator.remove();
					} else 	if(((Attaque) a).getSort().getDegat() == 0){
							listeCoupIterator.remove();
					}
				}
			}
		}
		return listeCoup;
	}

	/**
	 * Choisie et retourne le personnage le plus puissant dans la liste pass� en param�tre
	 * @param personnageEquipe liste de personnage
	 * @return personnage choisi
	 */
	private Personnage choix_personnage(List<Personnage> personnageEquipe) {
		Personnage persoChoisi = null;
		
		for (Personnage persoAutre : personnageEquipe) {
			if(persoAutre.isDejaJoue()){
				continue;
			}
			if (persoChoisi == null	|| monFacteurPuissance.getByPerso(persoAutre) < monFacteurPuissance.getByPerso(persoAutre)) {
				persoChoisi = persoAutre;
			}
		}
		
		
		return persoChoisi;
	}
}
