// cypress/e2e/session-form.cy.ts
describe('Session Form Page', () => {
    beforeEach(() => {
        cy.login();
        cy.visit('/sessions/new');
    });

    it('should fill and submit the form', () => {
        cy.get('input[name="title"]').type('Session Test');
        cy.get('input[name="date"]').type('2025-12-31');
        cy.get('form').submit();

        cy.url().should('include', '/sessions');
        cy.contains('Session Test').should('exist');
    });
});