package Client;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		try {
			Socket s = new Socket("localhost", 1499);
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);
			new ListeningThread(s).start();
			System.out.println("Connexion réussie !");
			Scanner sc=new Scanner(System.in);
			String message = "";
			while (message!="quit") {
				message = sc.nextLine();
				out.println(message);
			}
		} catch (Exception e) {}
	}
}
