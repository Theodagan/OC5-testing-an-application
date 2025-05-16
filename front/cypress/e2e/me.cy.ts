describe('Me Page', () => {
  const mockSession = {
    id: 1,
    username: 'test',
    firstName: 'test',
    lastName: 'test',
    admin: false,
    token: '',
    type: ''
  };

  const mockUser = {
    ...mockSession,
    email: 'test@yoga.com',
    password: 'mockPassword',
    createdAt: '2023-10-27T10:00:00.000Z',
    updatedAt: '2023-10-27T10:00:00.000Z'
  };

  it('should display me page if session is present', () => {
    cy.intercept('GET', '/api/user/1', mockUser).as('getUser');

    cy.visit('/me');

    // set the session inside the Angular context
    cy.window().then((win: any) => {
      const app = win.getAllAngularRootElements()[0];
      const injector = app.injector || win.ng?.getInjector?.(app);
      const sessionService = injector?.get?.(win.ng.getComponent(app).sessionService);

      expect(sessionService).to.exist;
      sessionService.logIn(mockSession);
    });

    // re-renders with session
    cy.reload();

    // Assert data 
    cy.wait('@getUser');
    cy.contains('test@yoga.com').should('exist');
    cy.url().should('include', '/me');
  });

  it('should redirect to login if no session', () => {
    cy.visit('/me');
    cy.url().should('include', '/login');
  });
});