

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

// ����ο�http://blog.sina.com.cn/s/blog_5a6efa330102v8st.html
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
 
/**
 * �ǶԳƼ����㷨RSA
 */
public class RSASecurityServerUtil {
	/** ָ�������㷨ΪRSA */
	private static final String ALGORITHM = "RSA";
	/** ��Կ���ȣ�������ʼ�� */
	private static final int KEYSIZE = 3072;
	/** ��Կ */
	private static Key publicKey = null;
	/** ˽Կ */
	private static Key privateKey = null;
	/** ��Կ�ַ��� */
	private static String publicKeyString = null;
	/** ˽Կ�ַ��� */
	private static String privateKeyString = null;
	/** ָ���ַ��� */
	private static String CHARSET = "UTF-8";
	/**
	 * RSA���������Ĵ�С
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;
	/**
	 * RSA���������Ĵ�С
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;

	/**
	 * ������Կ��
	 * 
	 * @throws Exception
	 */
	public static void generateKeyPair() throws Exception {

		// /** RSA�㷨Ҫ����һ�������ε������Դ */
		SecureRandom secureRandom = new SecureRandom();
		/** ΪRSA�㷨����һ��KeyPairGenerator���� */
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
		/** ����������������Դ��ʼ�����KeyPairGenerator���� */
		keyPairGenerator.initialize(KEYSIZE, secureRandom);
		/** �����ܳ׶� */
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		/** �õ���Կ */
		publicKey = keyPair.getPublic();
		BASE64Encoder encoder = new BASE64Encoder();
		publicKeyString = new String(encoder.encode(publicKey.getEncoded()));
		/** �õ�˽Կ */
		privateKey = keyPair.getPrivate();
		privateKeyString = new String(encoder.encode(privateKey.getEncoded()));
	}

	/**
	 * ���ɹ�Կ����
	 * @param publicKeyStr
	 * @throws Exception
	 */
	public static void setPublicKey(String publicKeyStr) throws Exception {
		RSASecurityServerUtil.publicKey = generatePublicKey(publicKeyStr);
	}
	
	/**
	 * ����˽Կ����
	 * @param publicKeyStr
	 * @throws Exception
	 */
	public static void setPrivateKey(String privateKeyStr) throws Exception {
		RSASecurityServerUtil.privateKey = generatePrivateKey(privateKeyStr);
	}
	
	/**
	 * ˽Կ���ܷ���
	 * @param sourceԴ����
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPrivateKey(String source,String privatekey) throws Exception {
		generatePrivateKey(privatekey);
		/** �õ�Cipher������ʵ�ֶ�Դ���ݵ�RSA���� */
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] data = source.getBytes();
		/** ִ�����ݷ�����ܲ��� */
		int inputLen = data.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // �����ݷֶμ���  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(encryptedData);
	}

	/**
	 * ʹ�ù�Կ�����㷨
	 * @param cryptoSrc ����
	 * @return
	 * @throws Exception
	 */
	public static String decryptByPublicKey(String cryptoSrc,String publicStr) throws Exception {
		setPublicKey(publicStr);
		/** �õ�Cipher��������ù�Կ���ܵ����ݽ���RSA���� */
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] encryptedData = decoder.decodeBuffer(cryptoSrc);
		/** ִ�н��ܲ��� */
		
		int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // �����ݷֶν���  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
        
		return new String(decryptedData);
	}

	
	/**
	 * ��Կ���ܷ���
	 * @param sourceԴ����
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPublicKey(String source, String pubString) throws Exception {
		setPublicKey(pubString);
		/** �õ�Cipher������ʵ�ֶ�Դ���ݵ�RSA���� */
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] data = source.getBytes();
		/** ִ�з�����ܲ��� */
		int inputLen = data.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // �����ݷֶμ���  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
		
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(encryptedData);
	}
	
	
	/**
	 * ˽Կ�����㷨
	 * @param cryptoSrc ����
	 * @return
	 * @throws Exception
	 */
	public static String decryptByPrivateKey(String cryptoSrc,String privatekey) throws Exception {
		//����˽Կ����
		setPrivateKey(privatekey);
		/** �õ�Cipher��������ù�Կ���ܵ����ݽ���RSA���� */
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] encryptedData = decoder.decodeBuffer(cryptoSrc);
        System.out.println(cryptoSrc);
        System.out.println(encryptedData);

		/** ִ�н��ܲ��� */
		int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // �����ݷֶν���  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
		return new String(decryptedData);
	}

