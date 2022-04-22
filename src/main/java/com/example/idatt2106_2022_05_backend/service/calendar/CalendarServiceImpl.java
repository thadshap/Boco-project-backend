package com.example.idatt2106_2022_05_backend.service.calendar;

import com.example.idatt2106_2022_05_backend.dto.CalendarDto;
import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.CalendarDate;
import com.example.idatt2106_2022_05_backend.repository.AdRepository;
import com.example.idatt2106_2022_05_backend.repository.CalendarDateRepository;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Service
public class CalendarServiceImpl implements CalendarService {

    @Autowired
    private CalendarDateRepository dateRepository;

    // autowire repo for rental

    @Autowired
    private AdRepository adRepository;


    // return true if dates between start and end are "typeOfAvailability"
    private boolean datesAre(LocalDate startDate, LocalDate endDate, boolean typeOfAvailability) {
        // Get all dates between start -and endDate
        Set<CalendarDate> dates = dateRepository.findByDateBetween(startDate, endDate);

        // This return value changes if date.isAvailable != typeOfAvailability
        boolean returnValue = true;

        // Loop through all dates
        for(CalendarDate date : dates) {
            if(date.isAvailable() != typeOfAvailability) {
                returnValue = false;
            }
        }

        // return
        return returnValue;
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

        //TODO if rental is made unavailable (cancelled) -->
        // delete rental using some type of id that has to be sent through the dto......

        // Because we want to change availability, we check for the OPPOSITE of dto.isAvailable()
        if(datesAre(dto.getStartDate(),dto.getEndDate(), !dto.isAvailable())) {

            // Get the ad the dto points to
            Optional<Ad> ad = adRepository.findById(dto.getAdId());

            if(ad.isPresent()) {

                // Get all dates with requested adId
                Set<CalendarDate> dates = adRepository.getDatesForAd(dto.getAdId());

                // Get all dates between startDate and endDate
                for (CalendarDate date : dates) {

                    // PS: plus/minus days because we want to include the start and end dates in our search
                    if(date.getDate().isAfter(dto.getStartDate().minusDays(1))
                            && date.getDate().isBefore(dto.getEndDate().plusDays(1)))
                    {
                        // Change the availability
                        date.setAvailable(dto.isAvailable());

                        // Persist the change to the specific date from the CalendarDate table
                        Optional<CalendarDate> dateFound = dateRepository.findById(date.getId());

                        if(dateFound.isPresent()) {
                            dateFound.get().setAvailable(dto.isAvailable());
                            dateRepository.save(dateFound.get());
                        }

                        // If the date found is not present something very wrong happened :p
                        return new Response(null, HttpStatus.NOT_FOUND);
                    }
                }
                // Persist the new list of dates to the ad
                ad.get().setDates(dates);
                adRepository.save(ad.get());

                // Return true inside response
                return new Response(true, HttpStatus.OK);
            }

            // If ad not found, return false
            return new Response(false, HttpStatus.NOT_FOUND);
        }

        // If somehow the user was able to choose dates that did not have correct availability, then
        // a mistake has happened when sending unavailable dates to frontend
        return new Response(null, HttpStatus.I_AM_A_TEAPOT);
    }

    /**
     *
     * @param dto contains:
     *            adId
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
