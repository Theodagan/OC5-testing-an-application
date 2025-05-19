describe('JWT Interceptor', () => {
    it('injects token from localStorage into headers', () => {
        cy.intercept('POST', '/api/auth/login', {
            statusCode: 200,
            body: {
              id: 1,
              email: 'test@yoga.com',
              firstName: 'Test',
              lastName: 'User',
              admin: false,
              token: 'fake-token'
            }
          }).as('login');
          
          cy.visit('/login');
          cy.get('input[formControlName=email]').type('test@yoga.com');
          cy.get('input[formControlName=password]').type('testpass');
          cy.get('button[type=submit]').click();
          cy.wait('@login');
          
          cy.intercept('GET', '/api/session', (req) => {
            expect(req.headers.authorization).to.equal('Bearer fake-token');
            req.reply([]);
          }).as('getSessions');
          
          cy.visit('/sessions');
          cy.wait('@getSessions');
    });
    it('does NOT inject token if not logged in', () => {
      // Aucune session posée
  
      cy.intercept('GET', '/api/session', (req) => {
        expect(req.headers).to.not.have.property('authorization');
        req.reply([]);
      }).as('getSessionsNoAuth');
  
      cy.visit('/sessions'); // doit déclencher l’appel sans header auth
      cy.wait('@getSessionsNoAuth');
    });
});