describe('Register Form', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('should register a new user and redirect to login', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {
        id: 1,
        email: 'test@studio.com',
        firstName: 'John',
        lastName: 'Doe'
      }
    }).as('registerRequest');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('test@studio.com');
    cy.get('input[formControlName=password]').type('StrongPass123');

    cy.get('button[type=submit]').click();
    cy.wait('@registerRequest');

    cy.url().should('include', '/login');
  });

  it('should show validation errors when required fields are empty', () => {
    cy.get('button[type=submit]').should('be.disabled');
    // Facultatif : taper puis effacer pour déclencher validation
    cy.get('input[formControlName=email]').type('a').clear();
    cy.get('button[type=submit]').should('be.disabled');
  });

  it('should show error message if API registration fails', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already in use' }
    }).as('registerError');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('duplicate@studio.com');
    cy.get('input[formControlName=password]').type('StrongPass123');

    cy.get('button[type=submit]').click();
    cy.wait('@registerError');

    cy.get('.error').should('contain', 'An error occurred');
  });
});