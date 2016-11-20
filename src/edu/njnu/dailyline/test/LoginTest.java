package edu.njnu.dailyline.test;

import java.util.List;

import edu.njnu.dailyline.domain.PushMsg;
import edu.njnu.dailyline.services.LoginService;
import edu.njnu.dailyline.services.PushMshService;
import android.test.AndroidTestCase;

public class LoginTest extends AndroidTestCase {

	public void testLogin() throws Exception {
		// LoginService loginService =new LoginService(getContext());

		// boolean r = loginService.login("wj", "123");
		// assertEquals(true, r);
	}

	public void getPushMsg() throws Exception {

		PushMshService s = new PushMshService(getContext());
		List<PushMsg> pushMsg = s.getPushMsg();
		assertEquals(2, pushMsg.size());
	}
}
