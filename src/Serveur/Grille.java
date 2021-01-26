package Serveur;


public class Grille {
	// ROLE : Gère les propriétés, la construction et l'affichage d'une grille
	
	
	private String[][] grid;

	// CONSTRUCTEURS
	public Grille() {	// A revoir, je mélange information et affichage. Il n'y a pas de raison pour que Grille soit un tableau de caractère
		grid = new String [11][11];
		// Remplissage de caractères par défaut
		for (int l=0; l<grid.length; l++) {
			for (int c=0; c<grid[l].length; c++) {
				grid[l][c]="~";
			}
		}
		// Annotation colonne A-J
		grid[0][0]="x";
		grid[0][1]="A";
		grid[0][2]="B";
		grid[0][3]="C";
		grid[0][4]="D";
		grid[0][5]="E";
		grid[0][6]="F";
		grid[0][7]="G";
		grid[0][8]="H";
		grid[0][9]="I";
		grid[0][10]="J";
		// Annotation ligne 0-9
		grid[1][0]="0";
		grid[2][0]="1";
		grid[3][0]="2";
		grid[4][0]="3";
		grid[5][0]="4";
		grid[6][0]="5";
		grid[7][0]="6";
		grid[8][0]="7";
		grid[9][0]="8";
		grid[10][0]="9";
	}


	// AFFICHAGE		// C'est ici que les caractères doivent être utilisé pour représenter les données
	@Override
	public String toString() {
		String affGrid="";
		for (int l=0; l<grid.length; l++) {
			for (int c=0; c<grid[l].length; c++) {
				affGrid=affGrid+" "+grid[l][c];
			}
			affGrid=affGrid+"\n";
		}
		return affGrid;
	}


	public void addShip(Ship ship, int[] coord) throws IllegalArgumentException{
		int[] origine = new int[]{coord[0],coord[1],coord[2]}; //Solution de facilitée pour ne pas avoir à gérer les modifications de coord par la verifySpace
		if (verifySpace(ship.getTaille(), coord)) {
			placeOnGrid(ship,origine);
		} else {
			System.out.println("Erreur au niveau de la vérification");
			throw new IllegalArgumentException();
		}

	}
	
	//Vérifie que l'espace nécessaire pour poser le navire est disponible.
	private boolean verifySpace(int shipLength, int[] coord) {
		
		//Récursivité
		boolean next=true; 							//True par défaut, la case suivante importe peu si on atteint le bout du bateau
		boolean local=true;
		
		//On s'arrête quand on atteint la dernière case du navire
		if (shipLength!=1) {
			//Définition de la case suivante
			int[] nextCoord=coord.clone();
			if (coord[2]==0){						//Coord[2] ==> Orientation ; Orientation = 0 ==> Vertical
				nextCoord[0]=coord[0]+1;				//Changement de ligne
			} else {								// Orientation != 0 <=> Orientation = 1 ==> Horizontal 
				nextCoord[1]=coord[1]+1;				//Changement de colonne
			}
			//Vérification à partir de la case suivante, on réduit la taille restante du navire.
			next=verifySpace(shipLength-1,nextCoord);	
		}
		
		
		//Vérification de la case courante
		
		//Existe-t-elle ?
		String currentCase="";
		try {
			currentCase=this.grid[coord[0]][coord[1]];
		} catch (Exception e) {
			local=false;
			System.out.println("Problème d'existence : "+currentCase);
		}
		
		//Est-elle vide ? 
		if (!currentCase.equals("~")) {
			local=false;
			System.out.println("Problème de présence : "+currentCase);
		}
		
		
		//On multiplie le résultat des suivants avec le résultat de la vérification locale pour avoir le résultat de la vérification finale.
		return next&&local;		//On pourrait gagner des opérations en vérifiant local à ce moment (avec une fonction à part), 
								//puisque si next est false le programme n'essaiera pas de vérifier local.
	}

	//Rempli des cases de O selon la taille et les coordonnées du navire, 
	//on a déjà vérifié que l'espace est libre avec verifySpace()
	private void placeOnGrid(Ship ship, int[] coord) {
		for (int i = 0; i < ship.getTaille(); i++) {
			if(coord[2]==1) {
				grid[coord[0]][coord[1]+i]="O";
			} else {
				grid[coord[0]+i][coord[1]]="O";
			}
		}
		  
	}

	//Fonction de réception d'un tir. Met à jour la grille, renvoie si un navire est touché ou non.
	public boolean addImpact(String target) {
		int l=target.charAt(1)-'0'+1;
		int c=ColumnStrToInt(target.substring(0,1));
		switch (grid[l][c]) {	//Croisement de la ligne associée au chiffre et la colonne associée à la lettre
			case "O": {
				grid[l][c]="X";
				System.out.println("Grille adverse : O->X");
				return true;
			}
			case "X": {
				System.out.println("Grille adverse : already X");
				return false;
			}
			case "~": {
				grid[l][c]="v";
				System.out.println("Grille adverse : ~->v");
				return false;
			}
		}
		return false;
	}

	//Sûrement moyen de simplifier pour éviter la duplication de code
	//Cette version d'addImpact a un résultat prédéfini, utilisé pour mettre à jour targetGrid
	public void addImpact(String target, boolean hit) {
		int l=target.charAt(1)-'0'+1;
		int c=ColumnStrToInt(target.substring(0,1));
		if (hit) {
			grid[l][c]="X";
			System.out.println("Grille cible : ~->X");
		} else {
			grid[l][c]="v";
			System.out.println("Grille cible : ~->v");
		}
		
	}


	//Code dupliqué de ThreadInit, devrait donc être ni dans l'un ni dans l'autre
	private int ColumnStrToInt(String colStr) throws IllegalArgumentException {
		int colInt=0;
		switch (colStr) {
			case "A","a": {
				colInt=1;
				break;
			}
			case "B","b": {
				colInt=2;
				break;
			}
			case "C","c": {
				colInt=3;
				break;
			}
			case "D","d": {
				colInt=4;
				break;
			}
			case "E","e": {
				colInt=5;
				break;
			}
			case "F","f": {
				colInt=6;
				break;
			}
			case "G","g": {
				colInt=7;
				break;
			}
			case "H","h": {
				colInt=8;
				break;
			}
			case "I","i": {
				colInt=9;
				break;
			}
			case "J","j": {
				colInt=10;
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + colStr);
		}
		return colInt;
	}


}
