// cypress/e2e/session-form.cy.ts
describe('Session Form Page', () => {
    beforeEach(() => {
        cy.loginAsAdmin();
        // Clique sur le bouton de navigation    
        cy.get('[data-testid="session-create-button"]').click();
    });
    
    it('should fill and submit the form', () => {
        cy.get('[data-testid="session-form"]').should('be.visible');
        // REMPLIR le formulaire
        cy.get('input[formcontrolname="name"]').type('Session Cypress');
        cy.get('input[formcontrolname="date"]').type('2025-12-31');
    
        cy.get('mat-select[formcontrolname="teacher_id"]').click();
        cy.get('mat-option').should('be.visible').first().click();
            
        cy.get('textarea[formcontrolname="description"]').type('Test via Cypress');
    
        // SOUMETTRE
        cy.get('button[type="submit"]').click();
    
        // ASSERT sur le résultat
        cy.url().should('include', '/sessions');
        cy.contains('Session Cypress').should('exist');
    });
});