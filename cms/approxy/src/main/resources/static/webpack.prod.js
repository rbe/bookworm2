const path = require('path');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = {
    entry: {
        'wbhonline': './hoerbuchkatalog/js/wbhonline.js'
    },
    mode: 'production',
    output: {
        filename: 'wbhonline.js',
        path: path.resolve(__dirname, 'dist-prod'),
    },
    optimization: {
        minimize: true,
        minimizer: [new TerserPlugin({
            terserOptions: {
                format: {
                    comments: false,
                },
            },
            extractComments: true,
        })],
    },
    plugins: [
    ]
};
