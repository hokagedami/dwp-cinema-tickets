import InvalidPurchaseException from "./lib/InvalidPurchaseException";
import TicketPaymentService from "../thirdparty/paymentgateway/TicketPaymentService.js";
import SeatReservationService from "../thirdparty/seatbooking/SeatReservationService.js";

export default class TicketService {
  constructor(paymentService = new TicketPaymentService(), reservationService = new SeatReservationService()) {
    this._paymentService = paymentService;
    this._reservationService = reservationService;
    this._ticketPrices = {
      INFANT: 0,
      CHILD: 15,
      ADULT: 25
    };
  }

  /**
   * Purchase tickets for the given account
   * @param {number} accountId - The account ID making the purchase
   * @param {...TicketTypeRequest} ticketTypeRequests - The ticket requests
   * @throws {InvalidPurchaseException} If the purchase request is invalid
   */
  purchaseTickets(accountId, ...ticketTypeRequests) {
    this._validateAccountId(accountId);
    this._validateTicketRequests(ticketTypeRequests);

    const ticketCounts = this._aggregateTicketCounts(ticketTypeRequests);
    this._validatePurchaseRules(ticketCounts);

    const totalAmount = this._calculateTotalAmount(ticketCounts);
    const totalSeats = this._calculateTotalSeats(ticketCounts);

    this._paymentService.makePayment(accountId, totalAmount);
    this._reservationService.reserveSeat(accountId, totalSeats);
  }

  /**
   * Validate the account ID
   * @private
   */
  _validateAccountId(accountId) {
    if (!Number.isInteger(accountId) || accountId <= 0) {
      throw new InvalidPurchaseException('Invalid account ID');
    }
  }

  /**
   * Validate the ticket requests array
   * @private
   */
  _validateTicketRequests(requests) {
    if (!requests || requests.length === 0) {
      throw new InvalidPurchaseException('No tickets requested');
    }

    if (requests.length > 25) {
      throw new InvalidPurchaseException('Maximum 25 tickets per purchase');
    }

    requests.forEach(request => {
      if (!request || !request.getTicketType || !request.getNoOfTickets) {
        throw new InvalidPurchaseException('Invalid ticket request format');
      }

      const ticketType = request.getTicketType();
      const quantity = request.getNoOfTickets();

      if (!this._ticketPrices.hasOwnProperty(ticketType)) {
        throw new InvalidPurchaseException(`Invalid ticket type: ${ticketType}`);
      }

      if (!Number.isInteger(quantity) || quantity <= 0) {
        throw new InvalidPurchaseException('Invalid ticket quantity');
      }
    });
  }

  /**
   * Aggregate ticket counts by type
   * @private
   */
  _aggregateTicketCounts(requests) {
    return requests.reduce((counts, request) => {
      const type = request.getTicketType();
      const quantity = request.getNoOfTickets();
      counts[type] = (counts[type] || 0) + quantity;
      return counts;
    }, {});
  }

  /**
   * Validate business rules for the purchase
   * @private
   */
  _validatePurchaseRules(ticketCounts) {
    const totalTickets = Object.values(ticketCounts).reduce((sum, count) => sum + count, 0);
    if (totalTickets > 25) {
      throw new InvalidPurchaseException('Maximum 25 tickets per purchase');
    }

    const adultCount = ticketCounts.ADULT || 0;
    const childCount = ticketCounts.CHILD || 0;
    const infantCount = ticketCounts.INFANT || 0;

    if ((childCount > 0 || infantCount > 0) && adultCount === 0) {
      throw new InvalidPurchaseException('Child and infant tickets require an adult ticket');
    }

    if (infantCount > adultCount) {
      throw new InvalidPurchaseException('Cannot have more infants than adults');
    }
  }

  /**
   * Calculate total amount to pay
   * @private
   */
  _calculateTotalAmount(ticketCounts) {
    return Object.entries(ticketCounts).reduce((total, [type, count]) => {
      return total + (this._ticketPrices[type] * count);
    }, 0);
  }

  /**
   * Calculate total seats to reserve
   * @private
   */
  _calculateTotalSeats(ticketCounts) {
    return (ticketCounts.ADULT || 0) + (ticketCounts.CHILD || 0);
  }
}