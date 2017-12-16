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

function displayOtherEvents(events) {
    var otherEvents = events.filter(function (event) {
        return event.type !== 'ONLINE';
    });
    if (otherEvents.length === 0) {
        $(".other").css("display", "none");
    } else {
        $("#other").JSONView(otherEvents);
    }
}

function displayOnlineEvents(events) {
    var onlineEvents = events.filter(function (event) {
        return event.type === 'ONLINE';
    });
    var online = [];
    var maxOnline = 0;
    for (var i = 0; i < onlineEvents.length; i++) {
        maxOnline = Math.max(maxOnline, onlineEvents[i].value);
    }
    var maxRadius = Math.min(50, maxOnline + 9);
    if (onlineEvents.length > 500) {
        maxRadius /= 2;
    }
    for (i = 0; i < onlineEvents.length; i++) {
        online.push({
            t: new Date(onlineEvents[i].time),
            y: onlineEvents[i].value,
            r: onlineEvents[i].value * maxRadius / maxOnline
        });
    }

    var ctx = document.getElementById("myChart").getContext('2d');
    var myChart = new Chart(ctx, {
        type: 'bubble',
        data: {
            datasets: [{
                label: '# of users online',
                data: online,
                borderWidth: 1,
                backgroundColor: "rgb(255, 99, 132)"
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    },
                    scaleLabel: {
                        display: true,
                        labelString: 'Online'
                    }
                }],
                xAxes: [{
                    type: "time",
                    display: true,
                    scaleLabel: {
                        display: true,
                        labelString: 'Date'
                    },
                    ticks: {
                        major: {
                            fontStyle: "bold",
                            fontColor: "#FF0000"
                        }
                    }
                }]
            },
            maintainAspectRatio: true,
            responsive: true,
            tooltips: {
                callbacks: {
                    label: function (tooltipItem) {
                        return '# of users online: ' + tooltipItem.yLabel;
                    },
                    title: function (tooltipItem) {
                        return tooltipItem[0].xLabel;
                    }
                }
            }
        }
    });
}

(function ($) {
    $(function () {
        $('.sidenav').sidenav();
    });
})(jQuery);


$(window).on('hashchange', function () {
    loadFragment()
});
loadFragment();