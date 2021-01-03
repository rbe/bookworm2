const path = require('path');

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
    mode: 'development',
    devtool: 'source-map',
    output: {
        //filename: 'wbhonline.js',
        path: path.resolve(__dirname, 'dist-dev'),
    },
    optimization: {
        minimize: false
    }
};
