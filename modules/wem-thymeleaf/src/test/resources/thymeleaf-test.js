exports.render_no_view = function () {

    return execute('thymeleaf.render', {});

};

exports.render = function () {

    var view = resolve('view/test.html');
    return execute('thymeleaf.render', {
        view: view,
        model: {
            fruits: [
                {
                    name: 'Apple',
                    color: 'Red'
                },
                {
                    name: 'Pear',
                    color: 'Green'
                }
            ]
        }
    });

};
