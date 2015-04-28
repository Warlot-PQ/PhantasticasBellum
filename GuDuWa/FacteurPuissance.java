package GuDuWa;
import java.util.ArrayList;
import java.util.List;

import Controleur.Partie;
import Model.Personnage;
import Model.Sort;
import Personnages.Cavalier;
import Personnages.Guerrier;
import Personnages.Magicien;
import Personnages.Voleur;

/**
 * Le facteur de puissance représente l'importance d'un personnage
 * Utilisation du design pattern Singleton, pour un calcul unique (économie de temps)
 */
public class FacteurPuissance
{
	private static FacteurPuissance mySingleton;
	
	private double magicien;
	private double guerrier;
	private double cavalier;
	private double voleur;
	private double maximumValue;
	
	private double bonusHandToHand = 0;
	private double bonusMediumRange = 0.5;
	private double bonusRange = 1;

	/**
	 * Retourne le singleton FacteurPuissance
	 * @param p partie sur laquelle le facteur de puissance des personnages est calculé
	 * @return instance unique de la classe FacteurPuissance
	 */
	public static FacteurPuissance getInstance(Partie p) {
		if (FacteurPuissance.mySingleton == null) {
			FacteurPuissance.mySingleton = new FacteurPuissance(p);
		}
		return FacteurPuissance.mySingleton;
	}
	
	/**
	 * Lance le calcul du facteur de puissance des personnages
	 * @param p partie sur laquelle le facteur de puissance des personnages est calculé
	 */
	private FacteurPuissance(Partie p) {
		//3 listes ordonnées (liens entre les SDD par l'index d'accès)
		List<Personnage> myPersos = p.getPersonnagesDisponibles();
		List<Integer> totalDammage = new ArrayList();
		List<Integer> totalLife = new ArrayList();
		
		int maxDammage = 0;
		int maxLife =  0;
		
		//Calcul les dégats totaux et vie de chaque perso
		for (Personnage myPerso : myPersos) {
			//Calcul le max des dégats du perso (somme dégat de toutes les attaques)
			int degatSomme = 0;
			for (Sort monSort : myPerso.getAttaques()) {
				degatSomme += monSort.getDegat();
			}
			totalDammage.add(degatSomme);

			//Calcul le max de la vie du perso
			int viePerso = myPerso.getMaxVie();
			totalLife.add(viePerso);
			
			//Sauvegarde les dégats du perso en faisant le plus
			if (maxDammage < degatSomme) {
				maxDammage = degatSomme;
			}
			
			//Sauvegarde la vie du perso en ayant le plus
			if (maxLife < viePerso) {
				maxLife = viePerso;
			}
		}
		
		//Calcul le facteur de puissance associé à chaque perso
		int index = 0;
		for (Personnage myPerso : myPersos) {
			float facteurPuissancePerso = (float) totalDammage.get(index) / maxDammage + (float) totalLife.get(index) / maxLife;
			
			if (myPerso instanceof Voleur) {
				this.voleur = facteurPuissancePerso + this.bonusHandToHand;
				if (this.maximumValue < this.voleur) this.maximumValue = this.voleur;
			} else if (myPerso instanceof Magicien) {
				this.magicien = facteurPuissancePerso + this.bonusRange;
				if (this.maximumValue < this.magicien) this.maximumValue = this.magicien;
			} else if (myPerso instanceof Cavalier) {
				this.cavalier = facteurPuissancePerso + this.bonusMediumRange;
				if (this.maximumValue < this.cavalier) this.maximumValue = this.cavalier;
			} else if(myPerso instanceof Guerrier) {
				this.guerrier = facteurPuissancePerso + this.bonusHandToHand;
				if (this.maximumValue < this.guerrier) this.maximumValue = this.guerrier;
			}
			index += 1;
		}
	}
	
	/**
	 * Retourne le facteur de puissance du personnage passé en paramètre
	 * @param myPerso personnage
	 * @return facteur de puissance du personnage
	 */
	public double getByPerso(Personnage myPerso) {
		if (myPerso instanceof Voleur) {
			return this.voleur;
		} else if (myPerso instanceof Magicien) {
			return this.magicien;
		} else if (myPerso instanceof Cavalier) {
			return this.cavalier;
		} else 	if (myPerso instanceof Guerrier) {
			return this.guerrier;
		}
		return 0;
	}

	/**
	 * Retourne le facteur de puissance compris entre 0 et 1 du personnage passé en paramètre
	 * @param myPerso personnage
	 * @return facteur de puissance du personnage
	 */
	public double getByPersoBetweenZeroAndOne(Personnage myPerso) {
		return getByPerso(myPerso) / this.maximumValue;
	}
}
