package es.upm.grise.subscriptionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
		this.subscribers = new ArrayList<User>();
	}

	/* 
	 * Method to code/test
	 */
	
	public void addSubscriber(User user) throws Exception {
		if (Objects.isNull(user))
			throw new NullUserException();
		else if (subscribers.contains(user))
			throw new ExistingUserException();
		else{
			if (user.getDelivery() == Delivery.LOCAL)
				subscribers.add(user);
			else{
				if(user.getEmail() == null)
					throw new UserDoesNotHaveEmailException();
				else
					subscribers.add(user);
			}
		}
	}
	
	/* 
	 * Method to code/test
	 */
	
	public void removeSubscriber(User user)throws Exception {
		if (Objects.isNull(user))
			throw new NullUserException();
		else if (!subscribers.contains(user))
			throw new NonExistingUserException();
		else
			subscribers.remove(user);
	}
	
	/* 
	 * Method to code/test
	 */
	
	public int sendMessage(Message message) {
		int count = 0;
		for (User user : subscribers){
			if (user.getDelivery() == Delivery.LOCAL)
				user.saveMessage(message);
			else if (user.getDelivery() == Delivery.DO_NOT_DELIVER)
				user.saveMessage(new Message(0, "Ha perdido ud. un mensaje"));
			else{
				emailService.sendMessage(user, message);
				count++;
			}
		}
		return count;
	}
	
	/* 
	 * Others getters and setters
	 */
	
	public Collection<User> getSubscribers() {
		return subscribers;
	}

}
