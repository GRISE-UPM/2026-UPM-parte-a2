package es.upm.grise.subscriptionService;

import java.util.Collection;
import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;



public class SubscriptionService {

	private Collection <User> subscribers;
	@SuppressWarnings("unused")
	private EmailService emailService;
	

	
	/* 
	 * Constructor
	 */
	
	public SubscriptionService(EmailService emailService) {}

	/* 
	 * Method to code/test
	 */
	
	public void addSubscriber(User user) throws ExistingUserException, NullUserException, UserDoesNotHaveEmailException {
		if (user == null) {
			throw new NullUserException();
		}
		if (subscribers.contains(user)) {
			throw new ExistingUserException();
		}
		if (user.getDelivery() != Delivery.LOCAL && user.getEmail() == null) {
			throw new UserDoesNotHaveEmailException();
		}
		subscribers.add(user);
	}
	
	/* 
	 * Method to code/test
	 */
	
	public void removeSubscriber(User user) throws NonExistingUserException, NullUserException {
		if (user == null) {
			throw new NullUserException();
		}
		if (!subscribers.contains(user)) {
			throw new NonExistingUserException();
		}
		subscribers.remove(user);
	}
	
	/* 
	 * Method to code/test
	 */
	
	public int sendMessage(Message message) {
		int discardedMessages = 0;

		for (User user : subscribers) {
			Delivery delivery = user.getDelivery();

			if (delivery == Delivery.EMAIL) {
				emailService.sendMessage(user, message);
			} else if (delivery == Delivery.LOCAL) {
				user.saveMessage(message);
			} else if (delivery == Delivery.DO_NOT_DELIVER) {
				user.saveMessage(new Message(discardedMessages, "Ha perdido ud. un mensaje"));
				discardedMessages++;
			}
		}

		return discardedMessages;
		
	}
	
	/* 
	 * Others getters and setters
	 */
	
	public Collection<User> getSubscribers() {
		return subscribers;
	}

}
