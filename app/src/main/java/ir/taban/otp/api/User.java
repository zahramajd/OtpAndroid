package ir.taban.otp.api;

import ir.taban.otp.totp.TotpClient;

public class User {

    private String email = "";
    private String password = "";
    TotpClient client;

    public User(String email, String password){
        this.email = email;
        this.password = password;
        client = new TotpClient();
    }

    public User (Data data){
        this.email=data.email;
        this.password=data.password;
        this.password=data.password;
        client=new TotpClient();
        client.setSeed(data.seed);
    }

    public Data toData(){
        Data d=new Data();
        d.email=this.email;
        d.password=this.password;
        d.seed=client.getSeed();
        return d;
    }

    public void login() throws Exception{
        client.requestSeed(email, password);
    }

    public String getEmail() {
        return email;
    }

    public String getCurrentOTP() {
        return client.generateToken();
    }

    public int getRemaining() {
        return client.getRemaining();
    }

    public class Data{
        String email;
        String password;
        String seed;
    }

}


