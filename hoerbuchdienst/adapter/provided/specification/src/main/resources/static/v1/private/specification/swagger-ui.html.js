window.onload = function () {
    const extract = function (v) {
        return decodeURIComponent(v.replace(/(?:(?:^|.*;\s*)contextPath\s*\=\s*([^;]*).*$)|^.*$/, "$1"));
    };
    const cookie = extract(document.cookie);
    const service = new URLSearchParams(window.location.search).get('service');
    const contextPath = cookie === '' ? extract(window.location.search.substring(1)) : cookie;
    const swaggerUrl = contextPath + '/swagger/' + service + '-1.0.0.yml';
    const f = contextPath === '' ? undefined : function (system) {
        return {
            statePlugins: {
                spec: {
                    wrapActions: {
                        updateJsonSpec: (oriAction, system) => (...args) => {
                            let [spec] = args;
                            if (spec && spec.paths) {
                                const newPaths = {};
                                Object.entries(spec.paths).forEach(([path, value]) => newPaths[contextPath + path] = value);
                                spec.paths = newPaths;
                            }
                            oriAction(...args);
                        }
                    }
                }
            }
        };
    };
    const ui = SwaggerUIBundle({
        url: swaggerUrl,
        dom_id: '#swagger-ui',
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],
        plugins: [
            SwaggerUIBundle.plugins.DownloadUrl,
            f
        ],
        layout: "StandaloneLayout",
        validatorUrl: null,
        deepLinking: true
    });
    window.ui = ui;
};
