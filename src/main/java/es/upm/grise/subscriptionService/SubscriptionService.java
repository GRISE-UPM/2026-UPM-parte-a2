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
	
	public SubscriptionService() {
		subscribers = null;
	}
	/* 
	 * Constructor
	 */
	
	public SubscriptionService(EmailService emailService) {}

	/* 
	 * Method to code/test
	 */
	
	public void addSubscriber(User user) throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {

		if(user == null) {
			throw new NullUserException();
		}

		if(subscribers.contains(user)) {
			throw new ExistingUserException();
		}

		if(user.getDelivery() == Delivery.LOCAL) {
			if(user.getEmail() == null || user.getEmail().isEmpty()) {
				throw new UserDoesNotHaveEmailException();
			}
		}

		subscribers.add(user);
	}
	
	/* 
	 * Method to code/test
	 */
	
	public void removeSubscriber(User user) throws NullUserException, NonExistingUserException {
		if(user == null) {
			throw new NullUserException();
		}
		if(!subscribers.contains(user)) {
			throw new NonExistingUserException();
		}

		subscribers.remove(user);
	}
	
	/* 
	 * Method to code/test
	 */
	
	public int sendMessage(Message message) {
		int sendCount = 0;
		
		for(User user : subscribers) {
			if(user.getDelivery() == Delivery.LOCAL) {
				user.saveMessage(message);
			} else if (user.getDelivery() == Delivery.DO_NOT_DELIVER) {
				sendCount++;
			} else {
				emailService.sendMessage(user, message);
			}
		}
		return sendCount;
		
	}
	
	/* 
	 * Others getters and setters
	 */
	
	public Collection<User> getSubscribers() {
		return subscribers;
	}

}
