package security;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

public class KeyReader {
	public static PrivateKey getPrivateKey(String filename) {
		try {
			File f = new ClassPathResource(filename).getFile();
			FileInputStream fis = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(fis);
			byte[] keyBytes = new byte[(int) f.length()];
			dis.readFully(keyBytes);
			dis.close();

			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePrivate(spec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			Logger logger = LogManager.getLogger(KeyReader.class.getName());
			logger.error("Private key retrieval failed: " + e.getMessage() + ". " + e.getStackTrace());
			e.printStackTrace();
		}
		return null;
	}

	public static PublicKey getPublicKey(String filename) {
		try {
			File f = new ClassPathResource(filename).getFile();
			FileInputStream fis = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(fis);
			byte[] keyBytes = new byte[(int) f.length()];
			dis.readFully(keyBytes);
			dis.close();

			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePublic(spec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			Logger logger = LogManager.getLogger(KeyReader.class.getName());
			logger.error("Private key retrieval failed: " + e.getMessage() + ". " + e.getStackTrace());
			e.printStackTrace();
		}
		return null;
	}
}
