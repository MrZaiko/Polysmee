package io.github.polysmee;


import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import io.github.polysmee.profile.UserItemAutocomplete;

@RunWith(AndroidJUnit4.class)
public class UserItemAutocompleteTest {

    @Test
    public void nonEmptyConstructorTest(){
        UserItemAutocomplete userItemAutocomplete = new UserItemAutocomplete("Kozak","PrettyPicture");
        assertEquals("Kozak",userItemAutocomplete.getUsername());
        assertEquals("PrettyPicture",userItemAutocomplete.getPictureId());
    }

    @Test
    public void settersTest(){
        UserItemAutocomplete userItemAutocomplete = new UserItemAutocomplete();
        String string = "String";
        userItemAutocomplete.setPictureId(string);
        userItemAutocomplete.setUsername(string);
        assertEquals(string,userItemAutocomplete.getUsername());
        assertEquals(string,userItemAutocomplete.getPictureId());
        assertEquals(string,userItemAutocomplete.toString());
    }

}
