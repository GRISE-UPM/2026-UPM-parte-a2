package es.upm.grise.subscriptionService;

import java.util.ArrayList;
import java.util.Collection;
import es.upm.grise.subscriptionService.exceptions.*;

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
	
	public void addSubscriber(User user) throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException{
		if (user == null){
			throw new NullUserException();
		}
		if(subscribers.contains(user)){
			throw new ExistingUserException();
		}
		if(!user.getDelivery().equals(Delivery.LOCAL) && user.getEmail()==null){
			throw new UserDoesNotHaveEmailException();
		}
		subscribers.add(user);
	}
	
	/* 
	 * Method to code/test
	 */
	
	public void removeSubscriber(User user) throws NullUserException, NonExistingUserException{
		if (user == null){
			throw new NullUserException();
		}
		if(!subscribers.contains(user)){
			throw new NonExistingUserException();
		}
		subscribers.remove(user);
	}
	
	/* 
	 * Method to code/test
	 */
	
	public int sendMessage(Message message) {
		int cont=0;
		Delivery d;
		for (User u : subscribers) {
			d=u.getDelivery();
			if(d.equals(Delivery.LOCAL)){
				u.saveMessage(message);
			}else if(d.equals(Delivery.DO_NOT_DELIVER)){
				u.saveMessage(new Message(0, "Ha perdido ud. un mensaje"));
				cont++;
			}else if(d.equals(Delivery.EMAIL)){
				emailService.sendMessage(u, message);
			}
		}
		return cont;
		
	}
	
	/* 
	 * Others getters and setters
	 */
	
	public Collection<User> getSubscribers() {
		return subscribers;
	}

}
