package es.upm.grise.subscriptionService;

import java.util.ArrayList;
import java.util.Collection;

public class User {

	private Delivery delivery;
	private String email;
	private Collection <Message> messages = new ArrayList<Message>();
	
	public User(Delivery delivery, String email) {
		
		this.delivery = delivery;
		this.email = email;
		
	}

	public String getEmail() {
		
		return email;
		
	}

	public Delivery getDelivery() {
		
		return delivery;
		
	}

	public void saveMessage(Message message) {
		
		messages.add(message);
		
	}

}
