package Server;

import Client.Cryptography;

public class SpamFilter {
	
	SpamOperation spamOperation;
	
	public SpamFilter(SpamOperation _spamOperation) {
		spamOperation = _spamOperation;
	}
	
	public boolean filterMessage(String msg) {
		return spamOperation.checkMassage(Cryptography.defaultEnDecrypt(msg));
	}

}
