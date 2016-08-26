package ir.taban.otp.api;

import android.util.Base64;

import ir.taban.otp.activity.DecidingClassActivity;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;

public class OkRSA {

    private File publicKeyFile, privateKeyFile;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private static JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public OkRSA(String publicKeyFile, String privateKeyFile) throws Exception {
        this.publicKeyFile = new File(publicKeyFile);
        this.privateKeyFile = new File(privateKeyFile);
    }

    public OkRSA() throws Exception {
        File data= DecidingClassActivity.context.getFilesDir();
        this.publicKeyFile = new File(data,"public.pem");
        this.privateKeyFile = new File(data,"private.pem");
        this.generateKeyPair();
        this.loadPair();
    }

    public void loadPair() throws Exception {
        PEMParser parser = new PEMParser(getPrivateKeyReader());
        KeyPair pair = converter.getKeyPair((PEMKeyPair) parser.readObject());
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
        parser.close();
    }

    public void loadPublic() throws Exception {
        PEMParser parser = new PEMParser(getPublicKeyReader());
        SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) parser.readObject();
        publicKey = converter.getPublicKey(publicKeyInfo);
        parser.close();
    }

    public void generateKeyPair() throws Exception {
        // Make a Generator Instance
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(512, new SecureRandom());

        // Generate key pair
        KeyPair pair = generator.generateKeyPair();

        // Write Public key
        JcaPEMWriter writer = new JcaPEMWriter(getPublicKeyWriter());
        writer.writeObject(pair.getPublic());
        writer.close();

        // Write Private Key
        writer = new JcaPEMWriter(getPrivateKeyWriter());
        writer.writeObject(pair);
        writer.close();
    }

    public byte[] encrypt(byte[] input) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, new SecureRandom());
        return cipher.doFinal(input);
    }

    public String encrypt(String input) throws Exception {
        byte[] input_bytes = input.getBytes();
        byte[] encrypt = encrypt(input_bytes);
        return Base64.encodeToString(encrypt, Base64.DEFAULT);
    }

    public byte[] decrypt(byte[] input) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(input);
    }

    public String decrypt(String input) throws Exception {
        byte[] input_bytes = Base64.decode(input, Base64.DEFAULT);
        byte[] decrypt = decrypt(input_bytes);
        return new String(decrypt);
    }

    public byte[] sign(byte[] input) throws Exception {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey, new SecureRandom());
        signature.update(input);
        return signature.sign();
    }

    public String sign(String input) throws Exception {
        return Base64.encodeToString(sign(input.getBytes()), Base64.DEFAULT);
    }

    public boolean verify(byte[] input, byte[] sig) throws Exception {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(publicKey);
        signature.update(input);
        return signature.verify(sig);
    }

    public boolean verify(String input, String sig) throws Exception {
        return verify(input.getBytes(), sig.getBytes());
    }

    public String getPublicKeyString() throws Exception{
        StringWriter w=new StringWriter();
        JcaPEMWriter writer = new JcaPEMWriter(w);
        writer.writeObject(publicKey);
        writer.close();
        return w.toString();
    }

    private Reader getPublicKeyReader() throws Exception {
        return new FileReader(publicKeyFile);
    }

    private Reader getPrivateKeyReader() throws Exception {
        return new FileReader(privateKeyFile);
    }

    private Writer getPublicKeyWriter() throws Exception {
        return new FileWriter(publicKeyFile);
    }

    private Writer getPrivateKeyWriter() throws Exception {
        return new FileWriter(privateKeyFile);
    }

}
