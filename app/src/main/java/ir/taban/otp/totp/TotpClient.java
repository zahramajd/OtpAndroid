package ir.taban.otp.totp;

import ir.taban.otp.api.OkRSA;
import okhttp3.*;

public class TotpClient {

    private String url = "";
    private String seed = "";
    private OkHttpClient client = new OkHttpClient();
    TotpToken token;

    public TotpClient() {
        this.url = "http://otp.pi0.ir";
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
        token= new TotpToken("", "", getSeed(), 30, 6);
    }

    public String generateToken() {
        return token.generateOtp();
    }

    private String request(String path, RequestBody body) {
        Request request = new Request.Builder().url(url + path).post(body).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean requestSeed(String email,String password) throws Exception {
        // Make a key pair
        OkRSA rsa = new OkRSA();
        String publicKey = rsa.getPublicKeyString();

        // Send request
        String response = request("/seed.php",
                new FormBody.Builder()
                        .add("email", email)
                        .add("pwd", password)
                        .add("key", publicKey)
                        .build());

        // Decrypt response
        try {
            String responseDecrypted = rsa.decrypt(response);
            this.setSeed(responseDecrypted);
            return true;
        } catch (Exception e) {
            throw new Exception(response);
        }
    }

    public int getRemaining() {
        return token.getRemaining();
    }

}