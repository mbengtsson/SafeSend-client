package se.teamgejm.safesend.pgp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Iterator;

import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.bcpg.CompressionAlgorithmTags;
import org.spongycastle.openpgp.PGPCompressedData;
import org.spongycastle.openpgp.PGPEncryptedData;
import org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedDataList;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPOnePassSignatureList;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyEncryptedData;
import org.spongycastle.openpgp.PGPSecretKeyRingCollection;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.spongycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.spongycastle.util.io.Streams;

import android.content.Context;
import android.util.Log;

public class PgpFileEncrypter {
	
	public static void decryptFile(
	        String inputFileName,
	        String keyFileName,
	        char[] passwd,
	        String defaultFileName)
	        throws IOException, NoSuchProviderException
	    {
	        InputStream in = new BufferedInputStream(PgpHelper.getContext().openFileInput(inputFileName));
	        InputStream keyIn = new BufferedInputStream(PgpHelper.getContext().openFileInput(keyFileName));
	        decryptFile(in, keyIn, passwd, defaultFileName);
	        keyIn.close();
	        in.close();
	    }

	    /**
	     * decrypt the passed in message stream
	     */
	    private static void decryptFile(
	        InputStream in,
	        InputStream keyIn,
	        char[]      passwd,
	        String      defaultFileName)
	        throws IOException, NoSuchProviderException
	    {
	        in = PGPUtil.getDecoderStream(in);
	        
	        try {
	            JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
	            PGPEncryptedDataList    enc;

	            Object                  o = pgpF.nextObject();
	            //
	            // the first object might be a PGP marker packet.
	            //
	            if (o instanceof PGPEncryptedDataList) {
	                enc = (PGPEncryptedDataList)o;
	            }
	            else {
	                enc = (PGPEncryptedDataList)pgpF.nextObject();
	            }
	            
	            //
	            // find the secret key
	            //
	            Iterator<?>                    it = enc.getEncryptedDataObjects();
	            PGPPrivateKey               sKey = null;
	            PGPPublicKeyEncryptedData   pbe = null;
	            PGPSecretKeyRingCollection  pgpSec = new PGPSecretKeyRingCollection(
	                PGPUtil.getDecoderStream(keyIn), new JcaKeyFingerprintCalculator());

	            while (sKey == null && it.hasNext()) {
	                pbe = (PGPPublicKeyEncryptedData)it.next();
	                
	                sKey = PgpUtils.findSecretKey(pgpSec, pbe.getKeyID(), passwd);
	            }
	            
	            if (sKey == null) {
	                throw new IllegalArgumentException("secret key for message not found.");
	            }
	    
	            InputStream         clear = pbe.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("SC").build(sKey));
	            
	            JcaPGPObjectFactory    plainFact = new JcaPGPObjectFactory(clear);
	            
	            Object              message = plainFact.nextObject();
	    
	            if (message instanceof PGPCompressedData) {
	                PGPCompressedData   cData = (PGPCompressedData)message;
	                JcaPGPObjectFactory    pgpFact = new JcaPGPObjectFactory(cData.getDataStream());
	                
	                message = pgpFact.nextObject();
	            }
	            
	            if (message instanceof PGPLiteralData) {
	                PGPLiteralData ld = (PGPLiteralData)message;

	                String outFileName = ld.getFileName();
	                if (outFileName.length() == 0) {
	                    outFileName = defaultFileName;
	                }

	                InputStream unc = ld.getInputStream();
	                OutputStream fOut = new BufferedOutputStream(PgpHelper.getContext().openFileOutput(outFileName, Context.MODE_PRIVATE));

	                Streams.pipeAll(unc, fOut);

	                fOut.close();
	            }
	            else if (message instanceof PGPOnePassSignatureList) {
	                throw new PGPException("encrypted message contains a signed message - not literal data.");
	            }
	            else {
	                throw new PGPException("message is not a simple encrypted file - type unknown.");
	            }

	            if (pbe.isIntegrityProtected())
	            {
	                if (!pbe.verify()) {
	                	Log.e("PgpFileEncrypter", "message failed integrity check");
	                }
	                else {
	                	Log.d("PgpFileEncrypter", "message integrity check passed");
	                }
	            }
	            else {
                	Log.d("PgpFileEncrypter", "no message integrity check");
	            }
	        }
	        catch (PGPException e)
	        {
	            System.err.println(e);
	            if (e.getUnderlyingException() != null) {
	                e.getUnderlyingException().printStackTrace();
	            }
	        }
	    }

	    public static void encryptFile(
	        String          outputFileName,
	       	String          inputFileName,
	        String          encKeyFileName,
	        boolean         armor,
	        boolean         withIntegrityCheck)
	        throws IOException, NoSuchProviderException, PGPException
	    {
	        OutputStream out = PgpHelper.getContext().openFileOutput(outputFileName, Context.MODE_PRIVATE);
	        PGPPublicKey encKey = PgpUtils.readPublicKey(PgpHelper.getContext(), encKeyFileName);
	        encryptFile(out, inputFileName, encKey, armor, withIntegrityCheck);
	        out.close();
	    }

	    private static void encryptFile(
	        OutputStream    out,
	        String          fileName,
	        PGPPublicKey    encKey,
	        boolean         armor,
	        boolean         withIntegrityCheck)
	        throws IOException, NoSuchProviderException
	    {
	        if (armor) {
	            out = new ArmoredOutputStream(out);
	        }

	        try {
	        	byte[] bytes = PgpUtils.compressFile(PgpHelper.getContext(), fileName, CompressionAlgorithmTags.ZIP);

	            PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
	                new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(withIntegrityCheck).setSecureRandom(new SecureRandom()).setProvider("SC"));

	            encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider("SC"));

	            OutputStream cOut = encGen.open(out, bytes.length);

	            cOut.write(bytes);
	            cOut.close();

	            if (armor) {
	                out.close();
	            }
	        }
	        catch (PGPException e) {
	            System.err.println(e);
	            if (e.getUnderlyingException() != null) {
	                e.getUnderlyingException().printStackTrace();
	            }
	        }
	    }

}
