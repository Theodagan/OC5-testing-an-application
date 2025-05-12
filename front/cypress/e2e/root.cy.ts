describe('Root Route', () => {
    it('should load the home page', () => {
        cy.visit('/'); // Visit the root URL

        // Basic assertion: checks that the page body exists
        cy.get('body').should('be.visible');

        // Optional: Check for an element you expect on the home page
        // cy.contains('Welcome')  // Adjust this to match something real
        //   .should('exist');
    });
});