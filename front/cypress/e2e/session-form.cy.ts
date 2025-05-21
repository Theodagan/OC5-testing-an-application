describe('Session Form Page', () => {
  beforeEach(() => {
    cy.fixture('teachers.json').then((teachers) => {
      cy.intercept('GET', '**/api/teacher**', { body: teachers }).as('getTeachers');
    });

    cy.loginAsAdmin();
    cy.get('[data-testid="session-create-button"]').click();
    cy.wait('@getTeachers'); 
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
    if (Cypress.env('BACKEND_ENABLED')) {
      cy.contains('Session Cypress').should('exist');
    } else {
      cy.log('Skipping DB-persisted check (mock mode)');
    }
  });

  it('should disable submit button when form is invalid', () => {
    cy.get('[data-testid="session-form"]').within(() => {
      cy.get('button[type=submit]').should('be.disabled');
    });
  });

  it('should load session data and update it', () => {
    cy.intercept('GET', '/api/session/42', {
      id: 42,
      name: 'Initial Name',
      description: 'Initial desc',
      date: '2025-06-01',
      teacher_id: 101
    }).as('getSession');
  
    cy.intercept('PUT', '/api/session/42', {}).as('updateSession');
  
    cy.get('[data-testid="session-form"]').click();

    cy.visit('/sessions/update/42');
    cy.wait('@getSession');
  
    cy.get('input[formControlName=name]').clear().type('Updated Name');
    cy.get('textarea[formControlName=description]').clear().type('Updated description');
  
    cy.get('button[type=submit]').click();
    cy.wait('@updateSession');
  });


  it('should update a session through the UI', () => {
    // 1. Interception des sessions listées dans /sessions
    cy.intercept('GET', '/api/session', {
      body: [
        {
          id: 42,
          name: 'Session à modifier',
          description: 'Description initiale',
          date: '2025-06-01',
          teacher_id: 101,
          users: []
        }
      ]
    }).as('getSessions');
  
    // 2. Interception des données session pour le formulaire d’update
    cy.intercept('GET', '/api/session/42', {
      id: 42,
      name: 'Session à modifier',
      description: 'Description initiale',
      date: '2025-06-01',
      teacher_id: 101
    }).as('getSession');
  
    // 3. Interception du PUT de mise à jour
    cy.intercept('PUT', '/api/session/42', {}).as('updateSession');
  
    // 4. Cliquer sur "Sessions" dans la navbar (revenir à la liste)
    cy.get('data-testid="nav-sessions"').click();
  
    cy.wait('@getSessions');
  
    // 5. Cliquer sur "Edit" sur la session mockée
    cy.contains('mat-card.item', 'Session à modifier')
      .within(() => {
        cy.contains('Edit').click();
      })
    ;
  
    cy.wait('@getSession');
  
    // 6. Modifier le formulaire
    cy.get('input[formControlName=name]').clear().type('Session modifiée');
    cy.get('textarea[formControlName=description]').clear().type('Nouvelle description');
    cy.get('input[formControlName=date]').clear().type('2025-07-01');
  
    // 7. Soumettre
    cy.get('button[type=submit]').click();
    cy.wait('@updateSession');

    //8. Assertions finales
    cy.get('[data-testid="session-item"]')
    .children('mat-card.item')
    .each(($card) => {
        if ($card.find('mat-card-title').text().trim().toLowerCase() === 'Session modifiée') {
        cy.wrap($card).within(() => {
          cy.contains('Nouvelle description').should('exist');
          cy.contains('2025-07-01').should('exist');
        });
        return false; // stop .each() early
        }
    });
    
  });

});
