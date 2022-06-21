package myapp.restcontrollers;

import myapp.model.validators.interfaces.IFestivalValidator;
import myapp.persistence.interfaces.IFestivalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import myapp.RestException;
import myapp.model.entities.Festival;

@CrossOrigin
@RestController
@RequestMapping("/myapp/festivals")
public class FestivalRestRequestController {

    @Autowired
    private IFestivalRepository festivalRepository;

    @Autowired
    private IFestivalValidator festivalValidator;

    @RequestMapping(method = RequestMethod.POST)
    public Festival create(@RequestBody Festival festival) {
        System.out.println("In Create;");
        try {
            festival.setId(0);
            festivalValidator.validate(festival);
        } catch (Exception e) {
            throw new RestException(e.getMessage());
        }
        Festival result = festivalRepository.create(festival);
        if (result == null) {
            throw new RestException("Couldn't create Festival;\n");
        }
        return result;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> readById(@PathVariable Integer id) {
        System.out.println("In Read;");
        try {
            festivalValidator.validateID(id);
        } catch (Exception e) {
            throw new RestException(e.getMessage());
        }
        Festival festival = festivalRepository.read(id);
        if (festival == null) {
            return new ResponseEntity<>("No entity with given id;\n", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(festival, HttpStatus.OK);
    }



    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Festival festival) {
        System.out.println("In Update;");
        festival.setId(id);
        try {
            festivalValidator.validate(festival);
        } catch (Exception e) {
            throw new RestException(e.getMessage());
        }
        Festival result = festivalRepository.update(festival);
        if (result == null) {
            return new ResponseEntity<>("No entity with given id;\n", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        System.out.println("In Delete;");
        try {
            festivalValidator.validateID(id);
        } catch (Exception e) {
            throw new RestException(e.getMessage());
        }
        Festival result = festivalRepository.delete(id);
        if (result == null) {
            return new ResponseEntity<>("No entity with given id;\n", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Festival[] readAll() {
        System.out.println("In Read All;");
        return festivalRepository.readAll().toArray(new Festival[0]);
    }

    @ExceptionHandler(RestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String festivalError(RestException e) {
        return e.getMessage();
    }
}
