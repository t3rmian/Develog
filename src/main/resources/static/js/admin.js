function clearLogs() {
    $.ajax({
        url: getLocation() + "/reset",
        type: 'post',
        data: $('#form').serialize()
    });
    loadFragment();
}

function loadFragment() {
    var fragment = window.location.hash.replace('#', '');
    if (fragment == null || fragment == "") {
        window.location.hash = "#users";
        fragment = "users"
    }
    $('li').removeClass('active');
    $('a[href*="' + window.location.hash + '"]').each(function () {
        $(this).parent().addClass('active');
    });

    fragment = getLocation() + "/" + fragment;
    $.ajax({
        url: fragment,
        type: 'post',
        data: $('#form').serialize(),
        success: function (response) {
            try {
                $("#fragment").JSONView(JSON.parse(response));
            } catch (jsonError) {
                try {
                    $("#fragment").JSONView(response);
                } catch (htmlError) {
                    $("#fragment").html(response);
                }
            }
        }
    });
}

function getLocation() {
    return location.protocol + '//' + location.host + location.pathname + (location.search ? location.search : "");
}

$(window).on('hashchange', function () {
    loadFragment()
});
loadFragment();