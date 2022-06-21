package myapp.model.entities;

import java.io.Serializable;

public class Ticket implements Identifiable<Integer>, Serializable {
    private Integer id;
    private String buyerName;
    private Festival festival;
    private Integer numberOfSpots;

    public Ticket() {
    }

    public Ticket(Integer id, String buyerName, Festival festival, Integer numberOfSpots) {
        this.id=id;
        this.buyerName = buyerName;
        this.festival = festival;
        this.numberOfSpots = numberOfSpots;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer integer) {
        this.id=integer;
    }

    public String getBuyerName() {
        return buyerName;
    }


    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }

    public Integer getNumberOfSpots() {
        return numberOfSpots;
    }

    public void setNumberOfSpots(Integer numberOfSpots) {
        this.numberOfSpots = numberOfSpots;
    }
}
