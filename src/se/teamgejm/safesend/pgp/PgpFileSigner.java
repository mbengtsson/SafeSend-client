package se.teamgejm.safesend.pgp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.Iterator;

import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.bcpg.BCPGOutputStream;
import org.spongycastle.openpgp.PGPCompressedData;
import org.spongycastle.openpgp.PGPCompressedDataGenerator;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPLiteralDataGenerator;
import org.spongycastle.openpgp.PGPOnePassSignature;
import org.spongycastle.openpgp.PGPOnePassSignatureList;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKey;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureGenerator;
import org.spongycastle.openpgp.PGPSignatureList;
import org.spongycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.spongycastle.openpgp.PGPSignatureSubpacketVector;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.spongycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import android.content.Context;
import android.util.Log;

public class PgpFileSigner {
	
	public static byte[] signPublicKey(PGPSecretKey secretKey, String secretKeyPass, PGPPublicKey keyToBeSigned,
			String notationName, String notationValue) throws Exception {

		PGPPrivateKey pgpPrivKey = secretKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("SC").build(secretKeyPass.toCharArray()));

		PGPSignatureGenerator sGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(), PGPUtil.SHA1).setProvider("SC"));

		sGen.init(PGPSignature.DIRECT_KEY, pgpPrivKey);

		PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();

		boolean isHumanReadable = true;

		spGen.setNotationData(true, isHumanReadable, notationName, notationValue);

		PGPSignatureSubpacketVector packetVector = spGen.generate();

		sGen.setHashedSubpackets(packetVector);

		return PGPPublicKey.addCertification(keyToBeSigned, sGen.generate()).getEncoded();
	}

	public static void signFile(File file, InputStream secretKey, OutputStream out, char[] pass, boolean armor)
			throws IOException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, SignatureException {
		if (armor) {
			out = new ArmoredOutputStream(out);
		}

		PGPSecretKey pgpSec = PgpUtils.readSecretKey(secretKey);

		PGPPrivateKey pgpPrivKey = pgpSec.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("SC").build(pass));
		PGPSignatureGenerator sGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(pgpSec.getPublicKey().getAlgorithm(), PGPUtil.SHA1).setProvider("SC"));

		sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

		Iterator<?> it = pgpSec.getPublicKey().getUserIDs();
		if (it.hasNext()) {
			PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();

			spGen.setSignerUserID(false, (String) it.next());
			sGen.setHashedSubpackets(spGen.generate());
		}

		PGPCompressedDataGenerator cGen = new PGPCompressedDataGenerator(
				PGPCompressedData.ZLIB);

		BCPGOutputStream bOut = new BCPGOutputStream(cGen.open(out));

		sGen.generateOnePassVersion(false).encode(bOut);

		PGPLiteralDataGenerator lGen = new PGPLiteralDataGenerator();
		OutputStream lOut = lGen.open(bOut, PGPLiteralData.BINARY, file);
		FileInputStream fIn = new FileInputStream(file);
		int ch;

		while ((ch = fIn.read()) >= 0) {
			lOut.write(ch);
			sGen.update((byte) ch);
		}

		lGen.close();

		sGen.generate().encode(bOut);

		cGen.close();

		if (armor) {
			out.close();
		}
		
		secretKey.close();
	}

	public static void verifyFile(String signedFile, PGPPublicKeyRing keyIn) throws Exception {

		InputStream in = PgpHelper.getContext().openFileInput(signedFile);

		in = PGPUtil.getDecoderStream(in);

		JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(in);

		PGPCompressedData c1 = (PGPCompressedData) pgpFact.nextObject();

		pgpFact = new JcaPGPObjectFactory(c1.getDataStream());

		PGPOnePassSignatureList p1 = (PGPOnePassSignatureList) pgpFact.nextObject();

		PGPOnePassSignature ops = p1.get(0);

		PGPLiteralData p2 = (PGPLiteralData) pgpFact.nextObject();

		InputStream dIn = p2.getInputStream();
		int ch;

		PGPPublicKey key = keyIn.getPublicKey(ops.getKeyID());
		FileOutputStream out = PgpHelper.getContext().openFileOutput(p2.getFileName(), Context.MODE_PRIVATE);

		ops.init(new JcaPGPContentVerifierBuilderProvider().setProvider("SC"), key);

		while ((ch = dIn.read()) >= 0) {
			ops.update((byte) ch);
			out.write(ch);
		}

		out.close();

		PGPSignatureList p3 = (PGPSignatureList) pgpFact.nextObject();

		if (ops.verify(p3.get(0))) {
			Log.d("PgpFileSigner", "Correct signature.");
		} else {
			Log.d("PgpFileSigner", "Verification failed.");
		}
		
		in.close();
	}

}