//	/**
//	 * ˽Կ����
//	 */
//	public static String decrypt(String cryptograph, String privateKeyStr) throws Exception {
//		/** �õ�Cipher��������ù�Կ���ܵ����ݽ���RSA���� */
//		Cipher cipher = Cipher.getInstance(ALGORITHM);
//		Key privateKey = generatePrivateKey(privateKeyStr);
//		cipher.init(Cipher.DECRYPT_MODE, privateKey);
//		BASE64Decoder decoder = new BASE64Decoder();
//		byte[] b1 = decoder.decodeBuffer(cryptograph);
//		/** ִ�н��ܲ��� */
//		byte[] b = cipher.doFinal(b1);
//		return new String(b);
//	}

	/**
	 * �������Ĺ�Կ�ַ���ת��Ϊ��Կ����
	 * 
	 * @param publicKeyStr
	 * @return
	 * @throws Exception
	 */
	private static Key generatePublicKey(String publicKeyStr) throws Exception {
		publicKeyString = publicKeyStr;
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
			return publicKey;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("�޴��㷨");
		} catch (InvalidKeySpecException e) {
			throw new Exception("��Կ�Ƿ�");
		} catch (IOException e) {
			throw new Exception("��Կ�������ݶ�ȡ����");
		} catch (NullPointerException e) {
			throw new Exception("��Կ����Ϊ��");
		}
	}

	/**
	 * ��������˽Կ�ַ���ת��Ϊ˽Կ����
	 * 
	 * @param privateKeyStr
	 * @return
	 * @throws Exception
	 */
	private static Key generatePrivateKey(String privateKeyStr) throws Exception {
		privateKeyString = privateKeyStr;
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
			return privateKey;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("�޴��㷨");
		} catch (InvalidKeySpecException e) {
			throw new Exception("˽Կ�Ƿ�");
		} catch (IOException e) {
			throw new Exception("˽Կ�������ݶ�ȡ����");
		} catch (NullPointerException e) {
			throw new Exception("˽Կ����Ϊ��");
		}
	}

	public static String getPublicKeyString() {
		return publicKeyString;
	}

	public static String getPrivateKeyString() {
		return privateKeyString;
	}

	public static void generateKeyPair(String publicKeyStr, String privateKeyStr) throws Exception {
		generatePublicKey(publicKeyStr);
		generatePrivateKey(privateKeyStr);
	}

	public static void main(String[] args) throws Exception {
		generateKeyPair();
		System.out.println("��Կ�ַ���: ");
		System.out.println(publicKeyString);
		System.out.println();
		System.out.println();
		System.out.println("��Կ�ַ�������" + publicKeyString.length());
		System.out.println();
		System.out.println();
		System.out.println("˽Կ�ַ���: ");
		System.out.println(privateKeyString);
		System.out.println();
		System.out.println();
		System.out.println("˽Կ�ַ�������" + privateKeyString.length());
		System.out.println();
		System.out.println();
		System.out.println("��Կ����: ");
		System.out.println(publicKey);
		System.out.println();
		System.out.println();
		System.out.println("˽Կ����: ");
		System.out.println(privateKey);
		int num = 0;
//		for (int i = 0; i< 100; i++ ) {
			String en = encryptByPrivateKey("123123", privateKeyString);
			System.out.println(en);
			System.out.println(decryptByPublicKey(en, publicKeyString));
			System.out.println(++ num);
//		}
		
	}
}
