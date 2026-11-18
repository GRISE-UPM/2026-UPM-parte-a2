package es.upm.grise.subscriptionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubscriptionServiceTest {

	private EmailService emailService;
	private SubscriptionService service;

	@BeforeEach
	void setUp() {
		emailService = mock(EmailService.class);
		service = new SubscriptionService(emailService);
	}

	@Test
	void testInicializacion() {
		assertTrue(service.getSubscribers().isEmpty());
	}

	@Test
	void testAddSubscriber() throws ExistingUserException, NullUserException, UserDoesNotHaveEmailException {
		User user = mock(User.class);
		when(user.getEmail()).thenReturn("1");
		when(user.getDelivery()).thenReturn(Delivery.EMAIL);

		service.addSubscriber(user);

		assertTrue(service.getSubscribers().contains(user));
	}

	@Test
	void testAddSubscriberNull() throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		assertThrows(NullUserException.class, () -> service.addSubscriber(null));
	}

	@Test
	void testAddSubscriberYaSuscrito() throws ExistingUserException, NullUserException, UserDoesNotHaveEmailException {
		User user = mock(User.class);
		when(user.getEmail()).thenReturn("2");
		when(user.getDelivery()).thenReturn(Delivery.EMAIL);

		service.addSubscriber(user);

		assertThrows(ExistingUserException.class, () -> service.addSubscriber(user));
	}

	@Test
	void testAddSubscriberSinEmail() throws UserDoesNotHaveEmailException, NullUserException, ExistingUserException {
		User user = mock(User.class);
		when(user.getEmail()).thenReturn(null);
		when(user.getDelivery()).thenReturn(Delivery.EMAIL);

		assertThrows(UserDoesNotHaveEmailException.class, () -> service.addSubscriber(user));
	}

	@Test
	void testAddSubscriberLocalSinEmail() throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User user = mock(User.class);
		when(user.getDelivery()).thenReturn(Delivery.LOCAL);

		service.addSubscriber(user);

		assertTrue(service.getSubscribers().contains(user));
	}

	@Test
	void testRemoveSubscriber() throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException, NonExistingUserException {
		User user = mock(User.class);
		when(user.getDelivery()).thenReturn(Delivery.LOCAL);
		service.addSubscriber(user);

		service.removeSubscriber(user);

		assertFalse(service.getSubscribers().contains(user));
	}

	@Test
	void testRemoveSubscriberNull() throws NullUserException, NonExistingUserException {
		assertThrows(NullUserException.class, () -> service.removeSubscriber(null));
	}

	@Test
	void testRemoveSubscriberNoSuscrito() throws NonExistingUserException, NullUserException {
		User user = mock(User.class);

		assertThrows(NonExistingUserException.class, () -> service.removeSubscriber(user));
	}

	@Test
	void testSendMessageEmail() throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User user = mock(User.class);
		Message message = mock(Message.class);
		when(user.getEmail()).thenReturn("3");
		when(user.getDelivery()).thenReturn(Delivery.EMAIL);
		service.addSubscriber(user);

		service.sendMessage(message);

		verify(emailService).sendMessage(user, message);
	}

	@Test
	void testSendMessageLocal() throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User user = mock(User.class);
		Message message = mock(Message.class);
		when(user.getDelivery()).thenReturn(Delivery.LOCAL);
		service.addSubscriber(user);

		service.sendMessage(message);

		verify(user).saveMessage(message);
	}

	@Test
	void testSendMessageDoNotDeliver() throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User user = mock(User.class);
		Message message = mock(Message.class);
		when(user.getEmail()).thenReturn("1");
		when(user.getDelivery()).thenReturn(Delivery.DO_NOT_DELIVER);
		service.addSubscriber(user);

		int descartados = service.sendMessage(message);

		verify(user).saveMessage(any(Message.class));
		assertEquals(1, descartados);
	}
}
