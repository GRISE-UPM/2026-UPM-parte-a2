package es.upm.grise.subscriptionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;

class SubscriptionServiceTest {

	@Mock
	private EmailService emailService;

	private SubscriptionService service;

	@BeforeEach
	public void setUp() {
		service = new SubscriptionService(emailService);
	}

	@Test
	public void addSubscriber_NullUser_ThrowsException() {
		assertThrows(NullUserException.class, () -> {
			service.addSubscriber(null);
		});
	}

	@Test
	public void addSubscriber_ExistingUser_ThrowsException() throws Exception {
		User user = mock(User.class);
		when(user.getEmail()).thenReturn("test@upm.es");

		service.addSubscriber(user);

		assertThrows(ExistingUserException.class, () -> {
			service.addSubscriber(user);
		});
	}

	@Test
	public void addSubscriber_NoEmailAndNotLocal_ThrowsException() {
		User user = mock(User.class);
		when(user.getEmail()).thenReturn(null);
		when(user.getDelivery()).thenReturn(Delivery.EMAIL);

		assertThrows(UserDoesNotHaveEmailException.class, () -> {
			service.addSubscriber(user);
		});
	}

	@Test
	public void addSubscriber_NoEmailButLocal_AddsSuccessfully() throws Exception {
		User user = mock(User.class);
		when(user.getEmail()).thenReturn(null);
		when(user.getDelivery()).thenReturn(Delivery.LOCAL);

		service.addSubscriber(user);

		assertTrue(service.getSubscribers().contains(user));
	}

	@Test
	public void addSubscriber_ValidUserWithEmail_AddsSuccessfully() throws Exception {
		User user = mock(User.class);
		when(user.getEmail()).thenReturn("alumno@upm.es");

		service.addSubscriber(user);

		assertEquals(1, service.getSubscribers().size());
		assertTrue(service.getSubscribers().contains(user));
	}

	@Test
	public void removeSubscriber_NullUser_ThrowsException() {
		assertThrows(NullUserException.class, () -> {
			service.removeSubscriber(null);
		});
	}

	@Test
	public void removeSubscriber_NonExistingUser_ThrowsException() {
		User user = mock(User.class);

		assertThrows(NonExistingUserException.class, () -> {
			service.removeSubscriber(user);
		});
	}

	@Test
	public void removeSubscriber_ExistingUser_RemovesSuccessfully() throws Exception {
		User user = mock(User.class);
		when(user.getEmail()).thenReturn("alumno@upm.es");
		service.addSubscriber(user);

		service.removeSubscriber(user);

		assertFalse(service.getSubscribers().contains(user));
	}

	@Test
	public void sendMessage_DeliveryEmail_CallsEmailService() throws Exception {
		User user = mock(User.class);
		Message message = mock(Message.class);
		when(user.getEmail()).thenReturn("alumno@upm.es");
		when(user.getDelivery()).thenReturn(Delivery.EMAIL);
		service.addSubscriber(user);

		int lostMessages = service.sendMessage(message);

		assertEquals(0, lostMessages);
		verify(emailService, times(1)).sendMessage(user, message);
	}

	@Test
	public void sendMessage_DeliveryLocal_SavesMessageInUser() throws Exception {
		User user = mock(User.class);
		Message message = mock(Message.class);
		when(user.getDelivery()).thenReturn(Delivery.LOCAL);
		service.addSubscriber(user);

		int lostMessages = service.sendMessage(message);

		assertEquals(0, lostMessages);
		verify(user, times(1)).saveMessage(message);
		verifyNoInteractions(emailService);
	}

	@Test
	public void sendMessage_DoNotDeliver_SendsErrorAndReturnsCount() throws Exception {
		User user = mock(User.class);
		Message message = mock(Message.class);
		when(user.getEmail()).thenReturn("alumno@upm.es");
		when(user.getDelivery()).thenReturn(Delivery.DO_NOT_DELIVER);
		service.addSubscriber(user);

		int lostMessages = service.sendMessage(message);

		assertEquals(1, lostMessages);
		verify(emailService, times(1)).sendMessage(eq(user), any(Message.class));
	}
}