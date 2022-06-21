package myapp.server;

import myapp.model.entities.Festival;
import myapp.model.entities.Ticket;
import myapp.model.validators.interfaces.IFestivalValidator;
import myapp.model.validators.interfaces.ITicketValidator;
import myapp.persistence.interfaces.IFestivalRepository;
import myapp.persistence.interfaces.ITicketRepository;
import myapp.services.ServiceException;
import myapp.model.validators.ValidationException;
import myapp.services.interfaces.IFestivalService;

import java.time.LocalDate;
import java.util.Collection;

public class FestivalService implements IFestivalService {


    private final IFestivalValidator festivalValidator;
    private final IFestivalRepository festivalRepository;
    private final ITicketValidator ticketValidator;
    private final ITicketRepository ticketRepository;

    public FestivalService(IFestivalRepository festivalRepository, IFestivalValidator festivalValidator,
                           ITicketRepository ticketRepository, ITicketValidator ticketValidator) {
        this.festivalValidator = festivalValidator;
        this.festivalRepository = festivalRepository;
        this.ticketValidator = ticketValidator;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Collection<Festival> getAllFestivals() {
        return festivalRepository.readAll();
    }

    @Override
    public Collection<Festival> getAllFestivalsOnDate(LocalDate date) throws ServiceException {
        try {
            festivalValidator.validateDate(date);
        } catch (ValidationException e) {
            throw new ServiceException(e.getMessage());
        }
        return festivalRepository.readAllOnDate(date);
    }

    @Override
    public Ticket sellTicket(Integer festivalID, String buyerName, Integer spots)
            throws ServiceException {

        try {
            festivalValidator.validateID(festivalID);
        } catch (ValidationException e) {
            throw new ServiceException(e.getMessage());
        }

        Festival festival = festivalRepository.read(festivalID);
        if (festival == null) {
            throw new ServiceException("No festival with given id found!;\n");
        }

        Ticket ticket = new Ticket(null, buyerName, festival, spots);
        try {
            ticketValidator.validate(ticket);
        } catch (ValidationException e) {
            throw new ServiceException(e.getMessage());
        }

        Ticket result = ticketRepository.create(ticket);
        if (result == null) {
            throw new ServiceException("Couldn't create ticket!;\n");
        }

        festival.setSoldSpots(festival.getSoldSpots() + spots);
        Festival festivalResult = festivalRepository.update(festival);
        if (festivalResult == null) {
            throw new ServiceException("Couldn't update festival!;\n");
        }
        return result;
    }
}
