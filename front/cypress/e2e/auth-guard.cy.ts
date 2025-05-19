describe('Auth Guard', () => {
    it('redirects to login if not authenticated', () => {
        cy.visit('/sessions');
        cy.url().should('include', '/login');
    });
});