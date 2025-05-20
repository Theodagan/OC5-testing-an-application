describe('Login spec (extended)', () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it('should disable submit button on empty submit', () => {
    cy.get('button[type=submit]').should('be.disabled')

    cy.get('input[formControlName=email]').type('test@user.com');
    cy.get('button[type=submit]').should('be.disabled')

    cy.get('input[formControlName=email]').type('');
    cy.get('input[formControlName=password]').type('wrongpass');
    cy.get('button[type=submit]').should('be.disabled')

  });

  it('should show error on invalid credentials', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Unauthorized' }
    }).as('loginFail');

    cy.get('input[formControlName=email]').type('fail@user.com');
    cy.get('input[formControlName=password]').type('wrongpass');
    cy.get('button[type=submit]').click();

    cy.wait('@loginFail');
    cy.get('.error').should('contain', 'An error occurred');
    cy.url().should('include', '/login'); // pas redirigé
  });

  it('Login successfull', () => {
    cy.fixture('user.json').then((user) => {
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