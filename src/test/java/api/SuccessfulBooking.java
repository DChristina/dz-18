package api;

import java.util.Date;
//
public class SuccessfulBooking {
    private Integer bookingid;
    private String booking;
    private BookingDates bookingdates;

    private String additionalneeds;

    public  SuccessfulBooking(){
        super();
    }
    public SuccessfulBooking(Integer bookingid,String booking,BookingDates bookingdates,String additionalneeds){
        this.bookingid = bookingid;
        this.booking = booking;
        this.bookingdates = bookingdates;
        this.additionalneeds = additionalneeds;

    }

    public Integer getBookingid() {
        return bookingid;
    }

    public String getBooking() {
        return booking;
    }

    public BookingDates getBookingdates() {
        return bookingdates;
    }

    public String getAdditionalneeds() {
        return additionalneeds;
    }

}
