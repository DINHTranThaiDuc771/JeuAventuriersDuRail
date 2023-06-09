package metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import controleur.Controleur;
import metier.partie.CarteWagon;

import java.awt.Color;
import java.io.Serializable;

public class Joueur implements Serializable
{
	private transient Controleur ctrl;

    private String                 nom;
    private int                    score;
	private int                    nbJetonsRestant;
    private Color                  couleur;
    private int                    nbCartesObjectif;

    private List<CarteObjectif>    alCartesObjectif;
	private List<CarteObjectif>    alObjectifsFinis;
    private HashMap<Color,Integer> hashMapCarteWagons;
	private List<Color>            alCouleurs;
    private List<CarteWagon>       alCartesWagons;
    
	/* ==================== */
	/*     CONSTRUCTEUR     */
	/* ==================== */
    public Joueur(Controleur ctrl, String nom)
    {
		this.ctrl = ctrl;

		this.alCartesObjectif   = new ArrayList<CarteObjectif>();
		this.alObjectifsFinis   = new ArrayList<CarteObjectif>();
		this.hashMapCarteWagons = new HashMap<Color,Integer>();
		this.alCouleurs         = new ArrayList<Color>();
		this.alCartesWagons     = new ArrayList<CarteWagon>();

        this.nom = nom;
        this.score = 0;
        this.nbCartesObjectif = 0;
    }

	/* ==================== */
	/*       GETTERS        */
	/* ==================== */
    public String                 getNom               () { return this.nom;                                                    }
    public int                    getScore             () { return this.score;                                                  }
    public List<CarteObjectif>    getAlCartesObjectif  () { return this.alCartesObjectif;                                       }
    public int                    getNbCartesObjectif  () { return this.nbCartesObjectif;                                       }
    public HashMap<Color,Integer> gethashMapCarteWagons() { return this.hashMapCarteWagons;                                     }
    public List<CarteWagon>       getAlCartesWagons    () { return this.alCartesWagons;                                         }
	public List<Color>            getAlCouleurs        () { return this.alCouleurs;                                             }
    public int                    getNbCartesWagon     () { return this.alCartesWagons.size();                                  }
    public int                    getNbJetonsRestant   () { return this.nbJetonsRestant;                                        }
    public Color                  getCouleur           () { return this.couleur;                                                }
	public Controleur             getControleur        () { return this.ctrl;                                                   }
    public List<CarteObjectif>    getAlObjectifsFinis  () { return this.alObjectifsFinis;                                       }
	public int                    getNbObjectifRestant () { return this.alCartesObjectif.size() - this.alObjectifsFinis.size(); }

	public int getNbArete () 
	{
		int res = 0;

		for (Arete a : this.ctrl.getAretes())
		{
			if (this.equals(a.getProprietaire1()) || this.equals(a.getProprietaire2()))
				res++;
		}

		return res;
	}

	public int getMalus()
	{
		int res = 0;

		for (CarteObjectif co : this.alCartesObjectif)
			if (!this.alObjectifsFinis.contains(co))
				res += co.getPoints();

		return res;
	}

	/* ==================== */
	/*       SETTERS        */
	/* ==================== */
	public void setCtrl              (Controleur             ctrl              ) { this.ctrl = ctrl;                             }
    public void setNom               (String                 nom               ) { this.nom = nom;                               }
    public void setScore             (int                    score             ) { this.score = score;                           }
    public void setAlCartesObjectif  (List<CarteObjectif>    alCartesObjectif  ) { this.alCartesObjectif = alCartesObjectif;     }
    public void sethashMapCarteWagons(HashMap<Color,Integer> hashMapCarteWagons) { this.hashMapCarteWagons = hashMapCarteWagons; }
    public void setNbJetonsRestant   (int                    nbJetonsRestant   ) { this.nbJetonsRestant = nbJetonsRestant;       }
    public void setCouleur           (Color                  couleur           ) { this.couleur = couleur;                       }

	/* ===================================================== */
	/*  METHODES DE GESTION DES RESSOURCES DE L'UTILISATEUR  */
	/* ===================================================== */
    
	// gestion des cartes wagons
    public void ajouterCarteWagon(CarteWagon carteWagon)
    {
		Color coul = null;
		
		if (!carteWagon.isJoker())
        	coul = carteWagon.getCouleur();

		if (!this.hashMapCarteWagons.containsKey(coul))
		{
			this.hashMapCarteWagons.put(coul, 0);
			this.alCouleurs.add(coul);
		}

		
		this.alCartesWagons.add(carteWagon);
        Integer nbCarte = this.hashMapCarteWagons.get(coul) + 1 ;
        this.hashMapCarteWagons.replace(coul,nbCarte);
    }

    public void retirerCarteWagon(CarteWagon carteWagon)
    {
        Color coul = carteWagon.getCouleur();
        Integer nbCarte = this.hashMapCarteWagons.get(coul) - 1 ;
        this.hashMapCarteWagons.replace(coul,nbCarte);

		if (nbCarte == 0)
		{
			this.hashMapCarteWagons.remove(coul);
			this.alCouleurs.remove(coul);
		}

        this.alCartesWagons.remove(carteWagon);
    }

	// gestion des objectifs
	public void ajouterCarteObjectif(CarteObjectif carteObjectif)
    {
        this.alCartesObjectif.add(carteObjectif);
        this.nbCartesObjectif++;
    }

	public void verifierObjectifs()
	{
		Iterator<CarteObjectif> it = this.alCartesObjectif.iterator();
		while (it.hasNext()) 
		{
			CarteObjectif co = it.next();
			
			if (co.estValide(this) && !this.alObjectifsFinis.contains(co)) 
			{
				this.alObjectifsFinis.add(co);
                this.ctrl.validerObjectif();
                this.nbCartesObjectif--;
			}
		}
	}

	// gestion des points
	public void ajouterScore(int score)
    {
        this.score += score;
    }

	public void ajouterObjectifs()
	{
		for (CarteObjectif co : this.alObjectifsFinis)
			this.score += co.getPoints();
	}

	// gestion des jetons
	public void retirerJeton(int nb)
	{
		this.nbJetonsRestant -= nb;
	}

	/* ================= */
	/*  AUTRES METHODES  */
	/* ================= */
    public Boolean equals(Joueur j)
    {
		if (j != null)
        	return j.nom.equals(this.nom);
		else
			return false;
    }

    public String toString()
    {
        return this.nom + " (" + this.score + " points)" + " : " + this.nbJetonsRestant + 
			" jetons posés" + " -- " + this.alCartesObjectif.size() + " cartes objectif" + 
			" : " + this.hashMapCarteWagons.size() + " cartes wagon";
    }
}