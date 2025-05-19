// cypress/e2e/session-form.cy.ts
describe('Session Form Page', () => {
    beforeEach(() => {
        cy.fixture('teachers.json').then((teachers) => {
            cy.intercept('GET', '/api/teacher', { body: teachers }).as('getTeachers');
            cy.loginAsAdmin();
            cy.wait('@getTeachers');
            cy.get('[data-testid="session-create-button"]').click();
        });
    });
  
    it('should fill and submit the form', () => {
      cy.get('[data-testid="session-form"]').should('be.visible');
  
      cy.get('input[formcontrolname="name"]').type('Session Cypress');
      cy.get('input[formcontrolname="date"]').type('2025-12-31');
  
      cy.fixture('teachers.json').then((teachers) => {
        const teacher = teachers[0];
        cy.get('mat-select[formControlName="teacher_id"]').click();
  
        cy.get('mat-option')
          .should('be.visible')
          .contains(`${teacher.firstName} ${teacher.lastName}`)
          .click()
        ;
        
      });
  
      cy.get('textarea[formcontrolname="description"]').type('Test via Cypress');
      cy.get('button[type="submit"]').click();
  
      cy.url().should('include', '/sessions');
      cy.contains('Session Cypress').should('exist');
    });
  });