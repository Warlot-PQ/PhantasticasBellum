package GuDuWa;
import java.util.List;

import Controleur.Partie;
import Model.Personnage;
import Model.Sort;
import Personnages.Cavalier;
import Personnages.Guerrier;
import Personnages.Magicien;
import Personnages.Voleur;

/**
 * Must be created by a MonIA instance,
 * Permet de calculer la valeur pondérée (entre 0 et 1) de chacun des personnages présents.
 * Facteurs affectables:
 * - coefPV: défini l'importance associée aux points de vie
 * - coefAtt: [...] à l'impact des attaques
 * - coefDepl: [...] à la portée max des attaques
 * 
 * La somme de ces coefs doit valoir 1
 * @author David Dufresne
 *
 */
public class FacteurPuissance
{
	private double[] tabFacteurPuissance;
	
	public FacteurPuissance(Partie p)
	{
		this(p, 0.1, 0.7, 0.2);		
	}
		
	public FacteurPuissance(Partie p, double coefPV, double coefAtt, double coefDepl)
	{
		tabFacteurPuissance = new double[4];
		generateFacteurPuissance(coefPV, coefAtt, coefDepl, p);
	}



	/**
	 * Calcul du facteur de puissance d'un personnage (importance de personnage
	 * en début de partie) Renvoie une valeur comprise entre 0 et 1
	 * 
	 * @param monPerso
	 *            personne à évaluer
	 * @return facteur de puissance
	 */
	public double facteur_puissance(Personnage monPerso) {
		return tabFacteurPuissance[getRefTableau(monPerso)];
	}

	private int getRefTableau(Personnage p) {
		if (p instanceof Voleur) {
			return 0;
		}
		if (p instanceof Magicien) {
			return 1;
		}
		if (p instanceof Cavalier) {
			return 2;
		}
		if (p instanceof Guerrier) {
			return 3;
		}
		return -1;
	}


	
	public void generateFacteurPuissance(double coefPV, double coefAtt, double coefDepl, Partie maPartie)
	{
		List<Personnage> mesPerso = maPartie.getPersonnagesDisponibles();
	
			
		int[] 	tabPV 	= new int[4],
				tabAtt 	= new int[4],
				tabDep 	= new int[4];
				
		System.out.println("listePerso: taille= " + mesPerso.size());
		for (Personnage temp : mesPerso)
		{
			int indice=getRefTableau(temp);
			// PV
			tabPV[indice]=	temp.getMaxVie();
			
			// Attaque

			List<Sort> listAtt = temp.getAttaques();
			
			int nbsort=0;
			int somme=0;
			
			int sommeDepl=0;//depl
			
			for (Sort s: listAtt)
			{
				//partie Attaque
				nbsort++;
				somme += s.getDegat();
				
				//partie Deplacement
				sommeDepl += s.getPorteeMax();
			}
			//att
			tabAtt[indice]= somme / nbsort;			
			//depl
			tabDep[indice]= sommeDepl /nbsort;

		}
		
		int maxPV=-1, 
			maxAtt=-1, 
			maxDepl=-1;
		
		for(int k=0;k<4;k++)
		{
			maxPV=Math.max(maxPV, tabPV[k]);
			maxAtt=Math.max(maxAtt, tabAtt[k]);
			maxDepl=Math.max(maxDepl, tabDep[k]);
		}
		
		// Formule: a * PVperso / PVmaxPersos + b * attaquePerso / AttaqueMax + c * deplPerso / dePlMax
		// Prérequis: a + b +c = ;; a,b,c > 0
		for (int j=0; j<4; j++)
		{
			tabFacteurPuissance[j]= 	coefPV * ((float) tabPV[j]) / ((float) maxPV) +
										coefAtt * ((float) tabAtt[j]) / ((float) maxAtt) +
										coefDepl * ((float) tabDep[j]) / ((float) maxDepl) ;					
		}
	}
	
}
