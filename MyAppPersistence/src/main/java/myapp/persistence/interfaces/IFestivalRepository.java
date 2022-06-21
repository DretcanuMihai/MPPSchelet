package myapp.persistence.interfaces;


import myapp.model.entities.Festival;

import java.time.LocalDate;
import java.util.Collection;

public interface IFestivalRepository extends IRepository<Integer, Festival> {

    Collection<Festival> readAllOnDate(LocalDate date);
}
