describe('Session Creation Flow', () => {

  it('should show list of session', () => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [
        { id: 1, name: 'Yoga 101', date: '2024-01-01' }
      ]
    }).as('getSessions');

    cy.login();

    cy.wait('@getSessions');
    // Clique sur le bouton de navigation
    cy.contains('Rentals available').should('exist');
    cy.get('[data-testid="session-item"]').should('exist')
  });

  it('shows sessions and admin actions', () => {
    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [
        { id: 1, name: 'Yoga 101', date: '2024-01-01' }
      ]
    }).as('getSessions');

    cy.loginAsAdmin();

    cy.wait('@getSessions');
    cy.contains('Yoga 101').should('exist');
    cy.contains('Create').should('exist');
  });
});