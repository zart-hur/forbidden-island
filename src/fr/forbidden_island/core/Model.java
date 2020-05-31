package fr.forbidden_island.core;

import fr.forbidden_island.Controls.Direction;
import fr.forbidden_island.data.Artefact;
import fr.forbidden_island.data.Cellule;
import fr.forbidden_island.data.Joueur;
import fr.forbidden_island.data.TypeTerrain;
import fr.forbidden_island.data.Ressources;
import fr.forbidden_island.data.Statut;

//pour la partie lecture fichier
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
//pour l'ile aleatoire et inonde ale'atoire..
import java.util.Random;

//
public class Model extends Observable {

	private final int dimGrilleAbsc = 35;
	private final int CarreCentralAbscMax =(dimGrilleAbsc * 3) / 4;
	private final int CarreCentralAbscMin =dimGrilleAbsc / 4;

	//	private final int dimGrilleAbscV2;
	private final int dimGrilleOrd = 21;
	private final int CarreCentralOrdMax =(dimGrilleOrd * 3) / 4;
	private final int CarreCentralOrdMin =dimGrilleOrd / 4;
	//	private final int dimGrilleOrdV2;
	private Cellule[][] grille;

	public int currentPlayerV2;
	private int nbJoueurs;
	private int nbArtefacts;
	private boolean endOfTheGame=false;
	public final int nbActionsmax=30;
	public int nbActions = nbActionsmax;
	public Joueur[] joueurs;
	public Artefact[] artefacts;
	Random r = new Random();

	//remplacer par initV2
	public Model(int nbJ) {
		this.nbJoueurs=nbJ;
		this.grille = new Cellule[dimGrilleAbsc][dimGrilleOrd];
		init();//Lance l'initialisation
		
	}

	/**
	 * Fonction utilitaire pour le constructeur:
	 * initialise la grille du mode'le plus particulie'rement le mode'le entier
	 * initialise les joueurs
	 * initialise les artefacts
	 */
	private void init() {
		this.joueurs = new Joueur[this.nbJoueurs];
		nbArtefacts=nbJoueurs;
		this.artefacts = new Artefact[this.nbArtefacts];
		for (int x = 0; x < dimGrilleAbsc; x++) {
			for (int y = 0; y < dimGrilleOrd; y++) {
				// donne des coords aux cellules
				grille[x][y] = new Cellule(this, x , y );
				// donne un e'tat terre au cellules "centrales" (l'ile)
				if (x > CarreCentralAbscMin && x < CarreCentralAbscMax && y > CarreCentralOrdMin && y < CarreCentralOrdMax) {
					grille[x][y].setTypeTerrain(TypeTerrain.terre);
				}
				else grille[x][y].setTypeTerrain(TypeTerrain.mer);
				 //rend plus "aleatoire les contours de l'ile  (carre' central)
							    float t = r.nextFloat();
				                for (float i = 1; i >= 0; i--) {
				                    if (!(x > (CarreCentralAbscMin + i) && x < (CarreCentralAbscMax - i) && y > (CarreCentralOrdMin + i) && y < (CarreCentralOrdMax - i))) {
				                            if (t < (0.2 + (1 / (i + 1))))
				                                grille[x][y].setTypeTerrain(TypeTerrain.mer); // redessine ale'atoirement les contours de l'ile
				                            
				                            
//				                            int voisineMer=0;
//				                            for(Cellule c : grille[x][y].voisines(grille)) {
//				                            	if (c.getTypeTerrain()==TypeTerrain.mer)
//				                            		voisineMer++;
//				                            	
//				                            }
//				                            if(voisineMer==4)
//				                            	 grille[x][y].setTypeTerrain(TypeTerrain.mer);
				                    }
				                }
			}
		}

		
		//initialise les joueurs en les mettant dans les cellules de la grille et les rajoutant dans un tab joueurs[] avec leurs positions respectives.		
		initJoueursV2(this.nbJoueurs);
		currentPlayerV2=0;
		//initialise les artefacts en les mettant dans les cellules de la grille et les rajoutant dans un tab artefacts[] avec leurs positions respectives.		
				initArtefacts();
		
		//System.out.println("le j0 absc:"+joueurs[0].getAbsc());
		//System.out.println("ord:"+joueurs[0].getOrd());

	}


