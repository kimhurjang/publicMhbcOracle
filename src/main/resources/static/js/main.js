

/*-------------- main visual text animation  --------------*/
const elementsManhwa = document.querySelectorAll('.name .letter');
const subtitlePhrases = document.querySelectorAll('.subtitle .phrase');
const totalManhwa = elementsManhwa.length;

function resetLetters() {
  elementsManhwa.forEach(el => {
    el.style.opacity = 0;
  });

  subtitlePhrases.forEach(el => {
    el.style.opacity = 0;
  });
}

function fadeInLetters() {
  resetLetters();

  anime({
    targets: elementsManhwa,
    opacity: 1,
    scale: 1,
    easing: 'easeInOutQuad',
    duration: 800,
    delay: (el, i) => i * 150,
    complete: () => {
      setTimeout(() => fadeInSubtitle(), 500);
    }
  });
}

function fadeInSubtitle() {
  anime({
    targets: subtitlePhrases,
    opacity: 1,
    scale: 1,
    easing: 'easeInOutQuad',
    duration: 800,
    delay: (el, i) => i * 500, // 덩어리별로 보여지게
    complete: () => {
      setTimeout(() => fadeOutSubtitle(), 5000);
    }
  });
}

function fadeOutSubtitle() {
  anime({
    targets: subtitlePhrases,
    opacity: 0,
    scale: 0.8,
    easing: 'easeInOutQuad',
    duration: 800,
    delay: (el, i) => (subtitlePhrases.length - i - 1) * 300, // 역순 덩어리씩 지워짐
    complete: () => {
      fadeOutLetters();
    }
  });
}

function fadeOutLetters() {
  anime({
    targets: elementsManhwa,
    opacity: 0,
    scale: 0.8,
    easing: 'easeInOutQuad',
    duration: 800,
    delay: (el, i) => (totalManhwa - i - 1) * 150,
    complete: () => {
      setTimeout(() => fadeInLetters(), 2000);
    }
  });
}

fadeInLetters();
