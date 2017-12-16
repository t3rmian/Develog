(function () {
    function redirectionHappened() {
        return xhr.responseURL && (xhr.responseURL.indexOf("/owr/") !== -1 ||
            ((window.location.origin + "/") === xhr.responseURL && window.location.href !== "/"));
    }

    var xhr;
    var _orgAjax = $.ajaxSettings.xhr;
    $.ajaxSettings.xhr = function () {
        xhr = _orgAjax();
        return xhr;
    };

    $.ajaxSetup({
        dataFilter: function (data) {
            if (redirectionHappened()) {
                window.location.href = "/";
                data = "";
            }
            return data;
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
        }
    });
})();