	/**
	 *  pour tout la partie ile de la grille appelle a' la fin du tour:
	 * inonde
	 * submerge
	 * changePlayer
	 * purgePlayer
	 */
	public void endTurn() {
		if(!endOfTheGame) {
			for (int x = CarreCentralAbscMin-1; x <= CarreCentralAbscMax; x++) {
				for (int y = CarreCentralOrdMin-1; y <= CarreCentralOrdMax; y++) {
					submerge(x, y);				
					inonde(x, y);
				}
			}		
			nbActions=nbActionsmax;					
			updateStatutPlayers();
			//currentPlayerV2=changePlayer(currentPlayerV2);
			setEndOfTheGame();
			changePlayer();
			if(endOfTheGame)
				nbActions=0;
			
		}
	}
	/**
	 * Inonde plusieurs cellules ale'atoirement apre's clic sur le bouton fin de tour, fonction utilitaire a' endTurn
	 * @param x = coord Absc
	 * @param y = coord Ord
	 */
	private void inonde(int x, int y) {
		float t = r.nextFloat();
		int cpt = 0;
		for (Cellule c : grille[x][y].voisines(grille)) {
			if (c.getTypeTerrain()== TypeTerrain.mer )
				cpt++;
		}
		if(grille[x][y].getTypeTerrain() != TypeTerrain.mer && cpt>0)
			if (t < 0.09)grille[x][y].setTypeTerrain(TypeTerrain.inonde);
	}

	/**
	 * Submerge les cellules inonde's, fonction utilitaire a' endTurn
	 * @param x = coord Absc
	 * @param y = coord Ord
	 */
	private void submerge(int x,int y) {
		if(grille[x][y].getTypeTerrain() == TypeTerrain.inonde)
			grille[x][y].setTypeTerrain(TypeTerrain.mer);

	}
	
	/**
	 * si le joueur en cours est le dernier (nbJoueur-1)
	 *   si le suivant ca'd le premier est elimine'
	 *    alors return Changeplayer(0)
	 *   sinon return 0
	 * sinon
	 * 	si le suivant est elimine'
	 *    alors return changePlayer(current+1)
	 *  sinon return current++;
	 * @param current le joueur en cours
	 * @return le nouveau joueur courant
	 */

	
	private void changePlayer() {
		if(!endOfTheGame) {
		currentPlayerV2=(currentPlayerV2+1)%nbJoueurs;						
		while(joueurs[currentPlayerV2].getStatut()!=Statut.vivant ) {
			currentPlayerV2=(currentPlayerV2+1)%nbJoueurs;			
		}	
		notifyObservers();
		}
	}
//	private int changePlayer(int current) {
//		if(current==nbJoueurs-1) {
//			if(joueurs[0].getStatut()!=Statut.vivant) {
//				return changePlayer(0);
//			}else {
//				return 0;
//			}
//		}else {
//			if(joueurs[current+1].getStatut()!=Statut.vivant) {
//				return changePlayer(current+1);
//			}else {
//				return (++current);
//			}
//		}
//   notifyObservers();	
//	}
	
	/**
	 * fonction utilitaire a' endTurn, rend tout les joueurs dans la mer e'limine's
	 */
	private void updateStatutPlayers() {				
		for(Joueur j : joueurs) {
			//si un joueur remplit les condition pour mourir
			if(grille[j.getAbsc()][j.getOrd()].getTypeTerrain()==TypeTerrain.mer && j.getStatut()!=Statut.mort ) {
					j.setStatut(Statut.mourant);
					notifyObservers();
					j.setStatut(Statut.mort);
			}
			//si un joueur remplit les condition pour s'enfuir
		}
		if(joueurs[currentPlayerV2].getAbsc()==dimGrilleAbsc/2 && joueurs[currentPlayerV2].getOrd()==dimGrilleOrd/2 && joueurs[currentPlayerV2].getStatut()!=Statut.sauf) {
			for(Artefact a : artefacts) {
			if(a.hasProprio())
				if(joueurs[currentPlayerV2]==a.getProprio()) {
					joueurs[currentPlayerV2].setStatut(Statut.fuyant);	
					notifyObservers();
					joueurs[currentPlayerV2].setStatut(Statut.sauf);
					break;
				}
			}
		//System.out.println("Un joueur a e'te' e'limine'!");
		}
	}
	
	/**
	 * booleen vrai si les conditions pour gagner la partie sont r�unies:
	 * avoir (au moins) 1 artefact et etre sur la case de l'heliport.
	 */
	
	/**
	 * Recherche dans la liste quel est le joueur correspondant a' currentPlayer
	 * @return le nume'ro du joueur en cours
	 */
	public int getNumJoueurV2() {

		return this.currentPlayerV2;
	}
 
