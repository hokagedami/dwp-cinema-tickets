export default {
  testEnvironment: 'node',
  transform: {
    '^.+\\.jsx?$': 'babel-jest', // Use Babel to transform JavaScript files
  },
  moduleNameMapper: {
    // If you have any non-JS modules, you can mock them here
  },
}; 