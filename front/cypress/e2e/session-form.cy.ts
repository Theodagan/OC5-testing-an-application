// cypress/e2e/session-form.cy.ts
describe('Session Form Page', () => {
  
    it('should fill and submit the form', () => {

        cy.fixture('teachers.json').then((teachers) => {
            const teacher = teachers[0];

            cy.intercept('GET', '/api/session', { body: teachers }).as('getTeachers');
            cy.loginAsAdmin();
            cy.wait('@getTeachers');
            cy.get('[data-testid="session-create-button"]').click();

            //Debut

            cy.get('[data-testid="session-form"]').should('be.visible');
  
            cy.get('input[formcontrolname="name"]').type('Session Cypress');
            cy.get('input[formcontrolname="date"]').type('2025-12-31');
        
            cy.get('mat-select[formControlName="teacher_id"]').click();
    
            cy.get('mat-option')
            .should('be.visible')
            .contains(`${teacher.firstName} ${teacher.lastName}`)
            .click();
                
        
            cy.get('textarea[formcontrolname="description"]').type('Test via Cypress');
            cy.get('button[type="submit"]').click();
        
            cy.url().should('include', '/sessions');
            cy.contains('Session Cypress').should('exist');
        });
    });
  });