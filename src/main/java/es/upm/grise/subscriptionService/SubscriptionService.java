package es.upm.grise.subscriptionService;

import java.util.Collection;

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
	
	public void addSubscriber(User user) throws NullUserException, ExistingUserException {
		if(user == null) {
			throw new NullUserException("User cannot be null");
		}
		else if(subscribers.contains(user)) {
			throw new ExistingUserException("User is already subscribed");
		}
		else if(user.getEmail() == null) { // || user.getEmail().isEmpty()
			throw new UserDoesNotHaveEmailException("User email cannot be null");

		}
		else {
			subscribers.add(user);
		}
	}
	
	/* 
	 * Method to code/test
	 */
	
	public void removeSubscriber(User user) {
		if(user == null){
			throw new NullUserException("User cannot be null");
		}
		else if(!subscribers.contains(user)) {
			throw new NonExistingUserException("User is not subscribed");
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
				emailService.sendMessage(user, new Message("Ha perdido ud. un mensaje"));
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
