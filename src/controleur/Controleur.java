package controleur;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import ihm.Ihm;
import metier.*;
import metier.partie.Partie;
import metier.partie.CarteWagon;

public class Controleur
{
    private Metier metier;
	private Partie partie;
	private Joueur joueur;
    private Ihm    ihm;

	private Arete   areteSelectionnee;
	private int     couleurSelectionnee;
	private boolean enTrainDePiocher;
	private boolean piocherObjectifsDebut;

	/* ==================== */
	/*     CONSTRUCTEUR     */
	/* ==================== */
    public Controleur()
    {
        this.metier = new Metier(this);
		this.partie = null;
		this.joueur = null;
        this.ihm    = new Ihm(this);

		this.enTrainDePiocher = false;

    }

	/* ==================== */
	/*      GETTERS         */
	/* ==================== */
	public List<Joueur>        getJoueurs             () { return this.metier.getJoueurs         (); }
	public Joueur[]            getJoueursPartie       () { return this.partie.getJoueurs         (); }
	public Joueur              getJoueur              () { return this.joueur; }
	public Joueur              getJoueurCourant       () { return this.partie.getJoueurCourant(); }
	public List<CarteObjectif> getCarteObjectif       () { return this.metier.getCarteObjectif   (); }
	public List<Noeud>         getNoeuds              () { return this.metier.getNoeuds          (); }
	public List<Arete>         getAretes              () { if (this.partie != null) return this.partie.geAretes(); return this.metier.getAretes          (); }
	public CarteWagon[]        getTabCarteWagon       () { return this.metier.getTabCarteWagon   (); }
	public CarteObjectif[]	   getTabCarteObjectif    () { return this.metier.getTabCarteObjectif(); }
	public CarteObjectif       getPiocheObjectif      () { return this.partie.getPiocheObjectif  (); }

	public int[]         getTaillePlateau () { return this.metier.getTaillePlateau (); }
	public BufferedImage getImagePlateau  () { return this.metier.getImagePlateau  (); }
	public Color         getCouleurPlateau() { return this.metier.getCouleurPlateau(); }
	public Font          getPolicePlateau () { return this.metier.getPolicePlateau (); }

	public int getNbJoueursMin     () { return this.metier.getNbJoueursMin     (); }
	public int getNbJoueursMax     () { return this.metier.getNbJoueursMax     (); }
	public int getNbCarteCoul      () { return this.metier.getNbCarteCoul      (); }
	public int getNbCarteLocomotive() { return this.metier.getNbCarteLocomotive(); }
	public int getNbJetonJoueur    () { return this.metier.getNbJetonJoueur    (); }
	public int getNbJetonFin       () { return this.metier.getNbJetonFin       (); }

	public List<Color>         getCouleurs            () { return this.metier.getCouleurs            (); }
	public BufferedImage       getImageVersoCouleur   () { return this.metier.getImageVersoCouleur   (); }
	public BufferedImage       getImageRectoLocomotive() { return this.metier.getImageRectoLocomotive(); }
	public List<BufferedImage> getImagesRectoCouleur  () { return this.metier.getImagesRectoCouleur  (); }
	public List<Integer>       getPoints              () { return this.metier.getPoints              (); }
	public CarteWagon[]        getTabCartesVisible    () { return this.partie.getTabCartesVisible    (); }

	public BufferedImage       getImageVersoObjectif() { return this.metier.getImageVersoObjectif(); }
	public BufferedImage       getImage             () { return this.ihm   .getImage             (); }

	public int     getSizeWagon         () { return this.partie.getSizeWagon();    }
	public int     getSizeObjectif      () { return this.partie.getSizeObjectif(); }
	public Arete   getAreteSelectionne  () { return this.areteSelectionnee;        }
	public int     getCouleurSelectionne() { return this.couleurSelectionnee;      }
	public boolean getEnTrainDePiocher  () { return this.enTrainDePiocher;         }
	public int	   getNbTours			() { return this.partie.getTours();		   }
	public boolean getEstMulti          () { return this.partie.getEstMulti();     }
	public Metier  getMetier            () { return this.metier;                   }
	

