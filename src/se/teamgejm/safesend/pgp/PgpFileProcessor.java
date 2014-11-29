package se.teamgejm.safesend.pgp;

import android.content.Context;
import android.util.Log;
import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.bcpg.CompressionAlgorithmTags;
import org.spongycastle.openpgp.*;
import org.spongycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.spongycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.spongycastle.util.io.Streams;

import java.io.*;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Iterator;

/**
 * A file processor for encrypting and decrypting files.
 *
 * @author Gustav
 */
public class PgpFileProcessor {

    private static final String TAG = "PgpFileProcessor";

    /**
     * Decrypts a file.
     *
     * @param inputFileName
     * @param keyFileName
     * @param passwd
     * @param defaultFileName
     *
     * @throws IOException
     * @throws NoSuchProviderException
     */
    public static void decryptFile (
            String inputFileName,
            String keyFileName,
            char[] passwd,
            String defaultFileName)
            throws IOException, NoSuchProviderException, PGPException {
        InputStream in = new BufferedInputStream(PgpHelper.getContext().openFileInput(inputFileName));
        InputStream keyIn = new BufferedInputStream(PgpHelper.getContext().openFileInput(keyFileName));
        decryptFile(in, keyIn, passwd, defaultFileName);
        keyIn.close();
        in.close();
    }

    private static void decryptFile (
            InputStream in,
            InputStream keyIn,
            char[] passwd,
            String defaultFileName)
            throws IOException, NoSuchProviderException, PGPException {
        in = PGPUtil.getDecoderStream(in);

        try {
            JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
            PGPEncryptedDataList enc;

            Object o = pgpF.nextObject();
            //
            // the first object might be a PGP marker packet.
            //
            if (o instanceof PGPEncryptedDataList) {
                enc = (PGPEncryptedDataList) o;
            }
            else {
                enc = (PGPEncryptedDataList) pgpF.nextObject();
            }

            //
            // find the secret key
            //
            Iterator<?> it = enc.getEncryptedDataObjects();
            PGPPrivateKey sKey = null;
            PGPPublicKeyEncryptedData pbe = null;
            PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
                    PGPUtil.getDecoderStream(keyIn), new JcaKeyFingerprintCalculator());

            while (sKey == null && it.hasNext()) {
                pbe = (PGPPublicKeyEncryptedData) it.next();

                sKey = PgpUtils.findSecretKey(pgpSec, pbe.getKeyID(), passwd);
            }

            if (sKey == null) {
                //throw new IllegalArgumentException("secret key for message not found.");
                throw new PGPException("[Decryption failed.]");
            }

            InputStream clear = pbe.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider("SC").build(sKey));

            JcaPGPObjectFactory plainFact = new JcaPGPObjectFactory(clear);

            Object message = plainFact.nextObject();

            if (message instanceof PGPCompressedData) {
                PGPCompressedData cData = (PGPCompressedData) message;
                JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(cData.getDataStream());

                message = pgpFact.nextObject();
            }

            if (message instanceof PGPLiteralData) {
                PGPLiteralData ld = (PGPLiteralData) message;

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

            if (pbe.isIntegrityProtected()) {
                if (!pbe.verify()) {
                    Log.e(TAG, "message failed integrity check");
                    throw new PGPException("[Message failed integrity check.]");
                }
                else {
                    Log.d(TAG, "message integrity check passed");
                }
            }
            else {
                Log.d(TAG, "no message integrity check");
            }
        }
        catch (PGPException e) {
            //System.err.println(e);
            throw new PGPException(e.getMessage(), e);
            //            if (e.getUnderlyingException() != null) {
            //                e.getUnderlyingException().printStackTrace();
            //            }
        }
    }

    /**
     * Encrypts a file.
     *
     * @param outputFileName
     * @param inputFileName
     * @param publicKeyIn
     * @param armor
     * @param withIntegrityCheck
     *
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws PGPException
     */
    public static void encryptFile (
            String outputFileName,
            String inputFileName,
            InputStream publicKeyIn,
            boolean armor,
            boolean withIntegrityCheck)
            throws IOException, NoSuchProviderException, PGPException {
        OutputStream out = PgpHelper.getContext().openFileOutput(outputFileName, Context.MODE_PRIVATE);
        PGPPublicKey publicKey = PgpUtils.readPublicKey(publicKeyIn);
        encryptFile(out, inputFileName, publicKey, armor, withIntegrityCheck);
        out.close();
        publicKeyIn.close();
    }

    private static void encryptFile (
            OutputStream out,
            String fileName,
            PGPPublicKey encKey,
            boolean armor,
            boolean withIntegrityCheck)
            throws IOException, NoSuchProviderException {
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
