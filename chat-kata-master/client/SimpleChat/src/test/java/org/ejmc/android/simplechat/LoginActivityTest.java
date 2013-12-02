package org.ejmc.android.simplechat;

/**
 * Created with IntelliJ IDEA.
 * User: bqnieves
 * Date: 2/12/13
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
 */

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {

    @Test
    public void shouldHaveProperAppName() throws Exception{
        String appName = new ChatActivity().getResources().getString(R.string.app_name);
        assertThat(appName, equalTo("SimpleChat"));
    }

    @Test
    public void hasACharacterA() throws Exception{
        boolean res = new LoginActivity().someCharacter(" ");
        assertFalse(res);
    }

    @Test
    public void hasACharacterB() throws Exception{
        boolean res = new LoginActivity().someCharacter(" asdf");
        assertFalse(false);
    }

    @Test
    public void hasACharacterC() throws Exception{
        boolean res = new LoginActivity().someCharacter(" \n \t");
        assertFalse(res);
    }

    @Test
    public void signUpCorrectA() throws Exception{
        boolean res = new LoginActivity().checkPass(" ", " ");
        assertFalse(res);

    }




}
