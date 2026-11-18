package es.upm.grise.subscriptionService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;

class SubscriptionServiceTest {

	private EmailService emailService;
	private SubscriptionService subscriptionService;

	@BeforeEach
	public void setUp() {
		emailService = mock(EmailService.class);
		subscriptionService = new SubscriptionService(emailService);
	}

	@Test
	public void smokeTest() {}

	// ---------------------------------------------------------
	// Inicialización
	// ---------------------------------------------------------

	@Test
	public void testInitialization_SubscribersListIsEmpty() {
		assertNotNull(subscriptionService.getSubscribers());
		assertTrue(subscriptionService.getSubscribers().isEmpty());
	}

	// ---------------------------------------------------------
	// addSubscriber
	// ---------------------------------------------------------

	@Test
	public void testAddSubscriber_Success() throws Exception {
		User user = new User(Delivery.EMAIL, "user@example.com");
		subscriptionService.addSubscriber(user);

		assertEquals(1, subscriptionService.getSubscribers().size());
		assertTrue(subscriptionService.getSubscribers().contains(user));
	}

	@Test
	public void testAddSubscriber_NullUser_ThrowsNullUserException() {
		assertThrows(NullUserException.class, () -> {
			subscriptionService.addSubscriber(null);
		});
	}

	@Test
	public void testAddSubscriber_ExistingUser_ThrowsExistingUserException() throws Exception {
		User user = new User(Delivery.EMAIL, "user@example.com");
		subscriptionService.addSubscriber(user);

		assertThrows(ExistingUserException.class, () -> {
			subscriptionService.addSubscriber(user);
		});
	}

	@Test
	public void testAddSubscriber_NullEmail_DeliveryEmail_ThrowsUserDoesNotHaveEmailException() {
		User user = new User(Delivery.EMAIL, null);

		assertThrows(UserDoesNotHaveEmailException.class, () -> {
			subscriptionService.addSubscriber(user);
		});
	}

	@Test
	public void testAddSubscriber_NullEmail_DeliveryDoNotDeliver_ThrowsUserDoesNotHaveEmailException() {
		User user = new User(Delivery.DO_NOT_DELIVER, null);

		assertThrows(UserDoesNotHaveEmailException.class, () -> {
			subscriptionService.addSubscriber(user);
		});
	}

	@Test
	public void testAddSubscriber_NullEmail_DeliveryLocal_Success() throws Exception {
		User user = new User(Delivery.LOCAL, null);
		subscriptionService.addSubscriber(user);

		assertEquals(1, subscriptionService.getSubscribers().size());
		assertTrue(subscriptionService.getSubscribers().contains(user));
	}

	// ---------------------------------------------------------
	// removeSubscriber
	// ---------------------------------------------------------

	@Test
	public void testRemoveSubscriber_Success() throws Exception {
		User user = new User(Delivery.EMAIL, "user@example.com");
		subscriptionService.addSubscriber(user);

		subscriptionService.removeSubscriber(user);

		assertEquals(0, subscriptionService.getSubscribers().size());
		assertFalse(subscriptionService.getSubscribers().contains(user));
	}

	@Test
	public void testRemoveSubscriber_NullUser_ThrowsNullUserException() {
		assertThrows(NullUserException.class, () -> {
			subscriptionService.removeSubscriber(null);
		});
	}

	@Test
	public void testRemoveSubscriber_NonExistingUser_ThrowsNonExistingUserException() {
		User user = new User(Delivery.EMAIL, "user@example.com");

		assertThrows(NonExistingUserException.class, () -> {
			subscriptionService.removeSubscriber(user);
		});
	}

	// ---------------------------------------------------------
	// sendMessage
	// ---------------------------------------------------------

	@Test
	public void testSendMessage_DeliveryEmail_SentUsingEmailService() throws Exception {
		User user = mock(User.class);
		when(user.getDelivery()).thenReturn(Delivery.EMAIL);
		when(user.getEmail()).thenReturn("user@example.com");

		subscriptionService.addSubscriber(user);

		Message message = new Message(1L, "Hola mundo");
		int discarded = subscriptionService.sendMessage(message);

		assertEquals(0, discarded);
		verify(emailService).sendMessage(user, message);
		verify(user, never()).saveMessage(any());
	}

	@Test
	public void testSendMessage_DeliveryLocal_SavedInUser() throws Exception {
		User user = mock(User.class);
		when(user.getDelivery()).thenReturn(Delivery.LOCAL);

		subscriptionService.addSubscriber(user);

		Message message = new Message(2L, "Mensaje local");
		int discarded = subscriptionService.sendMessage(message);

		assertEquals(0, discarded);
		verify(user).saveMessage(message);
		verify(emailService, never()).sendMessage(any(), any());
	}

	@Test
	public void testSendMessage_DeliveryDoNotDeliver_SavesLostNotificationAndIncrementsDiscarded() throws Exception {
		User user = mock(User.class);
		when(user.getDelivery()).thenReturn(Delivery.DO_NOT_DELIVER);
		when(user.getEmail()).thenReturn("user@example.com");

		subscriptionService.addSubscriber(user);

		Message message = new Message(3L, "Mensaje descartable");
		int discarded = subscriptionService.sendMessage(message);

		assertEquals(1, discarded);
		verify(emailService, never()).sendMessage(any(), any());

		ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
		verify(user).saveMessage(messageCaptor.capture());

		Message savedMessage = messageCaptor.getValue();
		assertEquals(3L, savedMessage.getId());
		assertEquals("Ha perdido ud. un mensaje", getMessageContent(savedMessage));
	}

	@Test
	public void testSendMessage_MultipleSubscribers_ReturnsCorrectDiscardedCount() throws Exception {
		User emailUser = mock(User.class);
		when(emailUser.getDelivery()).thenReturn(Delivery.EMAIL);
		when(emailUser.getEmail()).thenReturn("email@example.com");

		User localUser = mock(User.class);
		when(localUser.getDelivery()).thenReturn(Delivery.LOCAL);

		User doNotDeliverUser1 = mock(User.class);
		when(doNotDeliverUser1.getDelivery()).thenReturn(Delivery.DO_NOT_DELIVER);
		when(doNotDeliverUser1.getEmail()).thenReturn("dnd1@example.com");

		User doNotDeliverUser2 = mock(User.class);
		when(doNotDeliverUser2.getDelivery()).thenReturn(Delivery.DO_NOT_DELIVER);
		when(doNotDeliverUser2.getEmail()).thenReturn("dnd2@example.com");

		subscriptionService.addSubscriber(emailUser);
		subscriptionService.addSubscriber(localUser);
		subscriptionService.addSubscriber(doNotDeliverUser1);
		subscriptionService.addSubscriber(doNotDeliverUser2);

		Message message = new Message(10L, "Mensaje masivo");
		int discarded = subscriptionService.sendMessage(message);

		assertEquals(2, discarded);
		verify(emailService).sendMessage(emailUser, message);
		verify(localUser).saveMessage(message);
		verify(doNotDeliverUser1).saveMessage(any(Message.class));
		verify(doNotDeliverUser2).saveMessage(any(Message.class));
	}

	// ---------------------------------------------------------
	// Métodos auxiliares
	// ---------------------------------------------------------

	private String getMessageContent(Message message) {
		try {
			Field field = Message.class.getDeclaredField("message");
			field.setAccessible(true);
			return (String) field.get(message);
		} catch (Exception e) {
			throw new RuntimeException("No se pudo acceder al campo message", e);
		}
	}

}
