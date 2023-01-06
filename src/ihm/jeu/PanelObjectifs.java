package ihm.jeu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import controleur.Controleur;
import metier.CarteObjectif;
import metier.Noeud;

public class PanelObjectifs extends JPanel implements ActionListener
{
    private Controleur ctrl;

    private JButton[] tabBtnObjectifs;
    private List<CarteObjectif>  listObjectifs;
    
    public PanelObjectifs(Controleur ctrl)
    {
        this.ctrl = ctrl;
        //int taille = this.ctrl.getCarteObjectif().size(); 
        int taille = 5; // a remplacer par le nombre de carte objectif du joueur
        
        int grid = taille;
        if(taille%2 != 0)
            grid = taille+1;

        this.setBackground(new Color(68, 71, 90));

        JPanel panel = new JPanel();
        panel.setBackground(new Color(68, 71, 90));
        panel.setLayout(new GridLayout(grid/2, 2, 0, 1));

        this.tabBtnObjectifs = new JButton[taille];
        this.listObjectifs = this.ctrl.getCarteObjectif();

        for (int i = 0; i < taille; i++)
        {
            this.tabBtnObjectifs[i] = new JButton();
            this.tabBtnObjectifs[i].setBackground(new Color(68, 71, 90));
            this.tabBtnObjectifs[i].setBorderPainted(false);
            this.tabBtnObjectifs[i].setFocusPainted(false);
            this.tabBtnObjectifs[i].setContentAreaFilled(false);

            this.tabBtnObjectifs[i].setIcon(new ImageIcon(creerCarte(this.listObjectifs.get(i))));

            panel.add(this.tabBtnObjectifs[i]);
        }

        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        container.setBackground(new Color(68, 71, 90));
        container.add(panel);
        JScrollPane scrollPane = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(500,300));
        scrollPane.getVerticalScrollBar().setUnitIncrement(5);
        scrollPane.getVerticalScrollBar().setBackground(new Color(68, 71, 90));

        this.add(scrollPane);   
        
        for(JButton btn : this.tabBtnObjectifs)
            btn.addActionListener(this);
    }


    private BufferedImage creerCarte(CarteObjectif carteObjectif) 
    {
        Noeud noeud1 = carteObjectif.getNoeud1();
        Noeud noeud2 = carteObjectif.getNoeud2();
        int nbPoints = carteObjectif.getPoints();

        BufferedImage img = new BufferedImage(200, 150, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();

        g.setColor(new Color(68, 71, 90));
        g.fillRect(0, 0, 220, 150);

        g.setColor(Color.WHITE);
        g.fillRect(10, 10, 220, 130);

        g.setColor(Color.BLACK);
        g.drawRect(10, 10, 200, 130);

        g.setColor(Color.BLUE);
        g.setFont(g.getFont().deriveFont(20f));
        FontMetrics fm = g.getFontMetrics();
        String str = "Objectif";
        Point p = new Point(100 - fm.stringWidth(str)/2, 30);
        g.drawString(str, p.x, p.y);

        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(15f));
        fm = g.getFontMetrics();
        str = noeud1.getNom() + " ==> " + noeud2.getNom();
        p = new Point(100 - fm.stringWidth(str)/2, 60);
        g.drawString(str, p.x, p.y);

        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(15f));
        fm = g.getFontMetrics();
        str = nbPoints + " points";
        p = new Point(100 - fm.stringWidth(str)/2, 90);
        g.drawString(str, p.x, p.y);

		BufferedImage bi = this.ctrl.getImage();
		double zoomLargeur = (double) 100 / bi.getWidth ();
		double zoomHauteur = (double) 100 / bi.getHeight();
		double facteurZoom = Math.min(zoomLargeur, zoomHauteur);
		/*
		 * AffineTransform at = new AffineTransform();
		at.scale(zoomFactor, zoomFactor);
        g2.transform(at);
		 */

        return img;
    }


    @Override
    public void actionPerformed(ActionEvent e) 
    {
        for(int i = 0; i < this.tabBtnObjectifs.length; i++)
        {
            if(e.getSource() == this.tabBtnObjectifs[i])
            {
                System.out.println("Carte objectif " + i + " cliquée");
            }
        }
        
    }

}
