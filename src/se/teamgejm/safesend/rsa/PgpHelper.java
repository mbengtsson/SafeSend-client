package se.teamgejm.safesend.rsa;

import android.content.Context;
import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.bcpg.HashAlgorithmTags;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ElGamalParameterSpec;
import org.spongycastle.openpgp.*;
import org.spongycastle.openpgp.operator.PGPDigestCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.spongycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.*;
import java.util.Date;

public class PgpHelper {

    public static final String PASSWORD = "password";
    public static final String IDENTITY = "john.doe@example.com";
    public static final String PRIME_MODULUS =
            "EF07B0F39662DC8600224E46AB8BE8CB72E552D52E88013D20EC039A0697ED9AAD018B16F0B910D4AD54437B8585AAA4EAE0CE216E31F50EDF0CD05DAF5E02A7" +
                    "3D399C91B38220EC3B62C42D1CF6BF06378533A70C1F8F4F4416DD542213D3432412125FDBFF7B9473CE6F8812D860E66282C9F34C1774D1EA57D54DADDF7E37A12C4A6AD5B4A30128C29D27D03B65" +
                    "35C0F7A8AF857E18ECAB992984E6D546918AAACB971A2AC2C2E7AF79A9547979E6342DB7443985E5F7EDF6F9F22B600EEB42CB84A5F1ACD76E213C52E3052DAE1A9119801CFA28E6EFD4F6BC35FA06" +
                    "C8724D78A96AF054826C0BF865D0EC5F6F4D31C1D3F7CF2FE6F16AF267A7BA04753AEF420D4D8C36BCE8D9694814B9E9C3DF468064EB5636405C71CA9D8D50D36570B42639C9C2C02FB3A3D0C6B28D" +
                    "D200B0AF164C621D60B12E35E4D00129C8900F6EFDBB49FF34DD64CB13CD4087A7F84FEFD77D4E8099C2B804BA643EAFCA66D1F02BD09AE44AC83A5149F60711B7B108C01D53FF15FA59B36BE62A87" +
                    "0F163F5063CEE103B377808343AFBD32271199E26D93734011BED2305EDE2E841EAD512E23B8C9B8CD4D398C7B4C8B76B355CC150B66B8EB7779E2CA519E10E45D0FB138676850C56F23DB135F546D" +
                    "364B92BC1C9423E089D30D4D57D27D7885EE14AE135A488C0542C3719FBEF46F4BB5FB53A28DA26DDF84C8BC55348A8AA473C7CE3F";
    public static final String BASE_GENERATOR = "2";

    public static void generateKeyPair (Context context, String identity, String password) {

        Security.addProvider(new BouncyCastleProvider());

        try {
            KeyPair dsaKeyPair = generateDsaKeyPair(1024);
            KeyPair elGamalKeyPair = generateElGamalKeyPair(PRIME_MODULUS, BASE_GENERATOR);

            FileOutputStream privKey = context.openFileOutput("private.asc", Context.MODE_PRIVATE);
            FileOutputStream pubKey = context.openFileOutput("public.asc", Context.MODE_PRIVATE);

            exportKeyPair(privKey, pubKey, dsaKeyPair, elGamalKeyPair, identity, password.toCharArray(), true);

        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void exportKeyPair (OutputStream secretOut, OutputStream publicOut, KeyPair dsaKp, KeyPair elgKp,
                                       String identity, char[] passPhrase, boolean armor) throws IOException, InvalidKeyException,
            NoSuchProviderException, SignatureException, PGPException {

        // Convert binary non-printable bytes to something text friendly.
        if (armor) {
            secretOut = new ArmoredOutputStream(secretOut);
        }

        PGPKeyPair dsaKeyPair = new JcaPGPKeyPair(PGPPublicKey.DSA, dsaKp, new Date());
        PGPKeyPair elgKeyPair = new JcaPGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, elgKp, new Date());
        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, dsaKeyPair,
                identity, sha1Calc, null, null, new JcaPGPContentSignerBuilder(dsaKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1), new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1Calc).setProvider("SC").build(passPhrase));

        keyRingGen.addSubKey(elgKeyPair);

        keyRingGen.generateSecretKeyRing().encode(secretOut);

        secretOut.close();

        if (armor) {
            publicOut = new ArmoredOutputStream(publicOut);
        }

        keyRingGen.generatePublicKeyRing().encode(publicOut);

        publicOut.close();
    }

    private static final KeyPair generateDsaKeyPair (int keySize) throws NoSuchAlgorithmException,
            NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA", "SC");
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    private static final KeyPair generateElGamalKeyPair (String primeModulous, String baseGenerator) throws Exception {

        BigInteger p = new BigInteger(primeModulous, 16);
        BigInteger g = new BigInteger(baseGenerator, 16);

        ElGamalParameterSpec paramSpecs = new ElGamalParameterSpec(p, g);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ELGAMAL", "SC");
        keyPairGenerator.initialize(paramSpecs);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

}
