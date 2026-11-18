package es.upm.grise.subscriptionService;

import java.util.Collection;

import java.util.ArrayList;
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

	public SubscriptionService(EmailService emailService) {
		this.emailService = emailService;
		this.subscribers = new ArrayList<>();
	}

	/* 
	 * Method to code/test
	 */

	public void addSubscriber(User user) throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
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

	public void removeSubscriber(User user) throws NullUserException, NonExistingUserException {
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
		int discarded = 0;
		for (User user : subscribers) {
			if (user.getDelivery() == Delivery.EMAIL) {
				emailService.sendMessage(user, message);
			} else if (user.getDelivery() == Delivery.LOCAL) {
				user.saveMessage(message);
			} else if (user.getDelivery() == Delivery.DO_NOT_DELIVER) {
				user.saveMessage(new Message(message.getId(), "Ha perdido ud. un mensaje"));
				discarded++;
			}
		}
		return discarded;
	}
	
	/* 
	 * Others getters and setters
	 */
	
	public Collection<User> getSubscribers() {
		return subscribers;
	}

}
