package io.github.polysmee.profile;

public class UserItemAutocomplete {
    private final String username;
    private final String pictureId;

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
}
