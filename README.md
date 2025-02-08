# Cinema Tickets

## Project Description

This project is a coding exercise designed to demonstrate the implementation of a ticket purchasing system for a cinema. The system allows users to purchase tickets for different types of attendees (Infant, Child, and Adult) while adhering to specific business rules and constraints. The implementation includes a `TicketService` class that interacts with external services for payment processing and seat reservation.

## Business Rules

- There are three types of tickets: Infant, Child, and Adult.
- The ticket prices are as follows:
    - **INFANT**: £0 (no seat allocated)
    - **CHILD**: £15
    - **ADULT**: £25
- Multiple tickets can be purchased at once, with a maximum limit of 25 tickets per transaction.
- Child and Infant tickets cannot be purchased without at least one Adult ticket.
- The system uses existing services for payment processing (`TicketPaymentService`) and seat reservation (`SeatReservationService`).

## Implementations

- **Java**: [cinema-tickets-java](./cinema-tickets-java)
- **JavaScript**: [cinema-tickets-javascript](./cinema-tickets-javascript)
- **Python**: [cinema-tickets-python](./cinema-tickets-python)


## JavaScript Implementation

## Installation

To set up the project locally, follow these steps:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/hokagedami/dwp-cinema-tickets.git
   cd cinema-tickets-javascript
   ```

2. **Install dependencies**:
   Make sure you have Node.js installed. Then run:
   ```bash
   npm install
   ```

3. **Run tests**:
   To ensure everything is working correctly, run the test suite:
   ```bash
   npm test
   ```

## Usage

The `TicketService` class is the main component of the ticket purchasing system. It provides a method to purchase tickets, which validates the requests and interacts with the payment and reservation services.

### Example Usage

Here’s an example of how to use the `TicketService`:

```javascript
import TicketService from './src/pairtest/TicketService.js';
import TicketTypeRequest from './src/pairtest/lib/TicketTypeRequest.js';

// Create an instance of the TicketService
const ticketService = new TicketService();

// Create ticket requests
const adultRequest = new TicketTypeRequest('ADULT', 1);
const childRequest = new TicketTypeRequest('CHILD', 2);

// Purchase tickets
try {
ticketService.purchaseTickets(1, adultRequest, childRequest);
console.log('Tickets purchased successfully!');
} catch (error) {
console.error('Error purchasing tickets:', error.message);
}
```


## TicketService Implementation

The `TicketService` class is responsible for:

- Validating ticket purchase requests.
- Calculating the total amount for the requested tickets.
- Making payment requests to the `TicketPaymentService`.
- Reserving seats through the `SeatReservationService`.

### Key Methods

- `purchaseTickets(accountId, ...ticketTypeRequests)`: Validates and processes the ticket purchase.

### Validation Rules

The following validation rules are enforced:

- Account ID must be greater than zero.
- A maximum of 25 tickets can be purchased at once.
- Child and Infant tickets cannot be purchased without an Adult ticket.
- Invalid ticket types and quantities are rejected.

## Testing

The project includes a comprehensive test suite using Jest. The tests cover various scenarios, including:

- Valid purchases for different ticket types.
- Invalid purchase requests (e.g., invalid account IDs, exceeding ticket limits).
- Edge cases (e.g., maximum allowed purchases).

To run the tests, use the following command:

```bash
npm test
```

## Technologies

The project is implemented in JavaScript and uses the Jest testing framework. It demonstrates the use of classes, modules, and unit testing in a Node.js environment.

