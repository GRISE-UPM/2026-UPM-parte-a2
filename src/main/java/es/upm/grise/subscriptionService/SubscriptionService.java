package es.upm.grise.subscriptionService;

import java.util.Collection;

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
	
	public void addSubscriber(User user) {}
	
	/* 
	 * Method to code/test
	 */
	
	public void removeSubscriber(User user) {}
	
	/* 
	 * Method to code/test
	 */
	
	public int sendMessage(Message message) {
		
		return 0;
		
	}
	
	/* 
	 * Others getters and setters
	 */
	
	public Collection<User> getSubscribers() {
		return subscribers;
	}

}
