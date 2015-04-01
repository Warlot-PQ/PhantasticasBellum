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
	
	//TODO ici chaque joueur joue à tour de role. En réalité un joueur peut jouer deux fois il l'autre possède un personnage de moins.
	//TODO classé les fils dans l'ordre
	
	public int alphaBeta(Partie model, Joueur joueur, int alpha, int beta, boolean noeudMax, int profondeur) {
		Partie modelClone = model.clone();
		boolean partieFini = modelClone.estTerminee();
		
		if (profondeur == 0) {
			//Si profondeur max atteinte
			return heuristique(joueur);
		} else if (partieFini) {
			//Si la partie est terminée
			
			modelClone.joueurSuivant();
			boolean partieGagne = modelClone.getJoueurActuel().estBattu();
			modelClone.joueurSuivant();
			
			boolean partiePerdu = modelClone.getJoueurActuel().estBattu();
			
			if (partieGagne) {
				//Terminée et gagnée => retourner la valeur maximum
				return this.aplha;
			} else if (partiePerdu) {
				//Terminée et perdu => retourner la valeur minimum
				return this.beta;
			} else {
				//Terminée et match nul => retourner la valeur moyenne
				return (this.beta + this.aplha) / 2;
			}
		} else {
			//Profondeur non atteinte et partie non terminée
			Personnage personnageChoisi;
			List<Coup> listeAction;
			int alphaCourant;
			int betaCourant;
			
			if (noeudMax) {
				//A moi de jouer
				
				/*
				//Choisie un personnage parmis ceux disponible 
				personnageChoisi = choixPersonnage(modelClone.getJoueurActuel().getEquipe().);
				
				//Récupére toutes les actions possibles du personnage selectionné
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
						//Si un meilleur coups est trouvé
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
				
				//Récupére toutes les actions possibles des personnages adverses
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
						//Si meilleur coups trouvé
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
	
	private int facteur_puissance() {
		
		//David
		
	}
	
	private int heuristique_coup(Joueur joueur) {
		


	}
	
	private int heuristique_plateau(Joueur joueur) {
		
		//Thomas

	}
	
	private void ordonne_coup_puis_elague(List<Coup> listeCoup) {
		
		
		
	}
	
	private Personnage choix_personnage(List<Personnage> personnageEquipe) {
		
		//PQ
		
	}
}