	/**
	 * Change la case du currentplayer on re'cupe're les vosines et on change la case selon la direction
	 * @param d la direction re'cupe're'e dans le controler
	 */
	// attention !! les joueurs peuvent se passer dessus les uns les autres
	public void moveV2(Direction d) {
		if(joueurs[currentPlayerV2].getStatut()!=Statut.vivant)
			nbActions=0;
		if(nbActions>0) {
			Cellule [] v=grille[joueurs[currentPlayerV2].getAbsc()][joueurs[currentPlayerV2].getOrd()].voisines(grille); //on re'cupe're les voisines !!
			//System.out.println("absc joueur"+currentPlayerV2+"est :"+joueurs[currentPlayerV2].getAbsc()+"   ord joueur"+currentPlayerV2+"est :"+joueurs[currentPlayerV2].getOrd());
			switch (d) {
			case up:
				if(grille[joueurs[currentPlayerV2].getAbsc()][joueurs[currentPlayerV2].getOrd()-1].getTypeTerrain()!=TypeTerrain.mer)
					if(!v[0].hasJoueur() || (v[0].getAbsc()==this.dimGrilleAbsc/2 && v[0].getOrd()==this.dimGrilleOrd/2 && !v[0].hasJoueur())) {  
					this.joueurs[currentPlayerV2].setOrd(joueurs[currentPlayerV2].getOrd()-1);
					nbActions--;
				}	

				break;
			case right:
				if(grille[joueurs[currentPlayerV2].getAbsc()+1][joueurs[currentPlayerV2].getOrd()].getTypeTerrain()!=TypeTerrain.mer)
					if(!v[1].hasJoueur() || (v[1].getAbsc()==this.dimGrilleAbsc/2 && v[1].getOrd()==this.dimGrilleOrd/2  && !v[1].hasJoueur())) {
					this.joueurs[currentPlayerV2].setAbsc(joueurs[currentPlayerV2].getAbsc()+1);
					nbActions--;
				}
				break;
			case down:
				if(grille[joueurs[currentPlayerV2].getAbsc()][joueurs[currentPlayerV2].getOrd()+1].getTypeTerrain()!=TypeTerrain.mer)
					if(!v[2].hasJoueur() || (v[2].getAbsc()==this.dimGrilleAbsc/2 && v[2].getOrd()==this.dimGrilleOrd/2  && !v[2].hasJoueur())) {
					this.joueurs[currentPlayerV2].setOrd(joueurs[currentPlayerV2].getOrd()+1);
					nbActions--;
				}
				break;
			case left:
				if(grille[joueurs[currentPlayerV2].getAbsc()-1][joueurs[currentPlayerV2].getOrd()].getTypeTerrain()!=TypeTerrain.mer)
					if(!v[3].hasJoueur() || (v[3].getAbsc()==this.dimGrilleAbsc/2 && v[3].getOrd()==this.dimGrilleOrd/2 && !v[3].hasJoueur())) {
					this.joueurs[currentPlayerV2].setAbsc(joueurs[currentPlayerV2].getAbsc()-1);
					nbActions--;
				}
				break;
			default:
				break;
			}
			notifyObservers();
		}
	}

	/**
	 * Asseche la cellule du currentPlayer
	 */
	public void dry() {
		if(grille[joueurs[currentPlayerV2].getAbsc()][joueurs[currentPlayerV2].getOrd()].getTypeTerrain()==TypeTerrain.inonde && nbActions>0) {
			grille[joueurs[currentPlayerV2].getAbsc()][joueurs[currentPlayerV2].getOrd()].setTypeTerrain(TypeTerrain.terre);
			nbActions--;
		}
		notifyObservers();
		// else met un message "vous pouvez pas assecher ici" ?
	}
	/**
	 * donne les differentes condition pour finir la partie (lose) en mettant endOfTheGame=true.
	 */
	private void setEndOfTheGame() {
		//si l'heliport est sous l'eau
		if(grille[dimGrilleAbsc/2][dimGrilleOrd/2].getTypeTerrain()==TypeTerrain.mer)
			endOfTheGame=true;
		//si il n'y a plus d'artefacts
		int countArte=nbArtefacts;
		for(Artefact a: artefacts) {			
			if((grille[a.getAbsc()][a.getOrd()].getTypeTerrain()==TypeTerrain.mer && !a.hasProprio()))//l'artefact est dans l'eau sans proprio
				countArte--;
			if(a.hasProprio() && (a.getProprio().getStatut()!=Statut.vivant))// l'artefact a un proprio qui a gagne' ou perdu
				countArte--;
		}
		if(countArte==0)
			endOfTheGame=true;		
		//si tout les joueurs sont elimine's	 
			int count=0;
			for (Joueur j :joueurs) {
				if(j.getStatut()!=Statut.vivant)count++;
			}
			if(count==nbJoueurs)
				endOfTheGame=true;
		
	notifyObservers();
	}
	
