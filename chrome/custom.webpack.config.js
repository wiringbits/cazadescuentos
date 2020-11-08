var merge = require('webpack-merge');
var generated = require('./scalajs.webpack.config');

var local = {
    // NOTE: development is useful for debugging but apparently it breaks the build for Chrome
    // with the current settings.
    mode: "production",
    output: {
        // by default, scalajs-bundler sets this to "var" but that breaks the build for Firefox.
        libraryTarget: "window"
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },
            {
                test: /\.(ttf|eot|woff|png|glb|svg)$/,
                use: 'file-loader'
            },
            {
                test: /\.(eot)$/,
                use: 'url-loader'
            }
        ]
    }
};

module.exports = merge(generated, local);
