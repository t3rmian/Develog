function initChips(tags) {
    var chips = [];
    for (var i = 0; i < tags.length; i++) {
        chips.push({tag: tags[i].id.value});
    }
    $('#tags').chips({
        data: chips,
        placeholder: 'Enter a tag',
        secondaryPlaceholder: '+Tag',
        autocompleteOptions: {
            data: {
                'Apple': null,
                'Microsoft': null,
                'Google': null
            },
            limit: Infinity,
            minLength: 2
        },
        onChipAdd: function (e, chip) {
            var value = chip.innerText.substring(0, chip.innerText.length - 'close'.length);
            if ($(location).attr('href').indexOf("search") !== -1) {
                searchByTags($('#tags')[0].M_Chips.chipsData)
            } else {
                $.post($(location).attr('href') + '/tag', {action: "ADD", value: value});
            }
        },
        onChipDelete: function (e, chip) {
            var value = chip.innerText.substring(0, chip.innerText.length - 'close'.length);
            if ($(location).attr('href').indexOf("search") !== -1) {
                searchByTags($('#tags')[0].M_Chips.chipsData)
            } else {
                $.post($(location).attr('href') + '/tag', {action: "REMOVE", value: value});
            }
        }
    });
}

function initEditor() {
    var pendingTimeout = false;
    $('#input').on('input propertychange change', function () {
        if (!pendingTimeout) {
            pendingTimeout = true;
            setTimeout(function () {
                updateNote();
                pendingTimeout = false;
            }, 1000);
        }
    });
}

function updateNote() {
    $.ajax({
        url: $(location).attr('href') + '/update',
        type: 'post',
        data: $('#form').serialize(),
        success: function (response) {
            $("#output").contents().find('html').html(response);
        }
    });
}

function search(url, data) {
    $.ajax({
        url: url,
        type: 'post',
        data: data,
        success: function (response) {
            if (response != "") {
                $("#editor").replaceWith(response);
                $("#output").contents().find('html').html($("#output0")[0].innerText);
                $("#output0").remove();
            } else {
                $("#editor").text("");
            }
        }
    });
}

function searchByTags(chips) {
    var tags = [];
    for (var i = 0; i < chips.length; i++) {
        tags.push(chips[i].tag);
    }
    search('/search/tag', {values: tags});
    history.replaceState({}, "Search", "/search");
}

function searchByDate(date) {
    search('/search/' + date);
}