	/* ==================== */
	/*       SETTERS        */
	/* ==================== */
	public void setImageButton(int indice)  { if ( this.ihm != null ) this.ihm.setImageButton(indice); }

	public void setInfo(int nbTours, String nomJoueurCourant){ this.ihm.setInfo(nbTours, nomJoueurCourant); }

	public void setMetier(Metier m)
	{
		m.setCtrl(this);
		m.copyTransients(this.metier);
		this.metier = m;
	}


	/* ================================= */
	/*  METHODES DE DEMARRAGE DE PARTIE  */
	/* ================================= */

	/**
	 * Permet de lancer la partie multijoueur quand on est dans la salle d'attente.
	 * Elle peux être appeler uniquement par l'hote de la partie.
	 * C'est elle qui s'occupe de fermer la salle d'attente.
	 */
	public void lancerPartieMulti()
	{
		this.partie = new Partie(this, this.metier, true, "Partie multi-joueurs");

		this.ihm.demarrerJeu();
	}

	/**
	 * Permet de lancer la partie multijoueur quand on est dans la salle d'attente.
	 * Elle peux être appeler uniquement par l'hote de la partie.
	 * C'est elle qui s'occupe de fermer la salle d'attente.
	 */
	public void lancerPartieLocal()
	{
		this.partie = new Partie(this, this.metier, false, "Partie local");
		this.ihm.demarrerJeu();
	}

	/**
	 * Permet de créer une partie multijoueur mais ne lance pas le jeu.
	 * Le jeu pourra être lancé par le créateur de la partie à l'intérieur de la fenêtre d'attente.
	 */
	public void creerPartieMulti(String password)
	{
		this.joueur = this.metier.getJoueurs().get(0);
		this.partie = new Partie(this, this.metier, true, "Partie local");

		this.metier.creeServer(true, password);
		this.ihm.demarrerAttente(true);
	}

	/**
	 * Permet de créer une partie solo.
	 * Cette méthode lance le jeu directement.
	 */
	public void creerPartieLocal()
	{
		this.joueur = this.metier.getJoueurs().get(0);

		this.ihm.demarrerAttenteLocal();
	}


	/* =========================== */
	/*  METHODES DE GESTION D'IHM  */
	/* =========================== */
	public void    disposeFrameJeu		() { this.ihm.disposeFrameJeu(); 		}
	public void    disposeFrameFinPartie() { this.ihm.disposeFrameFinPartie();  }

	public void majIHM()
	{
		this.ihm.majIHM();
	}

	public void afficherErreur(String message)
	{
		this.ihm.afficherErreur(message);
	}

	/**
	 * Affiche la carte objectif dans la main du joueur
	 * @param icon carte objectif
	 */
    public void afficherCarteObjectif(Icon icon) 
	{
		this.ihm.afficherCarteObjectif(icon);
    }

	public void validerObjectif() 
	{
		this.ihm.validerObjectif();
    }

	public void ouvrirFinPartie(Boolean finLocal)
	{
		this.ihm.ouvrirFinPartie(finLocal);
	}

	public void setPartieLancer(Boolean b)
	{
		this.ihm.basculerEnJeu(b);
	}

	public void Deconnecter()
	{
		this.ihm.RetourALaceuille();
	}

	public void piocherCarteObjectifDebutPartie() 
	{ 
		if ( this.piocherObjectifsDebut == true ) 
			this.ihm.piocherCarteObjectifDebutPartie(); 
	}

	/* ============================== */
	/*  METHODES DE GESTION DE PARTE  */
	/* ============================== */

	public void joueurSuivant()
	{
		if (!this.enTrainDePiocher)
		{
			this.partie.joueurSuivant();
			this.metier.joueurSuivant();
		}
	}

