document.addEventListener("DOMContentLoaded", function () {
  const allSections = $(".bg-holder");
  let isOnMap = false;

  // 마우스가 #map에 들어가거나 나갈 때 상태 업데이트
  const mapElement = document.querySelector("#map");
  if (mapElement) {
    mapElement.addEventListener("mouseenter", () => {
      isOnMap = true;
    });
    mapElement.addEventListener("mouseleave", () => {
      isOnMap = false;
    });
  }
  allSections.each(function (index) {
    // 'wheel' 이벤트를 직접 바인딩하고 passive:false 옵션 추가
    this.addEventListener("wheel", function (e) {
//      const isOnMap = e.target.closest('#map');
//      if (isOnMap) return;

      e.preventDefault();

      let delta = e.deltaY < 0 ? 1 : -1;

      let moveTop = window.scrollY;

      if (delta < 0 && index < allSections.length - 1) {
        moveTop = allSections[index + 1].offsetTop;
      } else if (delta > 0 && index > 0) {
        moveTop = allSections[index - 1].offsetTop;
      } else {
        return;
      }

      $("html, body").stop().animate({
        scrollTop: moveTop + "px"
      }, 600);

    }, { passive: false });
  });
});
