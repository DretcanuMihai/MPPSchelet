package myapp.services.interfaces;

import myapp.model.entities.Ticket;

public interface IObserver {
    void updateTicketSold(Ticket ticket);
}
