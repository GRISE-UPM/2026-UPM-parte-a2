package es.upm.grise.subscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;

class SubscriptionServiceTest {

	@Test
	public void smokeTest() {}

	@Test
	void constructorStartsEmpty() {
		SubscriptionService service = new SubscriptionService(mock(EmailService.class));

		assertTrue(service.getSubscribers().isEmpty());
	}

	@Test
	 public void addSubscriber() throws Exception {
        SubscriptionService service = new SubscriptionService(null);
        User user = new User(Delivery.EMAIL, "addtest@test.com");

        service.addSubscriber(user);

        assertTrue(service.getSubscribers().contains(user));
    }

	@Test
	public void addSubscriberNull() throws Exception {
		SubscriptionService service = new SubscriptionService(null);
		
		assertThrows(NullUserException.class, () -> service.addSubscriber(null));
	}

	@Test
	public void addSubscriberExisting() throws Exception {
		SubscriptionService service = new SubscriptionService(null);
		User user = new User(Delivery.EMAIL, "addtest@test.com");
		service.addSubscriber(user);

		assertThrows(ExistingUserException.class, () -> service.addSubscriber(user));
	}

	@Test
	public void addSubscriberNoEmail() throws Exception {
		SubscriptionService service = new SubscriptionService(null);
		User user = new User(Delivery.EMAIL, null);

		assertThrows(UserDoesNotHaveEmailException.class, () -> service.addSubscriber(user));
	}

	@Test
	public void removeSubscriber() throws Exception {
		SubscriptionService service = new SubscriptionService(null);
		User user = new User(Delivery.EMAIL, "remtest@test.com");

		service.addSubscriber(user);
		service.removeSubscriber(user);

		assertTrue(!service.getSubscribers().contains(user));
	}
	
	@Test
	public void removeSubscriberNull() throws Exception {
		SubscriptionService service = new SubscriptionService(null);
		
		assertThrows(NullUserException.class, () -> service.removeSubscriber(null));
	}

	@Test
	public void removeSubscriberNonExisting() throws Exception {
		SubscriptionService service = new SubscriptionService(null);
		User user = new User(Delivery.EMAIL, "remtest@test.com");

		assertThrows(NonExistingUserException.class, () -> service.removeSubscriber(user));
	}

	@Test
	public void sendMessage() throws Exception {
		SubscriptionService service = new SubscriptionService((u, m) -> {});
		User user = new User(Delivery.EMAIL, "msgtest@test.com");
	
		service.addSubscriber(user);

		assertEquals(0, service.sendMessage(new Message(1, "Hello")));
	}

	@Test
    public void sendMessageDoNotDeliver() throws Exception {
        SubscriptionService service = new SubscriptionService((u, m) -> {});
        User user = new User(Delivery.DO_NOT_DELIVER, "test@test.com");

        service.addSubscriber(user);

        assertEquals(1, service.sendMessage(new Message(1, "Hello")));
    }

	@Test
	public void sendMessageLocal() throws Exception {
		SubscriptionService service = new SubscriptionService((u, m) -> {});
		User user = new User(Delivery.LOCAL, "test@test.com");

		service.addSubscriber(user);

		assertEquals(0, service.sendMessage(new Message(1, "Hello")));
	}

	@Test 
	public void sendMessageMultipleUsers() throws Exception {
		SubscriptionService service = new SubscriptionService((u, m) -> {});
		User user1 = new User(Delivery.EMAIL, "test1@test.com");
		User user2 = new User(Delivery.LOCAL, "test2@test.com");
		User user3 = new User(Delivery.DO_NOT_DELIVER, "test3@test.com");

		service.addSubscriber(user1);
		service.addSubscriber(user2);
		service.addSubscriber(user3);

		assertEquals(1, service.sendMessage(new Message(1, "Hello")));
	}
}
