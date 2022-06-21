package myapp.model.validators.implementations;


import myapp.model.entities.Festival;
import myapp.model.validators.ValidationException;
import myapp.model.validators.interfaces.IFestivalValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FestivalValidator implements IFestivalValidator {


    @Override
    public void validate(Festival festival) throws ValidationException {
        Integer id = festival.getId();
        ;
        String artistName = festival.getArtistName();
        LocalDateTime dateTime = festival.getDateTime();
        String place = festival.getPlace();
        Integer availableSpots = festival.getAvailableSpots();
        Integer soldSpots = festival.getSoldSpots();
        String error = "";
        if (id == null) {
            error += "ID should be nonnull;\n";
        }
        if (artistName == null || artistName.equals("")) {
            error += "Artist Name should be nonnull and nonempty;\n";
        }
        if (dateTime == null) {
            error += "Festival date should be nonnull;\n";
        }
        if (place == null || place.equals("")) {
            error += "Place should be nonnull and nonempty;\n";
        }
        if (availableSpots == null || availableSpots < 1) {
            error += "Available Spots should be nonnull and positive;\n";
        }
        if (soldSpots == null || soldSpots < 1) {
            error += "Sold Spots should be nonnull and positive;\n";
        }
        if (soldSpots != null && availableSpots != null && availableSpots > 0 && soldSpots > 0) {
            if (soldSpots > availableSpots) {
                error += "More spots sold than available;\n";
            }
        }
        if (!error.equals("")) {
            throw new ValidationException(error);
        }
    }

    @Override
    public void validateID(Integer id) throws ValidationException {
        if (id == null) {
            throw new ValidationException("ID is null;\n");
        }
    }

    @Override
    public void validateDate(LocalDate date) throws ValidationException {
        if (date == null) {
            throw new ValidationException("Date is null;\n");
        }
    }
}
