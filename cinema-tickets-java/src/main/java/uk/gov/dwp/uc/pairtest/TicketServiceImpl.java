package uk.gov.dwp.uc.pairtest;

import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentService;
import uk.gov.dwp.uc.pairtest.validation.TicketPurchaseValidator;

import java.util.Arrays;

public class TicketServiceImpl implements TicketService {

    /**
     # Objective
     This is a coding exercise which will allow you to demonstrate how you code and your approach to a given problem.

     You will be assessed on:
     - Your ability to write clean, well-tested and reusable code.
     - How you have ensured the following business rules are correctly met.

     # Business Rules
     - There are 3 types of tickets i.e. Infant, Child, and Adult.
     - The ticket prices are based on the type of ticket (see table below).
     - The ticket purchaser declares how many and what type of tickets they want to buy.
     - Multiple tickets can be purchased at any given time.
     - Only a maximum of 20 tickets that can be purchased at a time.
     - Infants do not pay for a ticket and are not allocated a seat. They will be sitting on an Adult's lap.
     - Child and Infant tickets cannot be purchased without purchasing an Adult ticket.

     |   Ticket Type    |     Price   |
     | ---------------- | ----------- |
     |    INFANT        |    £0       |
     |    CHILD         |    £10      |
     |    ADULT         |    £20      |

     - There is an existing `TicketPaymentService` responsible for taking payments.
     - There is an existing `SeatReservationService` responsible for reserving seats.

     ## Constraints
     - The TicketService interface CANNOT be modified. (For Java solution only)
     - The code in the thirdparty.* packages CANNOT be modified.
     - The `TicketTypeRequest` SHOULD be an immutable object.

     ## Assumptions
     You can assume:
     - All accounts with an id greater than zero are valid. They also have sufficient funds to pay for
     any no of tickets.
     - The `TicketPaymentService` implementation is an external provider with no defects. You do not
     need to worry about how the actual payment happens.
     - The payment will always go through once a payment request has been made to the `TicketPaymentService`.
     - The `SeatReservationService` implementation is an external provider with no defects. You do not need
     to worry about how the seat reservation algorithm works.
     - The seat will always be reserved once a reservation request has been made to the `SeatReservationService`.

     ## Your Task
     Provide a working implementation of a `TicketService` that:
     - Considers the above objective, business rules, constraints & assumptions.
     - Calculates the correct amount for the requested tickets and makes a payment request to the
     `TicketPaymentService`.
     - Calculates the correct no of seats to reserve and makes a seat reservation request to the
     `SeatReservationService`.
     - Rejects any invalid ticket purchase requests. It is up to you to identify what should be
     deemed as an invalid purchase request.
     * */

    private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;
    private final TicketPurchaseValidator ticketPurchaseValidator;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService,
                             TicketPurchaseValidator ticketPurchaseValidator) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
        this.ticketPurchaseValidator = ticketPurchaseValidator;
    }

    private static final int CHILD_PRICE = 10;
    private static final int ADULT_PRICE = 20;

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {

        ticketPurchaseValidator.validate(accountId, ticketTypeRequests);

        var totalPrice = Arrays.stream(ticketTypeRequests)
                .mapToInt(this::calculateTotal)
                .sum();
        var totalSeats = Arrays.stream(ticketTypeRequests)
                .mapToInt(this::calculateSeats)
                .sum();

        paymentService.makePayment(accountId, totalPrice);
        reservationService.reserveSeat(accountId, totalSeats);
    }

    private int calculateTotal(TicketTypeRequest request) {
        int tickets = request.getNoOfTickets();

        switch(request.getTicketType()) {
            case ADULT:
                return tickets * ADULT_PRICE;
            case CHILD:
                return tickets * CHILD_PRICE;
            case INFANT:
                return 0;
        }
        return 0;
    }

    private int calculateSeats(TicketTypeRequest request) {
        if (request.getTicketType() == TicketTypeRequest.Type.INFANT) {
            return 0;
        } else {
            return request.getNoOfTickets();
        }
    }
}
