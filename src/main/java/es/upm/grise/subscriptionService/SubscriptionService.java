package es.upm.grise.subscriptionService;

import java.util.Collection;

import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;

public class SubscriptionService {

	private Collection <User> subscribers;
	private EmailService emailService;
	
	public SubscriptionService(EmailService emailService) {
		this.emailService = emailService;
		subscribers = new java.util.ArrayList<User>();
	}
	
	public void addSubscriber(User user) throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		if (user == null) {
			throw new NullUserException();
		}
		if (subscribers.contains(user)) {
			throw new ExistingUserException();
		}
		if (user.getEmail() == null && user.getDelivery() != Delivery.LOCAL) {
			throw new UserDoesNotHaveEmailException();
		}
		subscribers.add(user);
	}
	
	public void removeSubscriber(User user) throws NonExistingUserException, NullUserException {
		if (user == null) {
			throw new NullUserException();
		}
		if (!subscribers.contains(user)) {
			throw new NonExistingUserException();
		}
		subscribers.remove(user);
	}
	
	public int sendMessage(Message message) {	
		int n = 0;	
		for (User user : subscribers) {
			if (user.getDelivery() == Delivery.EMAIL) {
				emailService.sendMessage(user, message);
			}
			else if (user.getDelivery() == Delivery.LOCAL) {
				user.saveMessage(message);
			}
			else {
				Message errorMessage = new Message(1,"Ha perdido ud. un mensaje");
				emailService.sendMessage(user, errorMessage);
				n++;
			}
		}
		return n;
		
	}
	
	public Collection<User> getSubscribers() {
		return subscribers;
	}

}
