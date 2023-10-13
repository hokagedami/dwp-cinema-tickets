package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketPurchaseValidatorImpl implements TicketPurchaseValidator {

    private static final int MAX_TICKETS = 20;
    @Override
    public void validate(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if (accountId <= 0) {
            throw new InvalidPurchaseException("Invalid account id");
        }

        int totalTickets = 0;
        int totalAdultTickets = 0;
        int totalChildTickets = 0;
        int totalInfantTickets = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {

//            if (request.getNoOfTickets() <= 0) {
//                throw new InvalidPurchaseException("Ticket request must be greater than 0");
//            }
            totalTickets += request.getNoOfTickets();

            switch (request.getTicketType()) {
                case ADULT:
                    totalAdultTickets += request.getNoOfTickets();
                    break;
                case CHILD:
                    totalChildTickets += request.getNoOfTickets();
                    break;
                case INFANT:
                    totalInfantTickets += request.getNoOfTickets();
                    break;
            }
        }

        if (totalTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException("Too many tickets");
        }

        if (totalInfantTickets > 0 && totalAdultTickets == 0) {
            throw new InvalidPurchaseException("Infant tickets cannot be purchased without an adult ticket");
        }

        if (totalAdultTickets == 0 && totalChildTickets > 0) {
            throw new InvalidPurchaseException("Child tickets cannot be purchased without an adult ticket");
        }
    }


}
