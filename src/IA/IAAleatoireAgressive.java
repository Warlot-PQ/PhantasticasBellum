package IA;

import Controleur.Partie;
import Model.Action;
import Model.Attaque;
import Model.Coup;
import java.util.ArrayList;
import java.util.List;

/**
 * Joueur artificiel qui choisit chaque coup au hasard
 *
 * @author Gwenole Lecorve
 */
public class IAAleatoireAgressive extends IA {

    /**
     * Constructeur par defaut
     */
    public IAAleatoireAgressive() {
        super("IA aleatoire agressive");
    }

    /**
     * Constructeur
     *
     * @param nom Nom du joueur artificiel
     */
    public IAAleatoireAgressive(String nom) {
        super(nom);
    }

    protected boolean contientAttaque(Coup c) {
        for (Action a : c.getActions()) {
            if (a instanceof Attaque) {
//                System.out.println("Cible = " + ((Attaque) a).getCible().getProprio().getNom());
//                System.out.println("Auteur = " + this.getNom());
                return true;
            }
        }
        return false;
    }

    @Override
    public Coup getCoup(Partie p) {
        System.out.println(Thread.currentThread().getName() + ": " + "-------- getCoup ---------");
        List<Coup> coups = p.getTousCoups();
        List<Coup> attaques = new ArrayList();
        for (Coup c : coups) {
            if (contientAttaque(c)) {
                System.out.println(Thread.currentThread().getName() + ": " + "Coup possible : " + c.toString());
                attaques.add(c);
            }
        }
        Coup resultat;
        if (attaques.isEmpty()) {
            resultat = coups.get((int) (Math.random() * (coups.size() - 1)));
        } else {
            resultat = attaques.get((int) (Math.random() * (attaques.size() - 1)));
        }
        System.out.println(Thread.currentThread().getName() + ": " + "Coup calcule = " + resultat.toString());
        return resultat;
    }

}
