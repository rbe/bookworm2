const redoc = function () {
    const extract = function (v) {
        return decodeURIComponent(v.replace(/(?:(?:^|.*;\s*)contextPath\s*\=\s*([^;]*).*$)|^.*$/, "$1"));
    };
    const cookie = extract(document.cookie);
    const contextPath = cookie === '' ? extract(window.location.search.substring(1)) : cookie;
    const service = new URLSearchParams(window.location.search).get('service');
    const swaggerUrl = contextPath + '/swagger/' + service + '-1.0.0.yml';
    Redoc.init(swaggerUrl);
}
redoc();
