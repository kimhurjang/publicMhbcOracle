window.onload = function () {
    const allSections = $(".bg-holder"); // ms1 ~ ms5 + footer

    allSections.each(function (index) {
        $(this).on("mousewheel DOMMouseScroll", function (e) {
            e.preventDefault();

            let delta = 0;
            if (!event) event = window.event;
            if (event.wheelDelta) {
                delta = event.wheelDelta / 120;
                if (window.opera) delta = -delta;
            } else if (event.detail) {
                delta = -event.detail / 3;
            }

            let moveTop = $(window).scrollTop();

            if (delta < 0 && index < allSections.length - 1) {
                moveTop = $(allSections[index + 1]).offset().top;
            } else if (delta > 0 && index > 0) {
                moveTop = $(allSections[index - 1]).offset().top;
            } else {
                return; // 첫 섹션 or 마지막이면 이동 없음
            }

            $("html, body").stop().animate({
                scrollTop: moveTop + "px"
            }, 600);
        });
    });
}
