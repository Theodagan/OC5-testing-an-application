Cypress.Commands.add('login', () => {
    cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
        body: {
        id: 1,
        firstName: 'test',
        lastName: 'test',
        email: 'test@yoga.com',
        admin: false,
        token: 'fake-token'
    }
    }).as('login');

    cy.intercept('GET', '/api/session', []).as('getSessions');

    cy.visit('/login');

    cy.get('input[formControlName=email]').type('test@yoga.com');
    cy.get('input[formControlName=password]').type('password123{enter}');

    cy.wait('@login');
});

Cypress.Commands.add('loginAsAdmin', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 999,
        firstName: 'admin',
        lastName: 'root',
        email: 'admin@yoga.com',
        admin: true,
        token: 'admin-token'
      }
    }).as('adminLogin');
  
    cy.intercept('GET', '/api/session', []).as('getSessions');
  
    cy.visit('/login');
  
    cy.get('input[formControlName=email]').type('admin@yoga.com');
    cy.get('input[formControlName=password]').type('adminpass{enter}');
  
    cy.wait('@adminLogin');
  });