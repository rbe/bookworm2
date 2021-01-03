const path = require('path');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = {
    entry: {
        'wbhonline': './hoerbuchkatalog/js/wbhonline.js',
        'wbhonlineSuchformular': './hoerbuchkatalog/js/wbhonlineSuchformular.js',
        /*
        'wbhonlineA11y': './src/wbhonlineA11y.js',
        'wbhonlineButtons': './src/wbhonlineButtons.js',
        'wbhonlineCdBestellung': './src/wbhonlineCdBestellung.js',
        'wbhonlineDownloads': './src/wbhonlineDownloads.js',
        'wbhonlineHelper': './src/wbhonlineHelper.js',
        'wbhonlineHoerprobe': './src/wbhonlineHoerprobe.js',
        'wbhonlineMerkliste': './src/wbhonlineMerkliste.js',
        'wbhonlineWarenkorb': './src/wbhonlineWarenkorb.js',
        */
    },
    mode: 'production',
    output: {
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
