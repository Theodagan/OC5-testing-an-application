import { defineConfig } from 'cypress'

// if (!process.env['CYPRESS_PROJECT_ID']) {
//   throw new Error('CYPRESS_PROJECT_ID is not set in the environment.')
// }

export default defineConfig({
  projectId: process.env['CYPRESS_PROJECT_ID'], // from Nix env
  videosFolder: 'cypress/videos',
  screenshotsFolder: 'cypress/screenshots',
  fixturesFolder: 'cypress/fixtures',
  video: true, // needed to record runs on Cypress Cloud
  env: {
    BACKEND_ENABLED: process.env['BACKEND_ENABLED'] === 'true',
    codeCoverage: {
      exclude: ['src/main.ts', 'src/environments/*.ts']
    }
  },
  e2e: {
    setupNodeEvents(on, config) {
      return require('./cypress/plugins/index.ts').default(on, config)
    },
    baseUrl: process.env['CYPRESS_BASE_URL'] || 'http://localhost:4200/',
  },
})
