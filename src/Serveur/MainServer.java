package Serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Gère les connexions, instancie les parties

public class MainServer {
	public static void main(String[] args) {
		try {
			ServerSocket ecoute = new ServerSocket(1499);
			System.out.println("Server ready !");
			String message="/";
			while (true) {
				Socket client1 = ecoute.accept();
				Socket client2 = ecoute.accept();
				System.out.println("2 joueurs reçus, lancement de partie !");
				new ThreadPartie(client1, client2).start();
			}
		} catch (IOException e) {}
	}
}



/* Note pour le prof :
 * - Est-il possible de vider le contenu d'un input avant d'essayer de le lire ou de vérifier s'il contient quelque chose ?
 * Actuellement, il gardera en mémoire tout ce qui a été écrit pendant le tour adverse
 * Ca pourrait être une feature si je le contrôlais correctement, mais pour l'instant c'est un bug.
 */