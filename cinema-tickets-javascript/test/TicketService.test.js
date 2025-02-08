import { describe, it, beforeEach, expect, jest } from '@jest/globals';
import TicketService from '../src/pairtest/TicketService.js';
import InvalidPurchaseException from '../src/pairtest/lib/InvalidPurchaseException.js';

describe('TicketService', () => {
    let ticketService;
    let mockPaymentService;
    let mockReservationService;

    beforeEach(() => {
        mockPaymentService = {
            makePayment: jest.fn()
        };
        mockReservationService = {
            reserveSeat: jest.fn()
        };
        ticketService = new TicketService(mockPaymentService, mockReservationService);
    });

    const createTicketRequest = (type, quantity) => {
        return {
            getTicketType: () => type,
            getNoOfTickets: () => quantity
        };
    };

    describe('Valid purchases', () => {
        it('should process a valid adult ticket purchase', () => {
            const request = createTicketRequest('ADULT', 1);
            ticketService.purchaseTickets(1, request);

            expect(mockPaymentService.makePayment).toHaveBeenCalledWith(1, 25);
            expect(mockReservationService.reserveSeat).toHaveBeenCalledWith(1, 1);
        });

        it('should process a valid mixed ticket purchase', () => {
            const requests = [
                createTicketRequest('ADULT', 2),
                createTicketRequest('CHILD', 2),
                createTicketRequest('INFANT', 1)
            ];
            ticketService.purchaseTickets(1, ...requests);

            expect(mockPaymentService.makePayment).toHaveBeenCalledWith(1, 80); // 2*25 + 2*15
            expect(mockReservationService.reserveSeat).toHaveBeenCalledWith(1, 4); // 2 adults + 2 children
        });

        it('should handle multiple requests of the same type', () => {
            const requests = [
                createTicketRequest('ADULT', 2),
                createTicketRequest('ADULT', 1)
            ];
            ticketService.purchaseTickets(1, ...requests);

            expect(mockPaymentService.makePayment).toHaveBeenCalledWith(1, 75); // 3*25
            expect(mockReservationService.reserveSeat).toHaveBeenCalledWith(1, 3);
        });
    });

    describe('Invalid purchases', () => {
        it('should reject invalid account IDs', () => {
            const request = createTicketRequest('ADULT', 1);
            expect(() => ticketService.purchaseTickets(0, request))
                .toThrow(InvalidPurchaseException);
            expect(() => ticketService.purchaseTickets(-1, request))
                .toThrow(InvalidPurchaseException);
        });

        it('should reject purchases without tickets', () => {
            expect(() => ticketService.purchaseTickets(1))
                .toThrow(InvalidPurchaseException);
        });

        it('should reject purchases exceeding 25 tickets', () => {
            const request = createTicketRequest('ADULT', 26);
            expect(() => ticketService.purchaseTickets(1, request))
                .toThrow(InvalidPurchaseException);
        });

        it('should reject child tickets without adult tickets', () => {
            const request = createTicketRequest('CHILD', 1);
            expect(() => ticketService.purchaseTickets(1, request))
                .toThrow(InvalidPurchaseException);
        });

        it('should reject infant tickets without adult tickets', () => {
            const request = createTicketRequest('INFANT', 1);
            expect(() => ticketService.purchaseTickets(1, request))
                .toThrow(InvalidPurchaseException);
        });

        it('should reject more infants than adults', () => {
            const requests = [
                createTicketRequest('ADULT', 1),
                createTicketRequest('INFANT', 2)
            ];
            expect(() => ticketService.purchaseTickets(1, ...requests))
                .toThrow(InvalidPurchaseException);
        });

        it('should reject invalid ticket types', () => {
            const request = createTicketRequest('INVALID', 1);
            expect(() => ticketService.purchaseTickets(1, request))
                .toThrow(InvalidPurchaseException);
        });

        it('should reject invalid ticket quantities', () => {
            const request = createTicketRequest('ADULT', 0);
            expect(() => ticketService.purchaseTickets(1, request))
                .toThrow(InvalidPurchaseException);
        });
    });

    describe('Edge cases', () => {
        it('should handle maximum allowed purchase', () => {
            const request = createTicketRequest('ADULT', 25);
            expect(() => ticketService.purchaseTickets(1, request))
                .not.toThrow();
        });

        it('should handle purchase with maximum infants', () => {
            const requests = [
                createTicketRequest('ADULT', 2),
                createTicketRequest('INFANT', 2)
            ];
            expect(() => ticketService.purchaseTickets(1, ...requests))
                .not.toThrow();
        });
    });
});