describe('JWT Interceptor', () => {
    it('includes token in header', () => {
        window.localStorage.setItem('token', 'mocked-jwt-token');

        cy.intercept({
            method: 'GET',
            url: '/api/session'
        }, (req) => {
            expect(req.headers.authorization).to.equal('Bearer mocked-jwt-token');
            req.reply([]);
        }).as('getSessions');

        cy.login();
        cy.wait('@getSessions');
    });
});