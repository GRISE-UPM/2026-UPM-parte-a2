package es.upm.grise.subscriptionService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;

class SubscriptionServiceTest {

	private SubscriptionService subscriptionService;

	@BeforeEach
	void setUp() {
		subscriptionService = new SubscriptionService();
	}

	@Test
	public void smokeTest() {}

	@Test 
	public void testAddSubscriber() throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User user = new User(Delivery.LOCAL, "a@example.com");
		assertThrows(NullUserException.class, () -> subscriptionService.addSubscriber(null));
		verify(subscriptionService,times(1)).addSubscriber(user);
		assertThrows(ExistingUserException.class, () -> subscriptionService.addSubscriber(user));
		if(user.getDelivery() == Delivery.LOCAL) {
			if(user.getEmail() == null || user.getEmail().isEmpty()) {
				assertThrows(UserDoesNotHaveEmailException.class, () -> subscriptionService.addSubscriber(user));
			}
		}
		
	}

	@Test
	public void testRemoveSubscriber() throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException, NonExistingUserException {
		User user = new User(Delivery.LOCAL, "a@example.com");
		subscriptionService.addSubscriber(user);
		assertThrows(NullUserException.class, () -> subscriptionService.removeSubscriber(null));
		assertThrows(NonExistingUserException.class, () -> subscriptionService.removeSubscriber(new User(Delivery.LOCAL, "b@example.com")));
		subscriptionService.removeSubscriber(user);
		assertTrue(subscriptionService.getSubscribers().isEmpty());
	}

	@Test 
	public void testSendMessage() throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User user1 = new User(Delivery.LOCAL, "a@example.com");
		User user2 = new User(Delivery.DO_NOT_DELIVER, "b@example.com");
		User user3 = new User(Delivery.EMAIL, "c@example.com");

		subscriptionService.addSubscriber(user1);
		subscriptionService.addSubscriber(user2);
		subscriptionService.addSubscriber(user3);

		Message message = new Message(1, "Body");
		assertTrue(subscriptionService.sendMessage(message) == 1);
		assertTrue(!user1.messages.isEmpty());
		assertTrue(!user2.messages.isEmpty());

	}

}
