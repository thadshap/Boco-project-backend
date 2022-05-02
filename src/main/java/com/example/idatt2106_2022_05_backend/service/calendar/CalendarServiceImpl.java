package com.example.idatt2106_2022_05_backend.service.calendar;

import com.example.idatt2106_2022_05_backend.dto.CalendarDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.CalendarDate;
import com.example.idatt2106_2022_05_backend.model.Rental;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CalendarDateRepository;
import com.example.idatt2106_2022_05_backend.repository.RentalRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CalendarServiceImpl implements CalendarService {

    @Autowired
    private CalendarDateRepository dateRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private AdRepository adRepository;

    /**
     * Helper method to find dates.
     * @param startDate start date.
     * @param endDate end date.
     * @param typeOfAvailability type of availability.
     * @return true if dates between start and end are "typeOfAvailability".
     */
    private boolean datesAre(LocalDate startDate, LocalDate endDate, boolean typeOfAvailability, long adId) {
        // Get all dates between start -and endDate
        Set<CalendarDate> dates = dateRepository.findByDateBetween(startDate, endDate);
        // Find the ad
        Optional<Ad> adFound = adRepository.findById(adId);

        if(adFound.isPresent()) {
            // Get all the dates for an ad
            Set<CalendarDate> datesBelongingToAd = adFound.get().getDates();

            // If the date is between startDate and endDate && has the correct typeOfAvailability do nothing
            for(CalendarDate date : datesBelongingToAd) {
                LocalDate date1 = date.getDate();
                // If the date is between startDate and endDate
                if      (date1.isEqual(startDate) ||
                        (date1.isAfter(startDate) && date1.isBefore(endDate) ||
                         date1.isEqual(endDate)))
                {
                    // If the date is in the correct span and has the wrong typeOfAvailability, return false
                    if(date.isAvailable() != typeOfAvailability) {
                        return false;
                    }
                    // If date available, do nothing
                }
                // If date not within correct span, do nothing
            }

            // If iterated through all ads in span without finding wrong typeOfAvailability --> return true
            return true;
        }
        // If the ad was not found, return false
        return false;
    }

    /**
     *
     * @param dto contains:
     *            the start date
     *            the end date
     *            (boolean) available --> for use as mutator
     * @return true if everything went well
     */

    @Override
    public Response markDatesFromToAs(CalendarDto dto) {

        // If there exists a rental with the specified id
        Optional<Rental> rental = rentalRepository.findById(dto.getRentalId());
        if(rental.isPresent()) {

            // If isAvailable == true, a cancellation has been made
            if (dto.isAvailable()) {

                // Get the creation timestamp for the rental
                LocalDateTime created = rental.get().getCreated();
                LocalDateTime createdPlusOneDay = created.plusHours(24);

                // If 24 hrs hasn't passed yet
                if (LocalDateTime.now().isBefore(createdPlusOneDay)) {

                    return setDates(dto);
                }

                // If 24 hrs has passed, the cancellation cannot be made
                else {
                    return new Response("More than 24hrs after rental has passed",
                            HttpStatus.NOT_ACCEPTABLE);
                }
            }
            // isAvailable cannot be false when a rental has not yet been made
            else {
                return new Response("isAvailable cannot be false when a rental is present",
                        HttpStatus.NOT_ACCEPTABLE);
            }
        }
        // If a rental is not present, the dates may be set to unavailable or available
        else {
            return setDates(dto);
        }
    }

    private Response setDates(CalendarDto dto) {
        // Verify that all dates in the specified span are the opposite of what is specified in dto

        //if (datesAre(dto.getStartDate(), dto.getEndDate(),
          //      !dto.isAvailable(), dto.getAdId())) {

            // Get the ad the dto points to
            Optional<Ad> ad = adRepository.findById(dto.getAdId());

            if (ad.isPresent()) {

                // Get all dates with requested id
                Set<CalendarDate> dates = ad.get().getDates();

                // Get all dates between startDate and endDate
                for (CalendarDate date : dates) {

                    LocalDate theDate = date.getDate();

                    // PS: plus/minus days because we want to include the start and end dates in our search
                    if (theDate.isEqual(dto.getStartDate()) ||
                            (theDate.isAfter(dto.getStartDate()) && theDate.isBefore(dto.getEndDate())) ||
                            theDate.isEqual(dto.getEndDate()))
                    {

                        // Change the availability
                        date.setAvailable(dto.isAvailable());

                        // Persist the change to the specific date from the CalendarDate table
                        dateRepository.save(date);
                        /**
                        Optional<CalendarDate> dateFound = dateRepository.findById(date.getId());

                        if (dateFound.isPresent()) {
                            dateFound.get().setAvailable(dto.isAvailable());
                            dateRepository.save(dateFound.get());
                        }
                        else {
                            // If the date found is not present something very wrong happened :p
                            return new Response(null, HttpStatus.NOT_FOUND);
                        }
                         */
                    }
                //}
            }
                // Persist the new list of dates to the ad
                ad.get().setDates(dates);
                adRepository.save(ad.get());

                // Return true inside response
                return new Response("Changed the dates to: " + dto.isAvailable(), HttpStatus.OK);
        }
        // If ad not found, return false
        return new Response("Could not find the ad", HttpStatus.NOT_FOUND);
        // Not all dates in the span were of the same availability
        //return new Response("Not all dates in the span were of the same availability",
          //      HttpStatus.NOT_ACCEPTABLE);

    }

    /**
     * Method to add dates to Calender
     * @param adId
     * @return
     */
    @Override
    public Set<CalendarDate> addFutureDates(long adId) {
        // Find the ad
        Optional<Ad> ad = adRepository.findById(adId);

        if(ad.isPresent()) {

            // Get ad creation date
            LocalDate creationDate = ad.get().getCreated();

            // Add one year of CalendarDates to the ads
            Set<CalendarDate> calendarDates = new HashSet<>();

            for (int i = 1; i < 366; i++) {

                CalendarDate newDate = CalendarDate.builder().
                        available(true).
                        date(creationDate.plusDays(i)).
                        build();

                if(newDate.getAds() != null) {
                    newDate.getAds().add(ad.get());

                    // Persist date
                    dateRepository.save(newDate);
                }
                else {
                    Set<Ad> newAdSet = new HashSet<>();
                    newAdSet.add(ad.get());
                    newDate.setAds(newAdSet);

                    // Persist date
                    dateRepository.save(newDate);

                }
                calendarDates.add(newDate);
            }

            // List is created and must now be added to the ad
            ad.get().setDates(calendarDates);

            // Persist the update
            adRepository.save(ad.get());

            return calendarDates;
        }
        else {
            return null;
        }
    }

    /**
     *
     * @param dto contains:
     *            id
     *
     * @return the dates that are unavailable up until the ad expires (12 months past creation timestamp)
     */
    @Override
    public Response getUnavailableDates(CalendarDto dto) {

        // Array containing unavailable dates within span
        ArrayList<LocalDate> datesOut = new ArrayList<>();

        // Get the ad the dto points to
        Optional<Ad> ad = adRepository.findById(dto.getAdId());

        if(ad.isPresent()) {

            // Find out when the ad was created
            LocalDate startDate = ad.get().getCreated();

            // Find out when the ad expires --> startDate + 12 months
            LocalDate expirationDate = startDate.plusMonths(12);

            // Get all dates for the ad
            Set<CalendarDate> dates = adRepository.getDatesForAd(dto.getAdId());

            // Use creation and expiration to calculate span
            for (CalendarDate date : dates) {

                // If the date is within the specified span
                // PS: plus/minus days because we want to include the start and end dates in our search
                if(date.getDate().isAfter(startDate.minusDays(1))
                        && date.getDate().isBefore(expirationDate.plusDays(1)))
                {

                    // If the date is not available
                    if(!date.isAvailable()) {

                        // Add to return-array
                        datesOut.add(date.getDate());
                    }
                }
            }
            // Return the array and the HttpResponse
            return new Response(datesOut, HttpStatus.OK);
        }

        // If ad was not present in db, the dto containing an id that does not exist
        return new Response(null, HttpStatus.NOT_FOUND);
    }
}

