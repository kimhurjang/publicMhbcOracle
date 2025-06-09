// scroll-top 버튼 기능
const side = document.querySelector('.side');
const scrollTop = document.querySelector('.scroll-top');
function toggleScrollTop() {
const path = window.location.pathname;
  if (scrollTop) {
    // 메인 페이지일 경우 (예: "/index.html" 혹은 "/")
    if (path === '/' || path === '/home' || path === '/index') {
      window.scrollY > 100
        ? side.classList.add('active')
        : side.classList.remove('active');
    } else {
      // 서브 페이지는 항상 active
      side.classList.add('active');
    }
  }
}
scrollTop.addEventListener('click', (e) => {
  e.preventDefault();
  window.scrollTo({ top: 0, behavior: 'smooth' });
});
window.addEventListener('load', toggleScrollTop);
document.addEventListener('scroll', toggleScrollTop);

// AOS 초기화
document.addEventListener("DOMContentLoaded", function () {
  AOS.init({
    duration: 600,
    easing: 'ease-in-out',
    delay: 0,
    offset: 10,
    once: false
  });
});
window.addEventListener('load', () => AOS.refresh());

window.addEventListener("load", function () {
  //console.log(document.querySelectorAll(".ani_box").length);
  //console.log(typeof ScrollTrigger);

  gsap.registerPlugin(ScrollTrigger);

  // ani_box가 렌더링되었는지 감지
  const observer = new MutationObserver(() => {
    const boxes = gsap.utils.toArray(".ani_box");
    if (boxes.length > 0) {
      boxes.forEach((el, i) => {
        gsap.from(el, {
          scrollTrigger: {
            trigger: el,
            start: "top 90%",
            toggleActions: "play none none none"
          },
          duration: 1.5,
          rotateY: 90,
          opacity: 0,
          x: 100,
          ease: "power3.out",
          delay: i * 0.1
        });
      });
      ScrollTrigger.refresh(); // 꼭 필요
      observer.disconnect(); // 더 감시 안 해도 됨
    }
  });

  observer.observe(document.body, {
    childList: true,
    subtree: true
  });
});