	/** 
	 * @param nbJ le nombre de joueurs a' re'partir
	 * @return les cellules ou' sont re'partis les joueurs 
	 */
	//ne place pas au bon endroit selon la carte..
	//remplacer "dimGrille..." par "dimGrille...V2" quand initV2 sera pret..
	private void initJoueursV2(int nbJ) {
		if(nbJ>0) {			
			this.joueurs[0]=new Joueur(dimGrilleAbsc/2,(dimGrilleOrd/2)-1);
			//			System.out.println("j0 absc:"+joueurs[0].getAbsc()); // absc du joueur 1
			//			System.out.println("ord:"+joueurs[0].getOrd()); // ord du joueur 1
		}
		if(nbJ>1) {			
			this.joueurs[1]=new Joueur(dimGrilleAbsc/2,(dimGrilleOrd/2)+1);
		}
		if(nbJ>2) {			
			this.joueurs[2]=new Joueur((dimGrilleAbsc/2)+1,dimGrilleOrd/2);			
		}
		if(nbJ>3) {			
			this.joueurs[3]=new Joueur((dimGrilleAbsc/2)-1,dimGrilleOrd/2);			
		}
		if(nbJ>4) {		
			this.joueurs[3]=new Joueur((dimGrilleAbsc/2)+1,(dimGrilleOrd/2)+1);	
		}
		if(nbJ>5) {			
			this.joueurs[3]=new Joueur((dimGrilleAbsc/2)-1,(dimGrilleOrd/2)-1);				
		}
		if(nbJ>6) {
			this.joueurs[3]=new Joueur((dimGrilleAbsc/2)-1,(dimGrilleOrd/2)+1);			
		}
		if(nbJ>7) {
			this.joueurs[3]=new Joueur((dimGrilleAbsc/2)+1,(dimGrilleOrd/2)-1);	
		}
		for(int i=0;i<nbJ;i++) {
			this.joueurs[i].setName("joueur n�"+(i+1));
			//System.out.println(joueurs[i].getName());
		}
	}
	
	private void initArtefacts() {				
		for(int i= 0;i<nbArtefacts;i++) {		
			int x = r.nextInt((CarreCentralAbscMax-1)-(CarreCentralAbscMin+1));
			int absc = (x+(CarreCentralAbscMin+1));		
			int y = r.nextInt((CarreCentralOrdMax-1)-(CarreCentralOrdMin+1));
			int ord = (y+(CarreCentralOrdMin+1));
//			int eloignement = 2;
//			if (!(absc > (CarreCentralAbscMin + eloignement) && absc < (CarreCentralAbscMax - eloignement) && ord > (CarreCentralOrdMin + eloignement) && ord < (CarreCentralOrdMax - eloignement))) {
				if((absc!=dimGrilleAbsc/2 || ord!=dimGrilleOrd/2) && !grille[absc][ord].hasArtefact() && !grille[absc][ord].hasJoueur() && grille[absc][ord].getTypeTerrain()==TypeTerrain.terre)			
					this.artefacts[i]=new Artefact(absc,ord);
			//}
			else
				i--;
		}
			
		
	}

	
	public void ramasseArtefact() {
		Cellule v=grille[joueurs[currentPlayerV2].getAbsc()][joueurs[currentPlayerV2].getOrd()];
		if(v.hasArtefact() && nbActions>0) {
			for (Artefact a : artefacts) {
				if(a.getAbsc()==joueurs[currentPlayerV2].getAbsc() && a.getOrd()==joueurs[currentPlayerV2].getOrd() && !a.hasProprio()){
					a.setProprio(joueurs[currentPlayerV2]);					
					nbActions--;	
				}
				
			}
			notifyObservers();			
		}
	}
	
	public int getDimGrilleAbsc() {
		return this.dimGrilleAbsc;
	}

	public int getDimGrilleOrd() {
		return this.dimGrilleOrd;
	}

	public Cellule[][] getGrille() {
		return this.grille;
	}
	public int getNbJoueurs() {
		return this.nbJoueurs;
	}
	
	public int getNbArtefacts() {
		return this.nbArtefacts;
	}

	public String getNbActionsString() {
		return  Integer.toString(this.nbActions);
	}

	public int getSize() {
		return Cellule.getSize();
	}
	public boolean getEndOfTheGame() {
		return endOfTheGame;
	}
}
