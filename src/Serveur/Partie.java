package Serveur;

public class Partie { 			
	
	private static String columnRange="AaBbCcDdEeFfGgHhIiJj"; // Utilisé pour vérifier la validité des coordonnées
	
	private Joueur[] players;
	private int tourDe;
	private int waiting;
	private boolean won;

	// CONSTRUCTEURS
	public Partie() {
		this.players = new Joueur[2];
		players[0]=new Joueur();
		players[1]=new Joueur();
		this.tourDe = 0;
		this.waiting = 1;
	}
	
	// GETTERS / SETTERS
	public int getTourDe() {
		return tourDe;
	}
	public int getWaiting() {
		return waiting;
	}
	public Grille getCurrentSelfGrid() {
		return players[tourDe].getSelfGrid();
	}
	public Grille getCurrentTargetGrid() {
		return players[tourDe].getTargetGrid();
	}
	
	public boolean isWon() {
		return won;
	}
	
	public void setWon(boolean b) {
		won=b;
	}
	
	//Utilisé uniquement par ThreadInit, qui mérite d'être revu
	public Joueur getPlayer(int numJ) {
		return players[numJ];
	}
	
	//////// FONCTIONS ////////
	
	// GESTION TOURS
	public void changeTurn() {		//Appelé pour passer le tour au joueur en attente, utilisé par ThreadPartie.finDeTour()
		int pivot=tourDe;
		tourDe=waiting;
		waiting=pivot;
		System.out.println("Passage de tour effectué.");
	}
	
	// GESTION TIRS
	public boolean shoot(String target) throws IllegalArgumentException {
		int y;
		boolean isHit=false;
		// Test des coordonnées
		try{
			y = target.charAt(1)-'0';
		} catch (Exception e) {																		//Exception si coordonnée à un seul caractère
			throw new IllegalArgumentException();
		}	
		if (target.length()!=2 || !columnRange.contains(target.substring(0,1)) || !(y>=0 && y<=9)) { //Autres conditions
			throw new IllegalArgumentException();
		}
		// Tir
		else {
			System.out.println("Tir valide en "+target+" (J"+tourDe+" -> J"+waiting+")");
			if (players[waiting].getSelfGrid().addImpact(target)) {				//Tire sur la case, marque la grille adverse et renvoie true si touché
				players[tourDe].getTargetGrid().addImpact(target,true);			//Si touché, ajouté marque touché sur la grille de tir
				players[tourDe].addPoint();
				isHit=true;
				won=checkVictory();
			} else {
				players[tourDe].getTargetGrid().addImpact(target,false);		//Sinon, ajouté marque raté
			}
			
		}
		return isHit; //Plus utilisé
	}

	// Renvoie true si le nombre de points correspond à la somme des longueurs des navires
	private boolean checkVictory() {
		boolean reached = false;
		int seuil=0;
		for (Ship ship : players[tourDe].getShips()) {
			seuil+=ship.getTaille();
		}
		if (players[tourDe].getPoints()>=seuil) {
			reached=true;
		}
		return reached;
		
	}







	
}
