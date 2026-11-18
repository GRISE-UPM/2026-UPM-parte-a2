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

		try {
			if (user == null) {
				throw new NullUserException();
			}
			if (subscribers.contains(user)) {
				throw new ExistingUserException();
			}
			if (user.getEmail() == null) {
				throw new UserDoesNotHaveEmailException();
			}

			subscribers.add(user);
		} catch (NullUserException e) {
			throw e;
		} catch (ExistingUserException e) {
			throw e;
		} catch (UserDoesNotHaveEmailException e) {
			throw e;
		}
	}
	
	/* 
	 * Method to code/test
	 */
	
	public void removeSubscriber(User user) throws NullUserException, NonExistingUserException {
		
		try {
			if (user == null) {
				throw new NullUserException();
			}
			if (!subscribers.contains(user)) {
				throw new NonExistingUserException();
			}

			subscribers.remove(user);
		} catch (NullUserException e) {
			throw e;
		} catch (NonExistingUserException e) {
			throw e;
		}

	}
	
	/* 
	 * Method to code/test
	 */
	
	public int sendMessage(Message message) {
		int sentMessages = 0;

		for (User user : subscribers) {
			Delivery delivery = user.getDelivery();

			if (delivery == Delivery.LOCAL) {
				user.saveMessage(message);
				sentMessages++;
			} else if (delivery == Delivery.DO_NOT_DELIVER) {
				user.saveMessage(new Message(message.getId(), "Ha perdido ud. un mensaje"));
				sentMessages++;
			}
		}

		return sentMessages;
	}
	
	/* 
	 * Others getters and setters
	 */
	
	public Collection<User> getSubscribers() {
		return subscribers;
	}

}
