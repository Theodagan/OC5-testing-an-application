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
    cy.login();

    cy.intercept('GET', '/api/user/1', mockUser).as('getUser');

    cy.visit('/me');
    cy.wait('@getUser');

    cy.contains('test@yoga.com').should('exist');
    cy.url().should('include', '/me');
  });

  it('should redirect to login if no session', () => {
    cy.clearCookies(); // Si ta session passe par cookie
    cy.visit('/me');
    cy.url().should('include', '/login');
  });
});