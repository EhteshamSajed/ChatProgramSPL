package Server;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.MalformedInputException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import Client.Cryptography;
import Client.Cryptography.Mode;

import java.util.regex.Matcher;
import java.awt.Color;

public class Server {

	private int port;
	private List<User> clients;
	private ServerSocket server;
	
	static SpamFilter spamFilter;

	public static void main(String[] args) {
		try {
			Cryptography.mode = Mode.NONE;
			spamFilter = new SpamFilter(new SpamStrategy2());		// SpamStrategy2() or SpamStrategy1()
			new Server(1234).run();
		} catch (IOException e) {
		}
	}

	public Server(int port) {
		this.port = port;
		this.clients = new ArrayList<User>();
	}

	public void run() throws IOException {
		server = new ServerSocket(port) {
			protected void finalize() throws IOException {
				this.close();
			}
		};
		System.out.println("Port 1234 is now open.");

		while (true) {
			// accepts a new client
			Socket client = server.accept();

			// get nickname of newUser

			String nickname = (new Scanner(client.getInputStream())).nextLine();
			
			nickname = nickname.replace(",", ""); // ',' use for serialisation
			nickname = nickname.replace(" ", "_");

			System.out.println(
					"New Client: \"" + nickname + "\"\n\t     Host:" + client.getInetAddress().getHostAddress());

			// create new User
			User newUser = new User(client, nickname);

			// add newUser message to list
			this.clients.add(newUser);

			// create a new thread for newUser incoming messages handling
			new Thread(new UserHandler(this, newUser)).start();

			System.out.println(newUser.loggedIn);

			if (!newUser.loggedIn)
				sendMessageToUserFromServer("Hello, please provede pass to login...", newUser.getNickname());

			
		}
	}

	// delete a user from the list
	public void removeUser(User user) {
		this.clients.remove(user);
		System.out.println("ServerClosed " + this.clients.size());

		if (this.clients.isEmpty()) {
			try {
				server.close();

				System.out.println("ServerClosed");
				System.exit(1);
			} catch (IOException e) {
				System.exit(1);
			}
		}
	}

	// send incoming msg to all Users
	public void broadcastMessages(String msg, User userSender) {
		if(!spamFilter.filterMessage(msg)) {
		
		for (User client : this.clients) {
			//String encriptedMsg = rot13(userSender.toString() + "<span>: " + rot13(msg) + "</span>");
			//String encriptedMsg = Cryptography.rot13(userSender.toString() + "<span>: " + Cryptography.rot13(msg) + "</span>");
			String encriptedMsg = Cryptography.defaultEnDecrypt(userSender.toString() + "<span>: " + Cryptography.defaultEnDecrypt(msg) + "</span>");

			
			client.getOutStream().println("#GUI" + encriptedMsg);

			
			// CommandLine
			
			if (!userSender.getNickname().equals(client.getNickname()))
				client.getOutStream().println("#CLI" + Cryptography.defaultEnDecrypt(userSender.getNickname()) + ": " + msg);
			
			// CommandLine
		}
		}
		else {
			System.out.println("Detected!");
		}
	}

	// send list of clients to all Users
	public void broadcastAllUsers() {
		for (User client : this.clients) {
			client.getOutStream().println("#GUI" + this.clients);

			// CommandLine
			client.getOutStream().println("#CLI[List of clients]");
			for (int i = 0; i < clients.size(); i++) {
				client.getOutStream().println("#CLI[" + this.clients.get(i).getNickname() + "]");
			}
			// CommandLine
		}
	}

	// send message to a User (String)
	public void sendMessageToUser(String msg, User userSender, String user) {
		boolean find = false;
		for (User client : this.clients) {
			if (client.getNickname().equals(user) && client != userSender) {
				find = true;
				userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() + ": " + msg);
				client.getOutStream()
						.println("(<b>Private</b>)" + userSender.toString() + "<span>: " + msg + "</span>");
			}
		}
		if (!find) {
			userSender.getOutStream().println(userSender.toString() + " -> (<b>no one!</b>): " + msg);
		}
	}

	// send message to a User (String) from Server
	public void sendMessageToUserFromServer(String msg, String user) {		
		for (User client : this.clients) {
			if (client.getNickname().equals(user)) {
				client.getOutStream().println("#GUI#Server:" + msg);
				
				client.getOutStream().println("#CLI#Server:" + msg);
			}
		}
	}

	/*public static String rot13(String value) {

		char[] values = value.toCharArray();
		for (int i = 0; i < values.length; i++) {
			char letter = values[i];

			if (letter >= 'a' && letter <= 'z') {
				// Rotate lowercase letters.

				if (letter > 'm') {
					letter -= 13;
				} else {
					letter += 13;
				}
			} else if (letter >= 'A' && letter <= 'Z') {
				// Rotate uppercase letters.

				if (letter > 'M') {
					letter -= 13;
				} else {
					letter += 13;
				}
			}
			values[i] = letter;
		}
		// Convert array to a new String.
		return new String(values);
	}*/

	public static void saveLog(String s) {
		Logger logger = Logger.getLogger("ChatLog");
		FileHandler fh;

		try {

			// This block configure the logger with handler and formatter
			fh = new FileHandler("ChatLogFile.log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

			// the following statement is used to log any messages
			logger.info(s);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}





class ColorInt {
	public static String[] mColors = { "#3079ab", // dark blue
			"#e15258", // red
			"#f9845b", // orange
			"#7d669e", // purple
			"#53bbb4", // aqua
			"#51b46d", // green
			"#e0ab18", // mustard
			"#f092b0", // pink
			"#e8d174", // yellow
			"#e39e54", // orange
			"#d64d4d", // red
			"#4d7358", // green
	};

	public static String getColor(int i) {
		return mColors[i % mColors.length];
	}
}
