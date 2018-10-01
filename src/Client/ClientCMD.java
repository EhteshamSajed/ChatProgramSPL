package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.CryptoPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ClientCMD {
	private Thread read;
	private String serverName;
	private int PORT;
	private String name;
	BufferedReader input;
	PrintWriter output;
	Socket server;
	
	public static void main(String[] args) throws Exception {
		new ClientCMD();
	}
	
	public ClientCMD() {
		try {
			this.serverName = "localhost";
			this.PORT = 1234;
			this.name = "nickname";
			
			System.out.println("...Connecting to server: " + serverName + " port: " + PORT);
			server = new Socket(serverName, PORT);

			input = new BufferedReader(new InputStreamReader(server.getInputStream()));
			output = new PrintWriter(server.getOutputStream(), true);

			// output.println(name);

			Write write = new Write();
			write.start();

			read = new Read();
			read.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String message) {
		if (message.equals("")) {
			return;
		}		

		//message = Cryptography.rot13(message);
		message = Cryptography.defaultEnDecrypt(message);

		// System.out.println("Sending msg to server: " + message);
		output.println(message);
	}
	
	class Write extends Thread {
		public void run() {
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter a nickname: ");
			String nickname = sc.nextLine();

			// sendMessage(nickname);
			output.println(nickname);

			System.out.println("Messages: \n");

			while (sc.hasNextLine()) {
				// output.println(sc.nextLine());
				sendMessage(sc.nextLine());
			}
		}
	}
	
	class Read extends Thread {
		public void run() {
			String message;
			while (!Thread.currentThread().isInterrupted()) {
				try {
					message = input.readLine();
					// System.out.println(message);
					if (message != null) {
						if (message.substring(0, 4).equals("#GUI")) {
							
						}

						else {
							// CommandLine
							
							message = message.replaceFirst("#CLI", "");
							// System.out.println(message);
							
							if (message.charAt(0) == '[') {
								message = message.substring(1, message.length() - 1);
								ArrayList<String> ListUser = new ArrayList<String>(Arrays.asList(message.split(", ")));
								
								for (String user : ListUser) {
									System.out.println(user);
								}
							}

							else if (message.charAt(0) == '#') {
								message.replaceFirst("#Server(<b>Private</b>)", "");

								System.out.println(message);
							} else {
								//message =  Cryptography.rot13(message);
								message = Cryptography.defaultEnDecrypt(message);

								System.out.println(message);
							}
							
							// CommandLine
						}
					}
				} catch (IOException ex) {
					// System.err.println("Failed to parse incoming message");
				}
			}
		}
	}
}
