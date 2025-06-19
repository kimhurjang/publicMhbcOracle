
// DOM 로드 후 실행
document.addEventListener("DOMContentLoaded", function () {
  // 스크롤 타겟 섹션들 (class="bg-holder")
  const allSections = document.querySelectorAll(".bg-holder");

  // 터치 시작 위치 저장용
  let startY = 0;

  // 연속 스크롤 방지를 위한 flag
  let isMoving = false;

  // 각 섹션마다 이벤트 바인딩
  allSections.forEach((section, index) => {
    // PC용 휠 이벤트 처리
    section.addEventListener("wheel", function (e) {
      e.preventDefault(); // 기본 휠 스크롤 막기
      const delta = e.deltaY < 0 ? 1 : -1; // 위로 스크롤 = 1, 아래로 = -1
      scrollByDelta(delta, index); // 방향에 따라 섹션 이동
    }, { passive: false });

    // 모바일 터치 시작 지점 저장
    section.addEventListener("touchstart", function (e) {
      startY = e.touches[0].clientY;
    });

    // 모바일 터치 이동 처리
    section.addEventListener("touchmove", function (e) {
      if (isMoving) return; // 연속 작동 방지

      const currentY = e.touches[0].clientY;
      const deltaY = currentY - startY;

      // 스와이프 방향 감지 (50px 이상일 때만 작동)
      const delta = deltaY > 50 ? 1 : deltaY < -50 ? -1 : 0;

      if (delta !== 0) {
        isMoving = true;
        scrollByDelta(delta, index); // 방향대로 섹션 이동
        setTimeout(() => { isMoving = false; }, 800); // 0.8초 후 다시 가능
      }
    });
  });

  // 방향에 따른 섹션 이동 함수
  function scrollByDelta(delta, index) {
    let targetIndex = delta > 0 ? index - 1 : index + 1;

    if (targetIndex >= 0 && targetIndex < allSections.length) {
      const moveTop = allSections[targetIndex].offsetTop;
      window.scrollTo({ top: moveTop, behavior: 'smooth' }); // 부드러운 스크롤
    }
  }
});
