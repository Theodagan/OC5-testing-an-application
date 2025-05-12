describe('Register Form', () => {
  it('should register a new user and redirect to login', () => {
    cy.visit('/register');
    cy.url().should('include', '/register');

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {
        id: 1,
        email: 'test@studio.com',
      },
    }).as('registerRequest');

    cy.get('input[type="email"]').type('test@studio.com');
    cy.get('input[name="firstName"]').type('test');
    cy.get('input[name="lastName"]').type('test');
    cy.get('input[type="password"]').type('test!1234');

    cy.get('button[type="submit"]').click();

    cy.wait('@registerRequest').then((interception) => {
        expect(interception.response.statusCode).to.eq(200)
    });
    
    cy.url().should('include', '/login');
  });
});