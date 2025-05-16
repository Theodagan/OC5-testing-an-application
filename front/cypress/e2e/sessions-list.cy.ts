describe('Session Creation Flow', () => {
  beforeEach(() => {
    cy.login();
  });

  it('should show list of session', () => {
    // Clique sur le bouton de navigation
    cy.contains('Rentals available').should('exist');
    cy.get('[data-testid="session-item"]').click();
  });
});