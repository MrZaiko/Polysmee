package io.github.polysmee.profile;

public class UserItemAutocomplete {
    private String username;
    private String pictureId;

    public UserItemAutocomplete(){
        //empty constructor
    }
    public UserItemAutocomplete(String username,String pictureId){
        this.username = username;
        this.pictureId = pictureId;
    }

    public String getUsername(){
        return username;
    }
    public String getPictureId(){
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }
    public  void setUsername(String username){
        this.username = username;
    }
}
