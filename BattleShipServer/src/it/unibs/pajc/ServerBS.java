package it.unibs.pajc;

import java.io.*;
import java.net.*;
import java.util.Vector;

public class ServerBS //extends Thread
{
	ServerSocket server;
	
	private Vector <Socket> players = new Vector<Socket>();
	
	public static void main(String[] args) 
	{
		new ServerBS();
	}
	
	public ServerBS()	//costruttore
	{
		int port = 1234;
				
		System.out.println("Starting server on port: " + port + "\n");
		try
		{
			server = new ServerSocket(port);
			int numPlayer = 0;
			
			//continua ad accettare connessioni e crea una partita ogni 2
			while(true)
			{
				// chiamata bloccante, in attesa di una nuova connessione
				Socket client = server.accept();
				
				numPlayer++;
				
				System.out.println("PLAYER " + numPlayer + " connected: ");
				System.out.println("Address: " + client.getInetAddress());
				System.out.println("Port: " + client.getPort() + "\n");
				
				//aggiunge la socket al vettore dei giocatori
				players.add(client);
				
				if(players.size() % 2 == 0)	//se clients pari faccio partire una partita
				{
					//se ho 2 giocatori: pos 0 e pos 1, se ho 4 giocatori: pos 2 e pos 3, ...
					Partita partita = new Partita(players.size()/2, players.get(players.size() - 2), players.get(players.size() - 1));
					//faccio partire la partita startando il thread
					Thread t = new Thread(partita);
					t.start();
				}
			}
		}
		catch(IOException e)
		{
			System.out.println("\nErrore2: " + e.getMessage());
		}
	}
	
}