	public void changerJoueur(Joueur j)
	{
		this.joueur = j;

		this.areteSelectionnee = null;
		this.couleurSelectionnee = 0;

		this.ihm.majIHM();
		if (this.partie.getTours() == 1) 
		{
			this.piocherObjectifsDebut = true;
			this.piocherCarteObjectifDebutPartie();
		}
	}

	public void switchEnTrainDePiocher()
	{
		this.enTrainDePiocher = !this.enTrainDePiocher;
	}

	public boolean ajouterJoueur(Joueur joueur)
	{
		return this.metier.ajouterJoueur(joueur);
	}

	public boolean estPrenable(Arete arete, int couleur)
	{
		try
		{
			// vérification de l'activation des voix doubles
			if (this.getJoueursPartie().length <= 3 && arete.getCouleur2() != null)
			{
				if (couleur == 1 && arete.getProprietaire2() != null) return false;
				if (couleur == 2 && arete.getProprietaire1() != null) return false;
			}

			if ((couleur == 1 && arete.getProprietaire1() == null) ||
		        (couleur == 2 && arete.getProprietaire2() == null)   )
			{
				Color coul = null;

				if (couleur == 1) coul = arete.getCouleur1();
				else              coul = arete.getCouleur2();
				
				// if : voix neutre | else : voix couleur
				if ( coul.equals(this.metier.getCouleurs().get(0)))
				{
					for (Color c : this.joueur.getAlCouleurs())
					{
						int nbCoul  = 0;
						int nbJoker = 0;

						if (c != null) 
							nbCoul  = this.joueur.gethashMapCarteWagons().get(c);

						if (this.joueur.getAlCouleurs().contains(null))
							nbJoker = this.joueur.gethashMapCarteWagons().get(null);

						if (nbCoul + nbJoker >= arete.getDistance()) return true;
					}
				}
				else
				{
					int nbCoul  = 0;
					int nbJoker = 0;

					if (this.joueur.getAlCouleurs().contains(coul))
						nbCoul  = this.joueur.gethashMapCarteWagons().get(coul);

					if (this.joueur.getAlCouleurs().contains(null))
						nbJoker = this.joueur.gethashMapCarteWagons().get(null);

					if (nbCoul + nbJoker >= arete.getDistance()) return true;
				}
			}
		}
		catch(Exception e) { return false; }
		
		return false;
	}

	public void setSelectionnee(Arete arete, int couleur)
	{
		this.areteSelectionnee   = arete;
		this.couleurSelectionnee = couleur;
	}

