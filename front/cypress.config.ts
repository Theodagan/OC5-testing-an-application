import { defineConfig } from 'cypress'

export default defineConfig({
  projectId: process.env['CYPRESS_PROJECT_ID'], // from Nix env
  videosFolder: 'cypress/videos',
  screenshotsFolder: 'cypress/screenshots',
  fixturesFolder: 'cypress/fixtures',
  video: true, // needed to record runs on Cypress Cloud
  e2e: {
    setupNodeEvents(on, config) {
      return require('./cypress/plugins/index.ts').default(on, config)
    },
    baseUrl: process.env['CYPRESS_BASE_URL'] || 'http://localhost:4200',
  },
})
