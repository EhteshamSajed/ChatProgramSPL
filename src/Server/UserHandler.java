package Server;

import java.util.Scanner;

class UserHandler implements Runnable {

	private Server server;
	private User user;

	public UserHandler(Server server, User user) {
		this.server = server;
		this.user = user;
		this.server.broadcastAllUsers();
	}

	public void run() {
		String message;

		// when there is a new message, broadcast to all
		Scanner sc = new Scanner(this.user.getInputStream());

		while (sc.hasNextLine()) {
			message = sc.nextLine();

			// Server.saveLog(user.getNickname() + ":" + Server.rot13(message));

			if (user.loggedIn) {

				// update user list

				server.broadcastMessages(message, user);
				// }
			} else {

				if (User.Loggin(user, message)) {
					System.out.println("Server: Login true!");
					server.sendMessageToUserFromServer("Thanks for login...", user.getNickname());
				} else {
					System.out.println("Server: Login failed!");
					server.sendMessageToUserFromServer("ID/Password mismatched!", user.getNickname());
				}

			}
		}

		// end of Thread
		server.removeUser(user);
		this.server.broadcastAllUsers();
		sc.close();
	}
}
