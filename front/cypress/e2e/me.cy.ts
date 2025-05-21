describe('Me Page', () => {
  const mockUser = {
    id: 1,
    firstName: 'test',
    lastName: 'test',
    email: 'test@yoga.com',
    admin: false,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    token: 'fake-token'
  };

  beforeEach(() => {
    cy.intercept('GET', '/api/user/1', mockUser).as('getUser');
    cy.login(); // mock login that sets session info + token

    cy.get('[data-testid="nav-account"]').click(); // navigate to /me
    cy.wait('@getUser');
  });

  it('should display user info', () => {
    cy.contains(mockUser.email).should('exist');
    cy.contains('Delete my account:').should('exist');
  });

  it('should call back() when back button is clicked', () => {
    cy.window().then((win) => {
      cy.stub(win.history, 'back').as('historyBack');
    });

    cy.get('button mat-icon')
      .contains('arrow_back')
      .closest('button')
      .click();

    cy.get('@historyBack').should('have.been.calledOnce');
  });

  it('should delete the account and redirect to home', () => {
    cy.intercept('DELETE', '/api/user/1', { statusCode: 200 }).as('deleteUser');
    cy.intercept('POST', '/api/logout', { statusCode: 200 }); // if needed

    cy.get('button mat-icon')
      .contains('delete')
      .closest('button')
      .click();

    cy.wait('@deleteUser');

    // Vérifie le snackbar (si visible dans DOM)
    cy.contains('Your account has been deleted !').should('exist');

    // Vérifie redirection vers l'accueil
    cy.url().should('eq', Cypress.config().baseUrl);
  });
});