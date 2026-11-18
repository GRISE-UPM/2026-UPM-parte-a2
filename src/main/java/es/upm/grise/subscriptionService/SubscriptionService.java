package es.upm.grise.subscriptionService;

import java.util.ArrayList;
import java.util.Collection;

import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;

public class SubscriptionService {

    private Collection<User> subscribers;
    private EmailService emailService;

    public SubscriptionService(EmailService emailService) {
        this.emailService = emailService;
        this.subscribers = new ArrayList<>();
    }

    public void addSubscriber(User user)
            throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
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

    public void removeSubscriber(User user)
            throws NullUserException, NonExistingUserException {
        if (user == null) {
            throw new NullUserException();
        }
        if (!subscribers.contains(user)) {
            throw new NonExistingUserException();
        }
        subscribers.remove(user);
    }

    public int sendMessage(Message message) {
        int discarded = 0;

        for (User user : subscribers) {
            Delivery delivery = user.getDelivery();

            if (delivery == Delivery.EMAIL) {
                emailService.sendMessage(user, message);
            } else if (delivery == Delivery.LOCAL) {
                user.saveMessage(message);
            } else if (delivery == Delivery.DO_NOT_DELIVER) {
                Message notice = new Message(0L, "Ha perdido ud. un mensaje");
                user.saveMessage(notice);
                discarded++;
            }
        }
        return discarded;
    }

    public Collection<User> getSubscribers() {
        return subscribers;
    }
}