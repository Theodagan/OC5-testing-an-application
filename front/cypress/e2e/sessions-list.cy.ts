describe('Session Creation Flow', () => {

  it('should show list of session', () => {
    cy.intercept('GET', '/api/session', { body: sessions }).as('getSessions');

    cy.login();

    cy.wait('@getSessions');
    cy.contains('Rentals available').should('exist');
    cy.contains(sessions[0].name).should('exist');
  });
  
  it('shows sessions and admin actions', function () {
    cy.fixture('sessions.json').then((sessions) => {
      cy.intercept('GET', '/api/session', { body: sessions }).as('getSessions');
      cy.loginAsAdmin();
      cy.wait('@getSessions');
      cy.contains(sessions[0].name).should('exist');
      cy.contains('Create').should('exist');
    });
  });
});