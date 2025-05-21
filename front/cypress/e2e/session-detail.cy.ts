describe('Session Detail Page', () => {
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

        if (this.currentTest?.title.toLowerCase().includes('admin')) {
            cy.loginAsAdmin();
            return;
        }
        else{
            cy.login(); 
        }
        cy.wait('@getSessions');

        cy.get('[data-testid="session-item"]')
        .children('mat-card.item')
        .each(($card) => {
            if ($card.find('mat-card-title').text().trim().toLowerCase() === session.name.toLowerCase()) {
            cy.wrap($card).within(() => {
                cy.contains('Detail').click();
            });
            return false; // stop .each() early
            }
        });
    });
  
    it('navigates to detail view via "Detail" button and displays session info', () => {
        cy.wait('@getSession');
        cy.wait('@getTeacher');
      
        cy.get('h1')
        .invoke('text')
        .then((text) => {
            expect(text.trim().toLowerCase()).to.eq(session.name.toLowerCase());
        });

        cy.contains('attendees')
        .invoke('text')
        .then((text) => {
            const attendeesCount = session.users.length.toString();
            expect(text.toLowerCase()).to.include(attendeesCount.toLowerCase());
        });
        
        cy.get('[data-testid="teacher-name"]')
        .invoke('text')
        .then((text) => {
            const teacherName = `${teacher.firstName} ${teacher.lastName}`;
            expect(text.toLowerCase()).to.eq(teacherName.toLowerCase());
        });

        cy.get('[data-testid="description"]')
        .invoke('text')
        .then((text) => {
            expect(text.trim().toLowerCase()).to.include(session.description.toLowerCase());

        });
    });
  
    it('shows correct participate button based on user status', () => {
        cy.wait('@getSession');

        const isParticipant = session.users.includes(userId);
        cy.get('button').contains(isParticipant ? 'Do not participate' : 'Participate').should('exist');
    });
  
    it('allows toggling participation', () => {  
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

        cy.wait('@getSession');

        cy.intercept('DELETE', `/api/session/${session.id}`, {}).as('delete');
        cy.get('[data-testid="teacher-name"]').click();
        cy.wait('@delete');
        cy.url().should('include', '/sessions');
    });
});