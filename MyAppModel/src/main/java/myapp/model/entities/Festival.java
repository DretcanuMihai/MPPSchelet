package myapp.model.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Festival implements Identifiable<Integer>, Serializable {

    private Integer id;
    private String artistName;
    private LocalDateTime dateTime;
    private String place;
    private Integer availableSpots;
    private Integer soldSpots;

    public Festival() {
    }

    public Festival(Integer id, String artistName, LocalDateTime dateTime, String place, Integer availableSpots, Integer soldSpots) {
        this.id = id;
        this.artistName = artistName;
        this.dateTime = dateTime;
        this.place = place;
        this.availableSpots = availableSpots;
        this.soldSpots = soldSpots;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer integer) {
        this.id = integer;
    }


    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }


    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(Integer availableSpots) {
        this.availableSpots = availableSpots;
    }

    public Integer getSoldSpots() {
        return soldSpots;
    }

    public void setSoldSpots(Integer soldSpots) {
        this.soldSpots = soldSpots;
    }

    public String getDateTimeSql() {
        return Timestamp.valueOf(dateTime).toString();
    }

    public void setDateTimeSql(String dateTime) {
        this.dateTime = Timestamp.valueOf(dateTime).toLocalDateTime();
    }


    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }
    public Integer getHour() {
        return dateTime.toLocalTime().getHour();
    }

    public Integer getRemainingSpots() {
        return availableSpots - soldSpots;
    }

    @Override
    public String toString() {
        return "Festival{" +
                "id=" + id +
                ", artistName='" + artistName + '\'' +
                ", dateTime=" + dateTime +
                ", place='" + place + '\'' +
                ", availableSpots=" + availableSpots +
                ", soldSpots=" + soldSpots +
                '}';
    }
}
