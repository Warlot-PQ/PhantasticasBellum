package GuDuWa;

import Controleur.Partie;
import Model.Personnage;

public class HeuristiquePlateau {
	Partie maPartie = null;
	
	/**
	 * Constructeur
	 * @param _maPartie partie dont on applique l'heuristique
	 */
	public HeuristiquePlateau(Partie _maPartie){
		maPartie = _maPartie;
	}
	
	/**
	 * calcul de l'heuristique maximal de l'heuristique du nombre de personnage
	 * @return int valeur de l'heuristique maximal
	 */
	public int calculHeuristiqueMaxnbPersonnage(){
//		12 = PV max du PFs
		return  maPartie.getTailleEquipe()*4 + maPartie.getTailleEquipe() * 12 / 2; 
	}
	
	/**
	 * calcul de l'heuristique maximal de l'heuristique du placement des personnages
	 * @return int valeur de l'heuristique maximal
	 */
	public int calculHeuristiqueMaxPlacement(){
//		1er "*2" est le coefficient si un magicien est colle a un PFs adverse
//		2eme morceau c'est pour l'inter-PFs
		return  maPartie.getTailleEquipe()*2 + (maPartie.getTailleEquipe()-1)*maPartie.getTailleEquipe();
	}
	
	/**
	 * calcul de l'heuristique maximal generale
	 * @return int valeur de l'heuristique maximal
	 */
	public int calculHeuristiqueMax(){
		return calculHeuristiqueMaxnbPersonnage() +calculHeuristiqueMaxPlacement() ;
	}
	
	/**
	 * Doit retourner une heuristique entre -50 et 50
	 * @return int l'heuristique generale
	 */
	public int calculHeuristique(){
		return ((h_nbPersonnage() + h_Placement()) *50 )/ calculHeuristiqueMax();
	}
	
	/**
	* Heuristique prenant en compte le nombre de personnage vivant ainsi que leurs point de vie
	* @return int heuristique comprise entre [38/2+4*4,-(38/2+4*3)] = 14 + 16,
	*/
	public int h_nbPersonnage(){
		int nb_Personnage_Allie = 0;
		int nb_Personnage_Adverse = 0;
		int nb_PointDeVie_Allie = 0;
		int nb_PointDeVie_Adverse = 0;
		for(Personnage PFs : maPartie.listerEquipeJoueur()){
//			Cas où c'est un PFs du joueur actuel
			nb_Personnage_Allie = nb_Personnage_Allie + 1;
			nb_PointDeVie_Allie += PFs.getVie();
		}
		for(Personnage PFs : maPartie.listerEquipesAdverses()){
//			Cas où c'est un PFs adversaire
			nb_Personnage_Adverse = nb_Personnage_Adverse + 1;
			nb_PointDeVie_Adverse += PFs.getVie();
		}
		return nb_Personnage_Allie*4 - nb_Personnage_Adverse*4 + nb_PointDeVie_Allie/2 - nb_PointDeVie_Adverse/2;
	}

	/**
	 * Heuristique prenant en compte le placement des personnages sur le plateau
	 * @return int heuristique
	 */
	public int h_Placement(){
		int distanceAdversaireLePlusProche =0;
		float heuristiqueCourante = 0;
//		On va verifier la proximiter d'un adversaire par rapport a nos personnage
		for(Personnage PFs : maPartie.listerEquipeJoueur() ){
			distanceAdversaireLePlusProche = distanceAdversairePlusProche(PFs);
			if(PFs.getClasse() == "Magicien"){
// 				C'est un magicien
				if(distanceAdversaireLePlusProche == 0){
					heuristiqueCourante += -1 ;
				} else if(distanceAdversaireLePlusProche < maPartie.getPlateauLargeur()/2){
						heuristiqueCourante += 0;
					}	else {
							heuristiqueCourante +=2;
						}
			} else {
// 					Ce n'est pas un magicien
					if(distanceAdversaireLePlusProche == 0){
						heuristiqueCourante += 0.5 ;
					} else if(distanceAdversaireLePlusProche < maPartie.getPlateauLargeur()/2){
							heuristiqueCourante += 0.25;
						}	else {
								heuristiqueCourante +=0;
							}
				}
		}
//		Ensuite on va verifier le placement de nos personnages entre eux
		heuristiqueCourante += PlacementInterPFs();
		return (int) Math.floor(heuristiqueCourante);
	}
	
	/**
	 * Retourne la distance de l'adversaire le plus proche
	 * @param PFs dont on veux connaitre l'adversaire le plus proche
	 * @return int la distance
	 */
	private int distanceAdversairePlusProche(Personnage PFs) {
		int meilleurDistanceCourante = Integer.MAX_VALUE;
		int valeurDeCalcul = Integer.MAX_VALUE;
		for(Personnage PFsAdverse : maPartie.listerEquipesAdverses()){
			valeurDeCalcul = Math.abs(PFs.getPosition().getX() - PFsAdverse.getPosition().getX()) + 
							Math.abs(PFs.getPosition().getY() - PFsAdverse.getPosition().getY());
			if(valeurDeCalcul < meilleurDistanceCourante){
				meilleurDistanceCourante = valeurDeCalcul;
			}
		}
		return meilleurDistanceCourante;
	}

	/**
	 * Calcul une heuristique de placement entre les Personnages
	 * @return int : malus par rapport aux nombre de Personnage collé compris entre 0 et -(taille equipe *  taille equipe)
	 */
	public int PlacementInterPFs(){
		int minX,maxX,minY,maxY;
		int malus = 0; 
		for(Personnage PFs : maPartie.listerEquipeJoueur()){
//			Traitement
			minX = PFs.getPosition().getX()-1;
			maxX = PFs.getPosition().getX()+1;
			minY = PFs.getPosition().getY()-1;
			maxY = PFs.getPosition().getY()+1;
			for(Personnage autrePFs : maPartie.listerEquipeJoueur()){
//				Si c'est un autre PFs que celui que l'on test
				if(PFs != autrePFs){
//					Si ce PFs est autour du PFs tester, donc si deux PFs se collent
					if(minY <= autrePFs.getPosition().getY() && maxY >= autrePFs.getPosition().getY()){
						if(minX <= autrePFs.getPosition().getX() && maxX >= autrePFs.getPosition().getX()){
							malus += 1;
						}
					}
				}
			}
		}
		return -malus;
	}
}
