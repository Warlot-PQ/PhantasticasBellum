package GuDuWa;

import java.util.List;
import java.util.ArrayList;

import Controleur.Partie;
import IA.IA;
import Model.Coup;
import Model.Joueur;
import Model.Personnage;

public class MonIA extends IA {
	private int aplha = 50;
	private int beta = -50;
	
	public MonIA(String nom) {
		super(nom);
	}

	@Override
	public Coup getCoup(Partie p) {
		
		
		
		
		alphaBeta(p, p.getJoueurActuel(), this.aplha, this.beta, true, 5);
		
		
		
		
		return null;
	}
	
	//TODO ici chaque joueur joue � tour de role. En r�alit� un joueur peut jouer deux fois il l'autre poss�de un personnage de moins.
	//TODO class� les fils dans l'ordre
	
	public int alphaBeta(Partie model, Joueur joueur, int alpha, int beta, boolean noeudMax, int profondeur) {
		Partie modelClone = model.clone();
		boolean partieFini = modelClone.estTerminee();
		
		if (profondeur == 0) {
			//Si profondeur max atteinte
			return heuristique(joueur);
		} else if (partieFini) {
			//Si la partie est termin�e
			
			modelClone.joueurSuivant();
			boolean partieGagne = modelClone.getJoueurActuel().estBattu();
			modelClone.joueurSuivant();
			
			boolean partiePerdu = modelClone.getJoueurActuel().estBattu();
			
			if (partieGagne) {
				//Termin�e et gagn�e => retourner la valeur maximum
				return this.aplha;
			} else if (partiePerdu) {
				//Termin�e et perdu => retourner la valeur minimum
				return this.beta;
			} else {
				//Termin�e et match nul => retourner la valeur moyenne
				return (this.beta + this.aplha) / 2;
			}
		} else {
			//Profondeur non atteinte et partie non termin�e
			Personnage personnageChoisi;
			List<Coup> listeAction;
			int alphaCourant;
			int betaCourant;
			
			if (noeudMax) {
				//A moi de jouer
				
				/*
				//Choisie un personnage parmis ceux disponible 
				personnageChoisi = choixPersonnage(modelClone.getJoueurActuel().getEquipe().);
				
				//R�cup�re toutes les actions possibles du personnage selectionn�
				listeAction = modelClone.getTousCoupsPersonnage(personnageChoisi);
				*/
				listeAction = modelClone.getTousCoups();
				
				//Ordonne les actions
				//ordonneActions(listeAction);
				
				//Elague la liste en fonction de la profondeur
				//elaguageActions(listeAction, profondeur);
				
				for(Coup action : listeAction) {
					//Applique l'action et passe au joueur suivant
					modelClone.appliquerCoup(action);
					model.joueurSuivant();
					
					//Noeud suivant
					alphaCourant = alphaBeta(modelClone, model.getJoueurActuel(), alpha, beta, !noeudMax, profondeur - 1);
					
					if (alphaCourant > alpha) {
						//Si un meilleur coups est trouv�
						alpha = alphaCourant;
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
				listeAction = modelClone.getTousCoups();
				
				//Elague la liste en fonction de la profondeur
				//elaguageActions(listeAction, profondeur);
				
				for(Coup action : listeAction) {
					//Applique l'action et passe au joueur suivant
					modelClone.appliquerCoup(action);
					model.joueurSuivant();
					
					//Noeud suivant
					betaCourant = alphaBeta(modelClone, model.getJoueurActuel(), alpha, beta, !noeudMax, profondeur - 1);
					
					if (betaCourant > alpha) {
						//Si meilleur coups trouv�
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
	 * Valeur d'utilit�e calcul�e en fonction de la partie
	 * @param joueur joueur actuel
	 * @return valeur d'utilit�e
	 */
	private int heuristique(Joueur joueur) {
		
		return 0;
	}
	
	/**
	 * Ordonne par ordre d�croissant la liste d'actions pass�e en param�tre
	 * @param listeActions liste d'actions
	 * @pre liste d'actions non ordonn�es
	 * @post list d'actions ordonn�es par ordre d'importance d�croissante
	 */
	private void ordonneActions(List<Coup> listeActions) {
		//Ordonne les fils selon un crit�re d'�valuation d'une action
		
		
	}
	
	/**
	 * Elague la liste d'actions pass�e en param�tre en fonction de la profondeur pass�e en param�tre
	 * @param listeActions liste d'actions
	 * @pre list d'actions ordonn�es par ordre d'importance d�croissante
	 * @post list d'actions ordonn�es par ordre d'importance d�croissante �lagu�e
	 */
	private void elaguageActions(List<Coup> listeActions, int profondeur){
		
	}
	
	/**
	 * Choisie le personnage qui va jouer, 
	 * un personnage ayant d�j� jou� sur ce tour n'est pas disponible
	 * @return personnage choisie pour jouer � ce tour
	 */
	private Personnage choixPersonnage(List<Personnage> personnageEquipe) {
		
		
		
		return null;
	}
}
