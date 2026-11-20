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
	
	public SubscriptionService(EmailService emailService) {
		this.emailService = emailService;
		this.subscribers = new java.util.ArrayList<User>();
	}

	/* 
	 * Method to code/test
	 */
	
	public void addSubscriber(User user) throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		if(user == null) {
			throw new NullUserException();
		}
		else if(subscribers.contains(user)) {
			throw new ExistingUserException();
		}
		else if(user.getEmail() == null) { // || user.getEmail().isEmpty()
			throw new UserDoesNotHaveEmailException();

		}
		else {
			subscribers.add(user);
		}
	}
	
	/* 
	 * Method to code/test
	 */
	
	public void removeSubscriber(User user) throws NullUserException, NonExistingUserException {
		if(user == null){
			throw new NullUserException();
		}
		else if(!subscribers.contains(user)) {
			throw new NonExistingUserException();
		}
		else {
			subscribers.remove(user);
			//emailService.sendMessage(user, new Message("You have been unsubscribed"));
		}
	}
	
	/* 
	 * Method to code/test
	 */
	
	public int sendMessage(Message message) {
		int descartados = 0;

		for(User user : subscribers) {
			if(user.getDelivery() == Delivery.LOCAL) {
				emailService.sendMessage(user, message);
			}
			else if(user.getDelivery() == Delivery.DO_NOT_DELIVER) {
				descartados++;
				emailService.sendMessage(user, new Message(descartados,"Ha perdido ud. un mensaje"));
			}


		}		
		return descartados;
		
	}
	
	/* 
	 * Others getters and setters
	 */
	
	public Collection<User> getSubscribers() {
		return subscribers;
	}

}
