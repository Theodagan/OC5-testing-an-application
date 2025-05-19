describe('Login spec (extended)', () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it('Login successfull', () => {
    cy.fixture('users.json').then((user) => {
      cy.intercept('POST', '/api/auth/login', {
        statusCode: 200,
        body: user
      }).as('login');

      cy.intercept('GET', '/api/session', []).as('getSessions');

      cy.get('input[formControlName=email]').type(user.email);
      cy.get('input[formControlName=password]').type('password123');
      cy.get('button[type=submit]').click();

      cy.wait('@login');
      cy.wait('@getSessions');

      cy.url().should('include', '/sessions');
    });
  });
});