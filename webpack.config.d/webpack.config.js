var nodeExternals = require('webpack-node-externals');
config.target = 'node';
config.externals = [nodeExternals()];