	public void prendreArete(int indMain)
	{
		if (this.peuxJouer() && this.areteSelectionnee != null && 
		    this.areteSelectionnee.getDistance() <= joueur.getNbJetonsRestant())
		{
			if ((this.couleurSelectionnee == 1 && this.areteSelectionnee.getProprietaire1() != null) ||
				(this.couleurSelectionnee == 2 && this.areteSelectionnee.getProprietaire2() != null)   )
			{
				this.ihm.afficherErreur("Cette voie est déjà prise !");
				return;
			}

			boolean estValide = false;
			int nbJoker = 0;
			Color c = this.joueur.getAlCouleurs().get(indMain);
			int nbCarte = this.joueur.gethashMapCarteWagons().get(c);
			if ( this.joueur.gethashMapCarteWagons().get(null) != null)
				nbJoker = this.joueur.gethashMapCarteWagons().get(null);

			Color cVoie;
			if (this.couleurSelectionnee == 1) cVoie = this.areteSelectionnee.getCouleur1();
			else                               cVoie = this.areteSelectionnee.getCouleur2();

			// Utilisation de carte joker uniquement
			if (c == null && nbCarte >= this.areteSelectionnee.getDistance())
			{
				this.joueur.gethashMapCarteWagons().put(c, nbCarte - this.areteSelectionnee.getDistance());
				estValide = true;

				int nbEnl = 0;
				Iterator<CarteWagon> it = this.joueur.getAlCartesWagons().iterator();
				while (it.hasNext() && nbEnl < this.areteSelectionnee.getDistance()) 
				{
					CarteWagon cw = it.next();
					if (cw.getCouleur() == null)
					{
						this.partie.ajouterCarteDefausse(cw);
						it.remove();
						nbEnl++;
					}
				}
			}
			// Utilisation de carte couleur sur une voie de la même couleur ou neutre
			else if (c != null && (c.equals(cVoie) || cVoie.equals(this.getCouleurs().get(0))) &&
				     nbCarte >= this.areteSelectionnee.getDistance()                             )
			{
				this.joueur.gethashMapCarteWagons().put(c, nbCarte - this.areteSelectionnee.getDistance());
				estValide = true;

				int nbEnl = 0;
				Iterator<CarteWagon> it = this.joueur.getAlCartesWagons().iterator();
				while (it.hasNext() && nbEnl < this.areteSelectionnee.getDistance()) 
				{
					CarteWagon cw = it.next();
					if (cw.getCouleur() == c)
					{
						this.partie.ajouterCarteDefausse(cw);
						it.remove();
						nbEnl++;
					}
				}
			}
			// Utilisation de carte couleur et joker sur une voie de la même couleur ou neutre
			else if (c != null && (c.equals(cVoie) || cVoie.equals(this.getCouleurs().get(0))) &&
				     nbCarte + nbJoker >= this.areteSelectionnee.getDistance()                   )
			{
				int nbJokerNeccessaire = this.areteSelectionnee.getDistance() - nbCarte;
				boolean confirmation = this.ihm.poserQuestion(
					"Voulez-vous utiliser " + nbJokerNeccessaire + " carte joker ?");

				if (confirmation)
				{
					this.joueur.gethashMapCarteWagons().put(c, 0);
					this.joueur.gethashMapCarteWagons().put(null, nbJoker - nbJokerNeccessaire);
					estValide = true;

					Iterator<CarteWagon> it = this.joueur.getAlCartesWagons().iterator();
					while (it.hasNext()) 
					{
						CarteWagon cw = it.next();
						if (cw.getCouleur() == c)
						{
							this.partie.ajouterCarteDefausse(cw);
							it.remove();
						}
					}

					int nbEnl = 0;
					it = this.joueur.getAlCartesWagons().iterator();
					while (it.hasNext() && nbEnl < nbJoker - nbJokerNeccessaire) 
					{
						CarteWagon cw = it.next();
						if (cw.getCouleur() == null)
						{
							this.partie.ajouterCarteDefausse(cw);
							it.remove();
							nbEnl++;
						}
					}
				}
			}

			if (estValide)
			{
				if (this.couleurSelectionnee == 1) this.areteSelectionnee.setProprietaire1(joueur);
				else                               this.areteSelectionnee.setProprietaire2(joueur);


				this.joueur.ajouterScore(this.metier.getPoints().get(
					this.areteSelectionnee.getDistance()-1));
				this.ihm.setScore();

				Iterator<Color> it = this.joueur.getAlCouleurs().iterator();
				while (it.hasNext()) 
				{
					Color coul = it.next();
					if (this.joueur.gethashMapCarteWagons().get(coul) == 0) 
					{
						it.remove();
						this.joueur.gethashMapCarteWagons().remove(coul);
					}
				}
				this.joueur.retirerJeton(this.areteSelectionnee.getDistance());

				this.areteSelectionnee = null;
				this.couleurSelectionnee = 0;

				this.ihm.majIHM();
				this.joueur.verifierObjectifs();
				this.joueurSuivant();
			}
		}
		else
		{
			if (this.areteSelectionnee == null)
				this.ihm.afficherErreur("Aucune arête selectionné");
			else
				this.ihm.afficherErreur("Nombre de jeton insuffisant");
		}
	}

	public void piocherPioche ()        { this.partie.piocherPioche ();    }
	public void piocherVisible(int ind) { this.partie.piocherVisible(ind); }

