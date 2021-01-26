package Serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

// Gère le déroulement de la partie
// Communique avec les deux joueurs
// Délègue un maximum de travail à Partie

// TODO : séparer le code en Partie et Grille (et Joueur ?)

public class ThreadPartie extends Thread{
	private BufferedReader[] ins = new BufferedReader[2];
	private PrintWriter[] outs = new PrintWriter[2];
	private Partie game;

	public ThreadPartie(Socket player1, Socket player2) {
		try {
			ins[0] = new BufferedReader(new InputStreamReader(player1.getInputStream()));
			outs[0] = new PrintWriter(player1.getOutputStream(), true);
			ins[1] = new BufferedReader(new InputStreamReader(player2.getInputStream()));
			outs[1] = new PrintWriter(player2.getOutputStream(), true);			
		} catch (Exception e) {}
		this.game=new Partie();
	}
	
	
	public void run() {
		// INITIALISATION	
		ThreadInit ti1= new ThreadInit(ins[0], outs[0], game, 0);
		ThreadInit ti2= new ThreadInit(ins[1], outs[1], game, 1);
		ti1.start();	
		ti2.start();
		try {
			System.out.println("En attente de mise en place des bateaux.");
			ti1.join();			//En pause tant que le joueur 1 n'a pas fini de poser (fin du ThreadInit it1).
			ti2.join();			//En pause tant que le joueur 2 n'a pas fini de poser (fin du ThreadInit it2), automatiquement passé s'il avait déjà fini quand ti1.join.
			System.out.println("Début de partie.");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		
		outs[game.getTourDe()].println("Vous êtes le joueur 1. Say something !");
		outs[game.getWaiting()].println("Vous êtes le joueur 2.");
		// DEROULEMENT
		// On effectue en boucle des tours, qui consistent en l'entrée de commande dans la console.
		while (!game.isWon()) {
			try{
				// ENTREE CONSOLE
				//resetBuffer(game.getTourDe()); //ne fonctionne pas
				String message = ins[game.getTourDe()].readLine();
				// Si '/...' alors commande
				if (message.charAt(0)==0x2F) {						// '/' est le char 0x2F 
					message=message.substring(1);					// On oublie le "/" de début de commande
					switch (message) {								
						case "show target":{this.showTarget(); break;}	// 		/show target	Affiche la grille des tirs effectués
						case "show self":{this.showSelf(); break;}		//		/show self		Affiche la grille personnelle (bateaux, tirs adverses)
						case "end": {this.finDeTour(); break;}			// 		/end 			Force la fin de tour											//COMMANDE DE TEST
						case "win":{this.win(); break;}					//		/win 			Met fin à la partie, le joueur dont c'est le tour gagne			//COMMANDE DE TEST
					default:
						tryShoot(message);							//	/xxxxxx			Essaie de tirer aux coordonnées renseignées (vérif validité, tir, résultat)
					}
				}
				// sinon message.									// xxxxxx
				else {	//Si ce n'est pas une commande, c'est un message.
					System.out.println("Message transmis - J"+(game.getTourDe()+1)+" : "+message);
					message="J"+(game.getTourDe()+1)+" : "+message;		// 	Donne son format au message
					outs[game.getWaiting()].println(message);		//	Jy : xxxxxx			Envoie le message
				}
			} catch (Exception e) {}
		}
		//CONCLUSION
		outs[game.getTourDe()].println("Toute la flotte du joueur"+(game.getWaiting()+1)+" a sombré ! Vous avez gagné !");
		outs[game.getWaiting()].println("C'était le dernier de vos navires. Le joueur "+(game.getTourDe()+1)+" a gagné ...");
	}


	
	
	
	// NE FONCTIONNE PAS, NE VIDE RIEN
	// Vide le bufferreader, utilisé au début du tour d'un joueur pour ne pas communiquer les messages 
	// et commandes passées pendant que ce n'était pas le tour du joueur.
	private void resetBuffer(int i) {
		boolean notEmpty = true;
		while(notEmpty) {
			try{
				ins[i].skip(1);
			} 
			catch (IOException e) {notEmpty=false;}
			catch (IllegalArgumentException iae){System.out.println("Erreur lors du nettoyage de l'input.");} 
		}
		System.out.println("Nettoyage accompli");
	}
	
	

	//COMMANDES
	public void showSelf() {				//Affiche la grille personnelle (bateaux, tirs adverses)
		outs[game.getTourDe()].println(game.getCurrentSelfGrid());
	}	
	public void showTarget() {				//Affiche la grille des tirs effectués
		outs[game.getTourDe()].println(game.getCurrentTargetGrid());		
	}
	public void win() {
		game.setWon(true);
	}

	public void tryShoot(String target) {	//Essaie de tirer aux coordonnées désignées, renvoie un message si impossible.
		try {
			outs[game.getTourDe()].println("Tir en "+target+" envoyé !");
			outs[game.getWaiting()].println("Le joueur "+(game.getTourDe()+1)+" tire en "+target+" !");
			if(game.shoot(target)) {
				outs[game.getTourDe()].println("Touché !");
				outs[game.getWaiting()].println("Touché !");
			} else {
				outs[game.getTourDe()].println("Raté.");
				outs[game.getWaiting()].println("Raté.");
			}
			finDeTour();	//Le tour se termine automatiquement après un tir
		} catch (IllegalArgumentException iae) {
			outs[game.getTourDe()].println(target+" est une cible invalide.");
			outs[game.getTourDe()].println("Essayez avec un lettre (A-J) et un chiffre (0-9). Exemple : /A1");
		}
	}
	

	
	
	
	//METHODES INTERMEDIAIRES
	public void finDeTour() {				//Appelé pour mettre fin à un tour.
		outs[game.getTourDe()].println("Fin de tour");
		outs[game.getWaiting()].println("Début de tour");
		if(!game.isWon()) {
			game.changeTurn();
		}
	}
}
