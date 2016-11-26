package freenet.crypt.ciphers;

import freenet.crypt.BlockCipher;
import freenet.crypt.UnsupportedCipherException;
import freenet.support.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Provider;

/*
  This code is part of the Java Adaptive Network Client by Ian Clarke. 
  It is distributed under the GNU Public Licence (GPL) version 2.  See
  http://www.gnu.org/ for further details of the GPL.
 */

/**
 * Interfaces with the Rijndael AES candidate to implement the Rijndael
 * algorithm
 */
public class Rijndael implements BlockCipher {
	private Object sessionKey;
	private final int keysize, blocksize;

	public static final Provider AesCtrProvider = null; //getAesCtrProvider();
	
	public static String getProviderName() {
		return AesCtrProvider != null ? AesCtrProvider.getName() : null;
	}
	
	static private long benchmark(Cipher cipher, SecretKeySpec key, IvParameterSpec IV) throws GeneralSecurityException
	{
		long times = Long.MAX_VALUE;
		byte[] input = new byte[1024];
		byte[] output = new byte[input.length*32];
		cipher.init(Cipher.ENCRYPT_MODE, key, IV);
		// warm-up
		for (int i = 0; i < 32; i++) {
			cipher.doFinal(input, 0, input.length, output, 0);
			System.arraycopy(output, 0, input, 0, input.length);
		}
		for (int i = 0; i < 128; i++) {
			long startTime = System.nanoTime();
			cipher.init(Cipher.ENCRYPT_MODE, key, IV);
			for (int j = 0; j < 4; j++) {
				int ofs = 0;
				for (int k = 0; k < 32; k ++) {
					ofs += cipher.update(input, 0, input.length, output, ofs);
				}
				cipher.doFinal(output, ofs);
			}
			long endTime = System.nanoTime();
			times = Math.min(endTime - startTime, times);
			System.arraycopy(output, 0, input, 0, input.length);
		}
		return times;
	}

	/** @return null if JCA is crippled (restricted to 128-bit) so we need 
	 * to use this class. */
	private static Provider getAesCtrProvider() {

//		PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
		byte[] k = new byte[32];
		byte[] iv = new byte[16];
//		aes.init(true, new ParametersWithIV(new KeyParameter(k), iv));

		final String algo = "AES/CTR/NoPadding";

		//CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(k), iv);
		try {
			Cipher c = Cipher.getInstance(algo, "BC");
			c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(k,"AES"), new IvParameterSpec(iv));
			return c.getProvider();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;


//		//return JceLoader.BouncyCastle;
//
//		final String algo = "AES/CTR/NoPadding";
//		Cipher c = null;
//		try {
//			byte[] key = k; // Test for whether 256-bit works.
//			byte[] iv = iv;
//
//			SecretKeySpec k = new SecretKeySpec(key, "AES");
//			IvParameterSpec IV = new IvParameterSpec(iv);
//
//
//			c = Cipher.getInstance("AES", JceLoader.BouncyCastle);
//			c.init(Cipher.ENCRYPT_MODE, k, IV);
//			// ^^^ resolve provider
//			return c.getProvider();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (NoSuchPaddingException e) {
//			e.printStackTrace();
//		} catch (InvalidAlgorithmParameterException e) {
//			e.printStackTrace();
//		} catch (InvalidKeyException e) {
//			e.printStackTrace();
//		}
//		return null;
	}

//			final Class<?> clazz = Rijndael.class;
//

//
//			Cipher c = Cipher.getInstance(algo);
//			c.init(Cipher.ENCRYPT_MODE, k, IV);
//			// ^^^ resolve provider
//			Provider provider = c.getProvider();
//			if (bcastle != null) {
//				// BouncyCastle provider is faster (in some configurations)
//				try {
//					Cipher bcastle_cipher = Cipher.getInstance(algo, bcastle);
//					bcastle_cipher.init(Cipher.ENCRYPT_MODE, k, IV);
//					Provider bcastle_provider = bcastle_cipher.getProvider();
//					if (provider != bcastle_provider) {
//						long time_def = benchmark(c, k, IV);
//						long time_bcastle = benchmark(bcastle_cipher, k, IV);
//						System.out.println(algo + " (" + provider + "): " + time_def + "ns");
//						System.out.println(algo + " (" + bcastle_provider + "): " + time_bcastle + "ns");
//						Logger.minor(clazz, algo + "/" + provider + ": " + time_def + "ns");
//						Logger.minor(clazz, algo + "/" + bcastle_provider + ": " + time_bcastle + "ns");
//						if (time_bcastle < time_def) {
//							provider = bcastle_provider;
//							c = bcastle_cipher;
//						}
//					}
//				} catch(GeneralSecurityException e) {
//					// ignore
//					Logger.warning(clazz, algo + "@" + bcastle + " benchmark failed", e);
//
//				} catch(Throwable e) {
//					// ignore
//					Logger.error(clazz, algo + "@" + bcastle + " benchmark failed", e);
//				}
//			}
//			c = Cipher.getInstance(algo, provider);
//			c.init(Cipher.ENCRYPT_MODE, k, IV);
//			c.doFinal(plaintext);
//			Logger.normal(Rijndael.class, "Using JCA: provider "+provider);
//			System.out.println("Using JCA cipher provider: "+provider);
//			return provider;
//		} catch (GeneralSecurityException e) {
//			Logger.warning(Rijndael.class, "Not using JCA as it is crippled (can't use 256-bit keys). Will use built-in encryption. ", e);
//			return null;
//		}
//	}
	
	/**
	 * Create a Rijndael instance.
	 * @param keysize The key size.
	 * @param blocksize The block size.
	 * @throws UnsupportedCipherException
	 */
	public Rijndael(int keysize, int blocksize) throws UnsupportedCipherException {
		if (! ((keysize == 128) ||
				(keysize == 192) ||
				(keysize == 256)))
			throw new UnsupportedCipherException("Invalid keysize");
		if (! ((blocksize == 128) ||
				(blocksize == 256)))
			throw new UnsupportedCipherException("Invalid blocksize");
		this.keysize=keysize;
		this.blocksize=blocksize;
	}

	// for Util.getCipherByName..  and yes, screw you too, java
	public Rijndael() {
		this.keysize   = 128;
		this.blocksize = 128;
	}

	@Override
	public final int getBlockSize() {
		return blocksize;
	}

	@Override
	public final int getKeySize() {
		return keysize;
	}

	@Override
	public final void initialize(byte[] key) {
		try {
			byte[] nkey=new byte[keysize>>3];
			System.arraycopy(key, 0, nkey, 0, nkey.length);
			sessionKey=Rijndael_Algorithm.makeKey(nkey, blocksize/8);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			Logger.error(this,"Invalid key");
		}
	}

	@Override
	public synchronized final void encipher(byte[] block, byte[] result) {
		if(block.length != blocksize/8)
			throw new IllegalArgumentException();
		Rijndael_Algorithm.blockEncrypt(block, result, 0, sessionKey, blocksize/8);
	}

	@Override
	public synchronized final void decipher(byte[] block, byte[] result) {
		if(block.length != blocksize/8)
			throw new IllegalArgumentException();
		Rijndael_Algorithm.blockDecrypt(block, result, 0, sessionKey, blocksize/8);
	}
}
