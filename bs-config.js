const history = require('connect-history-api-fallback');

module.exports = {
  server: {
    baseDir: './dist/frontend',
    middleware: [history()]
  }
};
