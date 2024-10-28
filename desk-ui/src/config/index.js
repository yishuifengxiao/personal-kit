export default {

    baseUrl: {
        dev: 'http://localhost:8600',
        prod: ''
    },
    rootUrl: function () {
        return process.env.NODE_ENV === 'development' ? 'http://localhost:8600' : ''
    }
}