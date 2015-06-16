package app.security;

import java.io.DataInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class KeyReader {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	public PrivateKey getPrivateKey(String filename) {
		try {
			DataInputStream dis = new DataInputStream(new ClassPathResource(filename).getInputStream());
			byte[] keyBytes = new byte[(int) dis.available()];
			dis.readFully(keyBytes);
			dis.close();

			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePrivate(spec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			log.error("Private key retrieval failed: " + e.getMessage() + ". " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public PublicKey getPublicKey(String filename) {
		try {
			DataInputStream dis = new DataInputStream(new ClassPathResource(filename).getInputStream());
			byte[] keyBytes = new byte[(int) dis.available()];
			dis.readFully(keyBytes);
			dis.close();

			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePublic(spec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			log.error("Private key retrieval failed: " + e.getMessage() + ". " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
