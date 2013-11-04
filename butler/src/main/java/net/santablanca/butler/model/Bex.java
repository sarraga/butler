package net.santablanca.butler.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Bex {

    @NotNull
    private String apiAccessKey;

    @NotNull
    private String apiSecretKey;
    
    @NotNull
    private String url = "https://api.btcchina.com/api_trade_v1.php";
    
    private Logger logger = Logger.getLogger("Bex");
    
       
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
 
	private static String getSignature(String data,String key) throws NoSuchAlgorithmException, InvalidKeyException {
 
		// get an hmac_sha1 key from the raw key bytes
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
 
		// get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
 
		// compute the hmac on input data bytes
		byte[] rawHmac = mac.doFinal(data.getBytes());
 
		return bytArrayToHex(rawHmac);	
	}
 
 
	private static String bytArrayToHex(byte[] a) {
	   StringBuilder sb = new StringBuilder();
	   for(byte b: a)
	      sb.append(String.format("%02x", b&0xff));
	   return sb.toString();
	}
 
	public  void sendRequest(String operacion, String postdata){
 

		try {
			String tonce = ""+(System.currentTimeMillis() * 1000); 
			String params = "tonce="+tonce.toString()+"&accesskey="+apiAccessKey+"&requestmethod=post&id=1&method=" + operacion + "&params="; 
			String hash = getSignature(params, apiSecretKey);


			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			String userpass = apiAccessKey + ":" + hash;
			String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());

			//add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("Json-Rpc-Tonce", tonce.toString());
			con.setRequestProperty ("Authorization", basicAuth);



			// Send post request
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postdata);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + postdata);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			System.out.println(response.toString());
		} catch (IOException e) {
			logger.fatal("Error enviando consulta a bex: " + e.getMessage(), e);			
		} catch (InvalidKeyException e) {
			logger.fatal("Error en key de encripcion de bex: " + e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			logger.fatal("Error en algoritmo - no tengo SHA?x: " + e.getMessage(), e);
		}
 
	}
    

	public void getMarketDepth() 
	{
		String operacion = "getAccountInfo";
		String postdata = "{\"method\": \"getAccountInfo\", \"params\": [], \"id\": 1}";
		sendRequest(operacion,postdata);
	}
}
