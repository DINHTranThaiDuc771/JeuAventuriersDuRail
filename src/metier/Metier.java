package metier;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;

import controleur.Controleur;
import metier.partie.CarteWagon;
import metier.reseau.Client;
import metier.reseau.Server;

public class Metier implements Serializable
{
	private static final int TAILLE_TAB_WAGON = 5;
	private static final int TAILLE_TAB_OBJECTIF = 3;
	private static final long serialVersionUID = 1L;

    private transient Controleur          ctrl;

    private List<Joueur>        lstJoueurs;
    private List<CarteObjectif> lstCartesObjectif;
    private List<Arete>         lstAretes;
    private List<Noeud>         lstNoeuds;

	private CarteWagon[]		tabCarteWagon;
	private CarteObjectif[]		tabCarteObjectif;
    
    private int[]         taillePlateau;
	private transient BufferedImage imagePlateau;
	private Color         couleurPlateau;
	private Font          policePlateau;

    private int nbJoueursMin;
	private int nbJoueursMax;
	private int nbCarteCoul;
	private int nbCarteLocomotive;
	private int nbJetonJoueur;
	private int nbJetonFin;

    private List<Color>         lstCouleurs;
	private transient BufferedImage       imageVersoCouleur;
	private transient BufferedImage       imageRectoLocomotive;
	private transient List<BufferedImage> lstImagesRectoCouleur;
	private List<Integer>       lstPoints;

	private transient BufferedImage       imageVersoObjectif;

	private HashMap<String, List<Color>> hmColorThemes;

	private String nomPartie;
	private transient String motDePassePartie;
	private transient Server server;
	
	private transient Client client;
	private transient String nomClient;



    public Metier(Controleur ctrl)
    {
        this.ctrl = ctrl;
		this.lstJoueurs = new ArrayList<Joueur>();
		this.lireFichier(new File("./bin/donnees/France.xml"));
		//this.initCartes();
		this.hmColorThemes = new HashMap<String, List<Color>>();
		this.chargerThemes(getThemeUsed());
    }

    public boolean ajouterJoueur(Joueur joueur)
    {
        if(this.lstJoueurs.size() < this.nbJoueursMax)
		{
            this.lstJoueurs.add(joueur);
			return true;
		}

		return false;
    }

	/* --------------------------- */
	/*          Getters            */
	/* --------------------------- */
	public List<Joueur>        getJoueurs             () { return this.lstJoueurs;           }
	public List<CarteObjectif> getCarteObjectif       () { return this.lstCartesObjectif;    }
	public List<Noeud>         getNoeuds              () { return this.lstNoeuds;            }
	public List<Arete>         getAretes              () { return this.lstAretes;            }
	public CarteWagon[]		   getTabCarteWagon		  () { return this.tabCarteWagon;        }
	public CarteObjectif[]	   getTabCarteObjectif	  () { return this.tabCarteObjectif;     }

	public int[]               getTaillePlateau       () { return this.taillePlateau;        }
	public BufferedImage       getImagePlateau        () { return this.imagePlateau;         }
	public Color               getCouleurPlateau      () { return this.couleurPlateau;       }
	public Font                getPolicePlateau       () { return this.policePlateau;        }

	public int                 getNbJoueursMin        () { return this.nbJoueursMin;         }
	public int                 getNbJoueursMax        () { return this.nbJoueursMax;         }
	public int                 getNbCarteCoul         () { return this.nbCarteCoul;          }
	public int                 getNbCarteLocomotive   () { return this.nbCarteLocomotive;    }
	public int                 getNbJetonJoueur       () { return this.nbJetonJoueur;        }
	public int                 getNbJetonFin          () { return this.nbJetonFin;           }

	public List<Color>         getCouleurs            () { return this.lstCouleurs;          }
	public BufferedImage       getImageVersoCouleur   () { return this.imageVersoCouleur;    }
	public BufferedImage       getImageRectoLocomotive() { return this.imageRectoLocomotive; }
	public List<BufferedImage> getImagesRectoCouleur  () { return this.lstImagesRectoCouleur;}
	public List<Integer>       getPoints              () { return this.lstPoints;            }

	public BufferedImage       getImageVersoObjectif  () { return this.imageVersoObjectif;   }
	
	public String 			   getNomPartie           () { return this.nomPartie;            }
	public String              getMotDePasse		  () { return this.motDePassePartie;     }
	public Server 			   getServer              () { return this.server;               }
	public Client 			   getClient              () { return this.client;               }
	public String 			   getNomClient           () { return this.nomClient;            }

	public void creeServer(Boolean demarer, String password)
	{
		this.motDePassePartie = password;
		this.server = new Server(this.ctrl);
		if (demarer)
			this.server.Start();
	}

