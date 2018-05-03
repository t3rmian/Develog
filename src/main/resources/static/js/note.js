function initChips(tags, autocompleteTags, label, secondaryLabel) {
    var chips = [];
    var autocompleteData = {};
    for (var i = 0; i < tags.length; i++) {
        chips.push({tag: tags[i]});
    }
    for (i = 0; i < autocompleteTags.length; i++) {
        autocompleteData[autocompleteTags[i]] = null;
    }
    $('#tags').chips({
        data: chips,
        placeholder: label,
        secondaryPlaceholder: '+' + secondaryLabel,
        autocompleteOptions: {
            data: autocompleteData,
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
            if (response != null && response.indexOf("#editor") !== -1) {
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

function saveAs(data, filename, type) {
    var file = new Blob([data], {type: type});
    if (window.navigator.msSaveOrOpenBlob) // IE10+
        window.navigator.msSaveOrOpenBlob(file, filename);
    else { // Others
        var a = document.createElement("a"),
            url = URL.createObjectURL(file);
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        setTimeout(function () {
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        }, 0);
    }
}

function saveAsText(content, fileName, tags, type) {
    saveAs("[tags]: <> (" + tags + ")\n" + content, 'Develog_' + fileName + '.txt', {type: type})
}

function saveAsHtml(html, fileName, tags, type) {
    html.getElementsByTagName('head')[0].innerHTML =
        "<title>Develog [" + fileName + "]</title>" +
        "<meta name='keywords' content='" + tags + "'/>";
    var xmlSerializer = new XMLSerializer();
    saveAs(xmlSerializer.serializeToString(html), 'Develog_' + fileName + '.html', {type: type})
}

function getChipsAsString() {
    var chips = $('#tags')[0].M_Chips.chipsData;
    var tags = "";
    var prefix = "";
    for (var i = 0; i < chips.length; i++) {
        tags += prefix + chips[i].tag;
        prefix = ", ";
    }
    return tags;
}

function initDownloadButtons(fileName) {
    $("#downloadRaw").click(function () {
        saveAsText($("#input").val(), fileName, getChipsAsString(), "text/plain;charset=" + document.characterSet);
    });
    $("#downloadHtml").click(function () {
        if (fileName === null) {
            fileName = "archive";
        }
        saveAsHtml($("#output").contents().find('html')[0], fileName, getChipsAsString(), "text/plain;charset=" + document.characterSet);
    });
}

function enableEditorResizing(containerSelector, leftSelector, rightSelector, handleSelector) {
    function startDragging() {
        dragging = true;
    }

    var stopDragging = function () {
        dragging = false;
    };
    var container = $(containerSelector),
        left = $(leftSelector),
        right = $(rightSelector),
        handle = $(handleSelector),
        dragging = false;

    handle.on('mousedown', startDragging);
    $(document).on('mouseup', stopDragging);
    $(document).on('mousemove', function (e) {
        if (dragging) {
            var rightOffset = container.width() - (e.clientX - container.offset().left);
            left.css('width', container.width() - rightOffset);
            right.css('width', rightOffset);
        }
    });
    $('iframe').contents().on({
        mouseup: stopDragging,
        mousemove: function (e) {
            if (dragging) {
                var rightOffset = right.width() - e.clientX;
                left.css('width', container.width() - rightOffset);
                right.css('width', rightOffset);
            }
        }
    });
}