package GuDuWa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import Controleur.Partie;
import IA.*;
import Model.Coup;
import Model.Joueur;
import Model.Personnage;

public class MonIA extends AbstractIA {
	private int aplha = 50;
	private int beta = -50;
	private int profondeur = 5;
	
	private int valeurMeilleurCoup = 0;
	private Coup monMeilleurCoup = null;
	
	public MonIA(String nom) {
		super(nom);
	}

	@Override
	public Coup getCoup(Partie p) {

		alphaBeta(p.clone(), this.aplha, this.beta, true, this.profondeur);
		
		return getCoupMemorise();
	}
	
	//TODO ici chaque joueur joue à tour de role. En réalité un joueur peut jouer deux fois il l'autre possède un personnage de moins.
	
	public int alphaBeta(Partie modelClone, int alpha, int beta, boolean noeudMax, int profondeur) {
		Joueur joueur = modelClone.getJoueurActuel();
		boolean partieFini = modelClone.estTerminee();
		
		if (profondeur == 0) {
			//Si profondeur max atteinte
			return heuristique_plateau(modelClone);
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
				
				//Récupére toutes les actions possibles du personnage selectionné
				listeAction = modelClone.getTousCoupsPersonnage(personnageChoisi);
				*/
				listeCoup = modelClone.getTousCoups();
				
				//Ordonne et elague la liste de coup
				listeCoup = (List<Coup>) ordonne_coup_puis_elague(listeCoup, Integer.MAX_VALUE);
				
				for(Coup coupJoue : listeCoup) {
					//Applique l'action et passe au joueur suivant
					modelClone.appliquerCoup(coupJoue);
					modelClone.joueurSuivant();
					
					//Noeud suivant
					alphaCourant = alphaBeta(modelClone, alpha, beta, !noeudMax, profondeur - 1);

					modelClone.joueurSuivant();
					
					if (alphaCourant > alpha) {
						//Si un meilleur coups est trouvé
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
				
				//Récupére toutes les actions possibles des personnages adverses
				listeCoup = modelClone.getTousCoups();
				
				//Ordonne et elague la liste de coup
				listeCoup = (List<Coup>) ordonne_coup_puis_elague(listeCoup, Integer.MAX_VALUE);
				
				for(Coup coupJoue : listeCoup) {
					//Applique l'action et passe au joueur suivant
					modelClone.appliquerCoup(coupJoue);
					modelClone.joueurSuivant();
					
					//Noeud suivant
					betaCourant = alphaBeta(modelClone, alpha, beta, !noeudMax, profondeur - 1);

					modelClone.joueurSuivant();
					
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
	
	/**
	 * Calcul l'heuristique de la partie (l'évalue) passé en paramètre et retourne la valeur calculé
	 * @param maPartie partie à évaluer
	 * @return valeur du plateau
	 */
	private int heuristique_plateau(Partie maPartie) {
		
		//Thomas

		return 0;
	}
	/**
	 * Calcul l'heuristique de chaque coup (sa valeur), ordonne par ordre décroissant et ne garde que les nbCoupRetour premiers
	 * @param listeCoup liste de coup à évalué, ordonné et élaguer
	 * @param nbCoupRetour nombre de coup conservé après élaguage
	 */
	private Collection<Coup> ordonne_coup_puis_elague(List<Coup> listeCoup, int nbCoupRetour) {
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

	/**
	 * Choisie et retourne le personnage le plus puissant dans la liste passé en paramètre
	 * @param personnageEquipe liste de personnage
	 * @return personnage choisi
	 */
	private Personnage choix_personnage(List<Personnage> personnageEquipe) {
		Personnage persoChoisi = null;
		
		for (Personnage persoAutre : personnageEquipe) {
			if (persoChoisi == null
					|| facteur_puissance(persoAutre) < facteur_puissance(persoAutre)
					) {
				persoChoisi = persoAutre;
			}
		}
		
		return persoChoisi;
	}

	/**
	 * Calcul l'heuristique du coup (l'évalue) passé en paramètre et retourne la valeur calculé
	 * @param monCoup coup à évaluer
	 * @return valeur du coup
	 */
	private int heuristique_coup(Coup monCoup) {
		

		return 0;
	}
	
	/**
	 * Calcul du facteur de puissance d'un personnage (importance de personnage en début de partie)
	 * @param monPerso personne à évaluer
	 * @return facteur de puissance
	 */
	private int facteur_puissance(Personnage monPerso) {
		
		//David
		
		return 0;
	}
}
