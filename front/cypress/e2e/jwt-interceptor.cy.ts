describe('JWT Interceptor', () => {
    it('injects token from localStorage into headers', () => {
        const token = 'mocked-jwt-token';
        window.localStorage.setItem('token', token);

        cy.intercept('GET', '/api/session', (req) => {
        expect(req.headers.authorization).to.equal(`Bearer ${token}`);
        req.reply([]);
        }).as('getSessions');

        cy.visit('/sessions'); 
        cy.wait('@getSessions');
    });
});