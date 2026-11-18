package es.upm.grise.subscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;

class SubscriptionServiceTest {
	private Collection <User> subscribers;
	@SuppressWarnings("unused")
	private EmailService emailService;

	@Test
	public void addSubscriberTestLocal() {
		SubscriptionService s = new SubscriptionService(emailService);
		User u = new User(Delivery.LOCAL, null);
		try{
			s.addSubscriber(u);
		}catch (Exception e){}
		assertTrue(s.getSubscribers().contains(u));
	}
	@Test
	public void addSubscriberTestemailnull() {
		SubscriptionService s = new SubscriptionService(emailService);
		User u = new User(Delivery.EMAIL, null);
		UserDoesNotHaveEmailException exception = assertThrows(UserDoesNotHaveEmailException.class, () -> {
            s.addSubscriber(u);
        });
	}
	@Test
	public void addSubscriberTestUserNull() {
		SubscriptionService s = new SubscriptionService(emailService);
		User u = null;
		NullUserException exception = assertThrows(NullUserException.class, () -> {
            s.addSubscriber(u);
        });
	}

	@Test
	public void deleteSubscriberTestUserNull() {
		SubscriptionService s = new SubscriptionService(emailService);
		User u = null;
		NullUserException exception = assertThrows(NullUserException.class, () -> {
            s.removeSubscriber(u);
        });
	}

}