	public void creeClient(String ip, Boolean demarer, String password)
	{
		this.nomClient = "Joueur " + Math.round(Math.random()*100);
		this.client = new Client(ip, this.ctrl);
		if (demarer)
			this.client.Connect(password);
	}

	/**
	 * Lecture du fichier XML afin de récupérer les infos du plateau
	 * @param fichier : fichier xml à lire
	 * @return true si le fichier a été lu, sinon false
	 */
    public boolean lireFichier(File fichier)
	{
		// read file into a reader
		try {
			if (this.chargerXML(new FileReader(fichier)))
				return true;
			else
				return false;
		} catch (FileNotFoundException e) { return false; }
	}


    public boolean chargerXML(Reader cs)
	{
		SAXBuilder sxb = new SAXBuilder();

		try
		{
			Document document = sxb.build(cs);

			/* <jeu> */
			Element racine = document.getRootElement();

			/* <information> */
			Element information = racine.getChild("information");

			Element dimension = information.getChild("dimension");
			this.taillePlateau    = new int[2];
			this.taillePlateau[0] = Integer.parseInt(dimension.getAttributeValue("x"));
			this.taillePlateau[1] = Integer.parseInt(dimension.getAttributeValue("y"));
			this.imagePlateau     = this.base64ToImage(information.getChild("image-fond").getText()); 
			this.couleurPlateau   = hexaToColor(information.getChild("couleur-fond").getText());   
			this.policePlateau    = new Font (information.getChild("police").getText(), Font.PLAIN, 12);
			
			Element nbJoueurs = information.getChild("nombre-joueurs");
			this.nbJoueursMin = Integer.parseInt(nbJoueurs.getAttributeValue("min"));
			this.nbJoueursMax = Integer.parseInt(nbJoueurs.getAttributeValue("max"));

			Element nbCarte = information.getChild("nombre-carte");
			this.nbCarteCoul       = Integer.parseInt(nbCarte.getAttributeValue("couleur"));
			this.nbCarteLocomotive = Integer.parseInt(nbCarte.getAttributeValue("multicouleur"));

			Element nbJeton = information.getChild("nombre-jeton");
			this.nbJetonJoueur = Integer.parseInt(nbJeton.getAttributeValue("joueur"));
			this.nbJetonFin    = Integer.parseInt(nbJeton.getAttributeValue("fin"));
			
			Element plateau = racine.getChild("plateau");
			
			/* <liste-lstCouleurs> */
			this.lstCouleurs = new ArrayList<Color>();
			List<Element> listlstCouleurs = plateau.getChild("liste-lstCouleurs").getChildren("couleur");
			Iterator<Element> itlstCouleurs = listlstCouleurs.iterator();
			
			while(itlstCouleurs.hasNext())
			{
				Element couleur = (Element)itlstCouleurs.next();
				this.lstCouleurs.add(Color.decode(couleur.getText()));
			}

			/* <liste-image_cartes> */
			this.imageVersoCouleur = this.base64ToImage(plateau.getChild("liste-image-cartes")
										.getChild("image-verso").getText());

			this.lstImagesRectoCouleur = new ArrayList<BufferedImage>();
			List<Element> listImagesCartes = plateau.getChild("liste-image-cartes").getChildren("image-recto");
			Iterator<Element> itImagesCartes = listImagesCartes.iterator();

			this.imageRectoLocomotive = this.base64ToImage(itImagesCartes.next().getText());
			while(itImagesCartes.hasNext())
			{
				Element imageCarte = (Element)itImagesCartes.next();
				BufferedImage image = this.base64ToImage(imageCarte.getText());
				this.lstImagesRectoCouleur.add(image);
			}
			
			/* <tableau-lstPoints */
			this.lstPoints = new ArrayList<Integer>();
			List<Element> listlstPoints = plateau.getChild("tableau-lstPoints").getChildren("distance");
			Iterator<Element> itlstPoints = listlstPoints.iterator();

			while(itlstPoints.hasNext())
			{
				Element point = (Element)itlstPoints.next();
				this.lstPoints.add(Integer.parseInt(point.getText()));
			}

			/* <liste-lstNoeuds> */
			this.lstNoeuds = new ArrayList<Noeud>();
			Noeud.reinitialiserId();
			List<Element> listlstNoeuds = plateau.getChild("liste-lstNoeuds").getChildren("noeud");
			Iterator<Element> itlstNoeuds = listlstNoeuds.iterator();

			while(itlstNoeuds.hasNext())
			{
				Element noeud = (Element)itlstNoeuds.next();

				Element position = noeud.getChild("position");
				int x = Integer.parseInt(position.getAttributeValue("x"));
				int y = Integer.parseInt(position.getAttributeValue("y"));

				String nom = noeud.getChild("nom").getText();

				Element positionNom = noeud.getChild("position-nom");
				int xNom = Integer.parseInt(positionNom.getAttributeValue("x"));
				int yNom = Integer.parseInt(positionNom.getAttributeValue("y"));

				Color couleur = Color.decode(noeud.getChild("couleur").getText());

				this.lstNoeuds.add(new Noeud(nom, x, y, xNom, yNom, couleur));
			}

			/* <liste-lstAretes> */
			this.lstAretes = new ArrayList<Arete>();
			List<Element> listlstAretes = plateau.getChild("liste-lstAretes").getChildren("arete");
			Iterator<Element> itlstAretes = listlstAretes.iterator();

			while(itlstAretes.hasNext())
			{
				Element arete = (Element)itlstAretes.next();

				Element noeud = arete.getChild("noeud");
				Noeud n1 = this.lstNoeuds.get(Integer.parseInt(noeud.getAttributeValue("n1"))-1);
				Noeud n2 = this.lstNoeuds.get(Integer.parseInt(noeud.getAttributeValue("n2"))-1);

				Color couleur1 = Color.decode(arete.getChild("couleur1").getText());

				Color couleur2;
				if(arete.getChild("couleur2").getText().equals("NULL"))
					couleur2 = null;
				else
					couleur2 = Color.decode(arete.getChild("couleur2").getText());

				int distance = Integer.parseInt(arete.getChild("distance").getText());

				this.lstAretes.add(new Arete(n1, n2, distance, couleur1, couleur2));
			}

			/* <liste-objectifs> */
			this.imageVersoObjectif = this.base64ToImage(racine.getChild("liste-objectifs")
										.getChild("image-verso").getText());

			this.lstCartesObjectif = new ArrayList<CarteObjectif>();
			List<Element> listObjectifs = racine.getChild("liste-objectifs").getChildren("objectif");
			Iterator<Element> itObjectifs = listObjectifs.iterator();

			while(itObjectifs.hasNext())
			{
				Element objectif = (Element)itObjectifs.next();

				Element noeud = objectif.getChild("noeud");
				Noeud n1 = this.lstNoeuds.get(Integer.parseInt(noeud.getAttributeValue("n1"))-1);
				Noeud n2 = this.lstNoeuds.get(Integer.parseInt(noeud.getAttributeValue("n2"))-1);

				int lstPoints = Integer.parseInt(objectif.getChild("lstPoints").getText());

				BufferedImage imageRecto = this.base64ToImage(objectif.getChild("image-recto").getText());

				this.lstCartesObjectif.add(new CarteObjectif(n1, n2, lstPoints, imageRecto));
			}

			return true;
		} 
		catch (Exception e)
		{
			return false;
		}
	}

