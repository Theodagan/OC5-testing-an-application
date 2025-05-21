declare namespace Cypress {
    interface Chainable {
      login(): Chainable<void>;
      loginAsAdmin(): Chainable<void>;
      // Ajoute d'autres commandes ici si besoin
    }
  }