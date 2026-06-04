// GSH Schedule — nav highlight + block fade-up
(function () {
  'use strict';

  var navLinks = document.querySelectorAll('.nav-links a');
  var sections = [];

  navLinks.forEach(function (link) {
    var href = link.getAttribute('href');
    if (href && href.startsWith('#')) {
      var el = document.getElementById(href.substring(1));
      if (el) sections.push({ id: href.substring(1), el: el, link: link });
    }
  });

  sections.sort(function (a, b) {
    return a.el.offsetTop - b.el.offsetTop;
  });

  // ---- Nav highlight ----
  var ticking = false;
  window.addEventListener('scroll', function () {
    if (!ticking) {
      ticking = true;
      requestAnimationFrame(function () {
        var scrollPos = window.scrollY + 120;
        var current = sections[0];
        sections.forEach(function (s) {
          if (s.el.offsetTop <= scrollPos) current = s;
        });
        navLinks.forEach(function (l) { l.classList.remove('active'); });
        if (current) current.link.classList.add('active');
        ticking = false;
      });
    }
  }, { passive: true });

  // ---- Block slow fade-in (triggers every time) ----
  var blocks = document.querySelectorAll('.feature-block');
  var visible = [];

  blocks.forEach(function (block, i) {
    visible[i] = false;
    block.style.opacity = '0';
    block.style.transition = 'opacity 0.8s ease-out';
  });

  var blockObserver = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      var idx = Array.prototype.indexOf.call(blocks, entry.target);
      if (entry.isIntersecting) {
        if (!visible[idx]) {
          visible[idx] = true;
          entry.target.style.opacity = '1';
        }
      } else {
        visible[idx] = false;
        entry.target.style.opacity = '0';
      }
    });
  }, { threshold: 0.15 });

  blocks.forEach(function (block) {
    blockObserver.observe(block);
  });
})();