    public BufferedImage base64ToImage(String base64) throws IOException 
	{
		if (base64.equals("NULL_IMAGE"))
		{
			BufferedImage imageIO = new BufferedImage(10, 50, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = imageIO.createGraphics();
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, 10, 50);
			g2d.drawString("Image invalide", 0, 10);
			g2d.dispose();
			return imageIO;
		}
		byte[] bytes = Base64.getDecoder().decode(base64);
		return ImageIO.read(new ByteArrayInputStream(bytes));
	}
    
    private Color hexaToColor(String hexa)
	{
		if (hexa.charAt(0) != '#') return null;

		return new Color( Integer.parseInt(hexa.substring(1, 3), 16),
		                  Integer.parseInt(hexa.substring(3, 5), 16),
		                  Integer.parseInt(hexa.substring(5, 7), 16) );
	}

	public HashMap<String, List<Color>> getTheme() { return this.hmColorThemes;}


	/**
	 * Récupère le thème utilisé dans le fichier xml de sauvegarde
	 * @return String : thème à utilisé
	 */
	public String getThemeUsed()
	{
		String themeUsed = "";
		SAXBuilder sxb = new SAXBuilder();

		try
		{
			themeUsed = sxb.build("./bin/donnees/themes/theme_sauvegarde.xml").getRootElement().getText();
		}
		catch (Exception e) { e.printStackTrace(); System.out.println("Erreur lors de la lecture du fichier XML du themes utilisé"); }

		return themeUsed;
	}

	/**
	 * Sauvegarde le thème selectionné par l'utilisateur dans le fichier xml de sauvegarde
	 * @param theme : thème à sauvegarder
	 */
	public void setThemeUsed(String theme)
	{
		try
		{
			PrintWriter pw = new PrintWriter("./bin/donnees/themes/theme_sauvegarde.xml");
			pw.println("<theme>" + theme + "</theme>");
			pw.close();

			// temporaire
			//pw = new PrintWriter("./donnees/themes/theme_sauvegarde.xml");
			//pw.println("<theme>" + theme + "</theme>");
			//pw.close();
		}
		catch (Exception e) { e.printStackTrace(); System.out.println("Erreur lors de l'écriture du fichier XML du themes utilisé"); }

		this.chargerThemes(theme);

		this.ctrl.appliquerTheme();
	}

