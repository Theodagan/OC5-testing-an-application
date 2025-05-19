describe('Unauth Guard', () => {
    it('should allow access to /login if not authenticated', () => {
      cy.visit('/login');
      cy.url().should('include', '/login');
    });
  
    it('should redirect to /sessions if already authenticated', () => {
      cy.login(); // simule un user déjà connecté
      cy.visit('/login'); // page non accessible aux connectés
      cy.url().should('include', '/sessions'); // redirigé
    });
});