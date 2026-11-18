package es.upm.grise.subscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;


class SubscriptionServiceTest {

	private SubscriptionService service;
	private EmailService email = mock(EmailService.class);

	@BeforeEach 
	public void setup(){
		service = new SubscriptionService(email);
	} 

	@Test
	public void addSubscriberTest_valido() throws Exception{
		User user = new User(Delivery.EMAIL, "test@test.com");
		service.addSubscriber(user);
		assertTrue(service.getSubscribers().contains(user));
		assertEquals(1, service.getSubscribers().size());
	}

	@Test
	public void addSubscriberTest_usuarioNulo(){
		assertThrows(NullUserException.class, () -> service.addSubscriber(null));
	}

	@Test
	public void addSubscriberTest_yaExiste() throws Exception{
		User u = new User(Delivery.EMAIL, "test@test.com");
		service.addSubscriber(u);
		assertThrows(ExistingUserException.class, () -> service.addSubscriber(u));		
	}

	@Test
	public void addSubscriberTest_sinEmail(){
		assertThrows(UserDoesNotHaveEmailException.class, () -> service.addSubscriber(new User(Delivery.EMAIL, null)));
	}

	@Test
	public void removeSubscriberTest_valido() throws Exception{
		User user = new User(Delivery.EMAIL, "test@test.com");
		service.addSubscriber(user);
		service.removeSubscriber(user);
		assertFalse(service.getSubscribers().contains(user));
		assertEquals(0, service.getSubscribers().size());
	}

	@Test
	public void removeSubscriberTest_usuarioNulo(){
		assertThrows(NullUserException.class, () -> service.removeSubscriber(null));
	}

	@Test
	public void removeSubscriberTest_noExiste(){
		assertThrows(NonExistingUserException.class, () -> service.removeSubscriber(new User(Delivery.EMAIL, "test@test.com")));
	}

	@Test
	public void sendMessageTest_valido() throws Exception{
		User u1 = new User(Delivery.EMAIL, "1");
		User u2 = mock(User.class);
		when(u2.getDelivery()).thenReturn(Delivery.LOCAL);
		when(u2.getEmail()).thenReturn("2");
		User u3 = mock(User.class);
		when(u3.getDelivery()).thenReturn(Delivery.DO_NOT_DELIVER);
		when(u3.getEmail()).thenReturn("3");
		service.addSubscriber(u1); service.addSubscriber(u2); service.addSubscriber(u3);
		assertEquals(3, service.getSubscribers().size());
		assertEquals(1, service.sendMessage(new Message(0, "test")));
		verify(u2).saveMessage(any());
		verify(u3).saveMessage(any());
		verify(email).sendMessage(any(), any());
	}
}
