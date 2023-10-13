# Ticket Service
## Description
This project implements a ticket purchase service that allows customers to buy different types of tickets (adult, child, infant) with the following requirements and constraints:
- Customers can purchase adult, child and infant tickets
- Tickets have fixed pricing based on type
- Customers must purchase at least 1 adult ticket to buy child/infant
- Only a max of 20 tickets can be purchased at once
- Invalid ticket requests should be rejected

## Assumptions
- All accounts with an id greater than zero are valid. They also have sufficient funds to pay for any no of tickets.
- The `TicketPaymentService` implementation is an external provider with no defects. You do not need to worry about how the actual payment happens.
- The payment will always go through once a payment request has been made to the `TicketPaymentService`.
- The `SeatReservationService` implementation is an external provider with no defects. You do not need to worry about how the seat reservation algorithm works.
- The seat will always be reserved once a reservation request has been made to the `SeatReservationService`.

## Implementation
- `TicketService` contains the main purchase tickets logic
- `TicketTypeRequest` immutable data class for ticket request
- `TicketPaymentService` and SeatReservationService interfaces to external services
- `InvalidPurchaseException` custom exception type

Key concepts used:
- Immutability using Java records
- Validation of business rules
- Delegation to external services
- Unit testing with JUnit 5

## Running the tests
The TicketServiceTest class contains JUnit tests covering the key scenarios and requirements.

To run all tests:
``` mvn test ```

## Next Steps
Some potential next steps for improvements:

- Implement integration tests against real payment/reservation services
- Build out REST API layer on top to expose purchase functionality
- Persist tickets purchased to database
- Add additional validations around payment accounts
- Improve exception handling and input validation