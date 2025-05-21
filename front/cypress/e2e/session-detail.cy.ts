describe('Session Detail Page — Dynamic & Accurate', () => {
    let session: any;
    let teacher: any;
    let userId = 1;
  
    beforeEach(() => {
      cy.fixture('sessions.json').then((sessions) => {
        session = sessions[0];
        cy.intercept('GET', '/api/session', { body: sessions }).as('getSessions');
        cy.intercept('GET', `/api/session/${session.id}`, session).as('getSession');
      });
  
      cy.fixture('teachers.json').then((teachers) => {
        teacher = teachers.find(t => t.id === session.teacher_id);
        cy.intercept('GET', `/api/teacher/${teacher.id}`, teacher).as('getTeacher');
      });
  
      cy.login(); 
      cy.wait('@getSessions');
    });
  
    it('navigates to detail view via "Detail" button and displays session info', () => {
      cy.get('[data-testid="session-item"]')
        .contains(session.name)
        .parents('mat-card.item')
        .within(() => {
          cy.contains('Detail').click();
        });
  
      cy.wait('@getSession');
      cy.wait('@getTeacher');
  
      cy.get('h1').should('contain.text', session.name);
      cy.contains(`${teacher.firstName} ${teacher.lastName}`).should('exist');
      cy.contains('attendees').should('contain.text', session.users.length.toString());
      cy.contains(session.description).should('exist');
    });
  
    it('shows correct participate button based on user status', () => {
      cy.get('[data-testid="session-item"]')
        .contains(session.name)
        .parents('mat-card.item')
        .within(() => {
          cy.contains('Detail').click();
        });
  
      cy.wait('@getSession');
  
      const isParticipant = session.users.includes(userId);
      cy.get('button').contains(isParticipant ? 'Do not participate' : 'Participate').should('exist');
    });
  
    it('allows toggling participation', () => {
      cy.get('[data-testid="session-item"]')
        .contains(session.name)
        .parents('mat-card.item')
        .within(() => {
          cy.contains('Detail').click();
        });
  
      cy.wait('@getSession');
  
      if (session.users.includes(userId)) {
        cy.intercept('DELETE', `/api/session/${session.id}/participate/${userId}`, {}).as('unParticipate');
        cy.get('button').contains('Do not participate').click();
        cy.wait('@unParticipate');
      } else {
        cy.intercept('POST', `/api/session/${session.id}/participate/${userId}`, {}).as('participate');
        cy.get('button').contains('Participate').click();
        cy.wait('@participate');
      }
    });
  
    it('admin can delete the session', () => {
      cy.loginAsAdmin();
      cy.wait('@getSessions');
  
      cy.get('[data-testid="session-item"]')
        .contains(session.name)
        .parents('mat-card.item')
        .within(() => {
          cy.contains('Detail').click();
        });
  
      cy.wait('@getSession');
  
      cy.intercept('DELETE', `/api/session/${session.id}`, {}).as('delete');
      cy.get('button').contains('Delete').click();
      cy.wait('@delete');
      cy.url().should('include', '/sessions');
    });
  });