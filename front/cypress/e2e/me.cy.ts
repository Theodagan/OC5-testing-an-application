describe('Me Page', () => {
  const mockUser = {
    id: 1,
    firstName: 'test',
    lastName: 'test',
    email: 'test@yoga.com',
    admin: false,
    token: 'fake-token'
  };

  it('should display me page if session is present', () => {
    cy.intercept('GET', '/api/user/1', mockUser).as('getUser');
    cy.login();

    cy.url().should('include', '/sessions');

    cy.get('[data-testid="nav-account"]').click();
    cy.wait('@getUser');

    cy.contains('test@yoga.com').should('exist');
  });

  it('should redirect to login if no session', () => {
    cy.clearCookies();
    cy.clearLocalStorage();
    cy.visit('/me');
    cy.url().should('include', '/login');
  });
});