describe('Me Page', () => {
  it('should redirect to login if no session', () => {
    cy.visit('/me');
    cy.url().should('include', '/login');
  });

  it('should display me page if session is present', () => {
    const session = {
      id: '1',
      email: 'yoga@studio.com',
      firstName: 'yoga',
      lastName: 'studio',
      admin: true,
    };
    localStorage.setItem('session', JSON.stringify(session));
    cy.visit('/me');
    cy.url().should('include', '/me');
    cy.contains('Logout');
  });
});