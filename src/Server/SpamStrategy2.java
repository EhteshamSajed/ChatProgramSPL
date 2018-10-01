package Server;

public class SpamStrategy2 implements SpamOperation{
	String filterWords[] = {"spam6","spam7","spam8", "spam9", "spam0"};
	
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