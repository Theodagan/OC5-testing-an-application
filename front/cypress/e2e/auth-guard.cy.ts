describe('Auth Guard', () => {
    it('should redirect to login if not authenticated', () => {
      cy.visit('/sessions'); // route protégée
      cy.url().should('include', '/login');
    });
  
    it('should allow access if authenticated', () => {
      cy.login(); // simule POST + set session info
      cy.url().should('include', '/sessions');
    });
  });