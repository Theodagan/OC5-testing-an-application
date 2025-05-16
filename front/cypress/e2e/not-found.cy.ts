// cypress/e2e/not-found.cy.ts
describe('Page Not Found', () => {
    it('should display 404 message for invalid route', () => {
        cy.visit('/route/inexistante', { failOnStatusCode: false });
        cy.contains('Page not found !').should('be.visible');
    });
});