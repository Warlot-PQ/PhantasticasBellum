package GuDuWa;

import java.util.List;

import Model.Matrice;
import Model.Personnage;
import Model.Sort;
import Personnages.Cavalier;
import Personnages.Guerrier;
import Personnages.Magicien;
import Personnages.Voleur;

/**
 * Must be created by a MonIA instance,
 * @author David Dufresne
 *
 */
public class FacteurPuissance
{
	private double[] tabFacteurPuissance = new double[4];
	private MonIA myIA;
	
	public FacteurPuissance(MonIA m)
	{
		this(m, 0.4, 0.4, 0.2);		
	}
	
	public FacteurPuissance(MonIA m, double a, double b, double c)
	{
		myIA=m;
		generateFacteurPuissance(a, b, c);
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


	
	public void generateFacteurPuissance(double a, double b, double c)
	{
		List<Personnage> mesPerso = myIA.getEquipe().getMembres();
			
		int[] 	tabPV 	= new int[4],
				tabAtt 	= new int[4],
				tabDep 	= new int[4];
				
		for (Personnage temp : mesPerso)
		{
			int indice=getRefTableau(temp);
			// PV
			tabPV[indice]=	temp.getMaxVie();
			
			// Attaque

			List<Sort> listAtt = temp.getAttaques();
			
			int nbsort=0;
			int somme=0;
			
			for (Sort s: listAtt)
			{
				nbsort++;
				somme += s.getDegat()*s.getPorteeMax();
			}
			tabAtt[indice]= somme / nbsort;
			
			//depl
			Matrice mat=temp.getMouvement();
			tabDep[indice]= Math.max(mat.getColonne(), mat.getLigne());

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
			tabFacteurPuissance[j]= 	a * ((float) tabPV[j]) / ((float) maxPV) +
										b * ((float) tabAtt[j]) / ((float) maxAtt) +
										c * ((float) tabDep[j]) / ((float) maxDepl) ;					
		}
	}
}
