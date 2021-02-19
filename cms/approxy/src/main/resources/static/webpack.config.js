const path = require('path');

module.exports = {
    entry: {
        'wbhonline': './hoerbuchkatalog/js/wbhonline.js'
    },
    mode: 'development',
    devtool: 'source-map',
    output: {
        filename: 'wbhonline.js',
        path: path.resolve(__dirname, 'dist-dev'),
    },
    optimization: {
        minimize: false
    }
};
