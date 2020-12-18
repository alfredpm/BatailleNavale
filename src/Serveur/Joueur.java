package Serveur;

public class Joueur {
	// ROLE : Gère l'initialisation, le stockage et la modification des différents attributs d'un joueur
	
	private Grille targetGrid;
	private Grille selfGrid;
	private Ship[] ships;
	private int points;	//Compte le nombre de tir réussi, utilisé pour déclencher la victoire.
	
	public Joueur() {
		this.targetGrid = new Grille();
		this.selfGrid=new Grille();
		this.ships = new Ship[5];
		ships[0]= new Ship("Porte-avion", 5);
		ships[1]= new Ship("Croiseur", 4);
		ships[2]= new Ship("Contre-torpilleur", 3);
		ships[3]= new Ship("Contre-torpilleur", 3); 
		ships[4]= new Ship("Torpilleur", 2);
		this.points=0;
	}
	
	public Joueur(Grille targetGrid, Grille selfGrid, Ship[] ships) {
		this.targetGrid = targetGrid;
		this.selfGrid = selfGrid;
		this.ships = ships;
	}
	
	// GETTERS / SETTERS
	public Grille getTargetGrid() {
		return targetGrid;
	}
	public Grille getSelfGrid() {
		return selfGrid;
	}
	
	public Ship[] getShips() {
		return ships;
	}

	//Incrémente points, appelé lors d'un tir réussi
	public void addPoint() {
		points++;
	}

	public int getPoints() {
		return points;
	}

}
