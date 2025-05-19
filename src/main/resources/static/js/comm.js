// scroll-top 버튼 기능
const scrollTop = document.querySelector('.scroll-top');
function toggleScrollTop() {
  if (scrollTop) {
    window.scrollY > 100 ? scrollTop.classList.add('active') : scrollTop.classList.remove('active');
  }
}
scrollTop?.addEventListener('click', (e) => {
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
