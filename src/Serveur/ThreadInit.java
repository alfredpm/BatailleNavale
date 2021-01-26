package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

// Appelé une fois par joueur en début de partie 
// pour qu'ils puissent poser leurs bateaux en même temps.

public class ThreadInit extends Thread{
	private BufferedReader in;	//Permet de recevoir les commandes du joueur
	private PrintWriter out;	//Permet de communiquer vers le joueur
	private Partie game;		//Permet d'accéder aux infos du joueur
	private int numJ;			//Permet de récupérer des infos du joueur à partir de game
	
	private Ship[] ships;
	private Grille grid;
	
	public ThreadInit(BufferedReader in, PrintWriter out, Partie game, int numJ) {
		this.in = in;
		this.out = out;
		this.game = game;
		this.numJ = numJ;
		
		ships=game.getPlayer(numJ).getShips();
		grid=game.getPlayer(numJ).getSelfGrid();
	}
	
	public void run() {
		boolean done;
		for (Ship ship : ships) {
			done=false;
			while(!done) {
				try {
					done=placeShip(ship);
				} catch (IllegalArgumentException e) {
					out.println("Coordonnées invalides.");
					out.println("Essayez avec un lettre (A-J), un chiffre (0-9) et une orientation (H ou V). Exemple : /A0H");
					out.println("La coordonnée renseignée est celle de la case la plus haute ou la plus à gauche.");
				}
			}
		}
		out.println("Tous les navires sont en position. En attente du joueur adverse.");
	}
	
	
	//Demande les coordonnées, les formate, puis demande à la grille de vérifier s'il y a la place et poser le navire si c'est le cas.
	private boolean placeShip(Ship ship) throws IllegalArgumentException{
		//Affichage de la grille actuelle
		out.println(grid);
		
		// Vérifier le format
		int[] coord=new int[3];
		//while (Arrays.equals(coord, new int[3])) {
			try {
				coord=askCoord(ship);
			} catch (Exception e) {
				System.out.println("Erreur ask : "+e);
				throw new IllegalArgumentException();
			}
		//}
		try {
			grid.addShip(ship, coord);
		} catch (Exception e) {
			System.out.println("Erreur add : "+e);
			throw new IllegalArgumentException();
		}
		
		
		return true;
	}

	
	//Demande les coordonnées et les formate
	public int[] askCoord(Ship ship) throws IllegalArgumentException{
		int l=0;
		int c=0;
		int orient=0;
		String coord="";
		// Demande
		out.println("Où placer le "+ship.getNom()+"? ("+ship.getTaille()+" cases)");
		out.println("Format LigneColonneOrientation (ex : A1H)");
		// Réponse
		try {
			coord=in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Vérification du format
		try {							//On transforme le deuxième caractère en chiffre
			l=coord.charAt(1)-'0'+1;
			c=ColumnStrToInt(coord.substring(0, 1));
			orient=OrientationStrToInt(coord.substring(2,3));
		} catch (Exception e) {			//Erreur s'il n'y a pas de deuxième caractère														//Exception si coordonnée à un seul caractère
			System.out.println("Coordonnées trop courtes");
			throw new IllegalArgumentException();		//==> coordonnées trop courtes
		}
		if (!(l>=1 && l<=10)) {
			System.out.println("l invalide");
			throw new IllegalArgumentException();
		}
		return new int[]{l,c,orient};
	}

	private int ColumnStrToInt(String colStr) {
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
	private int OrientationStrToInt(String orStr) {
		int orInt;
		switch (orStr) {
			case "H","h": {
				orInt=1;
				break;
			}
			case "V","v": {
				orInt=0;
				break;
			}
		default:
			throw new IllegalArgumentException("Unexpected value: " + orStr);
		}
		return orInt;
	}

}
