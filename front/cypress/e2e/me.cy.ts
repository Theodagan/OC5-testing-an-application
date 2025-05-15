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

  beforeEach(() => {
    // Intercept user fetch
    cy.intercept('GET', '/api/user/1', mockUser).as('getUser');
  });

  it('should display me page if session is present', () => {
    cy.visit('/');

    // Simulate logged-in session using window object
    cy.window().then((win: any) => {
      const ng = win.ng; // Angular debug hook (optional, works better in non-prod mode)
      const app = win.getAllAngularRootElements?.()[0];
      const injector = app?.injector ?? ng?.getInjector(app);
      const sessionService = injector?.get?.(ng?.getComponent?.(app)?.sessionService) ?? undefined;

      if (sessionService && sessionService.logIn) {
        sessionService.logIn(mockSession);
      } else {
        // Fallback: inject using exposed test hook if app allows it
        win.__test_session = mockSession;
      }
    });

    // Navigate after setting session
    cy.visit('/me');

    // Confirm we intercepted the request
    cy.wait('@getUser');

    // Check user is rendered
    cy.contains('test').should('exist');
    cy.url().should('include', '/me');
  });

  it('should redirect to login if no session', () => {
    cy.visit('/me');
    cy.url().should('include', '/login');
  });
});