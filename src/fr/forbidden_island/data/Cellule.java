package fr.forbidden_island.data;

import fr.forbidden_island.Controls.Direction;
import fr.forbidden_island.core.Model;

public class Cellule{
	
	private final int x, y; //position de la cellule sur la grille
	private Model modele;
	public final static int size=30;
	private boolean estMer=true;
	private boolean inonde=false;
	private boolean Joueur=false;
	private Joueur jx;
	/*classe cellule :
	 *attribut terrain/joueur/special(artefact clef/element/heliport)
	 * 
	 * sous classe terrain:
	 * attribut  (booleen) terre/mer . (mer definitif)
	 * attribut booleen (avec condition terre) inond�/normal submerg� (deviens mer) .
	 * 
	 * sous classe special
	 * artefacts / clef/element/heliport )
	 */
	
	public Cellule(Model mod,int x,int y) {
		this.x=x;
		this.y=y;
		this.modele=mod;
	}
	
	/**
	 * 
	 * @param la grille !!
	 * @return
	 */
	public Cellule [] voisines(Cellule [][] grille) {
		//if(this.estMer)throw new IllegalArgumentException(); 
		Cellule [] res=new Cellule[4];
		int x=this.getAbsc();
		int y=this.getOrd();
		res[0]=grille[x/size][(y-size)/size];//haut
		res[1]=grille[((x+size)/size)][y/size];//droite
		res[2]=grille[x/size][((y+size)/size)];//bas
		res[3]=grille[(x-size)/size][y/size];//gauche

		return res;
		}

	

	
	public int getAbsc () {return this.x;}
	
	public int getOrd () {return this.y;}
	
	public int getSize () {return size;}
	
	public void setMer(boolean etat) { 
		this.estMer = etat; 
	}
	
	public boolean getMer(){
		return this.estMer;
	}
	
	public void setEstInonde(boolean etat) { 
		this.inonde = etat; 
	}
	
	public boolean getEstInonde(){
		return this.inonde;
	}
	
	public void setJoueur(boolean etat) { 
		this.Joueur = etat;
		this.jx = new Joueur(this);
	}
	
	public boolean getJoueur(){
		return this.Joueur;
	}
	public Joueur getJoueurInfo(){
		return this.jx;
	}
	

}
