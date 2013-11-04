package net.santablanca.butler;

import net.santablanca.butler.model.Bex;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Bex bex = new Bex();
		bex.setUrl("https://api.btcchina.com/api_trade_v1.php");
		bex.setApiAccessKey(args[0]);
		bex.setApiSecretKey(args[1]);
		bex.getMarketDepth();

	}

}
