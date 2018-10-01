package Server;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class User {
	static private String loginCredentials = "nickname;9876/john;6543/tom;3210";

	static private Map m1 = new HashMap();

	private static int nbUser = 0;
	private int userId;
	private PrintStream streamOut;
	private InputStream streamIn;
	private String nickname;
	private Socket client;
	private String color;
	public boolean loggedIn = false;

	public static void main(String[] args) {
	}

	// constructor
	public User(Socket client, String name) throws IOException {
		this.streamOut = new PrintStream(client.getOutputStream());
		this.streamIn = client.getInputStream();
		this.client = client;
		this.nickname = name;
		this.userId = nbUser;
		this.color = ColorInt.getColor(this.userId);
		nbUser += 1;
	}

	// change color user
	public void changeColor(String hexColor) {
		// check if it's a valid hexColor
		Pattern colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})");
		Matcher m = colorPattern.matcher(hexColor);
		if (m.matches()) {
			Color c = Color.decode(hexColor);
			// if the Color is too Bright don't change
			double luma = 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue(); // per ITU-R BT.709
			if (luma > 160) {
				this.getOutStream().println("<b>Color Too Bright</b>");
				return;
			}
			this.color = hexColor;
			this.getOutStream().println("<b>Color changed successfully</b> " + this.toString());
			return;
		}
		this.getOutStream().println("<b>Failed to change color</b>");
	}

	// getteur
	public PrintStream getOutStream() {
		return this.streamOut;
	}

	public InputStream getInputStream() {
		return this.streamIn;
	}

	public String getNickname() {
		return this.nickname;
	}

	// print user with his color
	public String toString() {

		return "<u><span style='color:" + this.color + "'>" + this.getNickname() + "</span></u>";

	}

	public static boolean Loggin(User user, String pass) {
		
		String[] allCredentials = loginCredentials.split("/");

		System.out.println(allCredentials.length);

		for (String singleCredential : allCredentials) {
			if (user.getNickname().equals(singleCredential.split(";")[0])) {
				if (pass.equals(singleCredential.split(";")[1])) {
					user.loggedIn = true;
					return true;
				}
			}
		}

		return false;
	}

}