	public boolean peuxJouer()
	{
		if ( this.partie != null || this.partie.getJoueurCourant() != null )
			return this.partie.getJoueurCourant().equals(this.joueur);
		else
			return false;
	}
	
	public void verifierVisible()
	{
		this.partie.verifierVisible();
	}

	/**
	 * Ajouter une carte objectif dans la main du joueur
	 * @param cartesObjectifs : carte que l'on veut ajouter
	 */
	public void ajouterObjectifsJoueurs(CarteObjectif carteObjectif) 
	{
		this.metier.ajouterObjectifsJoueurs(carteObjectif);
		this.ihm.majIHM();
	}

	/**
	 * Permet de remettre les cartes non piochés par le joueur dans la pioche
	 * @param carteObjectif 
	 */
	public void remettreCarteObjectif(CarteObjectif carteObjectif) 
	{
		this.partie.remettreCarteObjectif(carteObjectif);
	}

	/* =========================== */
	/*  METHODES DE GESTION D'XML  */
	/* =========================== */

	/**
	 * Permet de lire le fichier xml contenant toutes les informations du plateau.
	 * @param fichier :  fichier xml à lire
	 * @return boolean : true si le fichier a été lu correctement, sinon false
	 */
	public boolean ouvrir(File fichier) 
	{ 
		boolean readSuccess =  this.metier.lireFichier(fichier);
		if (this.metier.getServer()!= null)this.metier.getServer().majMetier();
		return readSuccess ; 	
	}

	/* ================================ */
	/*  METHODES DE GESTION DES THEMES  */
	/* ================================ */
	
	/**
     * Permet d'appliquer le thème à l'ihm
     */
    public void appliquerTheme() { this.ihm.appliquerTheme(); }

    /**
     * Permet de à l'ihm de récupérer la hashmap contenant les couleurs du thème
     * @return HashMap contenant les couleurs du thème
     */
    public HashMap<String, List<Color>> getTheme() { return this.metier.getTheme(); }

	/**
	 * Permet de récupérer le nom du thème utilisé
	 * @return Nom du thème utilisé
	 */
	public String getThemeUsed() { return this.metier.getThemeUsed(); }

    /**
     * Change le thème à utilisé dans le fichier de sauvegarde.
     * Charge en mémoire le nouveau thème.
     * Met à jour l'ihm.
     * @param theme : Nom du thème à utiliser
     */
    public void changerTheme(String theme) { this.metier.setThemeUsed(theme); }

	/* ================================ */
	/*  METHODES DE GESTION DU RESEAU   */
	/* ================================ */

	/**
	 * permet d'éberger une partie
	 */
	public void hostGame()
	{
		this.joueur = new Joueur(this, "Joueur 1");
		this.metier.ajouterJoueur(this.joueur);
		this.partie = new Partie(this, this.metier, true, "Partie multi-joueur");
	}

	public void updateJoueurs(Joueur[] joueurs)
	{
		for (Joueur j : joueurs)
			if (j.equals(this.joueur))
				this.joueur = j;
	}

	public int joinGame(String ip, String nom, String password)
	{
		
		this.metier.creeClient(ip, nom, true, password);

		this.joueur = new Joueur(this, nom);

		return 1;
	}
		
	public void connexionAccepter()
	{
		this.ihm.demarrerAttente(false);
	}

	/**
	 * Permet d'obtenir de chemin vers le fichier xml de la mappe charger en mémoire dans le metier
	 * @return String : chemin absolut vers le fichier xml de la mappe charger en mémoire dans le metier
	 */
	public String getPathMappe()
	{
		return this.metier.getPathMappe();
	}

	public Partie getPartie()
	{
		return this.partie;
	}

	public void setPartie(Partie partie)
	{
		this.partie = partie;
		partie.setCtrl(this);
		updateJoueurs(partie.getJoueurs());
	}

	/* ================= */
	/*        MAIN       */
	/* ================= */
	public static void main(String[] args)
    {
        new Controleur();


    }

}
