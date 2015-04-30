package GuDuWa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Controleur.Partie;
import IA.*;
import Model.Action;
import Model.Attaque;
import Model.Coup;
import Model.Joueur;
import Model.Personnage;

public class MonIA extends AbstractIA {
	FacteurPuissance monFacteurPuissance;
	private int alpha = 50;
	private int beta = -50;
	private int profondeur = 2;
	
	public MonIA(String nom) {
		super(nom);
	}

	@Override
	public Coup getCoup(Partie p) {

		monFacteurPuissance = FacteurPuissance.getInstance(p);
		
		alphaBeta(p.clone(), this.beta, this.alpha, true, this.profondeur);
		
		return getCoupMemorise();
	}
	
	//TODO ici chaque joueur joue � tour de role. En r�alit� un joueur peut jouer deux fois il l'autre poss�de un personnage de moins.
	
	public int alphaBeta(Partie model, int alpha, int beta, boolean noeudMax, int profondeur) {
//		System.out.println("Max : " + noeudMax + ", profondeur " + profondeur);
		
		boolean partieFini = model.estTerminee();
		
		if (profondeur == 0) {
			//Si profondeur max atteinte
//			System.out.println("------Profondeur max atteinte.------");
			return heuristique_plateau(model);
		} else if (partieFini) {
			//Si la partie est termin�e
			boolean partieGagne = model.listerEquipesAdverses().isEmpty();
			boolean partiePerdu = model.listerEquipes().isEmpty();
			
			if (partieGagne) {
				//Termin�e et gagn�e => retourner la valeur maximum
//				System.out.println("------Partie gagn�.------");
				return this.alpha;
			} else if (partiePerdu) {
				//Termin�e et perdu => retourner la valeur minimum
//				System.out.println("------Partie perdu.------");
				return this.beta;
			} else {
				//Termin�e et match nul => retourner la valeur moyenne
//				System.out.println("------Partie nulle.------");
				return (this.beta + this.alpha) / 2;
			}
		} else {
			//Profondeur non atteinte et partie non termin�e
			//Personnage personnageChoisi;	//Non utilis� pour le moment
			List<Coup> listeCoup;
			int alphaCourant;
			int betaCourant;
			
			if (noeudMax) {
				//A moi de jouer
				
				/*
				 * Pas de filtrage sur les personnages pour le moment
				 * 
				//Choisie un personnage parmis ceux disponible 
				personnageChoisi = choix_personnage(modelClone.getJoueurActuel().getEquipe().);
				
				//R�cup�re toutes les actions possibles du personnage selectionn�
				listeAction = modelClone.getTousCoupsPersonnage(personnageChoisi);
				*/
				listeCoup = model.getTousCoups();
				
				//Ordonne et elague la liste de coup
				listeCoup = (List<Coup>) elague_ordonne_reduit_coup(listeCoup, Integer.MAX_VALUE);

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
							System.out.println("------Coup m�moris�.------");
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
				listeCoup = (List<Coup>) elague_ordonne_reduit_coup(listeCoup, Integer.MAX_VALUE);
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
	 * Calcul l'heuristique de chaque coup (sa valeur), ordonne par ordre d�croissant et ne garde que les nbCoupRetour premiers
	 * @param listeCoup liste de coup � �valu�, ordonn� et �laguer
	 * @param nbCoupRetour nombre de coup conserv� apr�s �laguage
	 */
	private Collection<Coup> elague_ordonne_reduit_coup(List<Coup> listeCoup, int nbCoupRetour) {
		elaguage_coup(listeCoup);
		
		int nombreCoup = listeCoup.size();
		ArrayList<Coup> coupTrie = new ArrayList();
		
		Map<Integer, List<Coup>> coupsEtValeurs = new TreeMap();
		
		//Ordonner les coups par ordre croissant
		for (Coup monCoup : listeCoup) {
			int valeurCoup = heuristique_coup(monCoup);
			
			if (coupsEtValeurs.containsKey(valeurCoup) == false) {
				coupsEtValeurs.put(valeurCoup, new ArrayList<Coup>());
			}
			coupsEtValeurs.get(valeurCoup).add(monCoup);
		}
		
		int compteurCoup = 0;
		//Ne selectionne que les N derniers
		for (Map.Entry<Integer, List<Coup>> coupEtValeur : coupsEtValeurs.entrySet()) {
			compteurCoup += coupEtValeur.getValue().size();
			
			if (nombreCoup - compteurCoup <= nbCoupRetour) {
				for (Coup monCoup : coupEtValeur.getValue()) {
					coupTrie.add(monCoup);
				}
			}
		}
		Collections.reverse(coupTrie);
		
		return coupTrie;
	}
	
	private void elaguage_coup(List<Coup> listeCoup){
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
					}
				}
			}

		}
		
		//TODO supprimer action si auto-attaque ou attaque d'un alli�
	}
	
	//Non encore impl�ment�
	/*private List<Coup> ordonne_coup(List<Coup> listeCoup){
		
		return null;
	}*/

	/**
	 * Choisie et retourne le personnage le plus puissant dans la liste pass� en param�tre
	 * @param personnageEquipe liste de personnage
	 * @return personnage choisi
	 */
	private Personnage choix_personnage(List<Personnage> personnageEquipe) {
		Personnage persoChoisi = null;
		
		for (Personnage persoAutre : personnageEquipe) {
			if (persoChoisi == null	|| monFacteurPuissance.getByPerso(persoAutre) < monFacteurPuissance.getByPerso(persoAutre)) {
				persoChoisi = persoAutre;
			}
		}
		
		return persoChoisi;
	}

	/**
	 * Calcul l'heuristique du coup (l'�value) pass� en param�tre et retourne la valeur calcul�
	 * @param monCoup coup � �valuer
	 * @return valeur du coup
	 */
	private int heuristique_coup(Coup monCoup) {
		

		return 0;
	}
}
