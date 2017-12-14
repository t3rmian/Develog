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
            $.get($(location).attr('href') + '/tag', {action: "ADD", tag: value});
        },
        onChipDelete: function (e, chip) {
            var value = chip.innerText.substring(0, chip.innerText.length - 'close'.length);
            $.get($(location).attr('href') + '/tag', {action: "REMOVE", tag: value});
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