package es.upm.grise.subscriptionService;

import java.util.Collection;
import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;

public class SubscriptionService {

	private Collection <User> subscribers;
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

		int discardedMessages = 0;

		// En la especificacion pone que se envia a todos usuarios, por eso no diferencio entre delivery distintos para mandar

		for (User user : subscribers) {
			emailService.sendMessage(user, message);
			switch (user.getDelivery()) {
				case LOCAL:
					user.saveMessage(message);
					break;
				case DO_NOT_DELIVER:
					user.saveMessage(new Message(message.getId(), "Ha perdido ud. un mensaje"));
					discardedMessages++;
					break;
				default:
					break;
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
