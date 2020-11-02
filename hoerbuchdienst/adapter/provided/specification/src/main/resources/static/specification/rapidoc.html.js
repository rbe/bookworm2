const rapidoc = function () {
    const extract = function (v) {
        return decodeURIComponent(v.replace(/(?:(?:^|.*;\s*)contextPath\s*\=\s*([^;]*).*$)|^.*$/, "$1"));
    };
    const cookie = extract(document.cookie);
    const contextPath = cookie === '' ? extract(window.location.search.substring(1)) : cookie;
    const rapidoc = document.getElementById('rapidoc');
    if (contextPath !== '') {
        rapidoc.addEventListener('spec-loaded', e => {
            e.detail.tags.forEach(tag => tag.paths.forEach(path => path.path = contextPath + path.path));
            rapidoc.requestUpdate();
        });
    }
    const service = new URLSearchParams(window.location.search).get('service');
    const swaggerUrl = contextPath + '/swagger/' + service + '-1.0.0.yml';
    rapidoc.setAttribute('spec-url', swaggerUrl);
}
rapidoc();
