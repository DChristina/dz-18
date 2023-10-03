package api;
//
public class SuccessfulAuthorization {
    private String token;

    public SuccessfulAuthorization(String token){
        this.token = token;
    }

    public SuccessfulAuthorization(){
        super();
    }

    public String getToken() {
        return token;
    }


}
