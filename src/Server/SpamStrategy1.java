package Server;

public class SpamStrategy1 implements SpamOperation{
	String filterWords[] = {"spam1","spam2","spam3", "spam4", "spam5"};
	
	public boolean checkMassage(String msg) {
		for(String filterWord : filterWords) {
			if(msg.toLowerCase().contains(filterWord))
			{
				return true;
			}
		}		
		return false;
	}
}