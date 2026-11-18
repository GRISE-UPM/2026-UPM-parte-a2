package es.upm.grise.subscriptionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;

class SubscriptionServiceTest {

	private EmailService emailService;
	private SubscriptionService subscriptionService;

	@BeforeEach
	void setUp() {
		emailService = mock(EmailService.class);
		subscriptionService = new SubscriptionService(emailService);
	}

	@Test
	void subscribersListIsEmptyOnInitialization() {
		assertNotNull(subscriptionService.getSubscribers());
		assertTrue(subscriptionService.getSubscribers().isEmpty());
	}

	@Test
	void addSubscriberAddsValidUser()
			throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User user = new User(Delivery.EMAIL, "user@example.com");

		subscriptionService.addSubscriber(user);

		assertEquals(1, subscriptionService.getSubscribers().size());
		assertTrue(subscriptionService.getSubscribers().contains(user));
	}

	@Test
	void addSubscriberThrowsNullUserExceptionWhenUserIsNull() {
		assertThrows(NullUserException.class, () -> subscriptionService.addSubscriber(null));
	}

	@Test
	void addSubscriberThrowsExistingUserExceptionWhenAlreadySubscribed()
			throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User user = new User(Delivery.EMAIL, "user@example.com");
		subscriptionService.addSubscriber(user);

		assertThrows(ExistingUserException.class, () -> subscriptionService.addSubscriber(user));
	}

	@Test
	void addSubscriberThrowsUserDoesNotHaveEmailExceptionWhenEmailIsNullAndNotLocal() {
		User emailUser = new User(Delivery.EMAIL, null);
		User doNotDeliverUser = new User(Delivery.DO_NOT_DELIVER, null);

		assertThrows(UserDoesNotHaveEmailException.class, () -> subscriptionService.addSubscriber(emailUser));
		assertThrows(UserDoesNotHaveEmailException.class, () -> subscriptionService.addSubscriber(doNotDeliverUser));
	}

	@Test
	void addSubscriberAllowsNullEmailWhenDeliveryIsLocal()
			throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User localUser = new User(Delivery.LOCAL, null);

		subscriptionService.addSubscriber(localUser);

		assertTrue(subscriptionService.getSubscribers().contains(localUser));
	}

	@Test
	void removeSubscriberRemovesExistingUser()
			throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException, NonExistingUserException {
		User user = new User(Delivery.EMAIL, "user@example.com");
		subscriptionService.addSubscriber(user);

		subscriptionService.removeSubscriber(user);

		assertFalse(subscriptionService.getSubscribers().contains(user));
		assertTrue(subscriptionService.getSubscribers().isEmpty());
	}

	@Test
	void removeSubscriberThrowsNullUserExceptionWhenUserIsNull() {
		assertThrows(NullUserException.class, () -> subscriptionService.removeSubscriber(null));
	}

	@Test
	void removeSubscriberThrowsNonExistingUserExceptionWhenNotSubscribed() {
		User user = new User(Delivery.EMAIL, "user@example.com");

		assertThrows(NonExistingUserException.class, () -> subscriptionService.removeSubscriber(user));
	}

	@Test
	void sendMessageWithNoSubscribersReturnsZero() {
		Message message = new Message(1L, "Hola");

		int discarded = subscriptionService.sendMessage(message);

		assertEquals(0, discarded);
		verifyNoInteractions(emailService);
	}

	@Test
	void sendMessageSendsViaEmailServiceForEmailDelivery()
			throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User user = new User(Delivery.EMAIL, "user@example.com");
		subscriptionService.addSubscriber(user);
		Message message = new Message(1L, "Hola");

		int discarded = subscriptionService.sendMessage(message);

		assertEquals(0, discarded);
		verify(emailService).sendMessage(user, message);
	}

	@Test
	void sendMessageSavesMessageForLocalDelivery()
			throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User localUser = mock(User.class);
		when(localUser.getDelivery()).thenReturn(Delivery.LOCAL);
		subscriptionService.addSubscriber(localUser);
		Message message = new Message(2L, "Mensaje local");

		int discarded = subscriptionService.sendMessage(message);

		assertEquals(0, discarded);
		verify(localUser).saveMessage(message);
		verifyNoInteractions(emailService);
	}

	@Test
	void sendMessageSavesNoticeAndCountsDiscardedForDoNotDeliver()
			throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User doNotDeliverUser = mock(User.class);
		when(doNotDeliverUser.getDelivery()).thenReturn(Delivery.DO_NOT_DELIVER);
		when(doNotDeliverUser.getEmail()).thenReturn("user@example.com");
		subscriptionService.addSubscriber(doNotDeliverUser);
		Message message = new Message(3L, "Mensaje descartado");

		int discarded = subscriptionService.sendMessage(message);

		assertEquals(1, discarded);
		verify(doNotDeliverUser).saveMessage(any(Message.class));
		verifyNoInteractions(emailService);
	}

	@Test
	void sendMessageHandlesMixedDeliveryTypesAndReturnsTotalDiscarded()
			throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		User emailUser = new User(Delivery.EMAIL, "email@example.com");
		User localUser = mock(User.class);
		when(localUser.getDelivery()).thenReturn(Delivery.LOCAL);
		User doNotDeliver1 = mock(User.class);
		when(doNotDeliver1.getDelivery()).thenReturn(Delivery.DO_NOT_DELIVER);
		when(doNotDeliver1.getEmail()).thenReturn("d1@example.com");
		User doNotDeliver2 = mock(User.class);
		when(doNotDeliver2.getDelivery()).thenReturn(Delivery.DO_NOT_DELIVER);
		when(doNotDeliver2.getEmail()).thenReturn("d2@example.com");

		subscriptionService.addSubscriber(emailUser);
		subscriptionService.addSubscriber(localUser);
		subscriptionService.addSubscriber(doNotDeliver1);
		subscriptionService.addSubscriber(doNotDeliver2);

		Message message = new Message(4L, "Broadcast");

		int discarded = subscriptionService.sendMessage(message);

		assertEquals(2, discarded);
		verify(emailService).sendMessage(emailUser, message);
		verify(localUser).saveMessage(message);
		verify(doNotDeliver1).saveMessage(any(Message.class));
		verify(doNotDeliver2).saveMessage(any(Message.class));
	}

}
