package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public interface TicketPurchaseValidator {

        void validate(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException;
}