	/**
	 * Charge les couleurs du thème choisi par l'utilisateur dans la HashMap
	 * @param theme : thème à charger
	 * @return HashMap contenant les couleurs du thème
	 */
	public void chargerThemes(String theme)

	{
		SAXBuilder sxb = new SAXBuilder();

		try
		{
			Element racine = sxb.build("./bin/donnees/themes/theme_" + theme + ".xml").getRootElement();

			/*----------------------------*/
			/* BacKground Générale (=bkg) */
			/*----------------------------*/
			String lstCles[] = new String[] {"background", "disableColor", "enableColor"};

			List<Color> lst = new ArrayList<Color>();
			for (int i = 0; i < lstCles.length; i++)
			{
				Element bkg = racine.getChild(lstCles[i]);

				lst = new ArrayList<Color>();
				lst.add(new Color(Integer.parseInt(bkg.getAttributeValue("red")), Integer.parseInt(bkg.getAttributeValue("green")), Integer.parseInt(bkg.getAttributeValue("blue"))));
				this.hmColorThemes.put(lstCles[i], lst);
			}


			/*------------------------------------------*/
			/* Récupération de tout les autres éléments */
			/*------------------------------------------*/
			lstCles = new String[] {"titles", "labels", "saisies", "buttons", "menuBar"};
			for (int i = 0; i < lstCles.length; i++)
			{
				lst = new ArrayList<Color>();
				Element foreground = racine.getChild(lstCles[i]).getChild("foreground");
				Element background = racine.getChild(lstCles[i]).getChild("background");

				lst.add(0, new Color(Integer.parseInt(foreground.getAttributeValue("red")), Integer.parseInt(foreground.getAttributeValue("green")), Integer.parseInt(foreground.getAttributeValue("blue"))));
				lst.add(1, new Color(Integer.parseInt(background.getAttributeValue("red")), Integer.parseInt(background.getAttributeValue("green")), Integer.parseInt(background.getAttributeValue("blue"))));

				/* Récupération de la couleur du PlaceHolder */
				if (lstCles[i].equals("saisies"))
				{
					Element placeholder = racine.getChild(lstCles[i]).getChild("placeholder");
					lst.add(2, new Color(Integer.parseInt(placeholder.getAttributeValue("red")), Integer.parseInt(placeholder.getAttributeValue("green")), Integer.parseInt(placeholder.getAttributeValue("blue")), Integer.parseInt(placeholder.getAttributeValue("alpha"))));
				}


				this.hmColorThemes.put(lstCles[i], lst);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Erreur lors de la lecture du fichier XML des informations du theme");
		}
	}
	private void writeObject(ObjectOutputStream out) throws IOException 
	{
		ByteArrayOutputStream baos;
		out.defaultWriteObject();

		baos = new ByteArrayOutputStream();
		ImageIO.write(imagePlateau, "png", baos);
		out.writeObject(baos.toByteArray());

		baos = new ByteArrayOutputStream();
		ImageIO.write(imageRectoLocomotive, "png", baos);
		out.writeObject(baos.toByteArray());

		baos = new ByteArrayOutputStream();
		ImageIO.write(imageVersoCouleur, "png", baos);
		out.writeObject(baos.toByteArray());

		baos = new ByteArrayOutputStream();
		ImageIO.write(imageVersoObjectif, "png", baos);
		out.writeObject(baos.toByteArray());

		out.writeInt(lstImagesRectoCouleur.size());
		for (BufferedImage image : lstImagesRectoCouleur) 
		{
		   baos = new ByteArrayOutputStream();
		   ImageIO.write(image, "png", baos);
		   out.writeObject(baos.toByteArray());
		}

	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException 
	{
		in.defaultReadObject();
		imagePlateau = ImageIO.read(new ByteArrayInputStream((byte[]) in.readObject()));
		imageRectoLocomotive = ImageIO.read(new ByteArrayInputStream((byte[]) in.readObject()));
		imageVersoCouleur    = ImageIO.read(new ByteArrayInputStream((byte[]) in.readObject()));
		imageVersoObjectif   = ImageIO.read(new ByteArrayInputStream((byte[]) in.readObject()));
		int size = in.readInt();
		lstImagesRectoCouleur = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			lstImagesRectoCouleur.add(ImageIO.read(new ByteArrayInputStream((byte[]) in.readObject())));
		}
	}

	public void ajouterObjectifsJoueurs(CarteObjectif cartesObjectifs) 
	{
		this.lstJoueurs.get(0).ajouterCarteObjectif(cartesObjectifs);
		this.lstCartesObjectif.remove(cartesObjectifs);
	}
}	
