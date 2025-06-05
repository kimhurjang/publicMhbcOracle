/**
 * 관리자 예약 수정 화면 전용 스크립트
 * 기능:
 *  - 선택된 홀에 따라 인원수 라디오 버튼 동적 생성
 *  - 날짜 유효성 검사 (오늘 이전 불가, 상담일은 행사일 기준 2주 전이어야 함)
 *  - 상담 가능일 입력 필수 유효성 검사
 */

document.addEventListener("DOMContentLoaded", function () {
  //console.log("[디버그] 관리자 reservation_admin.js 실행됨");

  // guestCnt 초기화는 HTML <script>에서 처리하므로 여기선 생략
  // 선택된 hall 변경 시 → guestCnt 다시 그리기
  document.querySelectorAll('input[name="hallIdx"]').forEach(radio => {
    radio.addEventListener('change', function () {
      const capacity = parseInt(this.dataset.capacity);
      //console.log("[디버그] 홀 변경됨 → capacity =", capacity);
      renderGuestOptions(capacity, null);
    });
  });

  bindDateValidations(); // 날짜 유효성 검사
});

/**
 * 선택된 홀의 수용인원(capacity)에 따라 인원수 라디오 버튼 생성
 * @param {number} capacity - 선택된 홀의 최대 인원 수
 * @param {string|null} selectedValue - 기존 선택된 인원수 값 (초기 표시용)
 */
function renderGuestOptions(capacity, selectedValue = null) {
  const guestContainer = document.getElementById('guestCntContainer');
  if (!guestContainer) {
    console.warn("guestCntContainer가 존재하지 않음");
    return;
  }

  guestContainer.innerHTML = ''; // 기존 요소 초기화

  for (let i = 100; i <= capacity; i += 50) {
    const id = `guestCnt_${i}`;

    const input = document.createElement('input');
    input.type = 'radio';
    input.name = 'guestCnt';
    input.value = i;
    input.id = id;
    if (selectedValue && parseInt(selectedValue) === i) input.checked = true;

    const label = document.createElement('label');
    label.htmlFor = id;
    label.innerText = `${i}명`;

    guestContainer.appendChild(input);
    guestContainer.appendChild(label);
  }

  //console.log(`[디버그] guestCnt 라디오 ${capacity >= 100 ? Math.floor((capacity - 100) / 50) + 1 : 0}개 생성 완료`);
}

/**
 * 날짜 유효성 검사: 과거 선택 불가 + 상담일은 행사일 2주 이전
 */
function bindDateValidations() {
  const dateTargets = [
    { id: "contact_date", label: "상담 가능 날짜" },
    { id: "event_date_input", label: "행사 예정일" }
  ];

  dateTargets.forEach(({ id, label }) => {
    const input = document.getElementById(id);
    if (!input) return;

    input.addEventListener("change", function () {
      const selected = new Date(this.value + "T00:00:00");
      const today = new Date();
      today.setHours(0, 0, 0, 0);

      //console.log(`[디버그] ${label} 선택됨 → ${selected.toISOString().slice(0, 10)}`);
      //console.log(`[디버그] 오늘 날짜: ${today.toISOString().slice(0, 10)}`);

      if (selected <= today) {
        alert(`${label}은 당일 이후만 선택 가능합니다.`);
        this.value = "";
        this.focus();
      }

      if (id === "contact_date") {
        const eventInput = document.getElementById("event_date_input");
        if (eventInput && eventInput.value) {
          const eventDate = new Date(eventInput.value + "T00:00:00");
          const twoWeeksBeforeEvent = new Date(eventDate);
          twoWeeksBeforeEvent.setDate(eventDate.getDate() - 14);

          //console.log(`[디버그] 행사일: ${eventDate.toISOString().slice(0, 10)}`);
          //console.log(`[디버그] 상담 가능 최소일: ${twoWeeksBeforeEvent.toISOString().slice(0, 10)}`);

          if (selected > twoWeeksBeforeEvent) {
            alert(`상담 가능 날짜는 행사 예정일 최소 2주 이전이어야 합니다.`);
            this.value = "";
            this.focus();
          }
        }
      }
    });
  });
}

//onclick 용 금액산출버튼
function calcPrice() {
    const hallRadio = document.querySelector('input[name="hallIdx"]:checked');
    const guestRadio = document.querySelector('input[name="guestCnt"]:checked');
    const mealRadio = document.querySelector('input[name="mealType"]:checked');
    const flowerRadio = document.querySelector('input[name="flower"]:checked');
    const displayInput = document.getElementById("price_result_display");
    const input = document.getElementById("totalAmount");

    if (!hallRadio) { alert("홀을 먼저 선택하세요."); hallRadio?.focus(); return; }
    if (!guestRadio) { alert("인원수를 선택하세요."); guestRadio?.focus(); return; }
    if (!mealRadio) { alert("식사 종류를 선택하세요."); mealRadio?.focus(); return; }
    if (!flowerRadio) { alert("꽃 종류를 선택하세요."); flowerRadio?.focus(); return; }

    const hallId = parseInt(hallRadio.value);
    const hallPrice = hallMap[hallId].price;
    const mealPrice = { "뷔페A실속": 13000, "뷔페B실속": 8000, "도시락": 7000 }[mealRadio.value] || 0;
    const flowerPrice = { "생화": 7000, "조화 + 생화": 5000, "조화": 4000 }[flowerRadio.value] || 0;
    const guestCnt = parseInt(guestRadio.value);

    const total = (hallPrice + mealPrice + flowerPrice) * guestCnt;

    displayInput.textContent = total.toLocaleString() + "원"; // 사용자 보기용
    input.value = total.toLocaleString();                    // 사용자 입력란(콤마 O, '원' 제외)
    hiddenInput.value = total;                               // 서버 전송용 (숫자)
}

//서버전송용
function validateForm_edit() {
  const viewInput = document.getElementById("totalAmount");      // 콤마 포함
  const hiddenInput = document.getElementById("price_result");   // 서버 전송용

  if (viewInput && hiddenInput) {
    const value = viewInput.value.replace(/,/g, "").replace("원", "").trim();
    hiddenInput.value = value || "0";
    //console.log("[디버그] 서버 전송용 totalAmount =", hiddenInput.value);
  }

  return true;
}

