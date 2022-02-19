$(document).ready(function () {

    $('.fav-corner').on('click', function (e) {
        e.preventDefault();
        var courseId = $(this).attr('id');
        var method = $(this).attr('method');
        if (method == 'like') {
            $(this).attr('method', 'unlike');
            $(this).attr('class', 'fav-corner btn bx bi-heart-fill');
            $('#' + courseId).text(parseInt($('#' + courseId).text()) + 1);
        } else if (method === 'unlike') {
            $(this).attr('method', 'like');
            $(this).attr('class', 'fav-corner btn bx bx-heart');
            $('#' + courseId).text(parseInt($('#' + courseId).text()) - 1);
        }
        var obj = {courseId: courseId, method: method};
        var jsonString = JSON.stringify(obj);
        $.ajax({
            url: '/course/toggle_fav',
            contentType: "application/json; charset=utf-8",
            type: 'POST',
            data: jsonString,
            cache: false,
        });
    });

    var size = $('.load-more').size();
    var x = 6;
    if (size <= x) {
        $('#loadMore').hide();
    }
    $('.load-more:lt('+x+')').show();
    $('#loadMore').click(function (){
        x = (x + 6 <= size) ? x + 6 : size;
        $('.load-more:lt('+x+')').show();
        if (x === size) {
            $('#loadMore').hide();
        }
    });

}) // document.ready