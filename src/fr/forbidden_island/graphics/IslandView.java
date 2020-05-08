package fr.forbidden_island.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import fr.forbidden_island.core.Observer;
import fr.forbidden_island.core.Model;
/*
 * Correspond � VueGrille ou autre �lement de la vue, nom � modifier pour �tre plus explicite*
 *
 */
public class IslandView extends JPanel implements Observer{
	private int posX ;
	private int posY ;
	
	private Model modele;
	
	public IslandView(Model mod) {
		// TODO Auto-generated constructor stub
		this.modele = mod;
		/** On enregistre la vue [this] en tant qu'observateur de [modele]. */
		modele.addObserver(this); //ajoute � observers 
		this.posX=0;
		this.posY=0;
		this.setPreferredSize(new Dimension(100, 100));

		
	}

	public void paintComponent(Graphics g){
		super.repaint();
		g.setColor(Color.red);
		g.drawRect(posX, posY, 200, 40);
	}
	


	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}



	public void update() {
		// TODO Auto-generated method stub
		
	}
}
