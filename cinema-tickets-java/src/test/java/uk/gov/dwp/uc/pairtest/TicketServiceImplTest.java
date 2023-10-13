package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.validation.TicketPurchaseValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {
    @Mock
    private TicketPaymentService paymentServiceMock;
    @Mock
    private SeatReservationService reservationServiceMock;

    @Mock
    private TicketPurchaseValidator ticketPurchaseValidatorMock;
    @InjectMocks
    private TicketServiceImpl ticketServiceImpl;


    /* Happy Path Tests */

    //  Should properly purchase adult tickets
    @Test
    public void testPurchaseAdultTickets() {
        // given
        var ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        var ticketTypeRequests = new TicketTypeRequest[]{ticketTypeRequest};
        var accountId = 1L;

        // when
        ticketServiceImpl.purchaseTickets(accountId, ticketTypeRequests);

        // then
        verify(reservationServiceMock, times(1)).reserveSeat(accountId, 1);
        verify(paymentServiceMock, times(1)).makePayment(accountId, 20);
        verifyNoMoreInteractions(reservationServiceMock);
        verifyNoMoreInteractions(paymentServiceMock);

    }

    //  Should properly purchase child tickets
    @Test
    public void testPurchaseChildTickets() {
        // given
        var adultTicketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        var childTicketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        var ticketTypeRequests = new TicketTypeRequest[]{adultTicketTypeRequest, childTicketTypeRequest};
        var accountId = 1L;

        // when
        ticketServiceImpl.purchaseTickets(accountId, ticketTypeRequests);

        // then
        verify(reservationServiceMock, times(1)).reserveSeat(accountId, 2);
        verify(paymentServiceMock, times(1)).makePayment(accountId, 30);
        verifyNoMoreInteractions(reservationServiceMock);
        verifyNoMoreInteractions(paymentServiceMock);
        verifyNoMoreInteractions(reservationServiceMock);
    }

    //  Should properly purchase infant tickets
    @Test
    public void testPurchaseInfantTickets() {
        // given
        var adultTicketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        var infantTicketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        var ticketTypeRequests = new TicketTypeRequest[]{adultTicketTypeRequest, infantTicketTypeRequest};
        var accountId = 1L;

        // when
        ticketServiceImpl.purchaseTickets(accountId, ticketTypeRequests);

        // then
        verify(reservationServiceMock, times(1)).reserveSeat(accountId, 2);
        verify(paymentServiceMock, times(1)).makePayment(accountId, 30);
        verifyNoMoreInteractions(reservationServiceMock);
        verifyNoMoreInteractions(paymentServiceMock);
        verifyNoMoreInteractions(reservationServiceMock);
    }

    //  Should properly purchase mix of all ticket types
    @Test
    public void testPurchaseMixOfAllTicketTypes() {
        var adultTicketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        var childTicketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        var infantTicketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        var ticketTypeRequests = new TicketTypeRequest[]{adultTicketTypeRequest, childTicketTypeRequest,
                infantTicketTypeRequest};
        var accountId = 1L;

        // when
        ticketServiceImpl.purchaseTickets(accountId, ticketTypeRequests);

        // then
        verify(paymentServiceMock, times(1)).makePayment(accountId, 30);
        verify(reservationServiceMock, times(1)).reserveSeat(accountId, 2);
    }

    //  Should make correct payment for ticket purchase
    @Test
    public void testMakeCorrectPaymentForTicketPurchase() {
        // given
        var ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        var ticketTypeRequests = new TicketTypeRequest[]{ticketTypeRequest};
        var accountId = 1L;

        // when
        ticketServiceImpl.purchaseTickets(accountId, ticketTypeRequests);

        // then
        verify(paymentServiceMock, times(1)).makePayment(accountId, 20);
    }

    //  Should make correct seat reservation for ticket purchase
    @Test
    public void testMakeCorrectSeatReservationForTicketPurchase() {
        // given
        var ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        var ticketTypeRequests = new TicketTypeRequest[]{ticketTypeRequest};
        var accountId = 1L;

        // when
        ticketServiceImpl.purchaseTickets(accountId, ticketTypeRequests);

        // then
        verify(reservationServiceMock, times(1)).reserveSeat(accountId, 2);
    }


    /* Validation Tests */

    //  Should reject purchase when total tickets exceeds max
    @Test
    public void testRejectPurchaseWhenTotalTicketsExceedsMax() {
        // given
        var ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 21);
        var ticketTypeRequests = new TicketTypeRequest[]{ticketTypeRequest};
        var accountId = 1L;

        // when
        doThrow(new InvalidPurchaseException("Too many tickets"))
                .when(ticketPurchaseValidatorMock).validate(accountId, ticketTypeRequests);

        // then
        assertThrows(InvalidPurchaseException.class,
                () -> ticketServiceImpl.purchaseTickets(accountId, ticketTypeRequests));

    }

    //  Should reject purchase with child tickets but no adult
    @Test
    public void testRejectPurchaseWithChildTicketsButNoAdult() {
        // given
        var ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10);
        var ticketTypeRequests = new TicketTypeRequest[]{ticketTypeRequest};
        var accountId = 1L;

        // when
        doThrow(new InvalidPurchaseException("Child tickets cannot be purchased without an adult ticket"))
                .when(ticketPurchaseValidatorMock).validate(accountId, ticketTypeRequests);

        // then
        assertThrows(InvalidPurchaseException.class,
                () -> ticketServiceImpl.purchaseTickets(accountId, ticketTypeRequests));
    }

    //  Should reject purchase with infant tickets but no adult
    @Test
    public void testRejectPurchaseWithInfantTicketsButNoAdult() {
        // given
        var ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 10);
        var ticketTypeRequests = new TicketTypeRequest[]{ticketTypeRequest};
        var accountId = 1L;

        // when
        doThrow(new InvalidPurchaseException("Infant tickets cannot be purchased without an adult ticket"))
                .when(ticketPurchaseValidatorMock).validate(accountId, ticketTypeRequests);

        // then
        assertThrows(InvalidPurchaseException.class,
                () -> ticketServiceImpl.purchaseTickets(accountId, ticketTypeRequests));
    }

    //  Should reject purchase when no tickets requested
    @Test
    public void testRejectPurchaseWhenNoTicketsRequested() {
        // given
        var ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        var ticketTypeRequests = new TicketTypeRequest[]{ticketTypeRequest};
        var accountId = 1L;

        // when
        doThrow(new InvalidPurchaseException("Ticket request must be greater than 0"))
                .when(ticketPurchaseValidatorMock).validate(accountId, ticketTypeRequests);

        // then
        assertThrows(InvalidPurchaseException.class,
                () -> ticketServiceImpl.purchaseTickets(accountId, ticketTypeRequests));
    }

    //  Should reject purchase with negative number of tickets
    @Test
    public void testRejectPurchaseWithNegativeNumberOfTickets() {
        // given
        var ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -6);
        var ticketTypeRequests = new TicketTypeRequest[]{ticketTypeRequest};
        var accountId = 1L;

        // when
        doThrow(new InvalidPurchaseException("Ticket request must be greater than 0"))
                .when(ticketPurchaseValidatorMock).validate(accountId, ticketTypeRequests);

        // then
        assertThrows(InvalidPurchaseException.class,
                () -> ticketServiceImpl.purchaseTickets(accountId, ticketTypeRequests));
    }
}